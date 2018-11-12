package WorkerX86;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Scanner;


public class Worker {
	String ipStringServer;
	
		public Worker (String ip) {
			this.ipStringServer = ip;
			this.activateWorker();
		}
			
		private void activateWorker() {
			
			String workerName;
			try {
				InetAddress addr = InetAddress.getLocalHost();
				workerName = addr.getHostName();
			}catch (Exception e) {
				workerName = "noName";
			}
			System.out.println("WORKER NAME: "+workerName);
			
			int threadId = (int) Thread.currentThread().getId();
			
			while (true) {
				
				//System.out.println(" STEP 0 -  Obtaining Job");
				// STEP 1 - Obtain Job
				try {
					String url = "http://"+this.ipStringServer+":8080/getJob?name="+workerName;
					URL obj = new URL(url);
					HttpURLConnection con = (HttpURLConnection) obj.openConnection();
					int responseCode = con.getResponseCode();
					//System.out.println("\nSending 'GET' request to URL : " + url);
					//System.out.println("Response Code : " + responseCode);
					BufferedReader in =new BufferedReader(new InputStreamReader(con.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					} in.close();
					   //print in String
					System.out.println(" STEP 1 -  Job Obtained ");
					
					long initTime = System.currentTimeMillis();
				
					String msgNotEncoded = response.toString();
					System.out.println(msgNotEncoded.substring(msgNotEncoded.length()-10, msgNotEncoded.length()));
					JsonUtility jsonUt = new JsonUtility();
					jsonUt.setType("Message");
					
					// FIRST SPLIT THE MSG CODE FOR ACK
					//String[] msgParts = msgNotEncoded.split(Pattern.quote("|"));
					
					// 0 Index msg
					// 1 Index ackCode
					//String idForAck = (msgParts[1]).split(Pattern.quote("="))[1];
					// STEP 2 - Rearm Message
					Message msgRearmed = (Message) jsonUt.fromJson(msgNotEncoded);
					String idForAck = msgRearmed.getIdForAck();
					
					
					// - CREATE OBJECT depending on the task received.
					String service = msgRearmed.getService();
					
					if (service.equals("videoCompression")) {
						FFMpegClass ffmpegClass = new FFMpegClass (msgRearmed, jsonUt, workerName, idForAck, this.ipStringServer, con, initTime);
					}
					
			            //System.out.println(content.toString());
					
				}catch (Exception e) {
					System.err.println(" NOT a valid MSG ");
				}
				
		        
		       try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}

		
		
	
	public static void main(String[] args) throws IOException {
		
		System.out.println("Ingrese ip del servidor");
		Scanner keyboard = new Scanner(System.in);
		String ipSpringServer = keyboard.nextLine();
		Worker wk = new Worker(ipSpringServer);
		
		/*
		 * Worker activities:
		 * 1. First download Job (get to enterQueue)
		 * 2. Rearm MSG where
		 * 		-> String userAndQueueName
		 * 		-> filename (input)
		 * 		-> #part
		 * 		-> total parts
		 * 		-> typeOfService (current - FFMPEg compression
		 * 		-> binary data[]
		 * 		-> profile to apply (params)
		 * 
		 * 3. Save binary in localdisk
		 * 4. Apply filter (ffmpeg) and save in new place
		 * 5. Create new Message Structure (new encoded file)
		 * 6. create POST request to queue identify by String userAndQueueName
		 * All done.
		 */		
			
	}
}