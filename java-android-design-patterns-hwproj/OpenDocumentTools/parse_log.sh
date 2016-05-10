
cat opendoctools.log | egrep "insert into tran" | sed -r 's/pcz.*(insert into tr.*)/\1/g;s/tr_([^, ]*)/\1/g;s/, acid, insert_date//g;s/, .ppolcz[^\)]*\)/);/g;s/Rezsi_Upc/UPC/g;s/Egyeb_(Kiadas|Bevetel)|NONE/EGYEB/g;s/Rezsi_Futes/FOTAV/g;s/Alberlet/ALBERLET/g;s/Kaucio/KAUCIO/g;s/KAUCIO_Vissza/KAUCIO/g;s/Rezsi_Elmu/ELMU/g;s/Rezsi_Gaz/FOGAZ/g;s/Rezsi_Kozosk/KOZOSK/g;s/Rezsi_Bkv/BKV/g;s/Korrigalas/SZAMOLAS/g;s/newbalance/balance/g;s/clname/cluster/g;s/mkname/market/g' > opendoctools.log.parse

cat opendoctools.log.parse | egrep Athelyezes_Innen > opendoctools.innen
cat opendoctools.log.parse | egrep Athelyezes_Ide | tr ')(' ',' | sed -r 's/,//g' | cut -d ' ' -f14 > opendoctools.ide

# cat opendoctools.ide

cat opendoctools.log.parse | egrep -v Athelyezes > insert.sql
paste -d'>' opendoctools.innen opendoctools.ide | sed -r 's/..>/, /g;s/(^.*$)/\1);/g;s/Athelyezes_Innen/move/g;s/cluster/dir/g;s/(market)/\1, caid_tofrom/g' >> insert.sql
# | sed 's///g'
