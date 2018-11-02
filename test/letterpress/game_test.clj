(ns letterpress.game-test
  (:require [clojure.test :refer :all]
            [letterpress.game :as game]))

(def game
  {:_id "254508b2-1a3e-4bd8-87a7-78d2483a83ef"
   :tiles
   [{:letter "R", :id 0, :owner "player-one"}
    {:letter "S", :id 1, :owner "player-one"}
    {:letter "E", :id 2, :owner "player-one"}
    {:letter "K", :id 3, :owner "player-one"}
    {:letter "N", :id 4, :owner "player-one"}
    {:letter "R", :id 5, :owner "player-two"}
    {:letter "A", :id 6, :owner "player-two"}
    {:letter "U", :id 7, :owner "player-two"}
    {:letter "S", :id 8, :owner "player-one"}
    {:letter "Ä", :id 9, :owner "player-one"}]
   :player-one "foo"
   :score [0 0]
   :turn "player-one"
   :played-words
   [{:word "sekä", :turn "player-one"}
    {:word "kuha", :turn "player-two"}
    {:word "rakkaus", :turn "player-one"}]
   :size 5
   :player-two "Mikko"})

(def game-tiles (:tiles game))

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

(deftest valid-namesubmit
  (testing "Is the submission valid?"
    (is (#'game/valid-submit? "kesä" game tiles "foo"))
    (is (not (#'game/valid-submit? "kesä" game tiles "bar")))
    (is (not (#'game/valid-submit? "kesä" game tiles "Mikko")))
    (is (not (#'game/valid-submit? "kes" game tiles "foo")))
    (is (not (#'game/valid-submit? "kesä" game borked-tiles "foo")))
    (is (not (#'game/valid-submit? "kuha" game tiles "foo")))))
