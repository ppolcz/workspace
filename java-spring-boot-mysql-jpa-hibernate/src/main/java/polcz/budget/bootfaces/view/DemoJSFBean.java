/*
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package polcz.budget.bootfaces.view;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import polcz.budget.bootfaces.persistence.BookRepository;
import polcz.budget.bootfaces.persistence.ClusterRepository;
import polcz.budget.model.Cluster;
import polcz.budget.service.EntityService;

/**
 *
 * @author Peter Polcz <ppolcz@gmail.com>
 */
@ManagedBean(name = "demoBean", eager = true)
@SessionScoped
public class DemoJSFBean {

    /**
     * http://www.oakdalesoft.com/2015/09/spring-boot-with-jsfprimefaces/
     * We now have two different types of containers active, one Spring container and a
     * J2EE Servlet container (Tomcat) and really its better to keep them separate in
     * your mind and technically but if we are going to transfer values between them we
     * need some sort of interop bridge. Spring provides this through expression language
     * via a SpringBeanFacesELResolver which is referenced in your faces-config.xml and
     * injected by Spring for you. Now you can access Spring beans in your managed faces
     * bean, passing vales both ways.
     */
    @ManagedProperty(value = "#{entityService}")
    private EntityService entityService;

    @ManagedProperty(value = "#{clusterRepository}")
    private ClusterRepository clusterRepository;

    @ManagedProperty(value = "#{bookRepository}")
    private BookRepository bookRepository;

    public Cluster getCluster() {
        if (getEntityService() != null) {
            return getEntityService().findByName("Szamolas", Cluster.class);
        } else if (clusterRepository != null) {
            return clusterRepository.findOne(3);
        } else {
            return new Cluster("Kutyagumi (entityService == null)");
        }
    }

    /**
     * @return the entityService
     */
    public EntityService getEntityService() {
        return entityService;
    }

    /**
     * @param entityService the entityService to set
     */
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    public BookRepository getBookRepository() {
        return bookRepository;
    }

    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * @return the clusterRepository
     */
    public ClusterRepository getClusterRepository() {
        return clusterRepository;
    }

    /**
     * @param clusterRepository the clusterRepository to set
     */
    public void setClusterRepository(ClusterRepository clusterRepository) {
        this.clusterRepository = clusterRepository;
    }
}
