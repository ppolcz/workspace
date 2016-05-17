package polcz.budget.service.helper;

import polcz.budget.model.Ugylet;

public interface OdfRule {
    void apply(Ugylet tr, String msg);
}
