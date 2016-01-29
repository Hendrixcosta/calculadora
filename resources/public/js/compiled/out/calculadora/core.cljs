(ns calculadora.core
    (:require [reagent.core :as reagent]
              [ajax.core :refer [GET POST]]))

(def app-state (reagent/atom {:op1 "" :op2 "" :operando "" :resultado "" :cont 0}))

(defn zerar []
  (reset! app-state {:op1 "" :op2 "" :operando "" :resultado "" :cont 0} ))

(defn handler [response]
  (.log js/console (str response)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn fatorial []
  (GET "http://localhost:3000/fatorial/4"
       {:headers {"Access-Control-Allow-Origin"  "*"
                  "Access-Control-Allow-Headers" "Content-Type"}
        :handler handler
        :error-handler error-handler })
)


(defn calcular []
  (let [resultado
        (case (@app-state :operando)
          "+" (+ (int (@app-state :op1)) (int (@app-state :op2)))
          "-" (- (int (@app-state :op1)) (int (@app-state :op2)))
          "/" (/ (int (@app-state :op1)) (int (@app-state :op2)))
          "*" (* (int (@app-state :op1)) (int (@app-state :op2)))
          )]
    (swap! app-state assoc :resultado (str " = " resultado)))
  (swap! app-state update :cont inc)
  (.log js/console (print-str @app-state )))

(defn store-op [valor]
  (if (= 2 (@app-state :cont)) (zerar) )
  (cond
    (= 0 (@app-state :cont)) (swap! app-state assoc :op1 (str (@app-state :op1) valor ))
    (= 1 (@app-state :cont)) (swap! app-state assoc :op2 (str (@app-state :op2) valor )))
  (.log js/console (print-str @app-state )))

(defn store-sinal [sinal]
  (swap! app-state assoc :operando sinal)
  (swap! app-state update :cont inc)
  (.log js/console (print-str @app-state )))

(defn visor []
  [:div {:id "visor"}
   (@app-state :op1)(@app-state :operando) (@app-state :op2)  (@app-state :resultado)])


(defn teclado []
  [:div {:id "teclado"}
   [:input { :type "button" :value "7" :on-click #(store-op (-> % .-target .-value))}]
   [:input { :type "button" :value "8" :on-click #(store-op (-> % .-target .-value))}]
   [:input { :type "button" :value "9" :on-click #(store-op (-> % .-target .-value))}]
   [:input.sinal { :type "button" :value "/" :on-click #(store-sinal (-> % .-target .-value))}][:br]
   [:input { :type "button" :value "4" :on-click #(store-op (-> % .-target .-value))}]
   [:input { :type "button" :value "5" :on-click #(store-op (-> % .-target .-value))}]
   [:input { :type "button" :value "6" :on-click #(store-op (-> % .-target .-value))}]
   [:input.sinal { :type "button" :value "*" :on-click #(store-sinal (-> % .-target .-value))}][:br]
   [:input { :type "button" :value "1" :on-click #(store-op (-> % .-target .-value))}]
   [:input { :type "button" :value "2" :on-click #(store-op (-> % .-target .-value))}]
   [:input { :type "button" :value "3" :on-click #(store-op (-> % .-target .-value))}]
   [:input.sinal { :type "button" :value "+" :on-click #(store-sinal (-> % .-target .-value))}] [:br]
   ;[:input { :type "button" :value "." :on-click #(store-op (-> % .-target .-value))}]
   [:input { :type "button" :value "0" :on-click #(store-op (-> % .-target .-value))}]
   [:input.igual { :type "button" :value "=" :on-click #(calcular)}]
   [:input.igual { :type "button" :value "x!" :on-click #(fatorial)}]
   [:input.sinal { :type "button" :value "-" :on-click #(store-sinal (-> % .-target .-value))}]])

(defn calculadora []
  [:div.calculadora {:id "calculadora"}
   [visor]
   [teclado]
   ])

(defn page []
  [calculadora])

(defn ^:export main []
  (reagent/render [page]
                  (.getElementById js/document "app")))
