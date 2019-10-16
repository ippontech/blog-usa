---
authors:
- Pooja Krishnan
tags:
- Cloud
- Snowflake
date: 2019-08-07T10:33:00.000Z
title: "A Primer on Snowflake Stored Procedures"
image: https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/08/Snowflake.png
---

Snowflake is a data warehouse as a service hosted completely in the cloud. For a Snowflake Primer, take a look at the [Introduction to Snowflake](https://blog.ippon.tech/introduction-to-snowflake/) post. For a series of Snowflake Innovative Feature blogs, I refer you to the first, which takes an in-depth look at the benefits of [Snowflake's Architecture](https://blog.ippon.tech/innovative-snowflake-features-part-1-architecture/). In that series, I briefly examine Snowflake Procedures and discuss when Procedures should be used versus User Defined Functions. In the following, I am going to examine Snowflake Procedures further.

---
# Stored Procedures
Stored Procedures, much like functions, are created once and can be executed many times. They are created with the ```CREATE PROCEDURE``` command and are executed with the ```CALL``` command. It is important to note Stored Procedures in Snowflake always return a single value or nothing at all. So, while ```SELECT``` statements can be executed inside a procedure, their results must be utilized somewhere within the stored procedure or narrowed down to a single value to be returned.

Snowflake Stored Procedures use a combination of JavaScript and SQL. JavaScript provides the branching and looping aspects, while SQL statements are executed by calling functions via the [Snowflake Stored Procedure API](https://docs.snowflake.net/manuals/sql-reference/stored-procedures-api.html).

## The Benefits of Stored Procedures
Stored Procedures allow for procedural logic and error handling, as well as the dynamic execution of SQL Statements. Using store procedures, it is also possible to write code that executes with the privileges of the role **owning** the procedure, rather than the privileges of the role **running** the procedure.

## Differences Between Stored Procedures and User-Defined Functions
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
    * If the stored procedure is a [caller’s rights stored procedure](###caller's-rights-stored-procedures), you can store a result set in a temporary (or permanent) table, and use the table after returning from the stored procedure call.
    * If the volume of data is not too large, you can store multiple rows and multiple columns in a VARIANT (for example, as a JSON value) and return that VARIANT.
* Only one Stored Procedure can be called per CALL statement.
* Snowflake provides a JavaScript API which enables Stored Procedures to access the database and issue nested queries. UDFs do not have an API which allows them to perform database operations (CREATE, UPDATE, SELECT).

---
# Stored Procedures 101
Now that we've discussed some of the benefits of stored procedures and examined the differences between User Defined Functions and Stored Procedures, we will begin our deep dive into Stored Procedures starting with Creating a Stored Procedure.

## Creating a Stored Procedure
In Snowflake, Stored Procedures are First-Class Objects^[An entity that can be dynamically created, destroyed, passed to a function or returned as a value], and as such can use the following commands: CREATE PROCEDURE, ALTER PROCEDURE, DROP PROCEDURE, DESCRIBE PROCEDURE and SHOW PROCEDURES. Snowflake also provides the CALL command for executing Stored Procedures.

Let's walk through an example to view the different components of a Stored Procedure in Snowflake.
```plsql
CREATE OR REPLACE PROCEDURE stproc1()
  RETURNS array
  LANGUAGE javascript
  AS
  -- "$$" is the delimiter for the beginning and end of the stored procedure.
  $$
    var query_to_retrieve_only_cats = "select V:name::String as name from pets where V:species like '%cat%';";
    var statement1 = snowflake.createStatement({sqlText:query_to_retrieve_only_cats});
    var result_set = statement1.execute();
    var returned_count = statement1.getRowCount();

    var return_string = [];
    while (result_set.next())  {
       var column1 = result_set.getColumnValue(1);
       return_string.push(column1);
    }
    return return_string;
  $$
  ;
```

The parameters of stproc1():
1. ```CREATE OR REPLACE PROCEDURE stproc1()```
  Specifies the name (and optionally one or more arguments/inputs) for the stored procedure. ^[The name does not need to be unique in a schema since procedures are identified by both name and arguments. In this way, you can have overloaded procedures, as long as the procedures differ by the number of arguments or the argument type.]
2. ```RETURNS array```
  Specifies the results returned by the stored procedure. This return value cannot be used since the call cannot be a part of an expression.
3. ```LANGUAGE javascript```
  Specifies what language the stored procedure is written in.  Currently, JavaScript is the only language supported; specifying any other language will result in an error message.
4. ```AS $$ <procedure_logic> $$;```
  Defines the JavaScript code to be executed as the Stored Procedure. Snowflake does not validate the code on the creation of a procedure; the procedure will always be created successfully. If the code is not valid, errors will be returned when the stored procedure is called. The delimiters around the JavaScript code can either be single-quotes or '$$'. Using '$$' makes it easier to write procedures using single-quotes.

There are also optional parameters that can be specified in a CREATE PROCEDURE statement:
* ```CALLED ON NULL INPUT``` or ```RETURNS NULL ON NULL INPUT | STRICT```
  * ```CALLED ON NULL INPUT```: Snowflake will call the procedure even if the inputs are null. It is up to the procedure to handle the nulls appropriately.
  * ```RETURNS NULL ON NULL INPUT``` (or its synonym ```STRICT```): Snowflake will not call the procedure if any of the inputs are null. A null value will be returned instead.
  DEFAULT: ```CALLED ON NULL INPUT```
* ```VOLATILE | IMMUTABLE```
  * ```VOLATILE```: The procedure might return different values for different rows.
  * ```IMMUTABLE```: The procedure assumes that it will always return the same result when called with the same inputs. This guarantee is not checked. Specifying IMMUTABLE for a procedure that returns different values for the same input will result in undefined behavior.
  DEFAULT: ```VOLATILE```
* ```COMMENT = '<string_literal>'```
  * Specifies a comment for the stored procedure which will be displayed in the Description column of the SHOW PROCEDURES command output.
  DEFAULT: ```stored procedure```
* ```EXECUTE AS <CALLER | OWNER>```
  * Stored procedures can be executed with the privileges of the owner or those of the caller.
    * If you execute the statement ```CREATE PROCEDURE ... EXECUTE AS  CALLER```, then in the future the procedure will execute as a caller’s rights procedure.
    If you execute ```CREATE PROCEDURE ... EXECUTE AS OWNER```, then the procedure will execute as an owner’s rights procedure.
    * By default, if neither is specified explicitly at procedure creation, the procedure runs as an owner’s rights stored procedure. Owner’s rights stored have less access to the caller’s environment (caller’s session variables, etc.), and Snowflake defaults to this higher level of privacy and security.

## JavaScript in a Stored Procedure
Snowflake stored procedures are written in JavaScript and SQL statements are executed using the Snowflake JavaScript API. It is, therefore, possible to execute an SQL statement, retrieve the result set of a query and retrieve metadata about the result set within a stored procedure. Each of these operations is carried out by calling methods on the following objects:
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
This code uses the object **snowflake**, which is a special object that does not need to be declared. This object is present within the context of each stored procedure and exposes the API for use. All other variables, such as my_sql_command1 and statement1 are created as JavaScript vars. You can also retrieve the ResultSet and iterate through it.
```plsql
create or replace procedure read_result_set()
  returns float not null
  language javascript
  as     
  $$  
    var my_sql_command = "select * from table1";
    var statement1 = snowflake.createStatement( {sqlText: my_sql_command} );
    var result_set1 = statement1.execute();
    // Loop through the results...
    while (result_set1.next())  {
       var column1 = result_set1.getColumnValue(1);
       var column2 = result_set1.getColumnValue(2);
       // Do something with the retrieved values...
       }
  return 0.0; //return the singular result
  $$
  ;
```
For more examples on each of the four objects and API methods Snowflake provides, look at [Examples](https://docs.snowflake.net/manuals/sql-reference/stored-procedures-usage.html#label-stored-procedure-examples).

### How do SQL and JavaScript Data Types Map?
Snowflake will automatically convert SQL data types to JavaScript data types and vice versa. The SQL to JavaScript conversion can occur when calling a stored procedure with an argument (where the argument will be converted from an SQL type to a JavaScript type so it can be used in the procedure) or when retrieving a value from a ResultSet object into a JavaScript variable. The JavaScript to SQL conversion can occur when returning a value from the stored procedure, when dynamically constructing an SQL statement that uses a value from a JavaScript variable, or when binding a JavaScript variable's value to a PreparedStatement.

Here is a table which shows the Snowflake SQL data types and their corresponding JavaScript data types:
![SQL Data Type to JavaScript Data Type Conversion Table](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2019/08/Snowflake-SQL-to-JavaScript-Type-Conversion-Table.png) [source](https://docs.snowflake.net/manuals/sql-reference/stored-procedures-usage.html#converting-from-sql-to-javascript)

Not all Snowflake SQL data types have a corresponding JavaScript data type. JavaScript does not directly support the INTEGER or NUMBER data types. In these cases, you should convert the SQL data type to an appropriate alternative data type.^[SQL INTEGER can be converted to SQL FLOAT which will be mapped to JavaScript number]

## Session State in a Stored Procedure
The CALL statement used to execute a stored procedure, much like other SQL commands, runs within and inherits context from that session, including session variables, the current database, the current warehouse, and others. The context inherited depends on whether the procedure is a caller's rights procedure or an owner's rights procedure. Changes the stored procedure makes to the session can persist after the end of the CALL, if the stored procedure is a caller’s rights stored procedure. Owner’s rights stored procedures are not permitted to change session state.

### Caller's Rights Stored Procedures
Caller's rights stored procedures adhere to the following rules in a session:
* Run with the privileges of the caller
* Inherit the current warehouse of the caller
* Uses the database and schema the caller is currently using
* Can view, set and unset the caller's session variables and session parameters.

A Caller's Rights Stored Procedure can see variables that were set by statements before the procedure was called.  Statements executed after the stored procedure can see the variable that was set inside the procedure. This is a little difficult to grasp, so I've included an example below to help.
> Suppose you have a stored procedure named MyStoredProcedure which executes SQL Statements that read and set session-level variables. In pseudo-code, we'll call the two commands ```READ_SESSION_VAR1``` and ```SET_SESSION_VAR2```. In the same session, you can, therefore, execute the following statements:
> ```plsql
> SET SESSION_VAR1 = 'some value';
> CALL MyStoredProcedure(); -- Executes READ_SESSION_VAR1 and uses the value of SESSION_VAR1
>                           -- Executes SET_SESSION_VAR2, which is still set after the procedure all is completed.
> SELECT * FROM table WHERE column1 = $SESSION_VAR2;
> ```
> This is equivalent to executing the following statements:
> ``` plsql
> SET SESSION_VAR1 = 'some value';
> READ_SESSION_VAR1;
> SET_SESSION_VAR2;
> SELECT * FROM table WHERE column1 = $SESSION_VAR2;
> ```
>
> For more examples, refer to [Session Variables with Caller's Rights and Owner's Rights](https://docs.snowflake.net/manuals/sql-reference/stored-procedures-usage.html#label-sp-session-variables-example).

In many cases, this is the desired behavior since we may want to inherit contextual information. In cases where the stored procedure should be more isolated, Snowflake provides a few pointers:
* Avoid using session-level variables directly. Pass them as explicit parameters, as it forces the caller to think about exactly what values the stored procedure will use.
* Clean up any session-level variables that you set inside the stored procedure and use names that are not likely to be used anywhere else.
Above all, remember that unlike programming in languages such as C or Java, variables set inside a stored procedure will not be garbage collected once the function is finished. Isolating your stored procedure from its environment requires more effort in SQL than in C or Java.

### Owner's Rights Stored Procedures
Owner's Rights Stored Procedures adhere to the following rules in a session:
* Run with the privileges of the owner.
* Inherit the current warehouse of the caller.
* Use the database and schema that the stored procedure is created in.
* Cannot access most caller-specific information ^[This information includes viewing, setting and unsetting session variables, calling functions such as CURRENT_USER() and querying INFORMATION_SCHEMA table functions that return results based on the current user.]
* Can only read supported session parameters, and cannot set or unset any of the caller’s session parameters.
* Non-owners are not able to view information about the procedure from the PROCEDURES view.

### Choosing between Owner's Rights and Caller's Rights
Create a stored procedure as an owner's rights stored procedure if **all** of the following is true:
* A task needs to be delegated to another user who will run with owner's privileges, not their own.
  * Ex: If you need a user without DELETE privilege on a table to be able to call a procedure to delete old data, without manipulating current data.
* The restrictions on Owner's Rights Stored Procedures (discussed in the section above) will not prevent a procedure from working as intended.

Create a stored procedure as a caller's rights stored procedure if the following is true:
* The stored procedure operates only on objects the caller owns or has required privileges on.
* The restrictions on Owner's Rights Stored Procedures (discussed in the section above) would prevent the procedure from working.
  * Ex: Use a Caller's Rights Procedure if the caller of the procedure needs to use the caller's environment (session variables, account parameters, etc.).

---
# Access Control on Stored Procedures
To finish off our discussion of Snowflake Stored Procedures, I am going to delve a little bit into access control features Snowflake provides for Stored Procedures. There are two types of privileges that stored procedures utilize: Those directly on the procedure and those on the database objects that the procedure can access. Similar to other database objects in Snowflake, stored procedures are owned by a role and have one or more privileges that can be granted to other roles. There are currently two privileges that can apply to stored procedures: USAGE and OWNERSHIP. For a role to use a stored procedure, it must either be the owner of the stored procedure or have been granted USAGE on the procedure ^[For a more in-depth view on Access Control in Snowflake, I refer you to [Access Control Considerations](https://docs.snowflake.net/manuals/user-guide/security-access-control-considerations.html) in the Snowflake Documentation.].

----
During this blog, we've examined Snowflake Stored Procedures from Creation to Execution. We've also discussed some of the key differences between stored procedures and user-defined functions as well as examined session state and the two 'types' of procedures Snowflake provides. To round out procedures, we briefly viewed access control in Snowflake and how privileges can affect procedure execution.

For more information on how Ippon Technologies, a Snowflake partner, can help your organization utilize the benefits of Snowflake for a migration from a traditional Data Warehouse, Data Lake or POC, contact sales@ipponusa.com.

---
