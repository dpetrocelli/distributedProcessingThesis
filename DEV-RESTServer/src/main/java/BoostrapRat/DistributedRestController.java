package BoostrapRat;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.QueueInfo;

@RestController
public class DistributedRestController {
	
	// INIT RABBIT DATA
	private static String enterQueue = "enterQueue";
	private static String pendantQueue = "pendantQueue";
	ConnectionFactory factory;
	Connection enterConnection;
	Channel enterChannel;
	Connection pendantConnection;
	Channel pendantChannel;
	String ipAddress;
	String username;
	String password;
	// END RABBIT DATA
	
	// INIT DB DATA
	MariaDBConnection mdbc;
	// END DB DATA
	
	HashMap<String,ArrayList<String>> filterParameters;
	HashMap<Long, Long> AckService;
	
	public DistributedRestController () {
		// CREATE THE CONSTRUCTOR
		
		// FIRST OBTAIN ACCESS TO THE DATABASE
		 String host = "localhost";
		 String dbname = "distributedProcessing";
		 String url = "jdbc:mariadb://" + host + "/" + dbname;
		 String username = "root";
		 String password = "Osito1104**";
		    
		 this.mdbc = new MariaDBConnection(host, dbname, username, url, password);
		 this.mdbc.createConnection();
		
		// END DATABASE INITIALIZATION
		
		// OBTAIN IP ADDRESS (LAN )
		ipAddress = this.obtainIpAddress("192.168");
		if (!(ipAddress.length()>5)) {
			ipAddress = this.obtainIpAddress("10.1.");
		}
		System.err.println("IP: "+ipAddress);
		System.err.println("IP: "+ipAddress);
		System.err.println("IP: "+ipAddress);
		System.err.println("IP: "+ipAddress);
		this.factory = new ConnectionFactory();
		this.factory.setHost(ipAddress);
		this.username= "admin";
		this.password = "admin";
		this.factory.setUsername(this.username);
		this.factory.setPassword(this.password);
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
		int timeCheckInterval = 15*1000;
		int timeoutPackage = 120*1000;
		ManageAckList mal = new ManageAckList (this.enterChannel, this.AckService, timeCheckInterval, timeoutPackage);
		Thread malThread = new Thread (mal);
		malThread.start();
			
		
		//this.readFromFile (filterParameters, "src/main/resources/videoParameters");
		this.readFromContext (filterParameters);
		System.out.println("FILTER:"+filterParameters.toString());
	}
	
	
	

