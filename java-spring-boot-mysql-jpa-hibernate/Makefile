


run: init-mysql clean
	( mvn spring-boot:run & )

JAVA_HOME:
	export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre/

clean:
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
	mysql -u root -p --database ppolcz < src/main/resources/sql/views.sql --table


load: odf-load sql-views
