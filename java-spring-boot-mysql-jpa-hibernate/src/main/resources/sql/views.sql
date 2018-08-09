-- UGYLETEK SZEP



drop function if exists p1;
create function p1() returns INTEGER DETERMINISTIC NO SQL return @p1;

drop view if exists utolso_ugyletek;
create view utolso_ugyletek as
select date, accounts.name as caname, balance, amount*clusters.sgn as amount, clusters.name as clname, markets.name as mkname, remark
from ugyletek,markets,clusters,accounts
where
    cluster = clusters.uid and market = markets.uid and ca = accounts.uid
    and date between curdate() - interval p1() day and curdate()
    and clusters.name not like 'athelyezes'
order by amount;

drop view if exists ugyletek_mind;
create view ugyletek_mind as
select date, balance, `from`.name as ca, amount*clusters.sgn as amount, `to`.name as catransfer, clusters.name as cluster, markets.name as market, remark
from ugyletek,markets,clusters,accounts as `from`, accounts as `to`
where
    cluster = clusters.uid and market = markets.uid and ca = `from`.uid and catransfer = `to`.uid
order by date, ugyletek.uid;

drop view if exists ugyletek_szep;
create view ugyletek_szep as
select date, balance, accounts.name as caname, amount*clusters.sgn as amount, clusters.name as clname, markets.name as mkname, remark
from ugyletek,markets,clusters,accounts
where
    cluster = clusters.uid and market = markets.uid and ca = accounts.uid
    and clusters.name not like 'athelyezes'
order by date, ugyletek.uid;



-- SKODA


-- 2018.07.18. (július 18, szerda), 22:13
-- Skoda benzin, remark: <egysegar> <valuta>, <liter>, <benzin>, <kmallas>, <helyszin - remark>
drop view if exists Sk_Benzin;
create view Sk_Benzin as select
    date,
    amount,
    substring_index(remark,',',1) + 0.0 as `egysegar`,
    substring_index(substring_index(remark,',',1),' ',-1) as `valuta`,
    substring_index(substring_index(remark,',',2),',',-1) + 0.0 as `liter`,
    substring_index(substring_index(remark,',',3),',',-1) + 0.0 as `benzin`,
    substring_index(substring_index(remark,',',4),',',-1) + 0 as `kmallas`,
    if( mkname like 'Not_Applicable', '', mkname) as `mkname`,
    trim(substr(replace(remark, substring_index(remark,',',4), ''), 2)) as `remark`,
    balance,
    caname,
    clname
from ugyletek_szep where clname like 'Sk_Benzin';

-- 2018.07.18. (július 18, szerda), 22:13
-- Skoda szerviz
drop view if exists Sk_Szerviz;
create view Sk_Szerviz as select
    date,
    amount,
    0 as `egysegar`,
    'Ft' as `valuta`,
    0 as `liter`,
    substring_index(remark,',',1) + 0.0 as `benzin`,
    substring_index(substring_index(remark,',',2),',',-1) + 0 as `kmallas`,
    if( mkname like 'Not_Applicable', '', mkname),
    trim(substr(replace(remark, substring_index(remark,',',2), ''), 2)) as `remark`,
    balance,
    caname,
    clname
from ugyletek_szep where clname like 'Sk_Szerviz';

-- 2018.07.18. (július 18, szerda), 22:13
-- Sk_Benzin es Sk_Szerviz osszefuzve
drop view if exists Sk;
create view Sk as (select * from Sk_Benzin) union all (select * from Sk_Szerviz) order by kmallas;

-- 2018.07.18. (július 18, szerda), 22:13
-- Km allas vasarlaskor
drop view if exists Sk_kmallas_vasarlaskor;
create view Sk_kmallas_vasarlaskor as select min(kmallas) as `kmallas_vasarlaskor` from Sk;
-- select * from Sk;

-- 2018.07.18. (július 18, szerda), 22:13
-- Skoda km kulonbsegek is benne vannak
drop view if exists Sk_diff;
create view Sk_diff as select
    date,
    if(valuta like 'RON', amount*70, amount) as `amount_Ft`,
    amount,
    if(valuta like 'RON', egysegar*70, egysegar) as `egysegar_Ft`,
    egysegar,
    if(valuta like '0' or valuta like 'Ft', 'HUF', valuta) as `valuta`,
    liter, benzin, kmallas,
    Sk0.kmallas-(select Sk1.kmallas from Sk as `Sk1` where Sk1.kmallas < Sk0.kmallas order by Sk1.kmallas desc limit 1) as `kmdiff`,
    Sk0.kmallas-(select * from Sk_kmallas_vasarlaskor) as `kmdiffcum`,
    mkname, remark, balance, caname, clname