	private String obtainIpAddress(String baseRegex) {
		String command = null;
        if(System.getProperty("os.name").equals("Linux"))
            command = "ip a";
        else
            command = "ipconfig";
        Runtime r = Runtime.getRuntime();
        Process p = null;
		try {
			p = r.exec(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Scanner s = new Scanner(p.getInputStream());

        StringBuilder sb = new StringBuilder("");
        while(s.hasNext())
            sb.append(s.next());
        String ipconfig = sb.toString();
        Pattern pt=null;
        if (baseRegex.startsWith("192")){
        	pt = Pattern.compile("192\\.168\\.[0-9]{1,3}\\.[0-9]{1,3}");
        }else {
        	pt = Pattern.compile("10\\.1\\.[0-9]{1,3}\\.[0-9]{1,3}");
        }
        Matcher mt = pt.matcher(ipconfig);
        mt.find();
        String ip = mt.group();
        
		return ip;
	}

	// Client -> Obtaining it queue tasks
	
	@RequestMapping(value = "/getListTasks", method = RequestMethod.GET)
	public ArrayList<String> getListTasks(@RequestParam("name") String name) {
		System.out.println(" METHOD - GET LIST TASKS");
		ArrayList<String> result = new ArrayList<String>();
		try {
			Client c = new Client("http://"+this.ipAddress+":15672/api/", this.username, this.password);
			List<QueueInfo> listQueues = c.getQueues();
			for (QueueInfo queueInfo : listQueues) {
				result.add(queueInfo.getName());
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
		
	}
	// Client -> Obtain Finished Task (OK) 
	@RequestMapping(value = "/getEndTasks", method = RequestMethod.GET)
	public String getFinishedTask(@RequestParam("name") String name) {
		//NAME IS THE QUEUE to get data
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
	
	// Client -> Publish new Task (OK)
	@RequestMapping(value = "/uploadChunk", method = RequestMethod.POST)
	  public String publishNewClientTask(@RequestBody String msg) { 	
		  	// actions to do
		  	// STEP 1 - Obtain headers (baseQueueName + List of queues)
			// STEP 2 - Obtain each filter
			// STEP 3 - Rearm msg class
			// STEP 4 - based on filterParameter, replicate msgStructure + parameters for profile in the queue
			// STEP 5 - Create the finishedTaskQueue where workers will deploy the answers (1 per each queueFile)
	//@RequestParam("name") String name, @RequestParam("queues") String queuesList	
			
			// STEP 1 - Rearm MSG
			
			JsonUtility jsonUt = new JsonUtility();
			jsonUt.setType("Message");
			Message msgRearmed = (Message) jsonUt.fromJson(msg);
			System.out.println("MSG REARMED");
			
			String name = msgRearmed.getOriginalName();
					
			boolean durable = true;
		  	String finishedSpecificQueue;
		  	String msgEncoded;
		  	
		  	try {
		  		
		  		this.enterChannel.queueDeclare(DistributedRestController.enterQueue, durable, false, false, null);
		  		System.out.println("2 QUEUE DECLARED");
				// STEP 2 - obtain each filter
		  		
				String[] filters = msgRearmed.getEncodingProfiles().split(Pattern.quote("_"));
				String parameters=null;
				
				
			
				// FOR EACH FILTER -> take params from the arraylist and put in msg structure
				for (String filter : filters) {
					// STEP 4.0 - Rearm msg parameters
					parameters = (this.filterParameters.get(filter)).toString();
					System.out.println("PARAMS: "+parameters);
					msgRearmed.setParamsEncoding(parameters);
					msgRearmed.setName(name+"_"+filter);
					jsonUt.setObject(msgRearmed);
					msgEncoded = jsonUt.toJson();
					System.out.println("MSG ENCODED OK");
					// STEP 4.1 - Save in normal queue 
				
					this.enterChannel.basicPublish("", DistributedRestController.enterQueue, MessageProperties.PERSISTENT_TEXT_PLAIN, msgEncoded.getBytes());
					System.out.println(" MSG: saved in enteredQueue" );
					
					
					// STEP 5 - Create (or not) the specific finishedQueue 
					finishedSpecificQueue=name+"_"+filter;
					this.enterChannel.queueDeclare(finishedSpecificQueue, durable, false, false, null);
					System.out.println(" queueJob: "+finishedSpecificQueue+" has been created");
										
				}
				
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.getMessage();
			}

			return "ok - Ready";
	  }
	 
	// Worker -> Get Task (ok)
	@RequestMapping(value = "/getJob", method = RequestMethod.GET)
	  public String getJob (@RequestParam("name") String name) throws InterruptedException {
		//System.err.println("WORKER "+name+" IS GETTING JOB");
		GetResponse data = null;
		byte[] responseByte = null;
		String response = null;
		try {
			
				if (this.enterChannel.queueDeclarePassive(DistributedRestController.enterQueue).getMessageCount()>0) {
					//data = this.enterChannel.basicGet(this.enterQueue, true);
					data = this.enterChannel.basicGet(DistributedRestController.enterQueue,false);
					
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
	  public String persistFinishedWorkerTask(@RequestBody String msg ) { 	
		  	// actions to do
		  	// 0 - delete from list
			boolean validatedMsg = false;
			
			JsonUtility jsonUt = new JsonUtility();
			jsonUt.setType("Message");
			Message msgRearmed = (Message) jsonUt.fromJson(msg);
			
			synchronized (this.AckService) {
			
				System.out.println(this.AckService.toString());
				String idForAck = msgRearmed.getIdForAck();
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
			  	System.out.println("WORKER: "+msgRearmed.getWorkerName()+" / UPLOAD JOB "+msgRearmed.getName());
			  	System.err.println("PART:_ "+msgRearmed.getName()+"_"+msgRearmed.getPart());
			  	String queueFinishedUser = msgRearmed.getName();
			  	try {
					//this.enterChannel.queueDeclare(queueFinishedUser, false, false, false, null);
					this.enterChannel.basicPublish("", queueFinishedUser, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
					System.out.println(" TASK FINISHED saved " );
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			// SET DATA IN DATABASE
			
			String query = "insert into jobTracker (service, job, workerName, workerArchitecture, initTime, endTime, executionTime) values ('"+msgRearmed.getService()+"', '"+msgRearmed.getName()+'_'+msgRearmed.getPart()+"', '"+msgRearmed.getWorkerName()+"','"+msgRearmed.getWorkerArchitecture()+"',"+msgRearmed.getInitTime()+","+msgRearmed.getEndTime()+","+msgRearmed.getTotalTime()+")";
			System.out.println(" SQL: "+query);
			this.mdbc.doInsertOperation(query);
			// CONTINUE HERE
			return "ok - Ready";
	  }
	 
   
	private void readFromContext(HashMap<String, ArrayList<String>> filterParameters2) {
		// TODO Auto-generated method stub
				String par = "4k|high|4096x2160|libx264|15600|5.1|60|veryslow|6|3|2|ac3|512|48000|6";
				par+="//2K|high|2560x1440|libx264|7800|5.1|48|slower|6|3|2|ac3|512|48000|6";
				par+="//hd|high|1920x1080|libx264|3900|4.1|30|slow|6|3|2|ac3|320|48000|6";
				par+="//720|main|1280x720|libx264|2000|4.1|25|medium|3|3|1|aac|320|44100|2";
				par+="//480|main|852x480|libx264|900|3.1|25|fast|3|3|1|aac|256|44100|2";
				par+="//360|baseline|640x360|libx264|700|3.0|24|faster|0|0|0|aac|128|44100|2";
				par+="//240|baseline|424x240|libx264|500|3.0|24|ultrafast|0|0|0|aac|128|44100|2";
				
				String[] eachParam = par.split(Pattern.quote("//"));
				// this is grouped per line
				for (String string : eachParam) {
					String[] eachLineParts = string.split(Pattern.quote("|"));
					// 1st, header, 2nd parameters
					ArrayList<String> values = new ArrayList<String>();
					for (int i =1; i<eachLineParts.length; i++) {
						values.add(eachLineParts[i]);
					}
					filterParameters.put(eachLineParts[0], values);
				}
				
				
				
		
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