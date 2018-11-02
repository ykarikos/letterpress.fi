(ns letterpress.game-test
  (:require [clojure.test :refer :all]
            [letterpress.game :as game]))

(def game-tiles
  [{:letter "R", :id 0, :owner "player-one"}
   {:letter "S", :id 1, :owner "player-one"}
   {:letter "E", :id 2, :owner "player-one"}
   {:letter "K", :id 3, :owner "player-one"}
   {:letter "N", :id 4, :owner "player-one"}
   {:letter "R", :id 5, :owner "player-two"}
   {:letter "A", :id 6, :owner "player-two"}
   {:letter "U", :id 7, :owner "player-two"}
   {:letter "S", :id 8, :owner "player-one"}
   {:letter "Ä", :id 9, :owner "player-one"}
   {:letter "K", :id 10, :owner "player-one"}
   {:letter "U", :id 11, :owner "player-one"}
   {:letter "A", :id 12, :owner "player-two"}
   {:letter "U", :id 13, :owner "player-two"}
   {:letter "E", :id 14, :owner "player-one"}
   {:letter "N", :id 15, :owner "player-two"}
   {:letter "H", :id 16, :owner "player-two"}
   {:letter "I", :id 17, :owner "player-one"}
   {:letter "N", :id 18, :owner "player-two"}
   {:letter "I", :id 19, :owner "player-two"}
   {:letter "I", :id 20, :owner "player-two"}
   {:letter "I", :id 21, :owner "player-two"}
   {:letter "N", :id 22, :owner "player-two"}
   {:letter "K", :id 23, :owner "player-one"}
   {:letter "S", :id 24, :owner "player-one"}])

(def tiles
  [{:letter "K", :id 3, :owner "player-one"}
   {:letter "E", :id 2, :owner "player-one"}
   {:letter "S", :id 8, :owner "player-one"}
   {:letter "Ä", :id 9, :owner "player-one"}])

(def borked-tiles
  [{:letter "K", :id 3, :owner "player-one"}
   {:letter "E", :id 2}])

(def borked-tiles2
  [{:letter "K", :id 3, :owner "player-one"}
   {:letter "E", :id 2, :owner "player-one"}
   {:letter "S", :id 25, :owner "player-one"}])

(deftest game-contains-tiles
  (testing "Does game contain tiles"
    (is (#'game/game-contains-tiles? game-tiles tiles))
    (is (not (#'game/game-contains-tiles? game-tiles borked-tiles)))
    (is (not (#'game/game-contains-tiles? game-tiles borked-tiles2)))))
