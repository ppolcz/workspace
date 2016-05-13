package polcz.budget.service.helper;

import polcz.budget.model.TTransaction;

public interface OdfRule {
    void apply(TTransaction tr, String msg);
}

// public class OdfRule {
// interface Condition {
// boolean test(TTransaction tr);
// }
//
// interface Consequence {
// void apply(TTransaction tr);
// }
//
// Condition condition;
// Consequence modifier;
//
// public OdfRule(Condition condition, Consequence modifier) {
// this.condition = condition;
// this.modifier = modifier;
// }
//
// public void apply(TTransaction tr) {
// if (condition.test(tr)) {
// modifier.apply(tr);
// }
// }
//
// }
