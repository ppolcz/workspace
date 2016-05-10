package polcz.budget.service;

import javax.annotation.PostConstruct;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import polcz.budget.global.R;
import polcz.budget.model.TChargeAccount;
import polcz.budget.model.TCluster;
import polcz.budget.model.TMarket;

@Service
public class StartupService {

    // @PersistenceContext
    // EntityManager em;

    @Autowired
    EntityService service;

    Logger logger = Logger.getLogger("PPOLCZ_" + StartupService.class.getSimpleName());

    public TCluster cluster(TCluster c) {
        logger.infof("%s --> %s", c, c.getParent());
        return service.findByNameOrCreate(c, TCluster.class);
    }

    public TMarket market(TMarket c) {
        return service.findByNameOrCreate(c, TMarket.class);
    }

    public TChargeAccount ca(TChargeAccount c) {
        return service.findByNameOrCreate(c, TChargeAccount.class);
    }

    private TCluster Nem_Adott;
    private TCluster Egyeb_Kiadas;
    private TCluster Szukseges;
    private TCluster Napi_Szukseglet;
    private TCluster Szamolas;
    private TCluster Athelyezes;

    private TMarket Market_Not_Applicable;

    private TChargeAccount none;
    private TChargeAccount pkez;

    @PostConstruct
    public void init() {
        System.out.println("Hello World!");

        // ezeket mindenkeppen benne szeretnem hagyni az adatbazisban
        Nem_Adott = cluster(new TCluster(R.CLNAME_NOT_GIVEN, R.CLSGN_NOT_GIVEN, null));
        Egyeb_Kiadas = cluster(new TCluster("Egyeb_Kiadas", R.CLSGN_OUTCOME, Nem_Adott));
        Szukseges = cluster(new TCluster("Szukseges", Egyeb_Kiadas));
        Napi_Szukseglet = cluster(new TCluster("Napi_Szukseglet", Szukseges));
        Szamolas = cluster(new TCluster(R.CLNAME_PIVOT, R.CLSGN_PIVOT, Nem_Adott));
        Athelyezes = cluster(new TCluster(R.CLNAME_TRANSFER, R.CLSGN_TRANSFER, Nem_Adott));

        Market_Not_Applicable = market(new TMarket("Not_Applicable", null));

        none = ca(new TChargeAccount(R.CANAME_NONE, "Not applicable"));
        pkez = ca(new TChargeAccount("pkez"));
    }

    public TCluster Nem_Adott() {
        return Nem_Adott;
    }

    public TCluster Egyeb_Kiadas() {
        return Egyeb_Kiadas;
    }

    public TCluster Napi_Szukseglet() {
        return Napi_Szukseglet;
    }

    public TCluster Szamolas() {
        return Szamolas;
    }

    public TCluster Athelyezes() {
        return Athelyezes;
    }

    public TCluster Szukseges() {
        return Szukseges;
    }

    public TMarket Market_Not_Applicable() {
        return Market_Not_Applicable;
    }

    public TChargeAccount none() {
        return none;
    }

    public TChargeAccount pkez() {
        return pkez;
    }

}
