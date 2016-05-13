package polcz.demo.java8;

import java.util.HashMap;
import java.util.Map;

import polcz.budget.model.TCluster;
import polcz.budget.model.TMarket;
import polcz.budget.model.TTransaction;

public class LambdaRuleGeneration {

    interface TransactionAttribute {
        Object get(TTransaction tr);
    }

    TransactionAttribute cluster = TTransaction::getCluster;
    TransactionAttribute ca = TTransaction::getCa;
    TransactionAttribute market = TTransaction::getMarket;

    private TTransaction tr;
    private TTransaction tr2;
    private TCluster c1;
    private TCluster c2;
    private TMarket m1;
    private TMarket m2;
    private TCluster c3;
    private TMarket m3;

    interface Condition {
        boolean test(TTransaction tr);
    }

    interface Consequence {
        void apply(TTransaction tr);
        // void applyInt(int tr);
    }

    class Rule {
        Condition condition;
        Consequence modifier;

        public Rule(Condition condition, Consequence modifier) {
            this.condition = condition;
            this.modifier = modifier;
        }

        public void apply(TTransaction tr) {
            if (condition.test(tr)) {
                modifier.apply(tr);
            }
        }
    }

    public LambdaRuleGeneration() {

        c1 = new TCluster("kutyagumi");
        c1.setUid(12);

        c2 = new TCluster("Faroktoll");
        c2.setUid(123);

        c3 = new TCluster("kutya");
        c3.setUid(4);

        m1 = new TMarket("Kutyamarket", "");
        m1.setUid(321);

        m2 = new TMarket("Kutya2market", "");
        m2.setUid(1231);

        m3 = new TMarket("dsasadsda", "");
        m3.setUid(7);

        tr = new TTransaction();
        tr.setPivot(true);
        tr.setCluster(c1);

        tr2 = new TTransaction();
        tr2.setCluster(c2);
        tr2.setMarket(m3);

        rulePivot();
        ruleClusterMarket();
    }

    public void rulePivot() {
        System.out.println("\nRule: 'pivot'");

        Rule rule = new Rule(a -> a.isPivot(), a -> a.setMarket(new TMarket("Not_applicable_(pivot)", "")));

        System.out.println(tr);
        rule.apply(tr);

        System.out.println(tr);
    }

    public void ruleClusterMarket() {

        /* In order to ease the way of the rule-map definition I can also use this */
        @SuppressWarnings("unused")
        String[][] ruleMapInitializer = { { "asd", "asda" }, { "asdasda", "asdas" } };

        System.out.println("\nRule: 'if cluster -> set market'");

        Map<TCluster, TMarket> map = new HashMap<>();
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
