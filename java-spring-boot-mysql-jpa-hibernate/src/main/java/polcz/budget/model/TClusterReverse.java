package polcz.budget.model;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * The persistent class for the t_cluster database table.
 */
public class TClusterReverse extends AbstractNameDescEntity {
    private static final long serialVersionUID = 9002723472772706262L;

    private String roleName;

    private int sgn;

    @JsonBackReference
    private TClusterReverse parent;

    @JsonManagedReference
    private List<TClusterReverse> children;

    public TClusterReverse() {}

    public TClusterReverse(TCluster cl) {
        sgn = cl.getSgn();
        name = cl.getName();
        roleName = cl.getName() + " (" + sgn + ")";
        if (cl.getChildren() != null)
            for (TCluster child : cl.getChildren())
            this.addChild(new TClusterReverse(child));
    }

    public TClusterReverse(String name, TClusterReverse parent) {
        this.name = name;
        this.sgn = parent.sgn;
        this.parent = parent;
    }

    public TClusterReverse(String name) {
        this.name = name;
    }

    public int getSgn() {
        return this.sgn;
    }

    public void setSgn(int sgn) {
        this.sgn = sgn;
    }

    public TClusterReverse getParent() {
        return this.parent;
    }

    public void setParent(TClusterReverse cl) {
        this.parent = cl;
    }

    public List<TClusterReverse> getChildren() {
        if (children == null) children = new LinkedList<>();
        return this.children;
    }

    public void setChildren(List<TClusterReverse> children) {
        this.children = children;
    }

    public TClusterReverse addChild(TClusterReverse child) {
        getChildren().add(child);
        child.setParent(this);

        return child;
    }

    public TClusterReverse removeChild(TClusterReverse child) {
        getChildren().remove(child);
        child.setParent(null);

        return child;
    }

    public TClusterReverse rsetUid(int uid) {
        setUid(uid);
        return this;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}