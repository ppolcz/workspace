
-- INTO OUTFILE '/tmp/Eskuvo_koltsegei.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n'

-- drop table products, ugyletek, transactions, clusters, markets;


select u.* from (select @p1:=30 p) param, utolso_ugyletek u;

-- OTP-vel valo osszevetes vegett az utolso 30 nap
-- 2018.06.28. (június 28, csütörtök), 21:30
select u.* from (select @p1:=30 p) param, utolso_ugyletek u where caname like 'potp' and not clname like 'Szamolas' order by date asc;


-- Burgundi lekérdezés
-- 2018.03.03. (március  3, szombat), 15:38
select * from ugyletek_mind where (cluster like 'Burgundi' or catransfer like 'dkez') and date > '2017.09.01' order by date;


select sum(amount), cluster from ugyletek_mind where (cluster like 'Burgundi' or catransfer like 'dkez') and date > '2017.09.01' group by cluster;

-- Ez egy nagyon hasznos lekerdezes (Kumulalt szummalva es ertek szerint csokkeno sorrendben - legnagyobb koltseg elol)
SET @@group_concat_max_len = 120;
set @sum := 0;
set @nrmonth := 12;
set @nrdays := 30 * @nrmonth;
select date, caname, balance, lpad(format(amount,0), 8, ' ') as amount, clname, mkname, remark, lpad(format((@sum := @sum + amount),0), 8, ' ') as cumsum
from
(
    select date, accounts.name as caname, balance, amount*clusters.sgn as amount, clusters.name as clname, markets.name as mkname, remark
    from ugyletek,markets,clusters,accounts
    where
        cluster = clusters.uid and market = markets.uid and ca = accounts.uid
        and date between curdate() - interval @nrdays day and curdate()
        and clusters.name not like 'athelyezes'
        and clusters.name like 'Rezsi_futes'
    order by date
) as utolso_ugyletek;


-- 2017.11.04. (november 4, szombat), 13:26 [sql_mode=only_full_group_by]
-- Ez egy nagyon hasznos lekerdezes (Klaszterek szerint csoportositva az elmult par nap)
SET @@group_concat_max_len = 400;
set @sum := 0;
set @nrdays := 365;
select lpad(format(amount,0), 8, ' ') as amount, lpad(format((@sum := @sum + amount),0), 8, ' ') as cumsum, clname, mkname, remark
from
(
    select sum(amount*clusters.sgn) as amount, clusters.name as clname,
        group_concat(DISTINCT markets.name ORDER BY markets.name ASC separator ', ') as mkname,
        group_concat(DISTINCT remark separator ', ') as remark
    from ugyletek,markets,clusters
    where
        cluster = clusters.uid and market = markets.uid
        and ugyletek.date between curdate() - interval @nrdays day and curdate()
        and clusters.name not like 'athelyezes'
    group by clusters.uid
    order by amount
) as utolso_ugyletek;
--
-- REGI VERZIO - sql_mode=only_full_group_by miatt nem is mukodik
-- Ez egy nagyon hasznos lekerdezes (Klaszterek szerint csoportositva az elmult par nap)
SET @@group_concat_max_len = 120;
set @sum := 0;
set @nrdays := 90;
select date, caname, balance, lpad(format(amount,0), 8, ' ') as amount, clname, mkname, remark, lpad(format((@sum := @sum + amount),0), 8, ' ') as cumsum
from
(
    select date, accounts.name as caname, balance, sum(amount*clusters.sgn) as amount, clusters.name as clname,
        group_concat(DISTINCT markets.name ORDER BY markets.name ASC separator ', ') as mkname,
        group_concat(DISTINCT remark separator ', ') as remark
    from ugyletek,markets,clusters,accounts
    where
        cluster = clusters.uid and market = markets.uid and ca = accounts.uid
        and date between curdate() - interval @nrdays day and curdate()
        and clusters.name not like 'athelyezes'
    group by clusters.uid
    order by amount
) as utolso_ugyletek;


