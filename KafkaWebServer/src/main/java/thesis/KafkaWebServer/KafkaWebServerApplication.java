package thesis.KafkaWebServer;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;
import java.util.concurrent.TimeUnit;



@SpringBootApplication
public class KafkaWebServerApplication {

	public static void main(String[] args) throws InterruptedException {
		
			try {
				
				KafkaInitiateServerAndConf ktc = new KafkaInitiateServerAndConf("localhost", "D:\\Docs\\Thesis2018\\libraries\\kafka\\kafka_2.12-2.0.0");
				ktc.StartZookeper();
				Thread.sleep(10000);
				ktc.StartKafka();
				Thread.sleep(5000);
				ktc.CreateTopic("localhost", "2181", "1", "10", "testDavid3");
				
				
				//host, port, replicatorfactor, partitions, name
				//ktc.CreateTopic("localhost", "2181", "1", "10", "testDavid");
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Thread.sleep(5000);
			
		
		SpringApplication.run(KafkaWebServerApplication.class, args);
	}
}
