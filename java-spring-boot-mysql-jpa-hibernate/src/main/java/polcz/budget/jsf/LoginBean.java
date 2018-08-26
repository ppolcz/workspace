package polcz.budget.jsf;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@SessionScoped
@ManagedBean
public class LoginBean implements Serializable
{
    private static final long serialVersionUID = 2809838453617377105L;

    private String username;
    private String password;

    public void login()
    {

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        final HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();

        // request.isUserInRole(role); /* felhasználónak van-e joga az adott
        // szerepkörhöz – Expression Language-ből is elérhető*/

        try
        {
            request.login(username, password); /* programozott beléptetés */
            // request.getUserPrincipal(); /* visszaadja a bejelentkezett felhasználó objektumot*/
            // request.getUserPrincipal().getName(); /* visszaadja a bejelentkezett felhasználónevet*/

            externalContext.redirect(externalContext.getRequestContextPath() + "/faces/index.xhtml");
        }
        catch (ServletException e)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Hibás felhasználónév vagy jelszó!", "Hibás felhasználónév vagy jelszó!"));
            FacesContext.getCurrentInstance().validationFailed();
        }
        catch (IOException e)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Hiba az átirányitásnál!", "Hiba az átirányitásnál!"));
            FacesContext.getCurrentInstance().validationFailed();
        }

    }

    public String redirectToLogin()
    {
        return "login?faces-redirect=true";
    }

    public void logout()
    {
        // Elkérjük a HttpSessiont.
        ExternalContext extc = FacesContext.getCurrentInstance().getExternalContext();
        final HttpSession httpSession = (HttpSession) extc.getSession(false /* don’t create */);

        // Maga a kiléptetés.
        httpSession.invalidate();

        try
        {
            // extc.redirect(extc.getRequestContextPath() + "/faces/login.xhtml");

            // Küldjük az főoldalra.
            extc.redirect(extc.getRequestContextPath() + "/index.xhtml");
            // extc.redirect(extc.getRequestContextPath() + "/k_tr/view.xhtml");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

}
