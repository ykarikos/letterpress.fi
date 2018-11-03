(ns letterpress.tile-test
  (:require [clojure.test :refer :all]
            [letterpress.tile :as tile]))

(def size 3)

; 1  *  2L
; 1  1L 2
; 1  2  2
(def tiles
  [{:letter "R", :id 0, :owner "player-one"}
   {:letter "S", :id 1}
   {:letter "E", :id 2, :owner "player-two-locked"}
   {:letter "K", :id 3, :owner "player-one"}
   {:letter "N", :id 4, :owner "player-one-locked"}
   {:letter "R", :id 5, :owner "player-two"}
   {:letter "A", :id 6, :owner "player-one"}
   {:letter "U", :id 7, :owner "player-two"}
   {:letter "S", :id 8, :owner "player-two"}])

(def owners-count
  '("player-one" nil "player-two"
    "player-one-locked" "player-one" "player-two"
     "player-one" "player-two" "player-two-locked"))

(deftest normalize
  (testing "Does game contain tiles"
    (is (= owners-count
          (->> tiles
               (#'tile/normalize size)
               (map :owner))))))

(deftest neighbors
  (testing "get neighbor tile ids"
    (is (= #{1 3 5 7} (set (#'tile/neighbors size 4))))
    (is (= #{2 4 8} (set (#'tile/neighbors size 5))))
    (is (= #{1 5} (set (#'tile/neighbors size 2))))))
