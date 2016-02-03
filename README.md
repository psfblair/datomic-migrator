# data-store-migration

A Clojure library designed to manage Datomic data stores, primarily by running
schema migrations. It uses the Conformity library for running data migrations. 

## Usage

A standalone executable jar can be created using `lein uberjar`. If this jar is 
run from the command line, it assumes by default that migrations are in 
a directory with the relative path `schema-and-ref-data`. Code can also pass in
the directory containing the migrations to the `run-migrations` function.

The project also assumes that migrations are named with a naming schema that 
ensures proper sort order.

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
