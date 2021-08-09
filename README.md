# DB-assignment
A relational database server 

### Query Language


Now that you have a communication mechanism in place, we need something to transmit.
You should implement a database query language based on a simplified version of SQL.
This query language you should support the following keywords:

- USE: changes the database against which the following queries will be run
- CREATE: constructs a new database or table (depending on the provided parameters)
- INSERT: adds a new entity to an existing table
- SELECT: searches for entities that match the given condition
- UPDATE: changes the data contained within a table
- ALTER: changes the structure (rows) of an existing table
- DELETE: removes entities that match the given condition from an existing table
- DROP: removes a specified table from a database, or removes the entire database
- AND / OR: allows conditions to be combined (makes use of parentheses to define ordering) 
- LIKE: used for comparing partial substrings in conditions
- JOIN: performs an **inner** join on two tables (returning all permutations of all matching entities)

All of the above are reserved keywords and should NOT be used as names
for databases, tables or attributes. It is not your responsibility to check for this - if the user
chooses to use them for names, strange things may happen when they perform queries.

A BNF grammar that defines our simplified version of SQL is provided in 
<a href="resources/BNF.txt" target="_blank">this separate document</a>.
Note that your server should be able to correctly parse incoming commands irrespective of
the number of whitespace characters between tokens
(i.e. `name=='Clive'` and `name == 'Clive'` are equivalent and should have the same effect).
