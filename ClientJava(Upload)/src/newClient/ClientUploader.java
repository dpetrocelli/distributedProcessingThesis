package newClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import ClientSide.Client;




public class ClientUploader {

	public ClientUploader () {
		
		String FFMpegBasePath = "D:\\docs\\Thesis2018\\distributedProcessingThesis\\libraries\\ffmpeg\\bin\\";
		int id = 1;
		String videoPath = "D:\\saveDownload\\videos\\video.mp4";
		/* FILE PATH TO QUEUE IS: 
		 * u -> user
		 * id -> integer name
		 * name -> video name splitting extension
		 * FINALLY -> u1_video (for this example)
		 */
		String fileName = "u1_video";
		
		
		String outputPath = "D:\\saveDownload\\videos\\output";
		int chunkDuration = 5; // IN SECS
		
		// 2 - Obtain Video Duration
		float videoDuration = this.obtainVideoDuration (FFMpegBasePath, videoPath);
		
		// 3 -  Obtain numberOfChunks
		int chunks = (int) (videoDuration/chunkDuration);
		
		ClientDownloader cd = new ClientDownloader(fileName, (chunks+1));
		Thread cdThread = new Thread(cd);
		cdThread.start();
		
		
		String splittedFile = "";
		String output; 
		byte[] data;
		Message msg; 
		JsonUtility jsonUt = new JsonUtility();
		String msgEncoded;
		StringEntity entity;
		HttpClient httpClient = HttpClientBuilder.create().build();
		String ipSpringServer = "192.168.0.20";
		HttpPost request = new HttpPost("http://"+ipSpringServer+":8080/uploadChunk?name="+fileName);
		
		HttpResponse response; 
		String params;
		String base64Data;
		
		for (int i=0; i<3; i++) {
				output = outputPath+"_part_"+i+".mp4";
				splittedFile = this.splitVideoFile (i, videoPath, FFMpegBasePath, chunkDuration, output);
				params = "ffmpeg -loglevel quiet -y -i "+videoPath+" -s 320x180 -aspect 16:9 -c:v libx264 -g 50 -b:v 220k -profile:v baseline -level 3.0 -r 15 -preset ultrafast -threads 0 -c:a aac -strict experimental -b:a 64k -ar 44100 -ac 2 "+videoPath+"_part_"+i+".mp4";
				// Once splitted, create Message and save in Queue
				// 1 - Read video file to byte
				// 2 - Encode to base64 video.
				
				
				try {
					data = Files.readAllBytes(new File(output).toPath());
					msg = new Message(fileName, output, i, (chunks+1), data, params);
					jsonUt.setObject(msg);
					msgEncoded = jsonUt.toJson();
					
					
					entity = new StringEntity(msgEncoded, ContentType.APPLICATION_JSON);
					request.setEntity(entity);
					request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
					
					try (CloseableHttpResponse httpResponse = (CloseableHttpResponse) httpClient.execute(request)) {
				        String content = EntityUtils.toString(httpResponse.getEntity());
				 
				        int statusCode = httpResponse.getStatusLine().getStatusCode();
				        System.out.println("statusCode = " + statusCode);
				        //System.out.println("content = " + content);
				    } catch (IOException e) {
				        //handle exception
				        e.printStackTrace();
				    }
			        response = httpClient.execute(request);
			        System.out.println(response.getStatusLine().getStatusCode());
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					// TODO Auto-generated catch block
					
				}
				
			}
		
		}
			
	private String splitVideoFile(int part, String videoPath, String FFMpegBasePath, int chunkStep, String outputName) {
		
		String params = FFMpegBasePath+"ffmpeg.exe -y -i "+videoPath+" -ss "+(part*chunkStep)+" -t "+chunkStep+ " "+outputName;
		System.out.println(params);
		
		try {
			//Process powerShellProcess = Runtime.getRuntime().exec(params);
			this.errorStream(params);
			
  			// once if finished, if possible to add to list of tasks
			//Task taskStruct = new Task("1", "", new Video(videoName+"_part_"+index+".mp4"), "toProcess", videoName+"_part_"+index+".mp4");
			
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
