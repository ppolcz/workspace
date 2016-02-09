package polcz.budget.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import polcz.budget.interfaces.CustomerServiceLocal;
import polcz.budget.model.Customer;

@ManagedBean
@SessionScoped
@RolesAllowed({ "ADMIN" })
public class CustomerBean implements Serializable
{
    private static final long serialVersionUID = -7557135137585151683L;

    @EJB
    private CustomerServiceLocal customerService;

    private Integer id;
    private String username;
    private String firstName;
    private String lastName;
    private String role;
    private String password;

    private List<Customer> customers = new ArrayList<>();

    public String create()
    {
        this.id = null;
        this.username = null;
        this.firstName = null;
        this.lastName = null;
        this.role = null;
        this.password = null;
        return "create?faces-redirect=true";
    }

    public String edit(Integer id, String username, String lastName, String firstName, String role,
        String password)
    {
        this.id = id;
        this.username = username;
        this.lastName = lastName;
        this.firstName = firstName;
        this.role = role;
        this.password = password;
        return "edit?faces-redirect=true";
    }

    public List<Customer> getCustomers()
    {
        customers = customerService.getCustomers();
        return customers;
    }

    public String addCustomer()
    {
        customerService.addCustomer(username, lastName, firstName, password, role);
        return "view?faces-redirect=true";
    }

    public String updateCustomer()
    {
        customerService.updateCustomer(id, username, lastName, firstName, password, role);
        return "view?faces-redirect=true";

    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole(String role)
    {
        this.role = role;
    }

}
