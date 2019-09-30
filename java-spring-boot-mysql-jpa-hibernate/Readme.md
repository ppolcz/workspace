# Do these in three different terminal

<!-- Could not find artifact com.sun:tools:jar:1.7.0  -->
sudo apt-get install openjdk-8-jdk

mysql> drop database ppolcz; create database ppolcz ; use ppolcz;

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre/
mvn clean spring-boot:run

wget localhost:8080/odf-load

<!-- ~/Dropbox/Peti/Others/Config/scripts/export_mysql.sh -->
<!-- Run this command to export the contents of the database -->
export_mysql.sh



<!-- ThinkPad Java-8 -->
<!-- 2019.09.30. (szeptember 30, hétfő), 08:48 -->
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre
mvn -f pom-ThinkPad-2019-09-30-java8.xml clean spring-boot:run -e
