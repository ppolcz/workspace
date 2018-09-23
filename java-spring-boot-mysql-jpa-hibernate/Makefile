


run step_1_run: init-mysql clean
	( mvn spring-boot:run & )

JAVA_HOME:
	export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre/

clean:
	rm -f odf-load
	rm -f odf-load.*
	mvn clean


odf-load: force
	wget localhost:8080/odf-load

dbexport:
	export_mysql.sh


init-mysql:
	mysql -u root -p --database=ppolcz --execute="drop database ppolcz; create database ppolcz;"

force:

mysql:
	mysql -u root -p --database=ppolcz

sql: mysql

sql-views:
	mysql -u root -p --database=ppolcz < src/main/resources/sql/views.sql --table


load: odf-load sql-views

Sk_fancy Sk_Szerviz Sk Sk_Tankolas Sk_Benzin Sk_diff Sk_fancy_min:
	mysql -u root -p --database=ppolcz --execute="select * from $@" --table


kill:
	# for PID in $(ps -A | egrep java | awk '{print $1}') ; do kill $PID ; done
	kill -SIGTERM $(shell pidof java)