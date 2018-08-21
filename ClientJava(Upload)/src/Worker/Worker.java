package Worker;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonObject;

import newClient.Message;



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
			 * 		-> binary data[]
			 * 		-> profile to apply (params)
			 * 
			 * 3. Save binary in localdisk
			 * 4. Apply filter (ffmpeg) and save in new place
			 * 5. Create new Message Structure (new encoded file)
			 * 6. create POST request to queue identify by String userAndQueueName
			 * All done.
			 */
			String ipSpringServer = "192.168.0.20";
			while (true) {
				
				System.out.println(" STEP 0 -  Obtaining Job");
				// STEP 1 - Obtain Job
				String url = "http://"+ipSpringServer+":8080/getJob";
				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				int responseCode = con.getResponseCode();
				System.out.println("\nSending 'GET' request to URL : " + url);
				System.out.println("Response Code : " + responseCode);
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
					JsonUtility jsonUt = new JsonUtility();
					jsonUt.setType("Message");
					
					// STEP 2 - Rearm Message
					Message msgRearmed = (Message) jsonUt.fromJson(msgNotEncoded);
					System.out.println(" STEP 2 -  Msg rearmed");
					// STEP 3 - Download byte[] to file local disk
					String localPath = "C:\\fileToApplyFilter.mp4";
					try (FileOutputStream fos = new FileOutputStream(localPath)) {
						   fos.write(msgRearmed.getData());
						   //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
					}
					System.out.println(" STEP 3 -  binary File Saved");
					// STEP 4 - Apply Filter
					
					String FFMpegBasePath = "D:\\docs\\Thesis2018\\distributedProcessingThesis\\libraries\\ffmpeg\\bin\\";
					String outputPath = "C:\\output";
					String realOutput = outputPath+"_part_"+msgRearmed.getPart()+".mp4";
					String params = FFMpegBasePath+"ffmpeg.exe -loglevel quiet -y -i "+localPath+" -s 320x180 -aspect 16:9 -c:v libx264 -g 50 -b:v 220k -profile:v baseline -level 3.0 -r 15 -preset ultrafast -threads 0 -c:a aac -strict experimental -b:a 64k -ar 44100 -ac 2 "+realOutput;
					Process powerShellProcess = Runtime.getRuntime().exec(params);
					
					BufferedReader stderr = new BufferedReader(new InputStreamReader(powerShellProcess.getErrorStream()));
					String line2;
					System.out.println(" STEP 4 -  File filtered and saved");
					while ((line2 = stderr.readLine()) != null) {
						//
					}
					stderr.close();
					System.out.println(" STEP 5 -  Create msg response ");
					
					byte[] data = Files.readAllBytes(new File(realOutput).toPath());
					Message msg = new Message(null, null, msgRearmed.getPart(), msgRearmed.getqParts(), data, null);
					jsonUt.setObject(msg);
					String msgEncoded = jsonUt.toJson();
					
					System.out.println(" STEP 6 -  POST (push) to userQueueFile ");
					
					
					String request = "http://localhost:8080/uploadFinishedJob?name="+msgRearmed.getUserAndQueueName();			
					
					URL myurl = new URL(request);
		            con = (HttpURLConnection) myurl.openConnection();

		            con.setDoOutput(true);
		            con.setRequestMethod("POST");
		            con.setRequestProperty("User-Agent", "Java client");
		            con.setRequestProperty("Content-Type", "application/json");
		            
					byte[] dataFiltered = Files.readAllBytes(new File(realOutput).toPath());
					Message msgResponse = new Message(null, realOutput, msgRearmed.getPart(), msgRearmed.getqParts(), dataFiltered, params);
					jsonUt.setObject(msgResponse);
					msgEncoded = jsonUt.toJson();
					 try (PrintWriter pw = new PrintWriter (new OutputStreamWriter (con.getOutputStream()))) {
			                
							pw.write(msgEncoded);
			            }

			            StringBuilder content;

			            try (BufferedReader in1 = new BufferedReader(
			                    new InputStreamReader(con.getInputStream()))) {

			                String line;
			                content = new StringBuilder();

			                while ((line = in1.readLine()) != null) {
			                    content.append(line);
			                    content.append(System.lineSeparator());
			                }
			            }

			            System.out.println(content.toString());
					
				}catch (Exception e) {
					System.err.println(" NOT A VALID MESSAGE (Empty Queue)");
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
