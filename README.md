# pldb

A persistent core.logic database. The goal of pldb is to provide
core.logic fact/relation mechanism that doesn't use mutable namespace
references.  This makes it easier to use core.logic in multi-threaded
environments like web applications.


# Usage

Relations are defined in the same way as core.logic relations.  These relations
are bound to the namespace they are defined in, as in core.logic.

```clojure
(pldb/db-rel man p)
(pldb/db-rel woman p)
(pldb/db-rel likes p1 p2)
(pldb/db-rel fun p)
```

Facts are persistent values. They can be created into a new empty db.

```clojure
(def facts
  (pldb/db
   [man 'Bob]
   [man 'John]
   [man 'Ricky]

   [woman 'Mary]
   [woman 'Martha]
   [woman 'Lucy]

   [likes 'Bob 'Mary]
   [likes 'John 'Martha]
   [likes 'Ricky 'Lucy]))
```

Or they can extend a given base database using db-fact.

```clojure
(def facts
  (-> pldb/empty-db
     (pldb/db-fact man 'Bob)
     (pldb/db-fact man 'John)
     (pldb/db-fact man 'Ricky)

     (pldb/db-fact woman 'Mary)
     (pldb/db-fact woman 'Martha)
     (pldb/db-fact woman 'Lucy)

     (pldb/db-fact likes 'Bob 'Mary)
     (pldb/db-fact likes 'John 'Martha)
     (pldb/db-fact likes 'Ricky 'Lucy)))
```

To retract a fact, create a new database with the fact retracted.

```clojure
(def facts-retracted
  (-> facts
      (pldb/db-retraction likes 'Bob 'Mary))
```

To run a query with a given database, use with-db.

```clojure
(pldb/with-db facts
    (run* [q]
        (fresh [x y]
            (likes x y)
            (fun y)
            (== q [x y]))))
```

To run a query across multiple databases, use with-dbs.

```clojure
(pldb/with-dbs [facts1 facts2 facts3]
    (run* [q]
        (fresh [x y]
            (likes x y)
            (fun y)
            (== q [x y]))))
```

# Build Status

[![Build Status](https://buildhive.cloudbees.com/job/threatgrid/job/pldb/badge/icon)](https://buildhive.cloudbees.com/job/threatgrid/job/pldb/)

## License

Copyright © 2013 ThreatGRID Inc.

Distributed under the Eclipse Public License, the same as Clojure.