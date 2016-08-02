package polcz.budget.demo.jsf.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Created by Alex on 07/03/2015.
 */
@Entity
@Table(name = "book")
public class Book implements Serializable {

    private static final long serialVersionUID = 29851923847L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uid;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true)
    private Float price;

    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private Integer nbofpage;

    @Column(nullable = true)
    private Boolean illustrations;

    public Long getId() {
        return uid;
    }

    public void setId(Long id) {
        this.uid = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getnbofpage() {
        return nbofpage;
    }

    public void setnbofpage(Integer nbOfPage) {
        this.nbofpage = nbOfPage;
    }

    public Boolean getIllustrations() {
        return illustrations;
    }

    public void setIllustrations(Boolean illustrations) {
        this.illustrations = illustrations;
    }
}
