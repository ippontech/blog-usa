---
authors:
- Amine Ouali Alami
- Pooja Krishnan
tags:
- HIPAA
- Spring Data
- Spring Boot
- JHipster

date: 2020-01-10T20:16:10.000Z
title: "Audit your data with JaVers"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/07/container-2539942_1920.jpg
---

As an IT consultant, the first requirements that come to mind when you are working in the Healthcare Industry is HIPAA, The technology plays a significant role to ensure data remains secure and HIPAA-compliant. According to cybercrime experts, stolen PHI (Protected Health Information) is 10 times more valuable than credit card information.

The data contained in PHI, can be used to generate identities, obtain health services, submit false insurance claims, order pharmaceuticals, and perform other illegal acts. That is why auditing PHI is critical: the ability to show who and how the data is used.

On the application layer, the two main tools for data auditing are Envers and JaVers.
- Envers is a core Hibernate module, it provides an easy auditing solution when using Hibernate or JPA, Envers is not an option if you are using a NoSQL database.
- JaVers is a lightweight java library for auditing changes in your data. it can be used with any kind of database and any kind of persistence framework since audit data are decoupled from live data, we can choose where to store them.


# Audit trail with JaVers
Since our client is mainly using MongoDB, we choosed to use JaVers.
JaVers is an audit log framework that helps to track changes of entities in the application.

The usage of this tool is not limited to debugging and auditing only. It can be successfully applied to perform analysis, force security policies and maintaining the event log, too.

# Spring Boot Integration and Setup
JaVers provide a spring boot starter that integrate all the required JaVers beans with default configuration.
```xml
<dependency>
			<groupId>org.javers</groupId>
			<artifactId>javers-spring-boot-starter-mongo</artifactId>
			<version>5.8.8</version>
</dependency>
```


# JaVers repository configuration
JaVers spring boot starter is configured to reuse the application's database (managed by Spring Data).
Alternativelly, we can choose to store JaVers data in a separate database as shown bellow :

```
javers:
  mongodb:
    host: localhost
    port: 27017
    database: javers-audit
    authentication-database: admin
    username: javers
    password: password
```
Or

```
javers:
  mongodb:
    uri: mongodb://javers:password@localhost:27017/javers-audit&authSource=admin
```

# Auto audit annotations

Auditing specific entities can be done very easy without additionnal code, simply add the @JaversSpringDataAuditable on the corresponding Spring Data repositories.

Example on MongoRepository
```
import org.javers.spring.annotation.JaversSpringDataAuditable
import org.springframework.data.mongodb.repository.MongoRepository;
import org.javers.organization.structure.domain.Person;

@JaversSpringDataAuditable
public interface PersonRepository extends MongoRepository<Person, String> {
}
```
All the changes made to Person objects  will be audited by JaVers.

In case of a custom repository, the @JaversAuditable needs to be used a the method level

```
@Repository
class UserRepository {

    @JaversAuditable
    public void save(User user) {
        ...//
    }

    public User find(String login) {
        ...//
    }
}
```

For a fine-grained control, the JaVers spring bean can be used

```
@Service
class PersonService {
    private final Javers javers;
    private final PersonRepository personRepository;

    @Autowired
    public PersonService(Javers javers, PersonRepository personRepository) {
        this.javers = javers;
        this.personRepository = personRepository;
    }
    
    public void fire(Person person) {
        person.fire();
        personRepository.save(person);

        if (LocalDate.now().getDayOfWeek() == DayOfWeek.FRIDAY){
            javers.commit("author", person);
        }
    }
}
```
In this example the auditing is used only Friday

# Domain model annotations
One of the challenges faced in our client is to keep the size of the audited data reasonable, JaVers provide a good Domain model configurtion, we used the @ValueObject at the class level to audit or no our embedded objects

Example :

```
@ValueObject
public class Address {
    private final String city;
    private final String street;
    private final String zipCode;

    public Address(String city, String street, String zipCode) {
        this.city = city;
        this.street = street;
        this.zipCode = zipCode;
    }

    // Value Object instances are compared property-by-property,
    // so the Object.equals() method is ignored by Javers
    @Override
    public boolean equals(Object o) {
        ...
    }
}
```

We also excluded from the audit all the no-PHI data using the @DiffIgnore annotation

Example :

```
class MongoStoredEntity {
    
    // ... 

    @DiffIgnore
    @Property("description")
    private String description

    // ... 
} 
```

# Retrieve the change
As mentioned earlier, we stored all our auditing in a separate Mongo collection. We've been storing all of the changes across one specific collection, but needed to find a way to display back the information we had collected. JaVers provides its' own JaVers Query Language (JQL), which can be used to query the Javers Repository for changes on a given class, object or property.

Data can be retrieved from JaVers in 3 possible ways: Shadows, Changes and Snapshots.
  * *Shadows* are historical versions of objects
  * *Change* shows the difference between two objects
  * *Snapshots* show the state of an object at a point in times

As per our client requirements, we needed to display the changes for an object from creation. We decided to use the **findChanges** method to do so. There are 3 types of changes tracked by JaVers: NewObject, ObjectRemoved and PropertyChange. JaVers uses the primary key on the table or collection in order to compile this information.

# Conclusion





### Sources
* [JaVers official website](https://javers.org/documentation/)
