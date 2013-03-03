(ns pldb.logic-test
  (:use [clojure.test])
  (:require [clojure.core.logic :as l]
            [pldb.logic :as pldb]))

;; from core.logic tests
(pldb/db-rel man p)
(pldb/db-rel woman p)
(pldb/db-rel likes p1 p2)
(pldb/db-rel fun p)

(def facts0
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

(def facts1 (-> facts0
                (pldb/db-fact fun 'Lucy)))

(deftest test-facts0
  (is (= (pldb/with-db facts0
           (l/run* [q]
                   (l/fresh [x y]
                            (likes x y)
                            (fun y)
                            (l/== q [x y]))))
         '())))

(deftest test-facts1
  (is (= (pldb/with-db facts1
           (l/run* [q]
                   (l/fresh [x y]
                            (likes x y)
                            (fun y)
                            (l/== q [x y]))))
         '([Ricky Lucy]))))

(def facts1-retracted
  (-> facts1
      (pldb/db-retraction likes 'Bob 'Mary)))

(deftest test-rel-retract
  (is (= (into #{}
               (pldb/with-db facts1-retracted
                 (l/run* [q]
                         (l/fresh [x y]
                                  (likes x y)
                                  (l/== q [x y])))))
         (into #{} '([John Martha] [Ricky Lucy])))))

(pldb/db-rel rel1 ^:index a)
(def indexed-db
  (pldb/db-facts [rel1 [1 2]]))

(deftest test-rel-logic-29
  (is (= (pldb/with-db indexed-db
           (l/run* [q]
                 (l/fresh [a]
                        (rel1 [q a])
                        (l/== a 2))))
         '(1))))

(pldb/db-rel rel2 ^:index e ^:index a ^:index v)
(def facts2
  (pldb/db-facts
   [rel2 :e1 :a1 :v1]
   [rel2 :e1 :a2 :v2]))



#_(def facts2-retracted
  (-> facts2
      (pldb/db-retraction rel2 :e1 :a1 :v1)
      (pldb/db-retraction rel2 :e1 :a2 :v2)))

(def facts2-retracted
  (pldb/with-db facts2
    (pldb/db-retractions)
        [rel2 :e1 :a1 :v1]
        [rel2 :e1 :a2 :v2]))

(deftest rel2-dup-retractions
  (is (= '()
         (pldb/with-db facts2-retracted
           (l/run* [out]
                   (l/fresh [e a v]
                            (rel2 e :a1 :v1)
                            (rel2 e a v)
                            (l/== [e a v] out)))))))
