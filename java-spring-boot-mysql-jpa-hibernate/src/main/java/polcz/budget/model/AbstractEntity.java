package polcz.budget.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractEntity implements Serializable {
    private static final long serialVersionUID = 7323723228576675896L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(insertable = true, updatable = false, unique = true, nullable = false)
    protected int uid;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractEntity) {
            // first check if they are of the same class
            return getClass().equals(obj.getClass()) && getUid() == ((AbstractEntity) obj).getUid();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getUid();
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