-- 2017.11.04. (november 4, szombat), 13:25
-- Ez egy nagyon hasznos lekerdezes (Klaszterek és számla szerint csoportositva az elmult par nap, csak bevetel) [UJ - 2017.11.04. (november 4, szombat), 13:05 - only_full_groupby]
SET @@group_concat_max_len = 120;
set @sum := 0;
set @nrdays := 365;
select lpad(format(amount,0), 8, ' ') as amount, lpad(format((@sum := @sum + amount),0), 9, ' ') as cumsum, caname, clname, mkname, remark
from
(
    select accounts.name as caname, sum(amount*clusters.sgn) as amount, clusters.name as clname,
        group_concat(DISTINCT markets.name ORDER BY markets.name ASC separator ', ') as mkname,
        group_concat(DISTINCT remark separator ', ') as remark
    from ugyletek,markets,clusters,accounts
    where
        cluster = clusters.uid and market = markets.uid and ca = accounts.uid
        and ugyletek.date between curdate() - interval @nrdays day and curdate()
        and clusters.name not like 'athelyezes'
        and clusters.sgn > 0 and amount*clusters.sgn > 0
    group by clusters.uid, accounts.uid
    order by amount
) as utolso_ugyletek;


-- 2017.11.04. (november 4, szombat), 13:25
-- Ez egy nagyon hasznos lekerdezes (Klaszterek és számla szerint csoportositva az elmult par nap) [UJ - 2017.11.04. (november 4, szombat), 13:05 - only_full_groupby]
SET @@group_concat_max_len = 400;
set @sum := 0;
set @nrdays := 365;
select lpad(format(amount,0), 8, ' ') as amount, lpad(format((@sum := @sum + amount),0), 10, ' ') as cumsum, caname, clname, mkname, remark
from
(
    select accounts.name as caname, sum(amount*clusters.sgn) as amount, clusters.name as clname,
        group_concat(DISTINCT markets.name ORDER BY markets.name ASC separator ', ') as mkname,
        group_concat(DISTINCT remark separator ', ') as remark
    from ugyletek,markets,clusters,accounts
    where
        cluster = clusters.uid and market = markets.uid and ca = accounts.uid
        and ugyletek.date between curdate() - interval @nrdays day and curdate()
        and clusters.name not like 'athelyezes'
    group by clusters.uid, accounts.uid
    order by amount
) as utolso_ugyletek;


-- Ez egy nagyon hasznos lekerdezes (Kumulalt szummalva es ertek szerint csokkeno sorrendben - legnagyobb koltseg elol - Csak Napi_Szukseglet)
SET @@group_concat_max_len = 120;
set @sum := 0;
set @nrdays := 30;
select date, caname, balance, lpad(format(amount,0), 8, ' ') as amount, clname, mkname, remark, lpad(format((@sum := @sum + amount),0), 8, ' ') as cumsum
from
(
    select date, accounts.name as caname, balance, amount*clusters.sgn as amount, clusters.name as clname, markets.name as mkname, remark
    from ugyletek,markets,clusters,accounts
    where
        cluster = clusters.uid and market = markets.uid and ca = accounts.uid
        and date between curdate() - interval @nrdays day and curdate()
        and clusters.name not like 'athelyezes'
        and clusters.name like '%napi_szukseglet%'
    order by amount
) as utolso_ugyletek;


-- Csak napi_szukseglet az elmult idoben
SET @@group_concat_max_len = 200;
set @sum := 0;
set @nrdays := 90;
select date, lpad(format(amount,0), 8, ' ') as amount, clname, mkname, remark, lpad(format((@sum := @sum + amount),0), 8, ' ') as cumsum
from
(
    select date, accounts.name as caname, balance, sum(amount*clusters.sgn) as amount, clusters.name as clname, markets.name as mkname,
    replace(replace(group_concat(DISTINCT remark separator ', '), '{ tr_', ''), ' }', '') as remark
    from ugyletek,markets,clusters,accounts
    where
        cluster = clusters.uid and market = markets.uid and ca = accounts.uid
        and date between curdate() - interval @nrdays day and curdate()
        and clusters.name not like 'athelyezes'
        and clusters.name like '%napi_szukseglet%'
    group by markets.uid
    order by amount
) as utolso_ugyletek;






-- Mennyi rezsit fizettunk atlagosan az elmult `@nrmonth` honapban
SET @@group_concat_max_len = 120;
set @sum := 0;
set @nrmonth := 12;
set @nrdays := 30 * @nrmonth + 20;
select date as 'Mennyi rezsit fizettunk atlagosan az elmult `@nrmonth` honapban',
    lpad(concat(nr_of_ugyletek,'/',@nrmonth), 5, ' ') as 'count', caname,
    lpad(format(amount,0), 8, ' ') as amount,
    lpad(format(amount/@nrmonth,0), 8, ' ') as monthly,
    clname, mkname, remark, lpad(format((@sum := @sum + amount),0), 8, ' ') as cumsum
