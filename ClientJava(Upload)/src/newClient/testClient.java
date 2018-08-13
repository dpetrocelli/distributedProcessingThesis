package newClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class testClient {

	public testClient () {
		
		String FFMpegBasePath = "D:\\SOFT\\ffmpeg\\bin\\";
		
		String videoPath = "D:\\DownloadFromBrowser\\video.mp4";
		String outputPath = "D:\\DownloadFromBrowser\\output";
		int chunkDuration = 5; // IN SECS
		
		// Obtain Video Duration
		float videoDuration = this.obtainVideoDuration (FFMpegBasePath, videoPath);
		
		// Obtain numberOfChunks
		int chunks = (int) (videoDuration/chunkDuration);
		
		String splittedFile = "";
		String output; 
		byte[] data;
		Message msg; 
		JsonUtility jsonUt = new JsonUtility();
		String msgEncoded;
		StringEntity entity;
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost request = new HttpPost("http://localhost:8080/uploadChunk");
		HttpResponse response; 
		
		for (int i=0; i<1; i++) {
				output = outputPath+"_part_"+i+".mp4";
				splittedFile = this.splitVideoFile (i, videoPath, FFMpegBasePath, chunkDuration, output);
				// Once splitted, create Message and save in Queue
				// 1 - Read video file to byte
				// 2 - Encode to base64 video.
				try {
					data = Files.readAllBytes(new File(output).toPath());
					msg = new Message(output, i, (chunks+1), data, "nothing");
					jsonUt.setObject(msg);
					msgEncoded = jsonUt.toJson();
					//System.out.println(msgEncoded);
					System.out.println(output + " String Base64 MSG created");
					
					// 3 - Hacer post
					
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
		String[] commandList = {"powershell.exe", cmdString};
		Process powerShellProcess = Runtime.getRuntime().exec(commandList);
		
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
		testClient c = new testClient();
		
	}


}
