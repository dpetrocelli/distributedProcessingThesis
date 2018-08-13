package com.example.demo;



import java.util.Properties;

import org.I0Itec.zkclient.ZkClient;

public class KafkaTopicCreation
{
    String host;
    int port;
    String topicName;
    
	public KafkaTopicCreation (String host, int port, String topicName){
		this.host = host;
		this.port = port;
		this.topicName = topicName;
		this.doIt();
    }
	
	public void doIt (){
        ZkClient zkClient = null;
        ZkUtils zkUtils = null;
        try {
            String zookeeperHosts = this.host+":"+this.port;  
            // If multiple zookeeper then -> String zookeeperHosts = "192.168.1.1:2181,192.168.1.2:2181";
            int sessionTimeOutInMs = 15 * 1000; // 15 secs
            int connectionTimeOutInMs = 10 * 1000; // 10 secs

            zkClient = new ZkClient(zookeeperHosts, sessionTimeOutInMs, connectionTimeOutInMs, ZKStringSerializer$.MODULE$);
            zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeperHosts), false);

            String topicName = this.topicName;
            int noOfPartitions = 1;
            int noOfReplication = 1;
            Properties topicConfiguration = new Properties();

            AdminUtils.createTopic(zkUtils, topicName, noOfPartitions, noOfReplication, topicConfiguration, null);
            System.out.println("Topic has been Created");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (zkClient != null) {
                zkClient.close();
        }
    }
        
	}
	public static void main(String[] args) throws Exception {
		KafkaTopicCreation ktc = new KafkaTopicCreation("localhost", 2181, "testingTopic");
	}
    
}
