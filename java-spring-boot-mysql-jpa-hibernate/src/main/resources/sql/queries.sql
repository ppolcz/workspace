

-- +-----------------------+
-- | Tables_in_ppolcz      |
-- +-----------------------+
-- | accounts              |
-- | clusters              |
-- | market_not_applicable |
-- | markets               |
-- | products              |
-- | sum_by_market         |
-- | transactions          |
-- | ugyletek              |
-- | users                 |
-- +-----------------------+

-- +-----------------+--------------+------+-----+---------+----------------+
-- | Field           | Type         | Null | Key | Default | Extra          |
-- +-----------------+--------------+------+-----+---------+----------------+
-- | uid             | int(11)      | NO   | PRI | NULL    | auto_increment |
-- | amount          | int(11)      | NO   |     | NULL    |                |
-- | balance         | int(11)      | NO   |     | NULL    |                |
-- | date            | date         | NO   |     | NULL    |                |
-- | endofdayBalance | int(11)      | NO   |     | NULL    |                |
-- | pivot           | tinyint(1)   | NO   |     | NULL    |                |
-- | remark          | varchar(255) | YES  |     | NULL    |                |
-- | ca              | int(11)      | NO   | MUL | NULL    |                |
-- | catransfer      | int(11)      | NO   | MUL | NULL    |                |
-- | cluster         | int(11)      | NO   | MUL | NULL    |                |
-- | market          | int(11)      | YES  | MUL | NULL    |                |
-- +-----------------+--------------+------+-----+---------+----------------+

-- INTO OUTFILE '/tmp/Eskuvo_koltsegei.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n'

-- drop table products, ugyletek, transactions, clusters, markets;


create function p1() returns INTEGER DETERMINISTIC NO SQL return @p1;

create view utolso_ugyletek as
select date, accounts.name as caname, balance, amount*clusters.sgn as amount, clusters.name as clname, markets.name as mkname, remark
from ugyletek,markets,clusters,accounts
where
    cluster = clusters.uid and market = markets.uid and ca = accounts.uid
    and date between curdate() - interval p1() day and curdate()
    and clusters.name not like 'athelyezes'
order by amount;


select u.* from (select @p1:=30 p) param, utolso_ugyletek u;

create view ugyletek_mind as
select date, balance, `from`.name as ca, amount*clusters.sgn as amount, `to`.name as catransfer, clusters.name as cluster, markets.name as market, remark
from ugyletek,markets,clusters,accounts as `from`, accounts as `to`
where
    cluster = clusters.uid and market = markets.uid and ca = `from`.uid and catransfer = `to`.uid
order by date, ugyletek.uid;

create view ugyletek_szep as
select date, balance, accounts.name as caname, amount*clusters.sgn as amount, clusters.name as clname, markets.name as mkname, remark
from ugyletek,markets,clusters,accounts
where
    cluster = clusters.uid and market = markets.uid and ca = accounts.uid
    and clusters.name not like 'athelyezes'
order by date, ugyletek.uid;


select u.* from (select @p1:=30 p) param, utolso_ugyletek u;


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




-- ESKUVO:
select date, sum(amount*clusters.sgn) as 'sum of amounts', balance, accounts.name, clusters.name, remark
from ugyletek, accounts, clusters
where
    accounts.uid = ugyletek.ca and clusters.uid = ugyletek.cluster
    and clusters.name like 'eskuvo%'
group by cluster
order by date, ugyletek.uid;
