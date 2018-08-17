package BoostrapRat;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

@RestController
public class DistributedRestController {
	
	
	private static String enterQueue = "enterQueue";
	private static String pendantQueue = "pendantQueue";
	private static String solvedQueue = "solvedQueue";
	ConnectionFactory factory;
	Connection enterConnection;
	Channel enterChannel;
	Connection pendantConnection;
	Channel pendantChannel;
	Connection solvedConnection;
	Channel solvedChannel;
	
	public DistributedRestController () {
		// CREATE THE CONSTRUCTOR
		this.factory = new ConnectionFactory();
		this.factory.setHost("localhost");
		try {
			// connection and Channels
			this.enterConnection = this.factory.newConnection();
			this.enterChannel = this.enterConnection.createChannel();
			
			this.pendantConnection = this.factory.newConnection();
			this.pendantChannel = this.pendantConnection.createChannel();
			
			this.solvedConnection = this.factory.newConnection();
			this.solvedChannel = this.solvedConnection.createChannel();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	@RequestMapping(value = "/getJob", method = RequestMethod.GET)
	  public String getJob () {
		GetResponse data = null;
		byte[] responseByte = null;
		String response = null;
		try {
			data = this.enterChannel.basicGet(this.enterQueue, true);
			responseByte = data.getBody();
			response = new String(responseByte, "UTF-8");
			
			//System.err.println("RESPO: "+response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println("RECEIVED FROM CLIENT AND RETURNES");
		return response;
		 
	 }
  @RequestMapping(value = "/uploadChunk", method = RequestMethod.POST)
  public String persistPerson(@RequestBody String msg) { 	
	  
	  	try {
			this.enterChannel.queueDeclare(this.enterQueue, false, false, false, null);
			this.enterChannel.basicPublish("", this.enterQueue, null, msg.getBytes());
			System.out.println(" MSG: saved " );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		String message = "Hello World!";
//		channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
//		System.out.println(" [x] Sent '" + message + "'");
		return "ok - Ready";
  }
  
 
  
  
  
}
