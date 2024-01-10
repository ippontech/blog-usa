---
authors:
- Kenny Hegeland
  tags:
- gRPC
  date: 2015-04-23T12:21:50.000Z
  title: "Introduction to gRPC
  image:
---

# What is gRPC?
gRPC (remote procedure call) is used for communicating between distributed systems (microservices).
Google created and originally used an RPC framework called Stubby, which later was open sourced as gRPC.
While using protobuffers is not mandatory to use gRPC, it is generally the easiest approach (as it
works out of the box). You define a protobuf in a simple message format and use protoc (the protobuf
compiler) to generate code to handle serializing and creating your domain objects. You can also create
a service in the protobuf definition which allows generation of methods for the client to communicate
with the server.

## What are Protobufs?

Protobufs, short for Protocol Buffers, are used to create a language and platform-agnostic method for serializing
structured data. Google has [this](https://developers.google.com/protocol-buffers/docs/overview) to say about them:

> Protocol buffers provide a language-neutral, platform-neutral, extensible mechanism for serializing structured data in
> a forward-compatible and backward-compatible way. It’s like JSON, except it's smaller and faster, and it generates
> native language bindings.
>
> Protocol buffers are a combination of the definition language (created in .proto files), the code that the proto
> compiler generates to interface with data, language-specific runtime libraries, and the serialization format for data
> that is written to a file (or sent across a network connection).

The protobuf simply defines the contract between the services, and this allows us to be language agnostic.
Using libraries for specific language, you can generate language specific code and use a language specific
client to call the server. The server doesn't need to know what language (this is also true of REST) the
client is using, all it needs is the serialized message (which it knows how to deserialize).

Now that we’ve discussed what gRPC is, let’s flesh out an example. We will create a simple example
application (available on GitHub - https://github.com/hegek87/derivative-solver) to compute the derivative
of simple polynomials. If you don’t remember how a derivative is calculated, we will quickly show the
general rule our server will use.

## Polynomials and Derivatives

Remember that a polynomial is a function of the form
> P(x) = c_nx^n + c_{n-1}x^{n-1} + …. + c_1x + c_0,
>
> where c_1, c_2, …, c_n are simple constants.

To give a concrete example, let’s use the polynomial `x^4 + 2x^2 - x - 1`. Next, let’s get a better
understanding of how to find the derivative of a polynomial. If you never learned this, it’s a pretty
robotic procedure that will hopefully be straightforward to understand. To calculate a derivative, we
simply multiply the exponent by the coefficient, and subtract one from the original exponent. Given
a simple polynomial, `4x^2`, we find the derivative by multiplying the exponent and coefficient,
`4*2 = 8`, then we subtract one from the exponent, `2-1=1`, giving us the derivative, `8x^1 = 8x`.
This procedure works for simple polynomials, and can be expanded for polynomials of more than one term.
As another example, consider `x^3 + 3x^2 + 3`. Remember that x^0 = 1, so `3 = 3x^0`, and we can
calculate the derivative as `3*0 = 0`. The full derivative of the equations leads to `3x^2 + 6x + 0 = 3x^2 + 6x`.


## Creating A Message Format
Now that we understand what a derivative is, let’s discuss how we represent a polynomial for our protobuf.
It seems like we have two simple ways to represent the polynomial, a naive way (which we handle in our rest
call) to do this is by sending it as a raw string. While this works, it could be cumbersome, as we will
need to find a way to split the string, parse the constants and exponents into numeric values and map back
to a string after calculating the derivative. Perhaps a more elegant solution is to send the constant as an
array, and the exponents as an array, this way we can get the constant values and exponent values without
any parsing on our side. Something like

```java
Message Polynomial {
	repeated int32 coefficients = 1;
	repeated int32 exponents = 2;
}
```

Now that we have our message structure defined, we can compile to see the generated code. Compiling our protobuf can be
done by simply using the proto compile, or by using the `protobuf-maven-plugin` plugin, which is configured in our project
pom file. If we didn't have this configured, we could still compile using protoc. To install this, we follow instructions
[here](https://grpc.io/docs/protoc-installation/), and then simply type in our terminal

```
protoc --java_out=<output-directory> derivative.proto
```

After doing this, we should see generated java files in our output directory. For the remainder of this article, when we
compile our protobufs, we will be doing this using the protobuf maven plugin, and the command `mvn install`. For more
information on using the compiler, visit the docs on [protoc invocation](https://developers.google.com/protocol-buffers/docs/reference/java-generated#invocation)
Similar documentation exists for other languages.

Once we compile our protobufs, we will see POJOs generated with setters, getters, and builders. Now that we've
configured our message structure, we can define our service method that will be called in order to actually make
requests:
```java
service DerivativeSolver {
  rpc findDerivative(Polynomial) returns (Polynomial) {}
}
```

Compiling using protoc once again, we will now have everything we need to set up our simple application.

## Creating a gRPC Client and Server

Let’s discuss how we create a client to communicate with our server. Before we can create a client, we
must create a channel which contains host and port information of our server, the channel can also allow
us to easily add encryption, security, and more. For more information on what is available, look here:
https://grpc.github.io/grpc-java/javadoc/io/grpc/ManagedChannelBuilder.html. For our example, we will
create a simple channel that just sets a host, port (both of which will be grabbed from an application
properties file) and builds it for later use:

```java
ManagedChannelBuilder
        .forAddress(applicationProperties.getGrpc().getHost(), applicationProperties.getGrpc().getPort())
        .usePlaintext()
        .build();
```

Now that we have created a channel, it is fairly simple to actually create a gRPC client. In our generated code,
we will find a static method named `newBlockingStub`. This method takes a channel (which we just created) as an
argument and provides us with a client to call our server. Here is how we will generally create a client:
```java
DerivativeSolverGrpc.DerivativeSolverBlockingStub client  = DerivativeSolverGrpc.newBlockingStub(channel);
```

This creates a client, so now we can call our server. This is quite simple, as shown below, we create a polynomial,
then simply call the rpc method defined earlier through our newly created client.


```java
// these arrays are the same as our previous example
int[] coefficients = [1, 2, 1, 1]
int[] exponents = [4, 2, 1, 0]
// x^4 + 2x^2 + x + 1
Polynomial polynomial = Polynomial.coefficients(coefficients).exponents(exponents).build();
Polynomial derivative = client.findDerivative(polynomial);
```

Our server listens for requests and can calculate the derivative of a polynomial and return the new polynomial.

And our server:
```java
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

Setting this all up requires deploying both the server application, and the client application, both of which will use
the same protobuf definitions. To run this locally, we've created simple docker files which do simple mapping of ports,
file systems and sets up a bridge network where they will communicate. In an actual deployed environment, we will
likely have to use some sort of DNS to reference the host.


All of code samples from this article are snippets of our previously linked project which can be used as a
reference. You can find on the application available on repository, shared on this article, some additional configurations
related to the familiar spring patterns. Among other things, we have a `@Configuration` class which is used to configure
our gRPC server to be running inside our spring application. In this configuratioon, `DerivativeCalculatorService`
houses our previously shown derivative logic.

```java
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

Now we can finally start up our spring applications and see a message sent from our gRPC client to the gRPC server.
First we will want to build our two applications with maven, using the common `mvn clean install` (this will compile the
protobuffer), then we can start our two images with the command `docker-compose -f docker-compose.yml up —build`.
Each project in our GitHub has its own docker compose file, one in server/docker-compose, and one in client/docker-compose.
With both up and running, we can send requests to our client (also a spring boot app), via a simple rest endpoint.
It takes a polynomial in the form of a string (which is cumbersome as we said before), converts to our protobuf format
and sends the polynomial to the server. Assuming these are both up and running, we simply run the following command
inside our terminal:

```
curl --location --request POST 'localhost:8081/example/calculate-derivative' \
--header 'Content-Type: application/json' \
--data-raw '{"polynomial": "1x^4 + 2x^2 + 1x^1 + 1x^0"}'
```

Feel free to play around with the example and try other simple polynomials. Some caveats when using this, be aware that
some requirements exist, for one, our polynomials we send include all exponents and coefficients (even 1 and 0), and
that’s why we see `1x^1 + 1x^0` instead of `x + 1`, this is simply due to our simple parsing of the string polynomial.
It is possible to handle these cases more elegantly, but it makes the parsing logic more difficult. For this example,
we simplify our request to require a specific format to make parsing logic simpler. We can certainly expand on this to
solve these issues, but won’t do that here.

We’ve now completed our simple GRPC example, this has a small protobuf definition, but these can be much more complex,
if needed. We also created a simple docker configuration to get everything running locally and communication between
the service and client.