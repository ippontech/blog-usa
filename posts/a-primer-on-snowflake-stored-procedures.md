---
authors:
- Pooja Krishnan
tags:
- Cloud
- Snowflake
date: 2022-05-24T10:33:00.000Z
title: "A Primer on Snowflake Stored Procedures"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/08/Snowflake.png
---

Snowflake is a data warehouse as a service hosted completely in the cloud. For a Snowflake Primer, take a look at the [Introduction to Snowflake](https://blog.ippon.tech/introduction-to-snowflake/) post. For a look at some of Snowflake's Innovative Features, I refer you to [Snowflake's Architecture](https://blog.ippon.tech/innovative-snowflake-features-part-1-architecture/), which takes an in-depth look at Snowflake's Architecture and what its benefits are. In that blog, I briefly examine Snowflake Procedures and discuss when Procedures should be used versus User Defined Functions (UDF)s. In the following, I am going to examine Snowflake Procedures further.

---
# Stored Procedures
Stored Procedures, much like functions, are created once and can be executed many times. They are created with the ```CREATE PROCEDURE``` command and are executed with the ```CALL``` command. It is important to note Stored Procedures in Snowflake always return a single value or nothing at all. So, while ```SELECT``` statements can be executed inside a procedure, their results must be utilized somewhere within the stored procedure or narrowed down to a single value to be returned.

Stored Procedures can be written in one of the following languages:
* JavaScript
* Snowflake Scripting (SQL)
* Scala (via Snowpark)
* Java (via Snowpark)
Additionally, stored procedures can return either a single value or tabular data.

## The Benefits of Stored Procedures
Stored Procedures allow for procedural logic, error handling, as well as the dynamic execution of SQL Statements. Using store procedures, it is also possible to write code that executes with the privileges of the role **owning** the procedure, rather than the privileges of the role **running** the procedure. This allows the stored procedure owner to delegate the ability to perform specific operations to users who could not otherwise. There are limitations, which we will discuss when going into Owner's Rights and Caller's Rights Stored Procedures.

### Session State in a Stored Procedure
The CALL statement used to execute a stored procedure, much like other SQL commands, runs within and inherits context from that session, including session variables, the current database, the current warehouse, and others. The context inherited depends on whether the procedure is a caller's rights procedure or an owner's rights procedure. Changes the procedure makes to the session can persist after the end of the CALL if the stored procedure is a caller’s rights stored procedure. Owner’s rights stored procedures are not permitted to change the session state.

#### Caller's Rights Stored Procedures
Caller's rights stored procedures adhere to the following rules in a session:
* Run with the privileges of the caller
* Inherit the current warehouse of the caller
* Uses the database and schema the caller is currently using
* Can view, set and unset the caller's session variables and session parameters.

A Caller's Rights Stored Procedure can see variables that were set by statements before the procedure was called.  Statements executed after the stored procedure can see the variable(s) set inside the procedure. This is a little difficult to grasp, so I've included an example below to help.

1. Create and load a table:
> ```plsql
> create table sv_table (f float);
> insert into sv_table (f) values (49), (51);

2. Set a session variable:
> ```plsql
> set SESSION_VAR1 = 50;

3. Create a stored procedure that uses one session variable and sets another:
> ```plsql
> create procedure session_var_user()
>   returns float
>   language javascript
>   EXECUTE AS CALLER
>   as
>   $$
>   // Set the second session variable
>   var stmt = snowflake.createStatement(
>     {sqlText: "set SESSION_VAR2 = 'I was set inside the StProc.'"}
>     );
>   var rs = stmt.execute(); //we ignore the result in this case
>   // Run a query using the first session variable
>   stmt = snowflake.createStatement(
>     {sqlText: "select f from sv_table where f > $SESSION_VAR1"}
>     );
>   rs = stmt.execute();
>   rs.next();
>   var output = rs.getColumnValue(1);
>   return output;
>   $$
>   ;

4. Call the procedure:
> ```plsql
> CALL session_var_user();
> +------------------+
> | SESSION_VAR_USER |
> |------------------|
> |               51 |
> +------------------+

5. View the value of the session variable set inside the stored procedure:
> SELECT $SESSION_VAR2;
> +------------------------------+
> | $SESSION_VAR2                |
> |------------------------------|
> | I was set inside the StProc. |
> +------------------------------+

Although you can set a session variable inside a stored procedure and leave it set after the end of the procedure, Snowflake does **not** recommend doing this.

TL;DR:
* The stored procedure can see the variable that was set by statements before the procedure was called.
* The statements after the stored procedure can see the variable that was set inside the procedure.

In many cases, this is the desired behavior since we may want to inherit contextual information. In cases where the stored procedure should be more isolated, Snowflake provides a few pointers:
* Avoid using session-level variables directly. Pass them as explicit parameters, as it forces the caller to think about exactly what values the stored procedure will use.
* Clean up any session-level variables that you set inside the stored procedure and use names that are not likely to be used anywhere else.
Above all, remember that unlike programming in languages such as C or Java, variables set inside a stored procedure will not be garbage collected once the function is finished. Isolating your stored procedure from its environment requires more effort in SQL than in C or Java.

#### Owner's Rights Stored Procedures
Owner's Rights Stored Procedures:
* Run with the privileges of the owner.
* Inherit the current warehouse of the caller.
* Use the database and schema that the stored procedure is created in.
* Cannot access most caller-specific information ^[This includes viewing, setting and unsetting session variables, calling functions such as CURRENT_USER() and querying INFORMATION_SCHEMA table functions that return results based on the current user.]
* Can only read supported session parameters, and cannot set or unset any of the caller’s session parameters.
* Non-owners are not able to view information about the procedure from the PROCEDURES view.
* Does not have access to SQL variables created outside the stored procedure ^[ If your stored procedure needs values that are stored in the current session's SQL variables, then the values in those variables should be passed as explicit arguments to the procedure]

Owner's Rights Procedures have several additional restrictions which affect the following:
* [Built-in functions called from inside a procedure](#restrictions-on-built-in-functions)
* [The execution of ALTER USER statements](#alter-user)
* [Monitoring of stored procedures at execution](#monitoring-stored-procedures-at-execution) 
* [SHOW and DESCRIBE commands](#show-and-describe-commands)
* [The types of SQL that can be called from inside a stored procedure](#restrictions-on-sql-statements)

##### Restrictions on Built-In Functions
If a stored procedure is created as an owner's rights stored procedure, then callers (other than the owner), cannot call the following built-in functions:
* GET_DDL() ^[Only the stored procedure owner can view the source code of the stored procedure]
* SYSTEM$ENABLE_BEHAVIOR_CHANGE_BUNDLE() ^[Enables behavior changes included in the specified release bundle for the current account. By default, behavior change bundles are not enabled during the pre-announcement period. Use this function to test behavior changes before they are enabled for your account.]
* SYSTEM$DISABLE_BEHAVIOR_CHANGE_BUNDLE() ^[Disables behavior changes in the specified release bundle that are currently enabled by default. This is typically done in your production accounts to opt-out of the changes in the bundle while you continue testing the changes in your non-production accounts.]

##### ALTER USER
Owenr's rights stored procedures cannot execute ALTER USER statements which use the current user for the session. Owner's Rights Stored Procedures can execute ALTER USER statements that explicitly state the user as long as the user is not the current one.

##### Monitoring Stored Procedures at Execution
Neither the owner nor the caller of an owner’s rights stored procedure necessarily has privileges to monitor execution of the stored procedure.

A user with the WAREHOUSE MONITOR privilege can monitor execution of the individual warehouse-related SQL statements within that stored procedure. Most queries and DML statements are warehouse-related statements. DDL statements, such as CREATE, ALTER, etc. do not use the warehouse and cannot be monitored as part of monitoring stored procedures.

##### SHOW and DESCRIBE commands
An owner’s rights stored procedure does not have sufficient privileges to read information about users other than the caller. For example, running `SHOW USERS LIKE <current_user>` will show information about the current user, but the more general SHOW USERS does not work unless the current user is the only user.

The following SHOW commands are permitted:
* SHOW DATABASES.
* SHOW SCHEMAS.
* SHOW WAREHOUSES.

##### Restrictions on SQL Statements
Caller's rights stored procedures can execute any SQL statement that the caller has sufficient privileges to execute outside of a stored procedure. Owner's rights stored procedures, however, can call only a subset of SQL statements. These are the SQL statements that can be called from inside an owner's rights stored procedure:
* [SELECT](https://docs.snowflake.com/en/sql-reference/sql/select.html)
* [DML](https://docs.snowflake.com/en/sql-reference/sql-dml.html)
* [DDL](https://docs.snowflake.com/en/sql-reference/sql-ddl-summary.html) (See the restrictions of Alter User above,)
* [GRANT](https://docs.snowflake.com/en/sql-reference/sql/grant-privilege.html)/[REVOKE](https://docs.snowflake.com/en/sql-reference/sql/revoke-privilege.html)
* Variable assignment
* [DESCRIBE](https://docs.snowflake.com/en/sql-reference/sql/desc.html) and [SHOW](https://docs.snowflake.com/en/sql-reference/sql/show.html) (See limitations on describe and show above.)

All other SQL statements cannot be called from inside an owner's rights stored procedure.

> If an owner’s rights stored procedure is called by a caller’s rights stored procedure, or vice-versa, the following rules apply:
> * A stored procedure behaves as a caller’s rights stored procedure if and only if the procedure and the entire call hierarchy above it are caller’s rights stored procedures.
> * An owner’s rights stored procedure always behaves as an owner’s rights stored procedure, no matter where it was called from.
> * Any stored procedure called directly or indirectly from an owner’s rights stored procedure behaves as an owner’s rights stored procedure.

##### Choosing between Owner's Rights and Caller's Rights
Create a stored procedure as an owner's rights stored procedure if **all** of the following is true:
* A task needs to be delegated to another user who will run with owner's privileges, not their own.
  * Ex: If you need a user without DELETE privilege on a table to be able to call a procedure to delete old data, without manipulating current data.

Create a stored procedure as a caller's rights stored procedure if the following is true:
* The stored procedure operates only on objects the caller owns or has required privileges on.
* The restrictions on Owner's Rights Stored Procedures (discussed in the section above) would prevent the procedure from working.
  * Ex: Use a Caller's Rights Procedure if the caller of the procedure needs to use the caller's environment (session variables, account parameters, etc.).

If a particular procedure can work correctly with either caller's rights or owner's rights, then the following rule might help you chose which rights to use:
* If a procedure is an owner’s rights procedure, the caller does not have the privilege to view the code in the stored procedure (unless the caller is also the owner). If you want to prevent callers from viewing the source code of the procedure, then create the procedure as an owner’s rights procedure. Conversely, if you want callers to be able to read the source code, then create the procedure as a caller’s rights prodecure.

---

## Differences Between Stored Procedures and User-Defined Functions
Both stored procedures and UDFs (user-defined functions) make it easier to write modular code. However, there are important differences between UDFs and stored procedures.

* Stored Procedures are called as independent statements rather than a part of a statement.
```plsql
CALL procedure_1(argument_1); -- Stored Procedure Call
SELECT function_1(argument_1) from table_1; -- Function Call
```
* Stored Procedures **DO NOT** need to return a value. A function is required to return a value.
* Values returned by Stored Procedures are NOT directly usable in SQL. The syntax of the ```CALL``` command does not provide a place to store the returned value or a way to operate on it or pass it to another operation.
```plsql
y = stored_procedure1(x) --NOT ALLOWED
```
  * There are indirect ways to use the returned value of a stored procedure:
    * You can call a stored procedure inside a stored procedure and use the JavaScript from the outer procedure to retrieve and store the output of the inner. *The outer stored procedure is still unable to return more than one value to its caller.*
    * You can call the stored procedure, then call the RESULT_SCAN function and pass it the statement ID generated for the procedure.
    * If the stored procedure is a caller’s rights stored procedure, you can store a result set in a temporary (or permanent) table, and use the table after returning from the stored procedure call.
    * If the volume of data is not too large, you can store multiple rows and multiple columns in a VARIANT (for example, as a JSON value) and return that VARIANT.
* Only one Stored Procedure can be called per CALL statement.
* Snowflake provides a JavaScript API which enables Stored Procedures to access the database and issue nested queries. UDFs do not have an API which allows them to perform database operations (CREATE, UPDATE, SELECT).

---
# Stored Procedures 101
We've discussed some of the benefits of stored procedures, how session state can be handled in stored procedures and examined the differences between User Defined Functions and Stored Procedures. Now we will begin our deep dive into Stored Procedures by creating a new stored procedure that will, given the type of animal, return the names of all the creatures of that type.

## Creating a Stored Procedure
In Snowflake, Stored Procedures are First-Class Objects^[An entity that can be dynamically created, destroyed, passed to a function or returned as a value], and as such can use the following commands: `CREATE PROCEDURE`, `ALTER PROCEDURE`, `DROP PROCEDURE`, `DESCRIBE PROCEDURE` and `SHOW PROCEDURES`. Snowflake also provides the CALL command for executing Stored Procedures.

Let's walk through an example to view the different components of a Stored Procedure in Snowflake.
```plsql
CREATE OR REPLACE PROCEDURE stproc1(animal varchar)
  RETURNS table()
  LANGUAGE sql
  AS
  declare
    res resultset default (select pet:name::String as name, pet:species::String as species, pet:age_in_months::Number as age_in_months from pets where pet:species = :animal);
  begin
    return table(res);
  end;
```

The parameters of stproc1():
* ```CREATE OR REPLACE PROCEDURE stproc1()```
  Specifies the name (and optionally one or more arguments/inputs) for the stored procedure. ^[The name does not need to be unique in a schema since procedures are identified by both name and arguments. In this way, you can have overloaded procedures, as long as the procedures differ by the number of arguments or the argument type.]

* ```RETURNS table()```
  Specifies the results returned by the stored procedure. This return value cannot be used since the call cannot be a part of an expression.
  For result_data_type, use the Snowflake data type that corresponds to the type of the language that you are using:
  * For JavaScript stored procedures, see [SQL and JavaScript Data Type Mapping](https://docs.snowflake.com/en/sql-reference/stored-procedures-javascript.html#label-stored-procedure-data-type-mapping).
  * For Snowflake Scripting, a [SQL data type]().
  * For Snowpark (Scala and Java) stored procedures, see:
    * [Appendix: Mapping Scala Data Types to Snowflake Data Types](https://docs.snowflake.com/en/sql-reference/stored-procedures-scala.html#label-sql-types-to-scala-types)
    * [SQL-Java Data Type Mappings for Parameters and Return Types](https://docs.snowflake.com/en/developer-guide/udf/java/udf-java-designing.html#label-sql-java-data-type-mappings).

* ```LANGUAGE SQL```
  Specifies what language the stored procedure is written in.  A procedure can currently be written in one of the following languages: JavaScript, Snowflake Scripting (SQL), Scala (using Snowpark) and Java (using Snowpark) 

* ```AS $$ <procedure_logic> $$;```
  Defines the code executed by the stored procedure. The definition can consist of any valid code.
  Note the following:
  * For pre-compiled stored procedures in Snowpark Scala and and Java, omit the AS clause.
  * Use the IMPORTS clause instead to specify the location of the JAR file containing the code for the stored procedure. For details, see:Writing Stored Procedures in Snowpark (Scala) and Writing Stored Procedures in Snowpark (Java).
  * You must use string literal delimiters (`'` or `$$`) around the procedure definition if:
    * You are using a language other than Snowflake Scripting.
    * You are creating a Snowflake Scripting procedure in SnowSQL or the classic web interface.
  * For languages other than Snowflake Scripting, Snowflake does not completely validate the code when you execute the `CREATE PROCEDURE` command. If the code is not valid, `CREATE PROCEDURE` will succeed, but errors will be returned when the stored procedure is called.

For Snowpark (Scala and Java):
* ```RUNTIME_VERSION = '<scala_or_java_runtime_version>'```
  The runtime version of Scala or Jva to use. The only supported varsions are:
    * `2.12` - Scala
    * `11` - Java


* ```PACKAGES = ( '<fully_qualified_package_name>' )```
  The fully qualified package name of the Snowpark library. This must be fully qualified in this format: `com.snowflake:snowpark:<version>` where the version is the version number or `latest` for the most recent version.

  > * For Scala specify version 1.1.0 or later.
  > * For Java specify the version 1..3.0 or later.
  >
  >For the list of supported packages and versions, query the INFORMATION_SCHEMA.PACKAGES view for rows with LANGUAGE = 'scala' or LANGUAGE = 'java'. For example:
  >> select * from information_schema.packages where language = 'scala';
  >> select * from information_schema.packages where language = 'java';

* ```HANDLER = '<fully_qualified_method_name>'```
  The fully qualified name of the method or function for the stored procedure, in the form: `com.my_company.my_package.MyClass.myMethod`

There are also optional parameters that can be specified in a CREATE PROCEDURE statement:
* ```[[NOT] NULL]```
  Specifies whether the stored procedure can return NULL values or must only return non-null values

* ```CALLED ON NULL INPUT``` or ```RETURNS NULL ON NULL INPUT | STRICT```
  * ```CALLED ON NULL INPUT```: Snowflake will call the procedure even if the inputs are null. It is up to the procedure to handle the nulls appropriately.
  * ```RETURNS NULL ON NULL INPUT``` (or its synonym ```STRICT```): Snowflake will not call the procedure if any of the inputs are null. A null value will be returned instead.
  DEFAULT: ```CALLED ON NULL INPUT```

* ```COMMENT = '<string_literal>'```
  * Specifies a comment for the stored procedure which will be displayed in the Description column of the SHOW PROCEDURES command output.
  DEFAULT: ```stored procedure```

* ```EXECUTE AS <CALLER | OWNER>```
  * Stored procedures can be executed with the privileges of the owner or those of the caller.
    * If you execute the statement ```CREATE PROCEDURE ... EXECUTE AS  CALLER```, then in the future the procedure will execute as a caller’s rights procedure.
    If you execute ```CREATE PROCEDURE ... EXECUTE AS OWNER```, then the procedure will execute as an owner’s rights procedure.
    * By default, if neither is specified explicitly at procedure creation, the procedure runs as an owner’s rights stored procedure. Owner’s rights stored have less access to the caller’s environment (caller’s session variables, etc.), and Snowflake defaults to this higher level of privacy and security.

For Snowpark (Scala and Java):
* ```IMPORTS = ( '<stage_path_and_file_name_to_read>' [ '<stage_path_and_file_name_to_read>' ...] ) ```
  The location (stage), path, and name of the file(s) to import. You must set the `IMPORTS` clause to include any files that your stored procedure dependes on. This clause can be omitted for an in-line stored procedure unless your code depends on classes defined outside the stored procedure or resource files. If you are writing a pre-compiled store procedure, you must include the JAR file containing the definition of the stored procedure. Additionally, each file in the `IMPORTS` clause must have a unique name even if the files are in different subdirectories or different stages.

* ```TARGET_PATH = '<stage_path_and_file_name_to_write>'```
  The `TARGET_PATH` clause specifies the location to which Snowflake should write the compiled code (JAR file) after compiling the source code specified in the procedure definition. If this clause is omitted, Snowflake re-compiles the source code each time the code is needed.
  
  If you specify this clause:
  * You cannot set this to an existing file. Snowflake returns an error if the `TARGET_PATH` points to an existing file.
  * If you specify both the `IMPORTS` and `TARGET_PATH` clauses, the file name in the `TARGET_PATH` clause must be different from each file name in the `IMPORTS clause`, even if the files are in different subdirectories or different stages.
  * If you no longer need to use the stored procedure (e.g. if you drop the stored procedure), you must manually remove this JAR file.


## JavaScript in a Stored Procedure
Snowflake stored procedures can also be written in JavaScript. With Javascript, SQL statements are executed using the Snowflake JavaScript API. This way it is possible to execute an SQL statement, retrieve the result set of a query, and retrieve metadata about the result set within a stored procedure. Each of these operations is carried out by calling methods on the following objects:
* **snowflake** which has methods to create a **Statement** and execute an SQL command
* **Statement** which has methods to execute and access the metadata of prepared statements as well as allowing the return of a ResultSet object.
* **ResultSet** which holds the results of the query
* **SfDate** which extends JavaScript DateFormat and provides additional methods as well as serves as a return type for Snowflake Date Types TIMESTAMP_LTZ, TIMESTAMP_NTZ, and TIMESTAMP_TZ.

A typical stored procedure contains code similar to the following.
```plsql
var my_sql_command1 = "delete from history_table where event_year < 2016";
var statement1 = snowflake.createStatement(my_sql_command1);
statement1.execute();
```
This code uses the object **snowflake**, which is a special object that does not need to be declared. This object is present within the context of each stored procedure and exposes the API for use. All other variables, such as my_sql_command1 and statement1 are created as JavaScript vars. You can also retrieve the ResultSet and iterate through it. For more on each of the four objects and API methods Snowflake provides, look at the [Snowflake Example Documentation](https://docs.snowflake.net/manuals/sql-reference/stored-procedures-usage.html#label-stored-procedure-examples).

---
# Access Control on Stored Procedures
To finish off our discussion of Stored Procedures, I am going to delve a little bit into the access control features Snowflake provides for Stored Procedures. There are two types of privileges that stored procedures utilize: Those directly on the procedure and those on the database objects that the procedure can access. Similar to other database objects in Snowflake, stored procedures are owned by a role and have one or more privileges that can be granted to other roles.

There are currently two privileges that can apply to stored procedures: USAGE and OWNERSHIP. For a role to use a stored procedure, it must either be the owner of the stored procedure or have been granted USAGE on the procedure ^[For a more in-depth view on Access Control in Snowflake, I refer you to [Access Control Considerations](https://docs.snowflake.net/manuals/user-guide/security-access-control-considerations.html) in the Snowflake Documentation.]. In addition, the roles executing the procedure must also have the USAGE privilege for all database objects accessed during the course of the procedure.

----
During this blog, we've examined Snowflake Stored Procedures from creation to execution. We've also discussed some of the key differences between stored procedures and user-defined functions as well as examined session state and the two types of procedures Snowflake provides. To round out procedures, we briefly viewed access control in Snowflake and how privileges can affect procedure execution.

----
For more information on how Ippon Technologies, a Snowflake partner, can help your organization utilize the benefits of Snowflake for a migration from a traditional Data Warehouse, Data Lake or POC, contact sales@ipponusa.com.