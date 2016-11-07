package polcz.budget.jsf.validator;

import javax.faces.validator.Validator;

import org.jboss.logging.Logger;

import polcz.budget.global.R;

public abstract class AbstractValidator implements Validator
{
    Logger logger;
    
    public AbstractValidator(Class<?> childClass)
    {
        logger = R.getJBossLogger(childClass);
        logger.info(childClass.getSimpleName() + " instantiated");
    }
}
