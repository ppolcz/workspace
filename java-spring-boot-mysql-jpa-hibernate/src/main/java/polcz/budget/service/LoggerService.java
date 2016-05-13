package polcz.budget.service;

import org.jboss.logging.Logger;
import org.springframework.stereotype.Service;

import polcz.budget.global.R;

@Service
public class LoggerService {

    public Logger getJBossLogger(Class<?> c) {
        return R.getJBossLogger(c);
    }

    public Logger getJBossLogger(String str) {
        return R.getJBossLogger(str);
    }
}
