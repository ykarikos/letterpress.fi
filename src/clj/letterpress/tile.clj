(ns letterpress.tile)

; The cumulative frequency of a letter in Finnish alphabet
(def alphabet
  ['(\I 0.11607)
   '(\A 0.23011)
   '(\T 0.31651)
   '(\S 0.39098)
   '(\E 0.45836)
   '(\U 0.52488)
   '(\K 0.59058)
   '(\N 0.65494)
   '(\L 0.71334)
   '(\O 0.76748)
   '(\R 0.80699)
   '(\Ä 0.83889)
   '(\P 0.86920)
   '(\M 0.89892)
   '(\V 0.92313)
   '(\H 0.94481)
   '(\Y 0.96644)
   '(\J 0.97941)
   '(\D 0.98683)
   '(\Ö 0.99417)
   '(\G 0.99682)
   '(\F 0.99847)
   '(\B 1.00000)])

(defn- get-character [p]
  (->> alphabet
       (filter #(> (second %) p))
       first
       first))

(defn- letter-to-tile [acc letter]
  {:index (-> acc :index inc)
   :tiles (conj (:tiles acc)
                {:letter letter
                 :id (:index acc)})})

(defn generate-tiles [size]
  (let [tile-letters (take (* size size)
                           (repeatedly #(get-character (rand))))
        tiles (reduce letter-to-tile
                      {:index 0 :tiles []}
                      tile-letters)]
    (:tiles tiles)))

(defn- neighbors
  [size id]
  (let [col (mod id size)
        row (quot id size)
        neighbors [[(dec col) row]
                   [col (dec row)]
                   [(inc col) row]
                   [col (inc row)]]]
    (->> neighbors
         (filter (fn [c] (every? #(and (>= % 0) (< % size)) c)))
         (map #(+ (* (second %) size) (first %))))))

(defn- neighbor-owners
  [size tile tiles]
  (let [neighbor-ids (neighbors size (:id tile))
        neighbor-tiles (filter (fn [t] (some #(= (:id t) %) neighbor-ids)) tiles)]
    (map :owner neighbor-tiles)))

(defn- normalize-fn
  [size tiles]
  (fn [tile]
    (if (nil? (:owner tile))
        tile
        (let [n-owners (neighbor-owners size tile tiles)
              owner (-> tile :owner (.substring 0 10))]
          (if (every? #(and (not (nil? %)) (.startsWith % owner)) n-owners)
              (assoc tile :owner (str owner "-locked"))
              (assoc tile :owner owner))))))

(defn- normalize
  [size tiles]
  (map (normalize-fn size tiles) tiles))

(defn- tile-locked? [tile]
  (and (-> tile :owner nil? not)
       (-> tile :owner (.endsWith "-locked"))))

(defn- update-tiles-fn
  [owner]
  (fn [old-tiles tile]
    (if (tile-locked? tile)
      old-tiles
      (replace {tile (assoc tile :owner owner)} old-tiles))))

(defn update-tiles
  [game tiles]
  (->> tiles
    (reduce (update-tiles-fn (:turn game)) (:tiles game))
    (normalize (:size game))))
