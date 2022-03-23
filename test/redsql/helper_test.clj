(ns redsql.helper-test
  (:require [redsql.helper :as sut]
            [clojure.test :refer :all]))


(deftest test-helper-fn
  (testing "eq"
    (is (= (sut/eq "1") {:= "1"}))
    (is (= (sut/eq :id "1") [:= :id "1"]))
    (is (= (sut/eq true :id "1") [:= :id "1"]))
    (is (nil? (sut/eq false :id "1")))
    (is (= (sut/eq-condition :id "1") [:= :id "1"]))
    (is (nil? (sut/eq-condition :id nil))))
  (testing "ne"
    (is (= (sut/ne "1") {:<> "1"}))
    (is (= (sut/ne :id "1") [:<> :id "1"]))
    (is (= (sut/ne true :id "1") [:<> :id "1"]))
    (is (nil? (sut/ne false :id "1")))
    (is (= (sut/ne-condition :id "1") [:<> :id "1"]))
    (is (nil? (sut/ne-condition :id nil))))
  (testing "gt"
    (is (= (sut/gt "1") {:> "1"}))
    (is (= (sut/gt :id "1") [:> :id "1"]))
    (is (= (sut/gt true :id "1") [:> :id "1"]))
    (is (nil? (sut/gt false :id "1")))
    (is (= (sut/gt-condition :id "1") [:> :id "1"]))
    (is (nil? (sut/gt-condition :id nil))))
  (testing "ge"
    (is (= (sut/ge "1") {:>= "1"}))
    (is (= (sut/ge :id "1") [:>= :id "1"]))
    (is (= (sut/ge true :id "1") [:>= :id "1"]))
    (is (nil? (sut/ge false :id "1")))
    (is (= (sut/ge-condition :id "1") [:>= :id "1"]))
    (is (nil? (sut/ge-condition :id nil)))=)
  (testing "lt"
    (is (= (sut/lt "1") {:< "1"}))
    (is (= (sut/lt :id "1") [:< :id "1"]))
    (is (= (sut/lt true :id "1") [:< :id "1"]))
    (is (nil? (sut/lt false :id "1")))
    (is (= (sut/lt-condition :id "1") [:< :id "1"]))
    (is (nil? (sut/lt-condition :id nil))))
  (testing "le"
    (is (= (sut/le "1") {:<= "1"}))
    (is (= (sut/le :id "1") [:<= :id "1"]))
    (is (= (sut/le true :id "1") [:<= :id "1"]))
    (is (nil? (sut/le false :id "1")))
    (is (= (sut/le-condition :id "1") [:<= :id "1"]))
    (is (nil? (sut/le-condition :id nil))))
  (testing "like"
    (is (= (sut/like "1") {:like "%1%"}))
    (is (= (sut/like :id "1") [:like :id "%1%"]))
    (is (= (sut/like true :id "1") [:like :id "%1%"]))
    (is (nil? (sut/like false :id "1")))
    (is (= (sut/like-condition :id "1") [:like :id "%1%"]))
    (is (nil? (sut/like-condition :id nil))))
  (testing "like-left"
    (is (= (sut/like-left "1") {:like "%1"}))
    (is (= (sut/like-left :id "1") [:like :id "%1"]))
    (is (= (sut/like-left true :id "1") [:like :id "%1"]))
    (is (nil? (sut/like-left false :id "1")))
    (is (= (sut/like-left-condition :id "1") [:like :id "%1"]))
    (is (nil? (sut/like-left-condition :id nil))))
  (testing "like-right"
    (is (= (sut/like-right "1") {:like "1%"}))
    (is (= (sut/like-right :id "1") [:like :id "1%"]))
    (is (= (sut/like-right true :id "1") [:like :id "1%"]))
    (is (nil? (sut/like-right false :id "1")))
    (is (= (sut/like-right-condition :id "1") [:like :id "1%"]))
    (is (nil? (sut/like-right-condition :id nil))))
  (testing "is-null"
    (is (= (sut/is-null "1") {:= nil}))
    (is (= (sut/is-null :id "1") [:= :id nil]))
    (is (= (sut/is-null true :id "1") [:= :id nil]))
    (is (nil? (sut/is-null false :id "1")))
    (is (= (sut/is-null-condition :id "1") [:= :id nil]))
    (is (nil? (sut/is-null-condition :id nil))))
  (testing "is-not-null"
    (is (= (sut/is-not-null "1") {:not= nil}))
    (is (= (sut/is-not-null :id "1") [:not= :id nil]))
    (is (= (sut/is-not-null true :id "1") [:not= :id nil]))
    (is (nil? (sut/is-not-null false :id "1")))
    (is (= (sut/is-not-null-condition :id "1") [:not= :id nil]))
    (is (nil? (sut/is-not-null-condition :id nil))))
  )