from Sk as `Sk0` order by kmallas;

-- 2018.08.09. (augusztus  9, csütörtök), 22:22
drop view if exists Sk_Tankolas_;
create view Sk_Tankolas_ as select
    date, amount, egysegar, liter, benzin, kmallas,
    Sk0.kmallas-(select Sk1.kmallas from Sk as `Sk1` where Sk1.kmallas < Sk0.kmallas and Sk1.liter > 0 order by Sk1.kmallas desc limit 1) as `kmdiff`,
    Sk0.kmallas-(select * from Sk_kmallas_vasarlaskor) as `kmdiffcum`,
    mkname, remark, balance, caname, clname
from Sk as `Sk0` where Sk0.liter > 0 order by kmallas;

-- 2018.08.09. (augusztus  9, csütörtök), 22:22
drop view if exists Sk_Tankolas;
create view Sk_Tankolas as select
    date,
    benzin, kmallas,
    amount,
    egysegar,
    liter,
    if(isnull(kmdiff),0,kmdiff) as `kmdiff`,
    if(isnull(kmdiff),0,round(liter/kmdiff*100,2)) as `fogyasztas`,
    kmdiffcum,
    mkname, remark, balance, caname, clname
from Sk_Tankolas_;

-- 2018.08.09. (augusztus  9, csütörtök), 22:22
drop view if exists Sk_separator;
create view Sk_separator as select
    '' as `date`,
    '' as `amount_Ft`,
    '' as `amount`,
    '' as `egysegar_Ft`,
    '' as `egysegar`,
    '' as `valuta`,
    '' as `liter`,
    '' as `benzin`,
    '' as `kmallas`,
    '' as `kmdiff`,
    '' as `kmdifftank`,
    '' as `fogyasztas`,
    '' as `kmdiffcum`,
    '' as `mkname`,
    '' as `remark`,
    '' as `balance`,
    '' as `caname`,
    '' as `clname`;

-- 2018.08.09. (augusztus  9, csütörtök), 22:22
drop view if exists Sk_sum;
create view Sk_sum as select
    concat('SUM ', clname) as `date`,
    sum(amount_Ft) as `amount_Ft`,
    '' as `amount`,
    if(sum(liter) = 0, '', round(sum(egysegar_Ft*liter)/sum(liter),2)) as `egysegar_Ft`,
    '' as `egysegar`,
    '' as `valuta`,
    if(sum(liter) = 0, '', round(sum(liter),2)) as `liter`,
    '' as `benzin`,
    '' as `kmallas`,
    '' as `kmdiff`,
    '' as `kmdifftank`,
    '' as `fogyasztas`,
    '' as `kmdiffcum`,
    '' as `mkname`,
    '' as `remark`,
    '' as `balance`,
    '' as `caname`,
    '' as `clname`
    from Sk_diff group by clname;


-- 2018.07.18. (július 18, szerda), 22:13
-- Skoda szerviz es benzin szep lekerdezes
drop view if exists Sk_fancy;
create view Sk_fancy as ( select
    date,
    if(amount_Ft = 0, '', amount_Ft) as `amount_Ft`,
    if(amount = 0, '', amount) as `amount`,
    if(egysegar_Ft = 0, '', egysegar_Ft) as `egysegar_Ft`,
    if(egysegar = 0, '', egysegar) as `egysegar`,
    valuta,
    if(liter = 0, '', liter) as `liter`,
    benzin, kmallas,
    if(isnull(kmdiff),'',kmdiff) as `kmdiff`,
    if(liter > 0, (select kmdiff from Sk_Tankolas where Sk_Tankolas.kmallas = Sk_diff.kmallas), '') as `kmdifftank`,
    if(liter > 0, (select fogyasztas from Sk_Tankolas where Sk_Tankolas.kmallas = Sk_diff.kmallas), '') as `fogyasztas`,
    kmdiffcum,
    mkname, remark, balance, caname, clname
from Sk_diff )
union all ( select * from Sk_separator )
union all ( select * from Sk_sum );
-- select * from Sk_fancy;

-- 2018.08.09. (augusztus  9, csütörtök), 22:37
drop view if exists Sk_fancy_min;
create view Sk_fancy_min as select
    date, amount_Ft, egysegar_Ft, egysegar, valuta, liter, mkname, kmallas, kmdiff, kmdifftank, fogyasztas, kmdiffcum, remark, clname
from Sk_fancy;
