package com.example.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class KafkaInitiateServerAndConf {
	
	String ipServer;
	String baseApp;
	String configZookeperFile;
	String configKafkaFile;
	String binStartZookeper;
	String binStartKafka;
	
	public KafkaInitiateServerAndConf (String ipServer, String baseApp) {
		this.ipServer = ipServer;
		this.baseApp = baseApp;
		
		configZookeperFile = baseApp+"\\config\\zookeeper.properties";
		configKafkaFile = baseApp+"\\config\\server.properties";
		binStartZookeper = baseApp+"\\bin\\windows\\zookeeper-server-start.bat";
		binStartKafka = baseApp+"\\bin\\windows\\kafka-server-start.bat";
		
	}
	
	public void StartZookeper () {
		
		String command = binStartZookeper + " " + configZookeperFile;
		System.out.println("COMM: "+command );
		new Thread(new KafkaStarter(command)).start();
		//this.powerShellFullExecution(command);
	}
	
	
	public void StartKafka () {
		
		String command = binStartKafka + " " + configKafkaFile;
		System.out.println("COMM: "+command );
		//this.powerShellFullExecution(command);
		new Thread(new KafkaStarter(command)).start();
	}

	
	
	public static void main(String[] args) throws Exception {
		String baseApp = "D:\\kafka_2.12-2.0.0";
		KafkaInitiateServerAndConf ktc = new KafkaInitiateServerAndConf ("localhost", baseApp);
		ktc.StartZookeper();
		Thread.sleep(10000);
		ktc.StartKafka();
		
	}
}
