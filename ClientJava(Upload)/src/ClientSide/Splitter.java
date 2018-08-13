package ClientSide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import Objects.Task;

import java.util.ArrayList;

public class Splitter implements Runnable{
	
	String ffmpegPath, videoPath;
	ArrayList<Task> tasks;
	int quantityOfParts;
	public Splitter(String path, String ffmpegPath2, ArrayList<Task> tasks, int quantityOfParts) {
		// TODO Auto-generated constructor stub
		this.tasks = tasks;
		this.quantityOfParts = quantityOfParts;
		this.ffmpegPath = ffmpegPath2;
		this.videoPath = path;
	}

	@Override
	public void run() {
		
		
			
			
			String videoName = videoPath.substring(0, (videoPath.length()-4));
		
	  		// 2) Obtain the video duration
	  		System.out.println("Video Exists, continue ");
	  		long startTime = System.currentTimeMillis();
	  		try {
	  			// Obtain the video Duration
	  			String task = ffmpegPath+"ffprobe -v error -show_entries format=duration "+videoPath+" -of default=noprint_wrappers=1:nokey=1";
	  			//System.out.println(task);
	  			Process powerShellProcess = Runtime.getRuntime().exec(task);
	  			
	  			//Read Process Standart Output
	  			BufferedReader outputFromTerminal = new BufferedReader(new InputStreamReader(powerShellProcess.getInputStream()));
	  			String line;
	  			float duration = 0;
	  			
	  			while ((line = outputFromTerminal.readLine()) != null) {
	  				
	  				duration=Float.parseFloat(line);
	  			}
	  			outputFromTerminal.close();
	  			
	  			System.out.println("Duration: "+duration);
	  
	  	// 3) Once i have reached video duration:
	  	// a) Use Thread Splitter (Backend) to obtain parts
	  	// b) Use Thread Manager (Backend) to send parts to neighboors
	  		
	  			// B) The following object will be encharged of deliver parts to client
	  			
	  			// Taskslist is MAP entity <id, name, state>
	  			//ThreadManager tm = new ThreadManager(tasksList, workers);
	  			//Thread tmThread = new Thread(tm);
	  			//tmThread.start();
	  			
	  		int i= 0;
	  		int numberOfActiveThreads;
	  		int counter =0;
	  		int step = 5;
	  		ArrayList<Thread> activeThreads = new ArrayList<Thread>();
	  		ArrayList<Thread> threadsRemaining = new ArrayList<Thread>();
	  		
	  		// a) Prepare headers -> Create whole threads needed
	  		// TEST duration -> force
	  		duration = 5;
	  		this.quantityOfParts = (int) (duration/step);
	  		while (i<(int)duration){
	  			System.out.println("Entro a splitter thread");
	  			Thread a = new Thread(new ThreadSplitter(i, step,tasks, videoPath, ffmpegPath, videoName));
	  			threadsRemaining.add(a);
	  			i+=step;
	  			
	  		}
	  		
	  		// b) Assign threads in block, dinamically
	  		int numberOfSimultaneousThreads = 5;
	  		
	  		// while threads remaining do
	  		while (threadsRemaining.size() >0){
	  			// obtain active threads
	  			
	  			
  				// now, test if some active thread has finished
  				int activeThreadsSize = activeThreads.size();
  				int count = 0;
  				int index = 0;
  				while (count < activeThreadsSize){
  					Thread thr = activeThreads.get(index);
						if (!(thr.isAlive())){
							activeThreads.remove(thr);
							
						}
						else index++;
  					count++;
  				}
  				//System.out.println("active Threads: "+activeThreads.size());
	  			// if active < simultaneous -> complete
	  			//System.out.println("vuelta y vuelta");
	  			if (activeThreadsSize< numberOfSimultaneousThreads){
	  				
	  				// completing activity
	  				for (int z = activeThreadsSize; z< numberOfSimultaneousThreads; z++){
	  					try{
	  						Thread workerThread = threadsRemaining.get(0);
		  					threadsRemaining.remove(workerThread);
		  					activeThreads.add(workerThread);
		  					
		  					//start thread
		  					workerThread.start();
	  					} catch (Exception e) {
							
						}
	  					
	  				}
	  				
	  				
	  			}
	  			// Wait for next analysis loop
	  			Thread.sleep(1000);
	  		}
	  		System.out.println("salimos?");
	  			
	  		long endTime = System.currentTimeMillis();
	  		
	  		System.out.println("Elapsed time: "+(endTime-startTime)+ " ms");
	  		
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  	
		
	}

}
