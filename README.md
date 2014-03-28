## Javascript-SQL bridge

Allow querying a (Postgre)SQL datasource from JavaScript using the Rhino engine bundled with JDK 7.

# Usage

Javascript code (users.js):

```
var _ = require('./underscore.min.js');

var rows = [];

sql.q("select * from users").eachRow(function(id, name, email) {
    rows.push([id, name, email]);
});

print(rows);
_.each([1,2,3], print);
```

Use browserify to bundle the code:

```
$ browserify users.js -o users.bundle.js
```

Run the code:

```
mvn compile exec:java -Dexec.args="/path/to/users.bundle.js"
```