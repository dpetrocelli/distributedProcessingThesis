package WorkerX86;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.nio.file.Files;
import java.util.regex.Pattern;


public class Worker {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
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
		
		
			String workerName;
			try {
				InetAddress addr = InetAddress.getLocalHost();
				workerName = addr.getHostName();
			}catch (Exception e) {
				workerName = "noName";
			}
			
			String ipSpringServer = "192.168.0.29";
			int threadId = (int) Thread.currentThread().getId();
			
			while (true) {
				
				//System.out.println(" STEP 0 -  Obtaining Job");
				// STEP 1 - Obtain Job
				String url = "http://"+ipSpringServer+":8080/getJob?name="+workerName;
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
				
				try {
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
						FFMpegClass ffmpegClass = new FFMpegClass (msgRearmed, jsonUt, workerName, idForAck, ipSpringServer, con);
					}
					
			            //System.out.println(content.toString());
					
				}catch (Exception e) {
					System.err.println(" NOT a valid MSG ");
				}
				
		        
		       try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			
		}
			
			
			
	
			
			
			
	}
