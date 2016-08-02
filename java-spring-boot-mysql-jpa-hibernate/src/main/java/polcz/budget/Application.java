package polcz.budget;

import java.util.EnumSet;
import javax.faces.webapp.FacesServlet;
import javax.servlet.DispatcherType;
import org.ocpsoft.rewrite.servlet.RewriteFilter;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.PathResource;
//import org.springframework.core.io.Resource;
//import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

//@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(
{
    "polcz.budget"
})
public class Application extends SpringBootServletInitializer
{

    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
    {
        return application.sources(Application.class, Initializer.class);
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean()
    {
        FacesServlet servlet = new FacesServlet();
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(servlet, "*.jsf");
        return servletRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean rewriteFilter()
    {
        FilterRegistrationBean rwFilter = new FilterRegistrationBean(new RewriteFilter());
        rwFilter.setDispatcherTypes(EnumSet.of(DispatcherType.FORWARD, DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR));
        rwFilter.addUrlPatterns("/*");
        return rwFilter;
    }

//    /**
//     * In file ${init.json} I should check the name of the class (polcz.budget....Book),
//     * otherwise, will not work.
//     */
//    @Value("${init.json}")
//    private String init;
//
//    @Bean
//    public Jackson2RepositoryPopulatorFactoryBean repositoryPopulator() {
//        Resource sourceData;
//        Jackson2RepositoryPopulatorFactoryBean factory;
//        try {
//            sourceData = new PathResource(init);
//            if (!sourceData.exists()) {
//                sourceData = new ClassPathResource(init);
//            }
//            factory = new Jackson2RepositoryPopulatorFactoryBean();
//            factory.setResources(new Resource[]{sourceData});
//        } catch (Exception e) {
//            return null;
//        }
//
//        return factory;
//    }
}
