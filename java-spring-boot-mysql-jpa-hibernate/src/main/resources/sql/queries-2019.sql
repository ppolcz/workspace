
-- Ez egy nagyon hasznos lekerdezes (Kumulalt szummalva es ertek szerint csokkeno sorrendben - legnagyobb koltseg elol)
SET @@group_concat_max_len = 120;
set @sum := 0;
set @nrmonth := 12;
set @nrdays := 30 * @nrmonth;
select *
from ugyletek_szep
where
    date between curdate() - interval @nrdays day and curdate()
order by date;


-- Ez egy nagyon hasznos lekerdezes (Kumulalt szummalva es ertek szerint csokkeno sorrendben - legnagyobb koltseg elol)
SET @@group_concat_max_len = 120;
set @sum := 0;
set @nrmonth := 12;
set @nrdays := 30 * @nrmonth;
select date, caname, balance, lpad(format(amount,0), 8, ' ') as amount, clname, mkname, remark, lpad(format((@sum := @sum + amount),0), 8, ' ') as cumsum
from ugyletek_szep
where
    date between curdate() - interval @nrdays day and curdate()
    and amount < -10000
    and clname not like 'Befektetes'
    and caname like 'potp';
-- NAGY KIADASOK
-- +------------+--------+---------+----------+------------------+---------------+----------------------------------------------------------------------+----------+
-- | date       | caname | balance | amount   | clname           | mkname        | remark                                                               | cumsum   |
-- +------------+--------+---------+----------+------------------+---------------+----------------------------------------------------------------------+----------+
-- | 2019-01-06 | potp   | 1678833 |  -26,736 | Kolcson          | Anyaek        | Tesco utazas [-26699-37] { tr_2 }                                    |  -26,736 |
-- | 2019-01-15 | potp   | 1961266 |  -27,163 | Auto             | TerezvarosOnk | Parkolas 6. kerulet 2019 { tr_2 }                                    |  -53,899 |
-- | 2019-01-21 | potp   | 1915635 |  -34,470 | Ajandek          | Decathlon     | Lackonak roller es bukosisak { tr_2 }                                |  -88,369 |
-- | 2019-02-04 | potp   | 1792994 |  -50,037 | Vatikani         | Tuzeset       | tuzeset [-50000-37] { tr_6 }                                         | -138,406 |
-- | 2019-02-04 | potp   | 1779954 |  -13,040 | Lakas_Berendezes | IKEA          | sok kicsi { tr_7 }                                                   | -151,446 |
-- | 2019-02-06 | potp   | 1752499 |  -14,038 | Sk_Benzin        | Mol           | 343.9 Ft, 40.82, 8, 89198, Mol Egerben { tr_2 }                      | -165,484 |
-- | 2019-02-08 | potp   | 1915889 |  -10,900 | Szallas          | Minaret       | Hotel Minaret, Eger, 1 ejszaka { tr_3 }                              | -176,384 |
-- | 2019-02-08 | potp   | 1885203 |  -29,468 | Napi_Szukseglet  | Auchan        | kaja a burgundi hetvegere { tr_5 }                                   | -205,852 |
-- | 2019-03-01 | potp   | 1821216 |  -83,538 | Elektr_Cikk      | MediaMarkt    | Huawei P20 lite { tr_2 }                                             | -289,390 |
-- | 2019-03-01 | potp   | 1802760 |  -14,990 | Lakas_Berendezes | Multileder    | Kezi podgyas borond, WestEnd { tr_5 }                                | -304,380 |
-- | 2019-03-05 | potp   | 1745665 |  -25,294 | Napi_Szukseglet  | Nem_Adott     | PRESSBYRgN 4028116 79,320EUR 318,8900 2019.03.04 8926861904 { tr_6 } | -329,674 |
-- | 2019-03-06 | potp   | 1684296 |  -60,619 | Szallas          | Spar_Hotel    | { tr_2 }                                                             | -390,293 |
-- | 2019-04-15 | potp   | 2600004 |  -12,990 | Ruhazkodas       | Decathlon     | Futocipo { tr_5 }                                                    | -403,283 |
-- | 2019-04-16 | potp   | 2585485 |  -14,519 | Sk_Benzin        | OMV           | 397.9 Ft, 36.49, 8, 89780, Szatmarra menes elott { tr_2 }            | -417,802 |
-- | 2019-04-16 | potp   | 2573683 |  -10,698 | Gyogyszer        | BenuPatika    | Elevit, Magnerot (4199+6499) { tr_5 }                                | -428,500 |
-- | 2019-04-24 | potp   | 2473385 |  -98,738 | Autobizt         | Uniqa         | Kotelezo autobiztositas { tr_3 }                                     | -527,238 |
-- | 2019-05-08 | potp   | 2492911 |  -12,463 | Gyogyszer        | BenuPatika    | Vas es Femibion 2havi (1828+10635) { tr_3 }                          | -539,701 |
-- | 2019-05-26 | potp   | 2495741 |  -14,600 | Gyogyszer        | EgeszsegPlaza | Sport Restart es Endurance { tr_3 }                                  | -554,301 |
-- | 2019-05-27 | potp   | 2386664 |  -33,500 | Nogyogyasz       | RMC           | Dr. Gullai Nora { tr_8 }                                             | -587,801 |
-- | 2019-05-30 | potp   | 2371013 |  -10,490 | Gyogyszer        | BenuPatika    | etrendkiegeszitok { tr_6 }                                           | -598,291 |
-- | 2019-06-07 | potp   | 2308031 |  -12,690 | Egeszseg         | Thuasne       | Tertrogzito { tr_4 }                                                 | -610,981 |
-- | 2019-06-18 | potp   | 2522773 |  -16,200 | Szallas          | Gyula         | Polcz Dorottya, aug 10-16, eloleg { tr_3 }                           | -627,181 |
-- | 2019-06-18 | potp   | 2501506 |  -21,000 | Utazas           | MKK           | Kozitabor { tr_5 }                                                   | -648,181 |
-- | 2019-06-24 | potp   | 2469743 |  -10,790 | Gyogyszer        | KorondPatika  | { tr_10 }                                                            | -658,971 |
-- | 2019-07-01 | potp   | 2456005 |  -14,845 | Napi_Szukseglet  | Mol           | { tr_5 }                                                             | -673,816 |
-- | 2019-07-04 | potp   | 2628531 |  -10,944 | Napi_Szukseglet  | Aldi          | { tr_3 }                                                             | -684,760 |
-- | 2019-07-08 | potp   | 2873733 |  -13,412 | Sk_Benzin        | Mol           | 393.9 Ft, 34.05, 8, 92029, Budapesten { tr_3 }                       | -698,172 |
-- | 2019-08-01 | potp   |   49834 | -200,000 | Baba             | Brendon       | Babakocsi (elso reszlet) { tr_3 }                                    | -898,172 |
-- | 2019-08-20 | potp   |  266757 |  -22,608 | Elektr_Cikk      | Lidl          | Furo kalapacs (16900 Ft) { tr_3 }                                    | -920,780 |
-- | 2019-08-20 | potp   |  253874 |  -12,883 | Sk_Benzin        | Mol           | 378.9 Ft, 34, 8, TODO, Budapesten Alsoorsrol jottunk { tr_4 }        | -933,663 |
-- | 2019-08-21 | potp   |  223780 |  -26,866 | Napi_Szukseglet  | Tesco         | Burgundi7V { tr_4 }                                                  | -960,529 |
-- | 2019-08-23 | potp   |  185286 |  -11,800 | Auto_Mosas       | Homasita      | Polirozos cuccok { tr_3 }                                            | -972,329 |
-- | 2019-09-02 | potp   |  133731 |  -19,156 | Gyogyszer        | KorondPatika  | Vitaminok, magnezium, Femibion { tr_4 }                              | -991,485 |
-- | 2019-09-04 | potp   |   86182 |  -15,740 | Napi_Szukseglet  | Decathlon     | { tr_5 }                                                             | -1,007,2 |
-- | 2019-09-13 | potp   |  201587 |  -24,602 | Napi_Szukseglet  | Lidl          | { tr_5 }                                                             | -1,031,8 |
-- +------------+--------+---------+----------+------------------+---------------+----------------------------------------------------------------------+----------+

