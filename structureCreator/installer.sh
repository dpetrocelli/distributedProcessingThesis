#!/bin/bash
# -------------------- Server Side -------------------------
apt-get update

# Maria db basic
apt-get install mariadb-server
mysql -u root -p ""
use mysql;
UPDATE user SET plugin='mysql_native_password' WHERE User='root';
update user set authentication_string=password('Osito1104**') where user='root';
flush privileges;
quit;

# Create database
CREATE DATABASE distributedProcessing;

# Create basetable
USE distributedProcessing;

CREATE TABLE `jobTracker` (
  `id` tinyint(4) NOT NULL,
  `service` varchar(20) NOT NULL,
  `job` varchar(40) NOT NULL,
  `workerName` varchar(30) NOT NULL,
  `workerArchitecture` varchar(10) NOT NULL,
  `initTime` bigint(20) NOT NULL,
  `endTime` bigint(20) NOT NULL,
  `executionTime` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `jobTracker` ADD PRIMARY KEY (`id`);

#java 8 oracle
scp -P 2244 /home/david/Desktop/creator/* dpetrocelli@170.210.103.23:/home/dpetrocelli
mkdir /usr/local/oracle-java-8
tar -zxf jdk-8u191-linux-x64.tar.gz -C /usr/local/oracle-java-8
update-alternatives --install "/usr/bin/java" "java" "/usr/local/oracle-java-8/jdk1.8.0_191/bin/java" 1500
update-alternatives --install "/usr/bin/javac" "javac" "/usr/local/oracle-java-8/jdk1.8.0_191/bin/javac" 1500
update-alternatives --install "/usr/bin/javaws" "javaws" "/usr/local/oracle-java-8/jdk1.8.0_191/bin/javaws" 1500
java -version

#Rabbit mq
apt-get install rabbitmq-server
rabbitmq-plugins enable rabbitmq_federation
rabbitmq-plugins enable rabbitmq_federation_management
rabbitmqctl add_user admin admin
rabbitmqctl set_user_tags admin administrator
rabbitmqctl set_permissions -p / admin ".*" ".*" ".*"

# -------------------- End Server Side -------------------------

# -------------------- client side -------------------------
apt-get update
apt-get install default-jdk
#

