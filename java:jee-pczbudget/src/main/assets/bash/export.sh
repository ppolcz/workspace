f="${HOME}/Dropbox/Others/Koltsegvetes/export/pczbudget-$(date +%Y-%m-%d:%H:%M:%S).sql"
db="jee_budget"
echo "/**
 * MySQL database backup
 * 
 * author: Peter Polcz <ppolcz@gmail.com>
 * date:   $(date)
 */

create database if not exists $db;
use $db;


" > $f
mysqldump --opt -u root -pbudapest $db | sed -r "s/\),\(/\),\n    \(/g" | sed -r "s/VALUES /VALUES \n    /g" >> $f
