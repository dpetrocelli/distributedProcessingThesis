package thesis.KafkaWebServer;

import java.util.Collections;
import java.util.Properties;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DistributedRestController {
	
	
	private static String enterQueue = "enterQueue";
	private static String intermediateQueue = "intermediateQueue";
	private static String endQueue = "endQueue";
	
	public DistributedRestController () {
		// CREATE THE CONSTRUCTOR
	}
	
	@RequestMapping(value = "/getJob", method = RequestMethod.GET)
	  public void getJob () {
		
		//return "ok - Ready";
		 
	 }
  @RequestMapping(value = "/uploadChunk", method = RequestMethod.POST)
  public String persistPerson(@RequestBody String msg) { 	
		return "ok - Ready";
  }
  
  private void createActiveMQConnection () {
	
  }
  
  
  
}
