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
			
			String ipSpringServer = "192.168.1.103";
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
					JsonUtility jsonUt = new JsonUtility();
					jsonUt.setType("Message");
					
					// STEP 2 - Rearm Message
					Message msgRearmed = (Message) jsonUt.fromJson(msgNotEncoded);
					
					// STEP 2.5 - Save the queue where i must reply my msg
					String returnQueue = msgRearmed.getName();
					System.out.println(" vIDEO PART: "+returnQueue + msgRearmed.part);
					System.out.println(" STEP 2 -  Msg rearmed");
					// STEP 3 - Download byte[] to file local disk
					String originalName = "C:\\DTP\\video\\splittedVideo\\"+msgRearmed.getName()+"_splitted_part_0.mp4";
					String[] parts = msgRearmed.getName().split(Pattern.quote("\\"));
					String[] megaparts = (parts[(parts.length-1)].split(Pattern.quote("_")));
					String saveVideoName = megaparts[0]+"_"+megaparts[1];
					String numberOfPart = megaparts[(megaparts.length-1)].split(Pattern.quote("."))[0];
					saveVideoName+="_"+numberOfPart;
					
					String localPath = "C:\\DTP\\video\\fileToApplyFilter\\FTAF_"+saveVideoName+".mp4";
					//System.out.println("LOCAL PATH"+localPath);
					try (FileOutputStream fos = new FileOutputStream(localPath)) {
						   fos.write(msgRearmed.getData());
						   //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
					}
					System.out.println(" STEP 3 -  binary File Saved");
					// STEP 4 - Apply Filter - Organize
					
					String FFMpegBasePath = "C:\\DTP\\ffmpeg\\bin\\";
					String outputPath = "C:\\DTP\\video\\compressedInWorker\\"+saveVideoName+"_WKCOMP";
					//System.out.println("VIDEO NAME:"+outputPath);
					String realOutput = outputPath+"_part_"+msgRearmed.getPart()+".mp4";
					//System.out.println("VIDEO NAME:"+realOutput);
					// Obtain parameters from msg
					String parametersFromMsg = msgRearmed.getParamsEncoding();
					
					parametersFromMsg = parametersFromMsg.substring(1, ((parametersFromMsg.length()-1)));
					String[] paramsPart = parametersFromMsg.split(Pattern.quote(","));
					
					String params = FFMpegBasePath+"ffmpeg.exe -loglevel quiet -y -i "+localPath+" -s"+paramsPart[1]+" -aspect 16:9 -c:v"+paramsPart[2]+" -g 50 -b:v"+paramsPart[3]+"k -profile:v "+paramsPart[0]+" -level"+paramsPart[4]+" -r"+paramsPart[5]+" -preset"+paramsPart[6]+" -threads 0 -c:a aac -strict experimental -b:a";
					params+=paramsPart[11]+"k -ar"+paramsPart[12]+" -ac"+paramsPart[13]+" "+realOutput;
					
					
					//System.out.println(" PARAMS: "+params);					
					Process powerShellProcess = Runtime.getRuntime().exec(params);
					
					
					BufferedReader stderr = new BufferedReader(new InputStreamReader(powerShellProcess.getErrorStream()));
					String line2;
					
					System.out.println(" STEP 4 -  File filtered and saved");
					while ((line2 = stderr.readLine()) != null) {
						System.out.println(line2);
					}
					stderr.close();
					
					
					System.out.println(" STEP 5 -  Create msg response ");
					
					byte[] data = Files.readAllBytes(new File(realOutput).toPath());
					Message msg = new Message(null, null, msgRearmed.getPart(), msgRearmed.getqParts(), data, null);
					jsonUt.setObject(msg);
					String msgEncoded = jsonUt.toJson();
					
					System.out.println(" STEP 6 -  POST (push) to userQueueFile ");
					
					
					String request = "http://"+ipSpringServer+":8080/uploadFinishedJob?server="+workerName+"&name="+returnQueue+"&part="+(msgRearmed.getName()+"_part_"+msgRearmed.getPart());			
					
					URL myurl = new URL(request);
		            con = (HttpURLConnection) myurl.openConnection();

		            con.setDoOutput(true);
		            con.setRequestMethod("POST");
		            con.setRequestProperty("User-Agent", "Java client");
		            con.setRequestProperty("Content-Type", "application/json");
		            
					byte[] dataFiltered = Files.readAllBytes(new File(realOutput).toPath());
					Message msgResponse = new Message(returnQueue, realOutput, msgRearmed.getPart(), msgRearmed.getqParts(), dataFiltered, params);
					jsonUt.setObject(msgResponse);
					msgEncoded = jsonUt.toJson();
					 try (PrintWriter pw = new PrintWriter (new OutputStreamWriter (con.getOutputStream()))) {
			                
							pw.write(msgEncoded);
			            }
					 System.out.println(" STEP 7 -  POST DONE ");
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

			            //System.out.println(content.toString());
					
				}catch (Exception e) {
					e.printStackTrace();
				}
				
		        
		       try {
				Thread.sleep(5000000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			
		}
			
			
			
	
			
			
			
	}
