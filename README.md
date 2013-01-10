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

Facts are persistent values. They can be created using db-facts,
extending the current db context.

```clojure
(def facts
  (pldb/db-facts
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

Or they can be created from a given base database using db-fact.

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

To run a query with a given database, use with-db.

```clojure
(pldb/with-db facts
    (run* [q]	
        (l/fresh [x y]
            (likes x y)
            (fun y)
            (== q [x y]))))
```

# Limitations

- multiple arity relations are not supported yer
- indexing is not implemented yet
- retrations are not implemented yet


## License

Copyright © 2013 ThreatGRID Inc.

Distributed under the Eclipse Public License, the same as Clojure.