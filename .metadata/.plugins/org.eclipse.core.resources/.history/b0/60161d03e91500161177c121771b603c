package polcz.budget.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.jboss.logging.Logger;

import polcz.budget.global.R;
import polcz.budget.model.AbstractNameDescEntity;
import polcz.budget.model.TChargeAccount;
import polcz.budget.model.TCluster;
import polcz.budget.model.TMarket;

@Singleton
@Startup
@RolesAllowed({ "ADMIN", "CUSTOMER" })
public class StartupService
{

    @PersistenceContext(unitName = "mysqlPU")
    EntityManager em;

    Logger logger = Logger.getLogger("PPOLCZ_" + StartupService.class.getSimpleName());

    private <T extends AbstractNameDescEntity> T findOrCreate(Class<T> entityClass, T entity)
    {

        TypedQuery<T> query = em.createNamedQuery(entityClass.getSimpleName() + ".findByName", entityClass);
        query.setParameter("name", entity.getName());

        List<T> oldEntity = query.getResultList();

        if (oldEntity.size() > 0)
        {
            // entity.setKey(oldEntity.get(0).getKey());
            // return em.merge(entity);
            return oldEntity.get(0);
        }
        return em.merge(entity);
    }

    public TCluster cluster(TCluster c)
    {
        logger.infof("%s --> %s", c, c.getParent());
        return findOrCreate(TCluster.class, c);
    }

    public TMarket market(TMarket c)
    {
        return findOrCreate(TMarket.class, c);
    }

