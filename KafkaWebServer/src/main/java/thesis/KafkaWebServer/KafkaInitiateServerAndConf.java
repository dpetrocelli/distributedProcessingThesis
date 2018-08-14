package thesis.KafkaWebServer;



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

	
	public void CreateTopic (String host, String port, String replicationFactor, String numberOfPartitions, String topicName) {
		String command = this.baseApp+"\\bin\\windows\\kafka-topics.bat --create --zookeeper "+host+":"+port+" --replication-factor "+replicationFactor+" --partitions "+numberOfPartitions+" --topic "+topicName;
		System.out.println("String RUN: "+command);
		 
	}
	
	
}
