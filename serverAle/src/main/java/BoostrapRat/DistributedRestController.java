package BoostrapRat;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

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
	HashMap<String,ArrayList<String>> filterParameters;
	HashMap<Long, Long> AckService;
	
	public DistributedRestController () {
		// CREATE THE CONSTRUCTOR
		this.factory = new ConnectionFactory();
		this.factory.setHost("localhost");
		this.factory.setUsername("admin");
		this.factory.setPassword("admin");
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
		
		// Obtain parameter list (from configuration file)
		this.filterParameters = new HashMap<String,ArrayList<String>>();
		this.AckService = new HashMap<Long, Long>();
		// ACTIVATE THREAD FOR LOOPING AND DELETING UNCORRESPONDANT MSG
		int timeCheckInterval = 28*1000;
		int timeoutPackage = 120*1000;
		ManageAckList mal = new ManageAckList (this.enterChannel, this.AckService, timeCheckInterval, timeoutPackage);
		Thread malThread = new Thread (mal);
		malThread.start();
			
		
		this.readFromFile (filterParameters, "src\\main\\java\\BoostrapRat\\videoParameters");
		
	}
	
	
	// Client -> Obtain Finished Task
	@RequestMapping(value = "/getEndTasks", method = RequestMethod.GET)
	public String getFinishedTask(@RequestParam("name") String name) {
		//System.err.println("Client GETTING FINISHED JOBS");
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
			System.err.println(" the queue hasn't been created yet");
			
		}
		
		return response;
		
		
	}
	
	// Client -> Publish new Task
	@RequestMapping(value = "/uploadChunk", method = RequestMethod.POST)
	  public String publishNewClientTask(@RequestBody String msg, @RequestParam("name") String name, @RequestParam("queues") String queuesList) { 	
		  	// actions to do
		  	// STEP 1 - Obtain headers (baseQueueName + List of queues)
			// STEP 2 - Obtain each filter
			// STEP 3 - Rearm msg class
			// STEP 4 - based on filterParameter, replicate msgStructure + parameters for profile in the queue
			// STEP 5 - Create the finishedTaskQueue where workers will deploy the answers (1 per each queueFile)
		  	
		  	String finishedSpecificQueue;
		  	String msgEncoded;
		  	try {
				this.enterChannel.queueDeclare(this.enterQueue, false, false, false, null);
				
				// STEP 2 - obtain each filter
				String[] filters = queuesList.split(Pattern.quote("_"));
				String parameters=null;
				
				// STEP 3 - Rearm MSG
				JsonUtility jsonUt = new JsonUtility();
				jsonUt.setType("Message");
				Message msgRearmed = (Message) jsonUt.fromJson(msg);
				
				// FOR EACH FILTER -> take params from the arraylist and put in msg structure
				for (String filter : filters) {
					// STEP 4.0 - Rearm msg parameters
					parameters = (this.filterParameters.get(filter)).toString();
					System.out.println("PARAMS: "+parameters);
					msgRearmed.setParamsEncoding(parameters);
					msgRearmed.setName(name+"_"+filter);
					jsonUt.setObject(msgRearmed);
					msgEncoded = jsonUt.toJson();
					// STEP 4.1 - Save in normal queue 
					this.enterChannel.basicPublish("", this.enterQueue, null, msgEncoded.getBytes());
					System.out.println(" MSG: saved in enteredQueue" );
					
					// STEP 5 - Create (or not) the specific finishedQueue 
					finishedSpecificQueue=name+"_"+filter;
					this.enterChannel.queueDeclare(finishedSpecificQueue, true, false, false, null);
					System.out.println(" queueJob: "+finishedSpecificQueue+" has been created");
										
				}
				
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return "ok - Ready";
	  }
	 
	// Worker -> Get Task
	@RequestMapping(value = "/getJob", method = RequestMethod.GET)
	  public String getJob (@RequestParam("name") String name) throws InterruptedException {
		//System.err.println("WORKER "+name+" IS GETTING JOB");
		GetResponse data = null;
		byte[] responseByte = null;
		String response = null;
		try {
			
				if (this.enterChannel.queueDeclarePassive(this.enterQueue).getMessageCount()>0) {
					//data = this.enterChannel.basicGet(this.enterQueue, true);
					data = this.enterChannel.basicGet(this.enterQueue,false);
					
					long idForAck = data.getEnvelope().getDeliveryTag();
					long timestamp  = (new Timestamp(System.currentTimeMillis())).getTime();
					
					synchronized (this.AckService) {
						this.AckService.put(idForAck, timestamp);
					}
					//data = this.enterChannel.basicConsume(this.enterQueue, false, arg2)
					responseByte = data.getBody();
					response = new String(responseByte, "UTF-8");
					response = response.substring(0, (response.length()-1));
					String end = ",\"idForAck\":\""+String.valueOf(idForAck)+"\"}";
					response+=end;
					
					//Thread.sleep(100000000);
					
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
	  public String persistFinishedWorkerTask(@RequestBody String msg, @RequestParam("name") String name, @RequestParam("server") String worker, @RequestParam("part") String part, @RequestParam("idForAck") String idForAck) { 	
		  	// actions to do
		  	// 0 - delete from list
			boolean validatedMsg = false;
			
			synchronized (this.AckService) {
			
				System.out.println(this.AckService.toString());
				if (this.AckService.containsKey(Long.parseLong(idForAck))) {
					
					try {
						// if exist -> ok, we can save it 
						validatedMsg = true;
			
						// push the ACK to the queue
						this.enterChannel.basicAck(Long.parseLong(idForAck), false);
			
						// REMOVE FROM ACK LIST
						this.AckService.remove(Long.parseLong(idForAck));
						System.out.println(" REMOVED ID: "+idForAck);
						System.out.println(" LIST: "+this.AckService.toString());
			
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}

			}
			

			if (validatedMsg) {
				// 1. save resource in clientFinishedQueue;
			  	System.out.println("WORKER: "+worker+" / UPLOAD JOB "+name);
			  	System.err.println("PART:_ "+part);
			  	String queueFinishedUser = name;
			  	try {
					//this.enterChannel.queueDeclare(queueFinishedUser, false, false, false, null);
					this.enterChannel.basicPublish("", queueFinishedUser, null, msg.getBytes());
					System.out.println(" TASK FINISHED saved " );
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			

			return "ok - Ready";
	  }
	 
   
	
	private void readFromFile(HashMap<String,ArrayList<String>> filterParameters, String file) {
		// TODO Auto-generated method stub
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			ArrayList<String> parameters;
			
		    String line = br.readLine();

		    while (line != null) {
		        String[] partsParameters = line.split(Pattern.quote("|"));
		        parameters = new ArrayList<String>();
		        for (int i=1; i<(partsParameters.length); i++) parameters.add(partsParameters[i]);
		        filterParameters.put(partsParameters[0], parameters);
		        
		        // After fullfill line, read next
		        line = br.readLine();
		    }
		   
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		    try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		    
	}
 
  
  
  
}