    public TChargeAccount ca(TChargeAccount c)
    {
        return findOrCreate(TChargeAccount.class, c);
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
    public void init()
    {
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

        generateClusters();
        // disableHibernateLogging();
    }

    @SuppressWarnings("unused")
    private void disableHibernateLogging()
    {
        // org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger("org.hibernate");
        // log.setLevel(org.apache.log4j.Level.DEBUG);
        // log = org.apache.log4j.Logger.getLogger("stdout");
        // log.setLevel(org.apache.log4j.Level.DEBUG);
    }

    public TCluster Nem_Adott()
    {
        return Nem_Adott;
    }

    public TCluster Egyeb_Kiadas()
    {
        return Egyeb_Kiadas;
    }

    public TCluster Napi_Szukseglet()
    {
        return Napi_Szukseglet;
    }

    public TCluster Szamolas()
    {
        return Szamolas;
    }

    public TCluster Athelyezes()
    {
        return Athelyezes;
    }

    public TCluster Szukseges()
    {
        return Szukseges;
    }

    public TMarket Market_Not_Applicable()
    {
        return Market_Not_Applicable;
    }

    public TChargeAccount none()
    {
        return none;
    }

    public TChargeAccount pkez()
    {
        return pkez;
    }

    @SuppressWarnings("unused")
    public void generateClusters()
    {
        /// @formatter:off
//	    TCluster Nem_Adott = cluster(new TCluster("Nem_Adott", 0, null));
//	    TCluster     Egyeb_Kiadas = cluster(new TCluster("Egyeb_Kiadas", -1, Nem_Adott));
//	    TCluster         Szukseges = cluster(new TCluster("Szukseges", Egyeb_Kiadas));
//	    TCluster             Napi_Szukseglet = cluster(new TCluster("Napi_Szukseglet", Szukseges));
	    TCluster                 Elelem = cluster(new TCluster("Elelem", Napi_Szukseglet));
	    TCluster             Extra_Szukseglet = cluster(new TCluster("Extra_Szukseglet", Szukseges));
	    TCluster                 Ruhazkodas = cluster(new TCluster("Ruhazkodas", Extra_Szukseglet));
	    TCluster                 Lakas_Berendezes = cluster(new TCluster("Lakas_Berendezes", Extra_Szukseglet));
	    TCluster                 Gyogyszer = cluster(new TCluster("Gyogyszer", Extra_Szukseglet));
	    TCluster                 Szukseges_Rossz = cluster(new TCluster("Szukseges_Rossz", Szukseges));
	    TCluster                     Javitasok = cluster(new TCluster("Javitasok", Szukseges_Rossz));
	    TCluster                         Rezsi = cluster(new TCluster("Rezsi", Szukseges_Rossz));
	    TCluster                             Rezsi_Bkv = cluster(new TCluster("Rezsi_Bkv", Rezsi));
	    TCluster                             Rezsi_Gaz = cluster(new TCluster("Rezsi_Gaz", Rezsi));
	    TCluster                             Rezsi_Viz = cluster(new TCluster("Rezsi_Viz", Rezsi));
	    TCluster                             Rezsi_Futes = cluster(new TCluster("Rezsi_Futes", Rezsi));
	    TCluster                             Rezsi_Elmu = cluster(new TCluster("Rezsi_Elmu", Rezsi));
	    TCluster                             Rezsi_Kozosk = cluster(new TCluster("Rezsi_Kozosk", Rezsi));
	    TCluster                             Rezsi_Upc = cluster(new TCluster("Rezsi_Upc", Rezsi));
	    TCluster                             Rezsi_Otp = cluster(new TCluster("Rezsi_Otp", Rezsi));
	    TCluster                     Alberlet = cluster(new TCluster("Alberlet", Szukseges_Rossz));
	    TCluster                     Kaucio = cluster(new TCluster("Kaucio", Szukseges_Rossz));
	    TCluster                     Telefon = cluster(new TCluster("Telefon", Extra_Szukseglet));
	    TCluster                         Mobil_Peti = cluster(new TCluster("Mobil_Peti", Telefon));
	    TCluster                         Mobil_Helga = cluster(new TCluster("Mobil_Helga", Telefon));
	    TCluster                         Mobil_Dori = cluster(new TCluster("Mobil_Dori", Telefon));
        TCluster                     Hivatalos = cluster(new TCluster("Hivatalos", Szukseges_Rossz));
	    TCluster             Munkaeszkozok = cluster(new TCluster("Munkaeszkozok", Szukseges));
	    TCluster                 Konyv = cluster(new TCluster("Konyv", Munkaeszkozok));
	    TCluster         Kellemes = cluster(new TCluster("Kellemes", Egyeb_Kiadas));
	    TCluster             Utazas = cluster(new TCluster("Utazas", Kellemes));
	    TCluster             Eskuvo = cluster(new TCluster("Eskuvo", Kellemes));
	    TCluster             Ajandek = cluster(new TCluster("Ajandek", Kellemes));
	    TCluster             Szorakozas = cluster(new TCluster("Szorakozas", Kellemes));
	    TCluster                 Sport = cluster(new TCluster("Sport", Szorakozas));
	    TCluster                 Szorakozas_Bal = cluster(new TCluster("Szorakozas_Bal", Szorakozas));
	    TCluster                 Szorakozas_Mozi = cluster(new TCluster("Szorakozas_Mozi", Szorakozas));
	    TCluster                 Szorakozas_Tanc = cluster(new TCluster("Szorakozas_Tanc", Szorakozas));
	    TCluster         Luxus = cluster(new TCluster("Luxus", Egyeb_Kiadas));
	    TCluster             Ekszer = cluster(new TCluster("Ekszer", Luxus));
	    TCluster         Vatikani = cluster(new TCluster("Vatikani", Egyeb_Kiadas));
	    TCluster         Elektr_Cikk = cluster(new TCluster("Elektr_Cikk", Egyeb_Kiadas));
	    TCluster         Kolcson = cluster(new TCluster("Kolcson", Egyeb_Kiadas));
	    TCluster     Egyeb_Bevetel = cluster(new TCluster("Egyeb_Bevetel", R.CLSGN_INCOME, Nem_Adott));
	    TCluster         Otthon = cluster(new TCluster("Otthon", Egyeb_Bevetel));
	    TCluster         JozsaOtthon = cluster(new TCluster("JozsaOtthon", Otthon));
	    TCluster         Fizetes = cluster(new TCluster("Fizetes", Egyeb_Bevetel));
	    TCluster         Osztondij = cluster(new TCluster("Osztondij", Egyeb_Bevetel));
	    TCluster         Kaucio_Vissza = cluster(new TCluster("Kaucio_Vissza", Egyeb_Bevetel));
	    TCluster         Maganora = cluster(new TCluster("Maganora", Fizetes));
	    TCluster         VisszaVasarlas = cluster(new TCluster("VisszaVasarlas", Egyeb_Bevetel));
//	    TCluster     Szamolas = cluster(new TCluster("Szamolas", 1, Nem_Adott));
//	    TCluster     Athelyezes = cluster(new TCluster("Athelyezes", -1, Nem_Adott));
	    /// @formatter:on	    
    }

}