-- AUTORA ENNYIT
-- 2019.09.30. (szeptember 30, hétfő), 11:44
select sum(amount) from ugyletek_szep where clname like 'Auto%';
-- +-------------+
-- | sum(amount) |
-- +-------------+
-- |     -166581 |
-- +-------------+


-- 2019.09.30. (szeptember 30, hétfő), 10:39
SET @@group_concat_max_len = 200;
set @nrmonth := 12;
set @nrdays := 30 * @nrmonth;
select
    clname,
    sum(amount) as sum,
    group_concat(DISTINCT mkname separator ', ') as mkname
from ugyletek_szep
where
    date between curdate() - interval @nrdays day and curdate()
    and amount < 0
    and clname not like 'Befektetes'
    and clname not like 'Kolcson'
    and clname not like 'Athelyezes'
group by clname
order by sum;

-- PELDA LEKERDEZVE: 2019.09.30. (szeptember 30, hétfő), 10:39
-- +------------------+---------+------------------------------------------------------------------------------------------------------+
-- | clname           | sum     | mkname                                                                                               |
-- +------------------+---------+------------------------------------------------------------------------------------------------------+
-- | Napi_Szukseglet  | -966646 | ABC, Aldi, Auchan, Belfrit, Bellabolt, BestBakery, BiteBakary, BlackSheep, BleraPekseg, CafeFrei, Ca | -- Ez nem olyan eget rengetoen sok
-- | Baba             | -323141 | Babaszafari, Brendon, Maganszemely                                                                   |
-- | Elektr_Cikk      | -319693 | AquaElectromax, ExtremeDigital, LaptopSzalon, Lidl, MediaMarkt, Rufusz                               |
-- | Nogyogyasz       | -160500 | Bakos_Marcell, DeakTeren, RMC                                                                        |
-- | Gyogyszer        | -159893 | BenuPatika, BioABC, EgeszsegPlaza, KorondPatika, MozsonyiPatika, OktogonPatika, SzigonyPatika        |
-- | Szallas          | -133119 | Almos, Gyula, Minaret, Spar_Hotel                                                                    |
-- | Ruhazkodas       | -130575 | C&A, Decathlon, Maganszemely, Not_Applicable, Pepco, Tezenis                                         |
-- | Szamolas         | -101930 | Not_Applicable                                                                                       |
-- | Autobizt         |  -98738 | Uniqa                                                                                                |
-- | Utazas           |  -86904 | Flygbussarna, MAV, MKK, Vasttrafik                                                                   |
-- | Sk_Benzin        |  -85737 | Mol, OMV                                                                                             |
-- | Rezsi_Bkv        |  -72500 | BKV                                                                                                  |
-- | Ajandek          |  -56514 | Decathlon, Dm, Remo-Hobby, Spar                                                                      |
-- | Vatikani         |  -55037 | CitizenGo, Tuzeset                                                                                   |
-- | Lakas_Berendezes |  -40865 | Duralbau, IKEA, Multileder, TigerSt                                                                  |
-- | Egeszseg         |  -30297 | BioABC, Dm, Kozmetikus, PocziKft, Thuasne, YvesRocher                                                |
-- | Auto             |  -27163 | TerezvarosOnk                                                                                        |
-- | Mobil_Peti       |  -25000 | Telekom                                                                                              |
-- | Auto_Ado         |  -23100 | Allambacsi                                                                                           |
-- | Munkaeszkozok    |  -23030 | Goldbox, Gravir, Pirex                                                                               |
-- | Mobilfizetes     |  -18040 | Not_Applicable                                                                                       |
-- | Auto_Mosas       |  -17580 | Homasita, IMO                                                                                        |
-- | Levonas          |  -16897 | OTP_BANK                                                                                             |
-- | Szorakozas       |  -15300 | Grund, Gyula                                                                                         |
-- | Luxus            |  -15000 | Csoma_Erno                                                                                           |
-- | Mobil_Dori       |  -15000 | Telekom                                                                                              |
-- | Tabor            |  -10000 | Burgundi                                                                                             |
-- | Sport            |   -8690 | Decathlon, Futanet                                                                                   |
-- | Konyv            |   -4080 | Ecclesia, Maganszemely                                                                               |
-- | Tarsasjatek      |   -3299 | Jatekvar                                                                                             |
-- | Hivatalos        |   -1695 | Posta                                                                                                |
-- +------------------+---------+------------------------------------------------------------------------------------------------------+






