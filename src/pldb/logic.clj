(ns pldb.logic
  (:require [clojure.core.logic :as l]))

(def ^:dynamic *logic-db* {})

(defn db-keys []
  (keys *logic-db*))

(defn rel-key [rel]
  (if (keyword? rel)
    rel
    (:rel-key (meta rel))))

(defn facts-for [rel]
  ((rel-key rel) *logic-db*))

(defmacro db-rel [name & args]
  (let [kname
        (keyword name)]
    `(def ~name
       (with-meta (fn [& v#]
                    (fn [s#]
                      (l/to-stream (map (fn [potential#]
                                          (l/unify s# v# potential#))
                                        (facts-for ~kname)))))
         {:rel-key ~kname}))))

(defn db-fact [db rel & args]
  (let [key (rel-key rel)]
    (update-in db [key] #(conj %1 args))))

(defn db-facts [& facts]
  (reduce #(apply db-fact %1 %2) *logic-db* facts))

(def empty-db {})

(defmacro with-db [db & body]
  `(binding [*logic-db* ~db]
          ~@body))