from
(
    select
        group_concat(DISTINCT date order by date ASC separator ', ') as date, count(date) as nr_of_ugyletek,
        accounts.name as caname, balance, sum(amount*clusters.sgn) as amount, clusters.name as clname, markets.name as mkname,
        replace(replace(group_concat(DISTINCT remark separator ', '), '{ tr_', 't'), ' }', '') as remark
    from ugyletek,markets,clusters,accounts
    where
        cluster = clusters.uid and market = markets.uid and ca = accounts.uid
        and date between curdate() - interval @nrdays day and curdate()
        and clusters.name not like 'athelyezes'
        and clusters.name like '%rezsi%'
    group by clusters.uid
    order by amount
) as utolso_ugyletek;



-- Csak ruhazkodas az elmult idoben
SET @@group_concat_max_len = 120;
set @sum := 0;
set @nrdays := 90;
select date, caname, balance, lpad(format(amount,0), 8, ' ') as amount, clname, mkname, remark, lpad(format((@sum := @sum + amount),0), 8, ' ') as cumsum
from
(
    select date, accounts.name as caname, balance, amount*clusters.sgn as amount, clusters.name as clname, markets.name as mkname, remark
    from ugyletek,markets,clusters,accounts
    where
        cluster = clusters.uid and market = markets.uid and ca = accounts.uid
        and date between curdate() - interval @nrdays day and curdate()
        and clusters.name not like 'athelyezes'
        and clusters.name like '%ruha%'
    order by amount
) as utolso_ugyletek;




-- ESKUVO: - NEM MUKODIK, UJRA KELLENE CSINALNI!
select date, sum(amount*clusters.sgn) as 'sum of amounts', balance, accounts.name, clusters.name, remark
from ugyletek, accounts, clusters
where
    accounts.uid = ugyletek.ca and clusters.uid = ugyletek.cluster
    and clusters.name like 'eskuvo%'
group by cluster
order by date, ugyletek.uid;


SET @@group_concat_max_len = 160;
select date,caname,sum(amount) as 'sum',clname,
    group_concat(DISTINCT mkname ORDER BY mkname ASC separator ', ') as mkname,
    group_concat(DISTINCT remark separator ', ') as remark
from ugyletek_szep
where
    clname like 'Eskuvo%'
group by clname;

drop view v_ugyletek;
create view v_ugyletek as
select ugyletek.uid,date,if(pivot,'PIVOT','') as pivot, balance, ca_from.name as ca, amount*clusters.sgn as amount, ca_to.name as catransfer, clusters.name as cluster, markets.name as market, remark
from ugyletek,markets,clusters,accounts as ca_from, accounts as ca_to
where
    cluster = clusters.uid and market = markets.uid and ca = ca_from.uid and catransfer = ca_to.uid
order by date, ugyletek.uid;



-- PPKE_ITK-tol kapott fizetesek az elmult napokban
select u.date,u.amount,u.remark from (select @p1:=140 p) param, utolso_ugyletek u
where caname like 'potp' and mkname like 'PPKE_ITK' and amount > 0
order by date;


-- 2018.05.27. (május 27, vasárnap), 14:40
-- Balázsnak rezsikimutatás egyenkent
SET @@group_concat_max_len = 120;
set @sum := 0;
set @i :=0;
set @nrmonth := 36;
set @nrdays := 30 * @nrmonth + 15;
-- date_add(date,interval 3 day)
select @i := @i + 1 as nr, date, concat(year(date), ', ', monthname(date)) as 'honap', lpad(format(amount,0), 8, ' ') as amount, clname, lpad(format((@sum := @sum + amount),0), 8, ' ') as cumsum
from
(
    select date, accounts.name as caname, balance, amount*clusters.sgn as amount, clusters.name as clname, markets.name as mkname, remark
    from ugyletek,markets,clusters,accounts
    where
        cluster = clusters.uid and market = markets.uid and ca = accounts.uid
        and date between curdate() - interval @nrdays day and curdate()
        and clusters.name not like 'athelyezes'
        and clusters.name like 'Rezsi_Futes'
    order by date
) as ugyletek_szep;