-- 2019.09.30. (szeptember 30, hétfő), 10:39
SET @@group_concat_max_len = 200;
set @nrmonth := 12;
set @nrdays := 30 * @nrmonth;
select
    caname,
    sum(amount) as sum
from ugyletek_szep
where
    date between curdate() - interval @nrdays day and curdate()
    and amount < 0
    and clname not like 'Befektetes'
    and clname not like 'Kolcson'
    and clname not like 'Athelyezes'
group by caname;

-- 2019.09.30. (szeptember 30, hétfő), 11:07
-- +--------+----------+
-- | caname | sum      |
-- +--------+----------+
-- | dotp   |    -5359 |
-- | pkez   |  -944933 |
-- | potp   | -2095671 |
-- +--------+----------+



-- 2019.09.30. (szeptember 30, hétfő), 10:39
SET @@group_concat_max_len = 100;
set @nrmonth := 12;
set @nrdays := 30 * @nrmonth;
select
    mkname,
    sum(amount) as sum,
    replace(replace(group_concat(DISTINCT remark separator ', '), '{ tr_', 't'), ' }', '') as remark
from ugyletek_szep
where
    date between curdate() - interval @nrdays day and curdate()
    and clname like 'Napi_Szukseglet'
group by mkname
order by sum;

-- +-----------------+---------+-------------------------------------------------------------------------------------------+
-- | mkname          | sum     | remark                                                                                    |
-- +-----------------+---------+-------------------------------------------------------------------------------------------+
-- | Lidl            | -245831 | Ir kremlikor, mandula, korrektor, sok sajt t5, kaja az MKK-s bálra t2, kaja t3, ka        |
-- | Aldi            | -133457 | ALDI 65.SZ -ÉRINT?  2019.07.06 6618165120 t5, ALDI 65.SZ PPASS  2019.03.04 9295045248 t3  |
-- | Spar            |  -82342 | hagymas cipo t5, kaja t2, kaja t3, kaja t4, kaja t5, kaja t6, ka                          |
-- | Nem_Adott       |  -57435 | ALDI 65.SZ PPASS  2019.03.05 9295045248 t5, Dori kb ennyi ertekben vasarolt t2, ebed { t  |
-- | Gyula           |  -55000 | SZEP kartyas eszem-iszom (Klarinak visszafizetve) t2                                      |
-- | Auchan          |  -45819 | bor az Auchanbol t5, kaja a burgundi hetvegere t5, kaja t4, t9                            |
-- | Tesco           |  -40667 | Burgundi7V hus t3, Burgundi7V hus t7, Burgundi7V hus t8, Burgundi7V t4, { tr              |
-- | Decathlon       |  -18620 | t3, t5, t7                                                                                |
-- | Dm              |  -18404 | DM nem tudom t3, oblito t4, t2, t3, t4, t6, t7                                            |
-- | Mol             |  -15762 | 200. UZEMANYAGTO - 1925795 t4, kaja t3, t5                                                |
-- | Sommer          |  -13460 | SOMMER CUKRASZDA - 1934122 t5, torta Almosnak es Aginak t2, t2, t3, t6                    |
-- | Cafe_Linsen     |  -12896 | ebed t2, ebed t5                                                                          |
-- | FecskePresszo   |  -12180 | Helgaekkal kaja t6, t3                                                                    |
-- | StarKebab       |  -12100 | ebed t2, Gyros durum 2 db t4, gyros t2, Gyros t4, kaja t3, Star Kebab                     |
-- | Einstein        |  -10911 | t2, t4                                                                                    |
-- | IKEA_FOOD       |  -10870 | ebed t5, kaja [-2770-410] t9, kaja t3, t4, t5                                             |
-- | Hemkop          |  -10139 | kaja t2, t7                                                                               |
-- | IKEA            |   -8455 | serpenyo, egyeb kicsi dolog t2                                                            |
-- | Wikinger        |   -7070 | ebed t3, King-kong XL menu t6, t3                                                         |
-- | Muller          |   -6970 | t3, t7, t8                                                                                |
-- | CafeFrei        |   -6220 | kaja t4, kave t2, t4, t5, t7, t9                                                          |
-- | Sega_Gubben     |   -6210 | edesseg t2                                                                                |
-- | Joasszony       |   -5950 | JOASSZONY GYORSE - 1921395 t3, kaja t4                                                    |
-- | LeBar           |   -5698 | t4                                                                                        |
-- | Mcd             |   -5670 | t3                                                                                        |
-- | JacksBurger     |   -5370 | JACKS BURGER WES - 1906588 t3, kaja t4                                                    |
-- | Penny           |   -5317 | kaja a burgundi hetvegere t5, t6                                                          |
-- | TOBB            |   -5000 | kaja az elmult ket heten t4                                                               |
-- | Nokia_Kantin    |   -4899 | ebed t3, NOKIA ÉTTEREM PP - 1926403 t3, t4, t5                                            |
-- | KFC             |   -4630 | kaja t2, kaja t6, kaja t7                                                                 |
-- | Gyula_Kaja      |   -4570 | t5                                                                                        |
-- | PPKE_ITK        |   -4200 | Utolagos passzivaltatasi kerelem t4                                                       |
-- | Coop            |   -3633 | kaja t4, t3                                                                               |
-- | EzWorks         |   -3450 | ebed t6                                                                                   |
-- | ZingBurger      |   -3330 | 2 burger t7                                                                               |
-- | Kerova          |   -3120 | ebed t2                                                                                   |
-- | CorvinPlaza     |   -3010 | t2, t4                                                                                    |
-- | JonoYogo        |   -2822 | fagyi t4, t7                                                                              |
-- | GorogGyros      |   -2700 | ebed t2, gorog gyros t2                                                                   |
-- | Izlelo          |   -2590 | t2, t3                                                                                    |
-- | Not_Applicable  |   -2582 | kaja t8, [-175-199] t8, t4, t8                                                            |
-- | NokiaEtterem    |   -2475 | kaja t4, t3                                                                               |
-- | Cba             |   -2467 | CBA PRÍMA ANDRÁS - 1944416 t4, kaja t2, kaja t3, kaja t6, kaja t8                         |
-- | PrincessBakery  |   -2319 | kaja t3, kaja t5, Princess Bakery & B -ÉRINT?  2019.05.30 9295045248 t2, t3,              |
-- | FalankFanny     |   -2260 | ebed Egerben [-275-1985] t3                                                               |
-- | Fragola         |   -2050 | fagyi t2                                                                                  |
-- | Istanbul        |   -1950 | t5                                                                                        |
-- | Pepco           |   -1940 | t4                                                                                        |
-- | Real            |   -1918 | t2, t8                                                                                    |
-- | Diego           |   -1817 | t6                                                                                        |
-- | IlTreno         |   -1790 | t4                                                                                        |
-- | Harom_Sas       |   -1740 | HAROM SAS BT. PP - 1945124 t3                                                             |
-- | Telepocak       |   -1620 | ebed t3                                                                                   |
-- | DezsoBa         |   -1500 | t3                                                                                        |
-- | Corvin-Kebab    |   -1490 | gyros t6                                                                                  |
-- | LipotiPekseg    |   -1488 | t3, t6                                                                                    |
-- | NagyiPali       |   -1480 | t3                                                                                        |
-- | BleraPekseg     |   -1360 | t6                                                                                        |
-- | ITK_kantin      |   -1359 | kaja [-250-500-199-160] t2, szenyo t3                                                     |
-- | Gyrosos         |   -1340 | t2, t4                                                                                    |
-- | Mazsa           |   -1218 | t10                                                                                       |
-- | BestBakery      |   -1210 | t4                                                                                        |
-- | Dieta           |   -1209 | kaja t4                                                                                   |
-- | Kinai           |   -1190 | kaja t5, Vushtrri Kft PPA - 1930785 t3                                                    |
-- | Belfrit         |   -1190 | Belfrit Etterem  - 1932932 t3                                                             |
-- | Vegyes          |   -1146 | aprosagok [-149-80-409-308-200] t2                                                        |
-- | ChiliCsemege    |   -1125 | t4                                                                                        |
-- | Kingo           |   -1080 | t2                                                                                        |
-- | Millaheart      |   -1063 | t5                                                                                        |
-- | MKK             |   -1000 | agapera t2                                                                                |
-- | BiteBakary      |   -1000 | t3                                                                                        |
-- | Maganszemely    |   -1000 | Cabarnet Savignon t2                                                                      |
-- | KekFazek        |    -990 | kaja t3                                                                                   |
-- | Kiallitas       |    -968 | t3                                                                                        |
-- | Ecclesia        |    -870 | t4                                                                                        |
-- | RacNora         |    -790 | valami t7                                                                                 |
-- | Star_Kebab      |    -750 | kaja t8                                                                                   |
-- | Sugar           |    -748 | kaja t4                                                                                   |
-- | Nyugati         |    -665 | Szilardekhoz mentuk akkor, zsomle, peksuti t2                                             |
-- | Dilek           |    -658 | kaja t2                                                                                   |
-- | Puskin_Mozi     |    -600 | kokas t2                                                                                  |
-- | Cserpes         |    -600 | kave t2                                                                                   |
-- | Bellabolt       |    -525 | t5                                                                                        |
-- | BlackSheep      |    -500 | pia t3                                                                                    |
-- | ThermalFalatozo |    -450 | THERMAL FALATOZO - 1930335 t4                                                             |
-- | ABC             |    -259 | t3                                                                                        |
-- | Haris           |    -120 | nem tudom t5                                                                              |
-- +-----------------+---------+-------------------------------------------------------------------------------------------+




-- 2019.09.30. (szeptember 30, hétfő), 10:39
SET @@group_concat_max_len = 200;
set @nrmonth := 12;
set @nrdays := 30 * @nrmonth;
select
    mkname,
    amount,
    remark
from ugyletek_szep
where
    date between curdate() - interval @nrdays day and curdate()
    and clname like 'Napi_Szukseglet'
order by mkname, amount;






-- BEVETEL
-- 2019.09.30. (szeptember 30, hétfő), 10:39
SET @@group_concat_max_len = 200;
set @nrmonth := 12;
set @nrdays := 30 * @nrmonth;
select
    caname,
    sum(amount) as sum,
    group_concat(DISTINCT mkname separator ', ') as mkname
from ugyletek_szep
where
    date between curdate() - interval @nrdays day and curdate()
    and amount > 0
group by caname;

-- +--------+---------+------------------------------------------------------------------------+
-- | caname | sum     | mkname                                                                 |
-- +--------+---------+------------------------------------------------------------------------+
-- | pkez   |  936740 | Burgundi, Feco, Helga, Koli, Kristof, Lacko, Lidl, MKK, Not_Applicable |
-- | potp   | 3355632 | BKV, BME_TTK, NAV, OTP_BANK, PPKE_ITK                                  |
-- +--------+---------+------------------------------------------------------------------------+


-- OTTHONROL ENNYIT
select * from ugyletek_szep where caname like 'pkez' and amount > 0 and clname not like '%Vissza%' and clname not like 'Szamolas' and clname not like 'Jozsa%';
-- +------------+---------+--------+--------+--------+----------------+-----------------------------------+-------+
-- | date       | balance | caname | amount | clname | mkname         | remark                            | pivot |
-- +------------+---------+--------+--------+--------+----------------+-----------------------------------+-------+
-- | 2019-02-05 |   60005 | pkez   |  30000 | Otthon | Not_Applicable | Amikor Egerben voltunk { tr_1 }   |     0 |
-- | 2019-06-14 |   62699 | pkez   |  50000 | Otthon | Not_Applicable | { tr_1 }                          |     0 |
-- | 2019-08-10 |  357548 | pkez   | 300000 | Otthon | Not_Applicable | Anyaektol kaptunk ennyit { tr_1 } |     0 |
-- +------------+---------+--------+--------+--------+----------------+-----------------------------------+-------+





-- BEVETEL
-- 2019.09.30. (szeptember 30, hétfő), 10:39
SET @@group_concat_max_len = 200;
set @nrmonth := 12;
set @nrdays := 30 * @nrmonth;
select *
from ugyletek_szep
where
    date between curdate() - interval @nrdays day and curdate()
    and (clname like 'Osztondij' or clname like 'Fizetes');

-- +------------+---------+--------+--------+-----------+----------+------------------------------------------------------------+
-- | date       | balance | caname | amount | clname    | mkname   | remark                                                     |
-- +------------+---------+--------+--------+-----------+----------+------------------------------------------------------------+
-- | 2019-01-08 | 1757405 | potp   |  82022 | Fizetes   | PPKE_ITK | tanitasra (gondolom) { tr_1 }                              |
-- | 2019-01-11 | 1814031 | potp   |  85000 | Osztondij | PPKE_ITK | EFOP-3.6.3-VEKOP-16-2017-00002 UJ (5/6, dec.) { tr_1 }     |
-- | 2019-01-12 | 1991963 | potp   | 180555 | Osztondij | PPKE_ITK | doktorandusz osztondij { tr_1 }                            |
-- | 2019-02-08 | 1930239 | potp   | 180000 | Osztondij | PPKE_ITK | doktorandusz osztondij { tr_1 }                            |
-- | 2019-02-12 | 1960224 | potp   |  85000 | Osztondij | PPKE_ITK | EFOP-3.6.3-VEKOP-16-2017-00002 UJ (6/6, jan.) { tr_1 }     |
-- | 2019-03-05 | 1774414 | potp   |    680 | Osztondij | PPKE_ITK | { tr_1 }                                                   |
-- | 2019-03-07 | 1859344 | potp   | 180680 | Osztondij | PPKE_ITK | { tr_1 }                                                   |
-- | 2019-03-09 | 1936800 | potp   |  85000 | Osztondij | PPKE_ITK | EFOP-3.6.3-VEKOP-16-2017-00002 UJ (1/5, februar.) { tr_1 } |
-- | 2019-03-19 | 2366553 | potp   | 500000 | Osztondij | PPKE_ITK | UNKP { tr_1 }                                              |
-- | 2019-04-05 | 2400383 | potp   | 160425 | Osztondij | PPKE_ITK | Sved kiutazas napi dija (KAP) { tr_1 }                     |
-- | 2019-04-08 | 2480468 | potp   |  85000 | Osztondij | PPKE_ITK | EFOP-3.6.3-VEKOP-16-2017-00002 UJ (2/5,marcius.) { tr_1 }  |
-- | 2019-04-09 | 2657165 | potp   | 180680 | Osztondij | PPKE_ITK | { tr_1 }                                                   |
-- | 2019-05-08 | 2505374 | potp   | 180680 | Osztondij | PPKE_ITK | { tr_1 }                                                   |
-- | 2019-05-10 | 2564720 | potp   |  85000 | Osztondij | PPKE_ITK | EFOP-3.6.3-VEKOP-16-2017-00002 UJ (3/5,aprilis.) { tr_1 }  |
-- | 2019-06-09 | 2480732 | potp   | 180680 | Osztondij | PPKE_ITK | { tr_1 }                                                   |
-- | 2019-06-10 | 2562735 | potp   |  85000 | Osztondij | PPKE_ITK | EFOP-3.6.3-VEKOP-16-2017-00002 UJ (4/5,majus) { tr_1 }     |
-- | 2019-06-27 | 2477412 | potp   |  20000 | Fizetes   | PPKE_ITK | robomatika tabori tanitas (EFOP 3.4.6?) { tr_1 }           |
-- | 2019-07-03 | 2704787 | potp   | 256319 | Osztondij | BME_TTK  | OTKA 125739, III. Felev { tr_1 }                           |
-- | 2019-07-05 | 2709007 | potp   |  85000 | Osztondij | PPKE_ITK | EFOP-3.6.3-VEKOP-16-2017-00002 UJ (5/5,junius) { tr_1 }    |
-- | 2019-07-08 | 2887145 | potp   | 180680 | Osztondij | PPKE_ITK | { tr_1 }                                                   |
-- | 2019-08-06 |  124000 | potp   |  85000 | Osztondij | PPKE_ITK | { tr_1 }                                                   |
-- | 2019-08-07 |  304680 | potp   | 180680 | Osztondij | PPKE_ITK | { tr_1 }                                                   |
-- | 2019-09-09 |  257831 | potp   | 180680 | Osztondij | PPKE_ITK | { tr_4 }                                                   |
-- +------------+---------+--------+--------+-----------+----------+------------------------------------------------------------+






-- BEVETEL
-- 2019.09.30. (szeptember 30, hétfő), 10:39
SET @@group_concat_max_len = 200;
set @nrmonth := 12;
set @nrdays := 30 * @nrmonth;
select sum(amount) as sum
from ugyletek_szep
where
    date between curdate() - interval @nrdays day and curdate()
    and (clname like 'Osztondij' or clname like 'Fizetes');

-- Oszrondij es Fizetes
-- 2019.09.30. (szeptember 30, hétfő), 11:27
-- +-----------+
-- | sum       |
-- +-----------+
-- | 3.324.761 |
-- +-----------+


-- GRAFIKON
-- 2019.09.30. (szeptember 30, hétfő), 11:30
select balance,amount from ugyletek_szep where caname like 'potp';
