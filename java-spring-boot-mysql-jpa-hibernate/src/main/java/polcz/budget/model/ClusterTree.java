package polcz.budget.model;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * The persistent class for the t_cluster database table.
 */
public class ClusterTree extends AbstractNameDescEntity {
    private static final long serialVersionUID = 9002723472772706262L;

    private String roleName;
    private int sgn;

    @JsonBackReference
    private ClusterTree parent;

    @JsonManagedReference
    private List<ClusterTree> children;

    public ClusterTree() {}

    public ClusterTree(Cluster cl) {
        sgn = cl.getSgn();
        name = cl.getName();
        roleName = cl.getName() + " (" + sgn + ")";
        if (cl.getChildren() != null)
            for (Cluster child : cl.getChildren())
            this.addChild(new ClusterTree(child));
    }

    public ClusterTree(String name, ClusterTree parent) {
        this.name = name;
        this.sgn = parent.sgn;
        this.parent = parent;
    }

    public ClusterTree(String name) {
        this.name = name;
    }

    public int getSgn() {
        return this.sgn;
    }

    public void setSgn(int sgn) {
        this.sgn = sgn;
    }

    public ClusterTree getParent() {
        return this.parent;
    }

    public void setParent(ClusterTree cl) {
        this.parent = cl;
    }

    public List<ClusterTree> getChildren() {
        if (children == null) children = new LinkedList<>();
        return this.children;
    }

    public void setChildren(List<ClusterTree> children) {
        this.children = children;
    }

    public ClusterTree addChild(ClusterTree child) {
        getChildren().add(child);
        child.setParent(this);

        return child;
    }

    public ClusterTree removeChild(ClusterTree child) {
        getChildren().remove(child);
        child.setParent(null);

        return child;
    }

    public ClusterTree rsetUid(int uid) {
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