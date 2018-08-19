package BoostrapRat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

@RestController
public class DistributedRestController {
	
	
	private static String enterQueue = "enterQueue";
	private static String pendantQueue = "pendantQueue";
	ConnectionFactory factory;
	Connection enterConnection;
	Channel enterChannel;
	Connection pendantConnection;
	Channel pendantChannel;
	
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
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	// Client -> Obtain Finished Task
	@RequestMapping(value = "/getEndTasks", method = RequestMethod.GET)
	public String getFinishedTask(@RequestParam("name") String name) {
		System.err.println("Client GETTING FINISHED JOBS");
		GetResponse data = null;
		byte[] responseByte = null;
		String response = null;
		try {
			
			
			if (this.enterChannel.queueDeclarePassive(name).getMessageCount()>0) {
				data = this.enterChannel.basicGet(name, true);
				responseByte = data.getBody();
				response = new String(responseByte, "UTF-8");
			}else {
				response = "NO DATA INFO";
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
		}
		
		return response;
		
		
	}
	
	// Client -> Publish new Task
	@RequestMapping(value = "/uploadChunk", method = RequestMethod.POST)
	  public String publishNewClientTask(@RequestBody String msg, @RequestParam("name") String name) { 	
		  	// actions to do
		  	// 1. save resource in enterQueue;
		  	// 2. create outputQueue where workers will deploy the answers (1 per each client)
		  	String queueJob = name;
		  	try {
				this.enterChannel.queueDeclare(this.enterQueue, false, false, false, null);
				this.enterChannel.basicPublish("", this.enterQueue, null, msg.getBytes());
				System.out.println(" MSG: saved " );
				
				this.enterChannel.queueDeclare(queueJob, true, false, false, null);
				System.out.println(" queueJob: "+queueJob+" has been created");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return "ok - Ready";
	  }
	 
	// Worker -> Get Task
	@RequestMapping(value = "/getJob", method = RequestMethod.GET)
	  public String getJob () {
		System.err.println("WORKER GETTING JOB");
		GetResponse data = null;
		byte[] responseByte = null;
		String response = null;
		try {
			
			
			if (this.enterChannel.queueDeclarePassive(this.enterQueue).getMessageCount()>0) {
				data = this.enterChannel.basicGet(this.enterQueue, true);
				responseByte = data.getBody();
				response = new String(responseByte, "UTF-8");
			}else {
				response = "NO DATA INFO";
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
		}
		
		return response;
		 
	 }
	
	// Worker -> Upload (push) finished task
	@RequestMapping(value = "/uploadFinishedJob", method = RequestMethod.POST)
	  public String persistFinishedWorkerTask(@RequestBody String msg, @RequestParam("name") String name) { 	
		  	// actions to do
		  	// 1. save resource in clientFinishedQueue;
		  	System.out.println("UPLOAD FINISHED JOB - > ARRIVED "+name);
		  	String queueFinishedUser = name;
		  	try {
				//this.enterChannel.queueDeclare(queueFinishedUser, false, false, false, null);
				this.enterChannel.basicPublish("", queueFinishedUser, null, msg.getBytes());
				System.out.println(" TASK FINISHED saved " );
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return "ok - Ready";
	  }
	 
   
 
  
  
  
}
