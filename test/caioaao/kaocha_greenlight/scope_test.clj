(ns caioaao.kaocha-greenlight.scope-test
  (:require
   [caioaao.kaocha-greenlight.test-suite.blue-test]
   [caioaao.kaocha-greenlight.test-suite.red-test]
   [clojure.test :refer [deftest testing is]]
   [com.stuartsierra.component :as component]
   [kaocha.api :as api]
   [kaocha.config]
   [kaocha.result]
   [kaocha.testable :as testable]
   [matcher-combinators.matchers :as matchers]
   [matcher-combinators.parser :refer [mimic-matcher]]
   [matcher-combinators.test]))

(mimic-matcher matchers/equals clojure.lang.Var)

(def starts (atom 0))
(def stops (atom 0))

(defn new-system
  [& _]
  (component/system-map :greenlight.test-test/component
                        (with-meta {}
                          {`component/start (fn [this]
                                              (swap! starts inc)
                                              this)
                           `component/stop  (fn [this]
                                              (swap! stops inc)
                                              this)})))

(def test-suite-test
  {:kaocha/tests [{::testable/type                       :caioaao.kaocha-greenlight/test
                   ::testable/id                         :integration-test
                   :kaocha/ns-patterns                   ["scope-suite.*-test$"]
                   :kaocha/source-paths                  ["src"]
                   :kaocha/test-paths                    ["test"]
                   :caioaao.kaocha-greenlight/new-system 'caioaao.kaocha-greenlight.scope-test/new-system}]})

(def test-suite-ns
  {:kaocha/tests [{::testable/type                         :caioaao.kaocha-greenlight/test
                   ::testable/id                           :integration-test
                   :kaocha/ns-patterns                     ["scope-suite.*-test$"]
                   :kaocha/source-paths                    ["src"]
                   :kaocha/test-paths                      ["test"]
                   :caioaao.kaocha-greenlight/new-system   'caioaao.kaocha-greenlight.scope-test/new-system
                   :caioaao.kaocha-greenlight/system-scope :ns}]})

(deftest scope-tests
  (testing "system is created once when system-scope is :test"
    (reset! starts 0)
    (reset! stops 0)
    (api/run test-suite-test)
    (is (= @starts 1))
    (is (= @stops 1)))

  (testing "system is created per ns when system-scope is :ns"
    (reset! starts 0)
    (reset! stops 0)
    (api/run test-suite-ns)
    (is (= @starts 3))
    (is (= @stops 3))))
