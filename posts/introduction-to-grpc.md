What is gRPC? gRPC (remote procedure call) is used for communicating between distributed systems (micro services). Google created and originally used a system called stubby, which later was open sourced as gRPC. While using protobuffers is not mandatory to use gRPC, it is generally the easiest approach (as it works out of the box). You define a protobuf in a simple message format and use protoc (the protobuf compiler) to generate code to handle serializing and creating your domain objects. You can also create a service in the protobuf definition which allows generation of methods for the client to communicate with the server.

The protobuf simply defines the contract between the services, and this allows us to be language agnostic. Using libraries for specific language, you can generate language specific code and use a language specific client to call the server. The server doesn’t need to know what language (this is also true of REST) the client is using, all it needs is the serialized message (which it knows how to deserialize).

Now that we’ve discussed what gRPC is, let’s flesh out an example. We will create a simple example application (available on GitHub - https://github.com/hegek87/derivative-solver) to compute the derivative of simple polynomials. If you don’t remember how a derivative is calculated, we will quickly show the general rule our server will use.


Remember that a polynomial is a function of the form `c_nx^n + c_{n-1}x^{n-1} + …. + c_1x + c_0`, where `c_1, c_2, …, c_n` are simple constants. To give a concrete example, let’s use the polynomial `x^4 + 2x^2 - x - 1`. Next, let’s get a better understanding of how to find the derivative of a polynomial. If you never learned this, it’s a pretty robotic procedure and I hope will be straightforward to understand what is being done, even if you don’t necessarily understand why. To calculate, we simply multiply the exponent by the coefficient, and subtract one from the original exponent. Given a simple polynomial, `4x^2`, we find the derivative by multiplying the exponent and coefficient, `4*2 = 8`, then we subtract one from the exponent, `2-1=1`, giving us the derivative, `8x^1 = 8x`. This procedure works for simple polynomials, and can be expanded for polynomials of more than one term. Another example is `x^3 + 3x^2 + 3`. Remember that x^0 = 1, so 3 = 3x^0, and we can calculate the derivative as 3*0 = 0. The full derivative of the equations leads to 3x^2 + 6x + 0 = 3x^2 + 6x.


Now that we understand what a derivative is, let’s discuss how we represent a polynomial for our protobuf. It seems like we have two simple ways to represent the polynomial, a naive way to do this is by sending it as a raw string, while this would work, it could be cumbersome, as we will need to find a way to split the string, parse the constants and exponents into numeric values and map back to a string after calculating the derivative. Perhaps a more elegant solution is to send the constant as an array, and the exponents as an array, this way we can get the constant values and exponent values without any parsing on our side. Something like

``` 
Message Polynomial {
	repeated coefficients = 1;
	repeated exponents = 2;
}
```

Now that we have our message structure defined, we can compile to see the generated code. As expected, you will see POJOs generated with setters, getters, and builders. Once we have this created, we can begin setting up our service method that will be called in order to actually make requests:
```
service DerivativeSolver {
  rpc findDerivative(Polynomial) returns (Polynomial) {}
}
```

Compiling using protoc once again, we will not have everything we need to set up our simple application. Now that we have our simple proto definition, let’s discuss how we create a client to communicate with our server. Before we can create a client, we must create a channel which contains host and port information of our server, as well as allowing us to easily add encryption, security, and more. For more information on what is available, look here: https://grpc.github.io/grpc-java/javadoc/io/grpc/ManagedChannelBuilder.html. For our example, we will go with a simple channel that just sets a host, port (which is grabbed from application properties file) and builds it for later use:

```
ManagedChannelBuilder
        .forAddress(applicationProperties.getGrpc().getHost(), applicationProperties.getGrpc().getPort())
        .usePlaintext()
        .build();
```

Once we have a channel, we can move forward with creating our client. In our generated code, we will find a static method named newBlockingStub. This method takes as an argument, our channel, and provides us with a client to call our server. Here is how we will generally create a client: 
```
DerivativeSolverGrpc.DerivativeSolverBlockingStub client  = DerivativeSolverGrpc.newBlockingStub(channel);
```

Now that we have a client, let’s try to get our client and server running and see if we can send some messages. Both our server, and our client share a proto file (assuming these two pieces are in separate projects), but will be dockerized separately. We create a simple docker file and docker compose file just setting up file system mapping and port mappings, then we add a bridge network to allow communication between two docker images running on the same machine (ie locally. In a dev or higher environment, we will likely be connecting through some sort of dns name). Now that both are up and running, let’s set up our client and have it make a call to our server, and to verify we get the data, we will log the request on the server.

```
// these arrays are the same as our previous example
int[] coefficients = [1, 2, 1, 1]
int[] exponents = [4, 2, 1, 0]
// x^4 + 2x^2 + x + 1
Polynomial polynomial = Polynomial.coefficients(coefficients).exponents(exponents).build();
Polynomial derivative = client.findDerivative(polynomial);
```
And our server:
```
public void findDerivative(Polynomial polynomial, StreamObserver<Polynomial> responseObserver) {
    if(polynomial.getCoefficientsCount() != polynomial.getExponentsCount()) {
        RuntimeException err = new RuntimeException("Invalid polynomial definition - It should have the same amount of exponents and coefficients");
        responseObserver.onError(err);
        throw err;
    }

    List<Integer> coefficients = polynomial.getCoefficientsList();
    List<Integer> exponents = polynomial.getExponentsList();

    List<Integer> updatedCoefficient = new ArrayList<>();
    List<Integer> updatedExponents = exponents.stream().map(exponent -> exponent - 1).collect(Collectors.toList());

    for(int i = 0; i < coefficients.size(); ++i) {
        updatedCoefficient.add(i, coefficients.get(i) * exponents.get(i));
    }

    responseObserver.onNext(Polynomial.newBuilder().addAllCoefficients(updatedCoefficient).addAllExponents(updatedExponents).build());
    responseObserver.onCompleted();
}
```

Our example uses simple spring boot applications to easily run them, and in order to start up the GRPC server, we must set this up in a Spring @Configuration, seen below:

```
@Configuration
public class GrpcServerConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServerConfiguration.class);

    private int port;
    private Server server;
    private DerivativeCalculatorService derivativeCalculatorService;

    public GrpcServerConfiguration(ApplicationProperties applicationProperties, DerivativeCalculatorService derivativeCalculatorService) {
        this.port = applicationProperties.getGrpc().getPort();
        this.derivativeCalculatorService = derivativeCalculatorService;
    }

    @Bean
    public Server grpcServer() throws IOException {
        LOGGER.info("Starting GRPC Server");
        this.server = ServerBuilder.forPort(this.port).addService((BindableService) derivativeCalculatorService).build();
        this.server.start();
        return this.server;
    }
}
```

Simply put, we use a server builder to set the port we want to listen on (which we get from application properties), we inject the configuration of our server as shown earlier and now we can make client calls to our running server. In our docker compose file, we set up an external network so both of our containers can communicate

```
    networks:
      - client_mynet
networks:
  client_mynet:
    external: true
```

Now let’s try our example our. First we will want to build our two applications with maven, using the common `mvn clean install`, then we can start our two images with the command `docker-compose -f docker-compose.yml up —build`. Each project in our GitHub has it’s own docker compose file, one in server/docker-compose, and one in client/docker-compose. With both up and running, we can send requests to our a client (also a spring boot app), via a simple rest endpoint. It takes a polynomial in the form of a string, converts to our protobuf format and sends the polynomial to the server. Assuming these are bother up and running, we simple run the following command inside our terminal:

```
curl --location --request POST 'localhost:8081/example/calculate-derivative' \
--header 'Content-Type: application/json' \
--data-raw '{"polynomial": "1x^4 + 2x^2 + 1x^1 + 1x^0"}'
```

Feel free to play around with the example and try other simple polynomials. Some caveats when using this, be aware that some requirements exist, for one, our polynomials we send include all exponents and coefficients (even 1 and 0), and that’s why we see `1x^1 + 1x^0` instead of `x + 1`, this is simply due to our simple parsing of the string polynomial. Similarly, this relies on positive values for coefficients as it makes the parsing logic in our rest endpoint simple. We can certainly expand on this to solve these issues, but won’t do that here.

We’ve now completed our simple GRPC example, this has a small protobuf definition, but these can be much more complex, if needed. We also created a simple docker configuration to get everything running locally and communication between the service and client.