-- 2018.05.27. (május 27, vasárnap), 14:40
-- Balázsnak rezsikimutatás egyenkent
SET @@group_concat_max_len = 120;
set @sum := 0;
set @i :=0;
set @nrmonth := 36;
set @nrdays := 30 * @nrmonth + 40;
-- date_add(date,interval 3 day)
select @i := @i + 1 as nr, date, concat(year(date), ', ', monthname(date)) as 'honap', lpad(format(amount,0), 8, ' ') as amount, clname, lpad(format((@sum := @sum + amount),0), 8, ' ') as cumsum
from
(
    select date, accounts.name as caname, balance, amount*clusters.sgn as amount, clusters.name as clname, markets.name as mkname, remark
    from ugyletek,markets,clusters,accounts
    where
        cluster = clusters.uid and market = markets.uid and ca = accounts.uid
        and date between curdate() - interval @nrdays day and curdate()
        and clusters.name not like 'athelyezes'
        and clusters.name like 'Rezsi_Gaz'
    order by date
) as ugyletek_szep;

-- 2018.05.27. (május 27, vasárnap), 14:40
-- Balázsnak rezsikimutatás egyenkent
SET @@group_concat_max_len = 120;
set @sum := 0;
set @i :=0;
set @nrmonth := 24;
set @nrdays := 30 * @nrmonth + 15;
-- date_add(date,interval 3 day)
select @i := @i + 1 as nr, date, concat(year(date), ', ', monthname(date)) as 'honap', lpad(format(amount,0), 8, ' ') as amount, clname, lpad(format((@sum := @sum + amount),0), 8, ' ') as cumsum
from
(
    select date, accounts.name as caname, balance, amount*clusters.sgn as amount, clusters.name as clname, markets.name as mkname, remark
    from ugyletek,markets,clusters,accounts
    where
        cluster = clusters.uid and market = markets.uid and ca = accounts.uid
        and date between curdate() - interval @nrdays day and curdate()
        and clusters.name not like 'athelyezes'
        and clusters.name like 'Rezsi_Kozosk'
    order by date
) as ugyletek_szep;

-- 2018.05.27. (május 27, vasárnap), 14:40
-- Balázsnak rezsikimutatás egyenkent
SET @@group_concat_max_len = 120;
set @sum := 0;
set @i :=0;
set @nrmonth := 36;
set @nrdays := 30 * @nrmonth + 15;
-- date_add(date,interval 3 day)
select @i := @i + 1 as nr, date, concat(year(date), ', ', monthname(date)) as 'honap', lpad(format(amount,0), 8, ' ') as amount, clname, lpad(format((@sum := @sum + amount),0), 8, ' ') as cumsum
from
(
    select date, accounts.name as caname, balance, amount*clusters.sgn as amount, clusters.name as clname, markets.name as mkname, remark
    from ugyletek,markets,clusters,accounts
    where
        cluster = clusters.uid and market = markets.uid and ca = accounts.uid
        and date between curdate() - interval @nrdays day and curdate()
        and clusters.name not like 'athelyezes'
        and clusters.name like 'Rezsi_Elmu'
        and amount < 28000
    order by date
) as ugyletek_szep;

-- Rezsi_Bkv Rezsi_Elmu Rezsi_Futes Rezsi_Gaz Rezsi_Kozosk Rezsi_Upc


-- 2018.05.27. (május 27, vasárnap), 14:40
-- Balázsnak rezsikimutatás osszsitve
SET @@group_concat_max_len = 120;
set @sum := 0;
set @i :=0;
set @nrmonth := 36;
set @nrdays := 30 * @nrmonth + 20;
-- date_add(date,interval 3 day)
select @i := @i + 1 as nr, intervallum as 'idointervallum, fizetesek szama', lpad(format(amount,0), 8, ' ') as amount, clname
from
(
    select concat(min(date), " - ", max(date), ', ', count(date)) as intervallum, sum(amount*clusters.sgn) as amount, clusters.name as clname
    from ugyletek,markets,clusters,accounts
    where
        cluster = clusters.uid and market = markets.uid and ca = accounts.uid
        and date between curdate() - interval @nrdays day and curdate()
        and clusters.name not like 'athelyezes'
        and ( clusters.name like 'Rezsi_Elmu' or clusters.name like 'Rezsi_Futes' or clusters.name like 'Rezsi_Gaz' or clusters.name like 'Rezsi_Kozosk' )
    group by clname
) as ugyletek_szep;

