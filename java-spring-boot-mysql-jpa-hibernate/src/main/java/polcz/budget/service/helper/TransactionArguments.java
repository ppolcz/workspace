package polcz.budget.service.helper;

import org.junit.Assert;

import polcz.budget.global.R;
import polcz.budget.model.Ugylet;

public class TransactionArguments {
    private Ugylet acttr;
    private Ugylet oldtr;
    private int type;
    private String msg;

    public TransactionArguments(Ugylet acttr, Ugylet oldtr, int type) {
        this.acttr = acttr;
        this.oldtr = oldtr;
        this.type = type;
    }

    public TransactionArguments(Ugylet acttr, Ugylet oldtr, int type, String msg) {
        this.acttr = acttr;
        this.oldtr = oldtr;
        this.type = type;
        this.msg = msg;
    }

    public void validate() {
        if (oldtr != null) {
            Assert.assertTrue(type == R.TR_UPDATE);
        }
    }

    public Ugylet getActtr() {
        return acttr;
    }

    public void setActtr(Ugylet acttr) {
        this.acttr = acttr;
    }

    public Ugylet getOldtr() {
        return oldtr;
    }

    public void setOldtr(Ugylet oldtr) {
        this.oldtr = oldtr;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
