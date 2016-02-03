# datomic-migration

A Clojure library designed to manage Datomic data stores, primarily by running
schema migrations. It wraps the Conformity library for running data migrations. 

## Usage

The main method of `net.phobot.datomic.migrator` will run migrations against a
Datomic database whose URL may be specified using a command-line argument. The 
command line also accepts a `-s` or `--schema-dir` option indicating the directory 
where the migration files are to be found. If this option is not specified, the 
default value is a subdirectory of the current working directory, named 
`schema-and-ref-data`. After the migrations are run from the command line,
`datomic/shutdown` is called to clean up resources. To create a standalone 
executable jar, use `lein uberjar`.

This library can also be called from other code using the `run-migration` 
function, which accepts as arguments the URL of the database, the directory 
containing the migrations, and a function to accept logging statements. When
this function is called, the `datomic/shutdown` method is not called after the
migrations finish.

The project assumes that migrations are named with a naming schema that 
ensures proper sort order, and will run them in sort order..

## Migrations

Conformity uses the concept of a `norm` rather than a migration. Norms are datoms
that you want transacted once and only once. With norms, Conformity lets you 
declare expectations about the state of your database, and enforce those 
idempotently without repeatedly transacting schema, required data, etc.

Note that when using Conformity, each norm in the schema is a key-value pair in 
a map. Thus, when `ensure-conforms` is given the map, it cannot guarantee the 
order in which norms are transacted. In order to avoid this problem, you can 
break the schema into multiple migrations that have an ordering and call 
`ensure-conforms` serially on them. Alternatively, you can also use the 
`:requires` key in `ensure-conforms` to specify that one norm depends on another. 
(The norm-map parameter contains two keys -- :txes and :requires, where :txes is
the data to install, and :requires is an optional list of prerequisite norms.)

Within any given migration file, dependencies must be specified using the
`:requires` key.

## License

```
The MIT License (MIT)

Copyright (c) 2015 Yannick Scherer

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
