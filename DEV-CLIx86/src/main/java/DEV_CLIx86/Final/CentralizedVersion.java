package DEV_CLIx86.Final;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class CentralizedVersion {
	ArrayList<String> listOfVideos;
	
	public CentralizedVersion() {
		
		// STEP 1 - List of Videos
		listOfVideos = new ArrayList<String>();
		
		listOfVideos.add("C:\\DTP\\video\\OriginalVideo.mp4");
		//listOfVideos.add("C:\\DTP\\video\\bigbuckbunny_1500.mp4");
		//listOfVideos.add("C:\\DTP\\video\\BIRDS.mp4");
		
		// STEP 2 - List of parameters
		HashMap<String, ArrayList<String>> filterParameters = new HashMap<String,ArrayList<String>>();
		this.readFromFile (filterParameters, "src/main/java/videoParameters");
		
		// STEP 3 - Define profiles to apply each file
		ArrayList<String> filterSelected = new ArrayList<String>();
		filterSelected.add("hd");filterSelected.add("480");
		String videoName;
		
		String FFMpegBasePath = "C:\\DTP\\ffmpeg\\bin\\";
		
		String realOutput;
		
		long timeStart, timeEnd;
		
		// STEP 4 - FOR EACH VIDEO FILE
		for (String videoFile : listOfVideos) {
			
			
			 videoName = videoFile.split(Pattern.quote("\\"))[3];
			 timeStart = System.currentTimeMillis();
			// STEP 5 - FOR EACH PROFILE OF EACH VIDEOFILE
			for (String filter : filterSelected) {
				realOutput = "C:\\DTP\\video\\exitCentralized\\compressed_"+filter+"_"+videoName;
				ArrayList<String> paramsParts = filterParameters.get(filter);
				
				
				String params = FFMpegBasePath+"ffmpeg.exe -loglevel quiet -y -i "+videoFile+" -s "+paramsParts.get(1)+" -aspect 16:9 -c:v "+paramsParts.get(2)+" -g 50 -b:v "+paramsParts.get(3)+"k -profile:v "+paramsParts.get(0)+" -level "+paramsParts.get(4)+" -r "+paramsParts.get(5)+" -preset "+paramsParts.get(6)+" -threads 0 -c:a aac -strict experimental -b:a ";
				params+=paramsParts.get(11)+"k -ar "+paramsParts.get(12)+" -ac "+paramsParts.get(13)+" "+realOutput;
				
				
				System.out.println(" PARAMS: "+params);					
				Process powerShellProcess;
				try {
					powerShellProcess = Runtime.getRuntime().exec(params);
					BufferedReader stderr = new BufferedReader(new InputStreamReader(powerShellProcess.getErrorStream()));
					String line2;
					
					//System.out.println(" STEP 4 -  File filtered and saved");
					while ((line2 = stderr.readLine()) != null) {
						System.out.println(line2);
					}
					stderr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			timeEnd = System.currentTimeMillis();
			
			System.out.println(" THE COMPRESSION FOR: "+videoName+ " HAS SPENT: "+ ((timeEnd-timeStart)/1000) + " Segs");
			
		}
				
			
	}
	public static void main(String[] args) {
		CentralizedVersion cv = new CentralizedVersion();

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
