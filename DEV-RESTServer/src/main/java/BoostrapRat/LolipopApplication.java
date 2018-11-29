package BoostrapRat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// https://memorynotfound.com/spring-boot-passing-command-line-arguments-example/
@SpringBootApplication
public class LolipopApplication {

	public static void main(String[] args) {
		
		for(String arg:args) {
            System.out.println(arg);
        }
		SpringApplication.run(LolipopApplication.class, args);
	}
}
