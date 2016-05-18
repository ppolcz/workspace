package polcz.demo.java8;

import java.util.HashMap;
import java.util.Map;

import polcz.budget.model.Cluster;
import polcz.budget.model.Market;
import polcz.budget.model.Ugylet;

public class LambdaRuleGeneration {

    interface TransactionAttribute {
        Object get(Ugylet tr);
    }

    TransactionAttribute cluster = Ugylet::getCluster;
    TransactionAttribute ca = Ugylet::getCa;
    TransactionAttribute market = Ugylet::getMarket;

    private Ugylet tr;
    private Ugylet tr2;
    private Cluster c1;
    private Cluster c2;
    private Market m1;
    private Market m2;
    private Cluster c3;
    private Market m3;

    interface Condition {
        boolean test(Ugylet tr);
    }

    interface Consequence {
        void apply(Ugylet tr);
        // void applyInt(int tr);
    }

    class Rule {
        Condition condition;
        Consequence modifier;

        public Rule(Condition condition, Consequence modifier) {
            this.condition = condition;
            this.modifier = modifier;
        }

        public void apply(Ugylet tr) {
            if (condition.test(tr)) {
                modifier.apply(tr);
            }
        }
    }

    public LambdaRuleGeneration() {

        c1 = new Cluster("kutyagumi");
        c1.setUid(12);

        c2 = new Cluster("Faroktoll");
        c2.setUid(123);

        c3 = new Cluster("kutya");
        c3.setUid(4);

        m1 = new Market("Kutyamarket", "");
        m1.setUid(321);

        m2 = new Market("Kutya2market", "");
        m2.setUid(1231);

        m3 = new Market("dsasadsda", "");
        m3.setUid(7);

        tr = new Ugylet();
        tr.setPivot(true);
        tr.setCluster(c1);

        tr2 = new Ugylet();
        tr2.setCluster(c2);
        tr2.setMarket(m3);

        rulePivot();
        ruleClusterMarket();
    }

    public void rulePivot() {
        System.out.println("\nRule: 'pivot'");

        Rule rule = new Rule(a -> a.isPivot(), a -> a.setMarket(new Market("Not_applicable_(pivot)", "")));

        System.out.println(tr);
        rule.apply(tr);

        System.out.println(tr);
    }

    public void ruleClusterMarket() {

        /* In order to ease the way of the rule-map definition I can also use this */
        @SuppressWarnings("unused")
        String[][] ruleMapInitializer = { { "asd", "asda" }, { "asdasda", "asdas" } };

        System.out.println("\nRule: 'if cluster -> set market'");

        Map<Cluster, Market> map = new HashMap<>();
        map.put(c1, m1);
        map.put(c3, m2);

        Rule rule = new Rule(a -> map.containsKey(a.getCluster()), a -> a.setMarket(map.get(a.getCluster())));

        System.out.println(tr);
        rule.apply(tr);
        System.out.println(tr);

        System.out.println(tr2);
        rule.apply(tr2);
        System.out.println(tr2);
    }

    public static void main(String... args) {
        new LambdaRuleGeneration();
    }
}
