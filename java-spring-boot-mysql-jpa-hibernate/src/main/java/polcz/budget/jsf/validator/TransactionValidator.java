package polcz.budget.jsf.validator;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;

import org.junit.Assert;

import polcz.budget.global.R;
import polcz.budget.model.ChargeAccount;
import polcz.budget.model.Cluster;
import polcz.budget.service.StartupService;

/**
 * Less ID-safe than ChargeAccountValidator and ClusterValidator. But if the IDs
 * remain consistent, than this can be more advantageous.
 * @author polpe
 */
@ManagedBean(name = "trValidator")
@RequestScoped
@FacesValidator("trValidator")
@RolesAllowed({ "ADMIN", "CUSTOMER" })
public class TransactionValidator extends AbstractValidator
{
    /// @formatter:off
    @EJB StartupService ss;
    // @EJB ClusterService cls;
    // @EJB MarketService mks;
    // @EJB CAService cas;
    /// @formatter:on

    public TransactionValidator()
    {
        super(TransactionValidator.class);
    }

    private ChargeAccount cafrom;
    private UIInput cafromInput;

    private void validateChargeAccount(FacesContext context, UIInput input, Object value, boolean source)
    {
        logger.infof("value = %s", value);

        /* this value is not given, that's not a problem (here) */
        if (value == null)
        {
            if (!source) return;

            /* if the source charge account is null, that's a sever problem */
            sendMessage(input, false, new FacesMessage(FacesMessage.SEVERITY_FATAL,
                "Transaction problem.",
                "This charge account resolved to be null, try again."));
            return;
        }

        ChargeAccount ca = (ChargeAccount) value;

        if (ss.none().equals(ca))
        {
            sendMessage(input, false, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Source charge accound id is null.",
                "You must choose a valid charge accound ID (except 'none')."));
            return;
        }

        if (source)
        {
            cafrom = ca;
            cafromInput = input;
        }
        else
        {
            Assert.assertNotNull(cafrom);

            if (!ca.equals(ss.none()) && ca.equals(cafrom))
            {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Source and destination charge account are the same.",
                    "Source and destination charge account should be different!");

                sendMessage(input, false, msg);
                sendMessage(cafromInput, false, msg);
            }
        }

        logger.info("TChargeAccount::getID() = " + input.getId());
    }

    public void validateCluster(FacesContext context, UIComponent component, Object value)
    {
        if (value == null) return;

        UIInput input = (UIInput) component;
        Cluster v = (Cluster) value;

        if (ss.Athelyezes().equals(v))
        {
            sendMessage(input, false, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Transfer transaction problem.",
                "Consider choosing the transfer button instead."));
            return;
        }

        if (ss.Szamolas().equals(v))
        {
            sendMessage(input, false, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Pivot transaction problem.",
                "Consider choosing the pivot button instead."));
            return;
        }

        if (ss.Nem_Adott().equals(v))
        {
            sendMessage(input, false, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Cluster not given.",
                "Consider choosing a more specific cluster, since 'not given' can be disadvantageous in future."));
            return;
        }
    }

    public void validateMarket(FacesContext context, UIComponent component, Object value)
    {
        if (value == null) return;
    }

    private void sendMessage(UIInput input, boolean valid, FacesMessage msg)
    {
        input.setValid(valid);

        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(input.getClientId(context), msg);
        context.getPartialViewContext().getRenderIds().add(R.GLOBAL_MSG_ID);
    }

    public void validateChargeAccountTo(FacesContext context, UIComponent component, Object value)
    {
        validateChargeAccount(context, (UIInput) component, value, false);
    }

    public void validateChargeAccountFrom(FacesContext context, UIComponent component, Object value)
    {
        validateChargeAccount(context, (UIInput) component, value, true);
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException
    {
    }
}
