package polcz.budget.session.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator
public class PasswordValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

		String password = (String) value;
		if (password.length() < 6 || password.length() > 12) {

			UIInput input = (UIInput) component;
			input.setValid(false);
			context.addMessage(component.getClientId(context), new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"A jelszó hossza nem megfelelő.", "A jelszó hossza nem megfelelő."));
		}

	}

}
