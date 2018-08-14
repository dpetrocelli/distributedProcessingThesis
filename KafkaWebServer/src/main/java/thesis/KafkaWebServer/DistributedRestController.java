package thesis.KafkaWebServer;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class DistributedRestController {


	
  @RequestMapping(value = "/uploadChunk", method = RequestMethod.POST)
  public String persistPerson(@RequestBody String msg) {
	  
	  /*JsonUtility js = new JsonUtility();
	  js.setType("Message");
	  Message xyz = (Message) js.fromJson(msg);
	  System.out.println("MSG: "+xyz.name);*/
	  
	  //System.out.println("/POST request with trolo " + msg);
		// save Image to C:\\server folder
		/*String path = basePath;
		path+= image.getName();
		UtilBase64.decoder(image.getData(), path);
		return "/Post Successful!";*/
	  	SaveMessage sm = new SaveMessage (msg);
		return "ok - Ready";
	  
  }
}
