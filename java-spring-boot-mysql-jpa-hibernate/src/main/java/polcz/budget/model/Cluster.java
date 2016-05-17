package polcz.budget.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * The persistent class for the t_cluster database table.
 */
@Entity
@Table(name = "clusters")
@NamedQueries(value = {
    @NamedQuery(name = "Cluster.findOne", query = "SELECT e FROM Cluster e where e.uid = :uid"),
    @NamedQuery(name = "Cluster.findByName", query = "SELECT e FROM Cluster e where e.name = :name"),
    @NamedQuery(name = "Cluster.findAll", query = "SELECT e FROM Cluster e") })
public class Cluster extends AbstractNameDescEntity {
    private static final long serialVersionUID = 9002723472772706262L;

    @Column(nullable = false)
    private int sgn;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "parent")
    private Cluster parent;

    @JsonBackReference
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Cluster> children;

    public Cluster() {}

    public Cluster(String name, int sgn, Cluster parent) {
        this.name = name;
        this.sgn = sgn;
        this.parent = parent;
    }

    public Cluster(String name, Cluster parent) {
        this.name = name;
        this.sgn = parent.sgn;
        this.parent = parent;
    }

    public Cluster(String name) {
        this.name = name;
    }

    public int getSgn() {
        return this.sgn;
    }

    public void setSgn(int sgn) {
        this.sgn = sgn;
    }

    public Cluster getParent() {
        return this.parent;
    }

    public void setParent(Cluster cl) {
        this.parent = cl;
    }

    public List<Cluster> getChildren() {
        return this.children;
    }

    public void setChildren(List<Cluster> children) {
        this.children = children;
    }

    public Cluster addChild(Cluster child) {
        getChildren().add(child);
        child.setParent(this);

        return child;
    }

    public Cluster removeChild(Cluster child) {
        getChildren().remove(child);
        child.setParent(null);

        return child;
    }

    public Cluster rsetUid(int uid) {
        setUid(uid);
        return this;
    }
}