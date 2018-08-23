package newClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class ClientUploader {

	public ClientUploader () {
		
		// STEP 0 - Define FFMpeg Path
		String FFMpegBasePath = "C:\\DTP\\ffmpeg\\bin\\";
		
		// STEP 1 - Obtain ID for the activities
		int id = ThreadLocalRandom.current().nextInt(0, 100000);
		
		// STEP 2 - Define videoPath (1 per client up to now)
		String videoPath = "C:\\DTP\\video\\OriginalVideo.mp4";
		String[] a = videoPath.split(Pattern.quote("\\"));
		String videoName = (a[(a.length-1)].split(Pattern.quote(".")))[0];
		
		
		
		// STEP 2.6 - Select filters to apply
		ArrayList<String> filterSelected = new ArrayList<String>();
		filterSelected.add("hd");filterSelected.add("480"); 
		
		// STEP 3 - Define base for queueName (then add jobNumber)
		String baseQueueName = "u"+id+"_"+videoName;
		
		
		// STEP 4 - Define path for splitted video (only need once path and one splitted activitie)
		String outputPath = "C:\\DTP\\video\\splittedVideo\\"+videoName+"_splitted";
		
		// STEP 5 - Define chunks duration (now is based on a criteria, in SECS)
		int chunkDuration = 5; 
		
		// STEP 6 - Obtain Video Duration
		float videoDuration = this.obtainVideoDuration (FFMpegBasePath, videoPath);
		
		// STEP 7 -  Obtain numberOfChunks (duration/chunks)
		int chunks = (int) (videoDuration/chunkDuration);
		
		// Only per now chunks , then must be +1
		//ClientDownloader cd = new ClientDownloader(fileName, (chunks+1));
		
		// STEP 7.5 - Define arrayList with future threads (base on queue Name).
		//I have to wait until every queue is finish to end up the program.
		
		ArrayList<Thread> threadList = new ArrayList<Thread>();
		
		// STEP 7.6 - Obtain list of filters to apply (hd|480|etc) and
		// STEP 7.7 - Create a downlaoder thread for each filter (queue)
		
		String listOfProfilesToapply = null;
		for (int i=0; i<filterSelected.size();i++) {
			String filter = filterSelected.get(i);
			String queuePollingName = baseQueueName+"_"+filter;
			listOfProfilesToapply+=filter+"|";
			ClientDownloader cd = new ClientDownloader(queuePollingName, (chunks));
			Thread cdThread = new Thread(cd);
			cdThread.start();
			threadList.add(cdThread);
		}
		// remove last | in string 
		listOfProfilesToapply = listOfProfilesToapply.substring(0, (listOfProfilesToapply.length()-1));
		
		
		String splittedFile = "";
		String output; 
		byte[] data;
		Message msg; 
		JsonUtility jsonUt = new JsonUtility();
		String msgEncoded;
		
		// STEP 8 - Create HTTP Client request + POST REQUESTER
		HttpClient httpClient = HttpClientBuilder.create().build();
		String ipSpringServer = "10.2.3.67";
		// STEP 9 - Define URL based on name (base queue) + profiles (to create specific queue)
		String urlPost = "http://"+ipSpringServer+":8080/uploadChunk?name="+baseQueueName+"&queues="+listOfProfilesToapply;
		HttpURLConnection con = null;
		HttpPost request = new HttpPost("http://"+ipSpringServer+":8080/uploadChunk?name="+baseQueueName);
		
		HttpResponse response; 
		String params;
		String base64Data;
		
		// STEP 10 - Create (for each video fragmented part) a POST REQUEST
		// Then the server will replicate the request in the quantity of part that params filtered received
		for (int i=0; i<chunks; i++) {
				
				  
				try {
					output = outputPath+"_part_"+i+".mp4";
					splittedFile = this.splitVideoFile (i, videoPath, FFMpegBasePath, chunkDuration, output);
					params = "";
					//params = "ffmpeg -loglevel quiet -y -i "+videoPath+" -s 320x180 -aspect 16:9 -c:v libx264 -g 50 -b:v 220k -profile:v baseline -level 3.0 -r 15 -preset ultrafast -threads 0 -c:a aac -strict experimental -b:a 64k -ar 44100 -ac 2 "+videoPath+"_part_"+i+".mp4";
					// Once splitted, create Message and save in Queue
					// 1 - Read video file to byte
					// 2 - Encode to base64 video.
		            URL myurl = new URL(urlPost);
		            con = (HttpURLConnection) myurl.openConnection();

		            con.setDoOutput(true);
		            con.setRequestMethod("POST");
		            con.setRequestProperty("User-Agent", "Java client");
		            con.setRequestProperty("Content-Type", "application/json");

					data = Files.readAllBytes(new File(output).toPath());
					msg = new Message(baseQueueName, output, i, (chunks+1), data, params);
					jsonUt.setObject(msg);
					msgEncoded = jsonUt.toJson();
					
			            try (PrintWriter pw = new PrintWriter (new OutputStreamWriter (con.getOutputStream()))) {
			                
							pw.write(msgEncoded);
			            }

			            StringBuilder content;

			            try (BufferedReader in = new BufferedReader(
			                    new InputStreamReader(con.getInputStream()))) {

			                String line;
			                content = new StringBuilder();

			                while ((line = in.readLine()) != null) {
			                    content.append(line);
			                    content.append(System.lineSeparator());
			                }
			            }

			            //System.out.println(content.toString());

			        } catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
			            
			            con.disconnect();
			        }
				
			}
			try {
				
				// wait for all downloaded threads
				for (Thread t : threadList) {
					t.join();
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
			
	

	private String splitVideoFile(int part, String videoPath, String FFMpegBasePath, int chunkStep, String outputName) {
		
		String params = FFMpegBasePath+"ffmpeg.exe -y -i "+videoPath+" -ss "+(part*chunkStep)+" -t "+chunkStep+ " "+outputName;
		//System.out.println(params);
		
		try {
			
			this.errorStream(params);
			
  			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return outputName;
	}
	


	private String errorStream (String cmdString) throws IOException{
		//String[] commandList = {"powershell.exe", cmdString};
		//String[] commandList = {"cmd.exe", "/c", cmdString};
		String task = cmdString;
		Process powerShellProcess = Runtime.getRuntime().exec(task);
		
		BufferedReader stderr = new BufferedReader(new InputStreamReader(powerShellProcess.getErrorStream()));
		String line2;
		while ((line2 = stderr.readLine()) != null) {
			//System.out.println(line2);
		}
		stderr.close();
		return null;
	}
	
	private float obtainVideoDuration(String FFMpegBasePath, String videoPath) {
		String task = FFMpegBasePath+"ffprobe.exe -v error -show_entries format=duration "+videoPath+" -of default=noprint_wrappers=1:nokey=1";
		//System.out.println(task);
		Process powerShellProcess;
		float duration = 0;
		try {
			powerShellProcess = Runtime.getRuntime().exec(task);
			//Read Process Standart Output
			BufferedReader outputFromTerminal = new BufferedReader(new InputStreamReader(powerShellProcess.getInputStream()));
			String line;
			
			
			while ((line = outputFromTerminal.readLine()) != null) 	duration=Float.parseFloat(line);
	
			outputFromTerminal.close();
				
			System.out.println("Duration: "+duration);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return duration;
	}
	
	public static void main(String[] args) {
		ClientUploader cu = new ClientUploader();

	}

}
