package polcz.budget.service;

import javax.annotation.PostConstruct;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import polcz.budget.global.R;
import polcz.budget.model.ChargeAccount;
import polcz.budget.model.Cluster;
import polcz.budget.model.Market;

@Service
public class StartupService {

    // @PersistenceContext
    // EntityManager em;

    @Autowired
    EntityService service;

    Logger logger = Logger.getLogger("PPOLCZ_" + StartupService.class.getSimpleName());

    public Cluster cluster(Cluster c) {
        // logger.infof("%s --> %s", c, c.getParent());
        return service.findByNameOrCreate(c, Cluster.class);
    }

    public Market market(Market c) {
        return service.findByNameOrCreate(c, Market.class);
    }

    public ChargeAccount ca(ChargeAccount c) {
        return service.findByNameOrCreate(c, ChargeAccount.class);
    }

    private Cluster Nem_Adott;
    private Cluster Egyeb_Kiadas;
    private Cluster Szukseges;
    private Cluster Napi_Szukseglet;
    private Cluster Szamolas;
    private Cluster Athelyezes;

    private Market Market_Not_Applicable;

    private ChargeAccount none;
    private ChargeAccount pkez;

    @PostConstruct
    public void init() {
        System.out.println("Hello World!");

        // ezeket mindenkeppen benne szeretnem hagyni az adatbazisban
        Nem_Adott = cluster(new Cluster(R.CLNAME_NOT_GIVEN, R.CLSGN_NOT_GIVEN, null));
        Egyeb_Kiadas = cluster(new Cluster("Egyeb_Kiadas", R.CLSGN_OUTCOME, Nem_Adott));
        Szukseges = cluster(new Cluster("Szukseges", Egyeb_Kiadas));
        Napi_Szukseglet = cluster(new Cluster("Napi_Szukseglet", Szukseges));
        Szamolas = cluster(new Cluster(R.CLNAME_PIVOT, R.CLSGN_PIVOT, Nem_Adott));
        Athelyezes = cluster(new Cluster(R.CLNAME_TRANSFER, R.CLSGN_TRANSFER, Nem_Adott));

        Market_Not_Applicable = market(new Market(R.MKNAME_NOT_APPLICABLE));

        none = ca(new ChargeAccount(R.CANAME_NONE, "Not applicable"));
        pkez = ca(new ChargeAccount(R.CANAME_PKEZ, "Peti kezpenz"));
    }

    public Cluster Nem_Adott() {
        return Nem_Adott;
    }

    public Cluster Egyeb_Kiadas() {
        return Egyeb_Kiadas;
    }

    public Cluster Napi_Szukseglet() {
        return Napi_Szukseglet;
    }

    public Cluster Szamolas() {
        return Szamolas;
    }

    public Cluster Athelyezes() {
        return Athelyezes;
    }

    public Cluster Szukseges() {
        return Szukseges;
    }

    public Market Market_Not_Applicable() {
        return Market_Not_Applicable;
    }

    public ChargeAccount none() {
        return none;
    }

    public ChargeAccount pkez() {
        return pkez;
    }

}
