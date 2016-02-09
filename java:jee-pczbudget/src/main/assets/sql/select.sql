
-- 
select date, amount * cl.sgn as sgnamount, ca.name, cl.name, remark from t_transactions as tr, t_charge_accounts as ca, t_cluster as cl where date > '2015-12-25'and cl.uid = cluster and ca.uid = ca and ca.name = 'pkez' order by sgnamount desc;


