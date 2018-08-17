package thesis.KafkaWebServer;


import java.util.Collections;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;
import java.util.concurrent.TimeUnit;



@SpringBootApplication
public class KafkaWebServerApplication {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(KafkaWebServerApplication.class, args);
	}
}
