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

;; to be added when we support retraction

;;(retraction likes 'Bob 'Mary)

;; (deftest test-rel-retract
;;   (is (= (into #{}
;;                (run* [q]
;;                      (fresh [x y]
;;                             (likes x y)
;;                             (== q [x y]))))
;;          (into #{} '([John Martha] [Ricky Lucy])))))

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

;; (defrel rel2 ^:index e ^:index a ^:index v)
;; (facts rel2 [[:e1 :a1 :v1]
;;              [:e1 :a2 :v2]])
;; (retractions rel2 [[:e1 :a1 :v1]
;;                    [:e1 :a1 :v1]
;;                    [:e1 :a2 :v2]])

;; (deftest rel2-dup-retractions
;;   (is (= (run* [out]
;;                (fresh [e a v]
;;                       (rel2 e :a1 :v1)
;;                       (rel2 e a v)
;;                       (== [e a v] out))))
;;       '()))



