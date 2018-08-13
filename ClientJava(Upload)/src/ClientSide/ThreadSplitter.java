package ClientSide;

import java.io.IOException;
import java.util.ArrayList;
import Objects.Task;
import Objects.Video;

public class ThreadSplitter implements Runnable{
	int sp, step ; 
	ArrayList <Task> tasks; 
	String videoPath, videoName, ffmpegPath;
	
	public ThreadSplitter (int i, int step, ArrayList <Task> tasks, String videoPath, String ffmpegPath, String videoName){
		this.sp = i;
		this.step = step;
		this.tasks = tasks;
		this.videoPath = videoPath;
		this.ffmpegPath = ffmpegPath;
		this.videoName = videoName;
	}
	@Override
	public void run() {
		int index = this.sp/this.step;
		
		
				
		//System.out.println("Index = "+index);
		String params = this.ffmpegPath+"ffmpeg -y -i "+this.videoPath+" -ss "+this.sp+" -t "+this.step+ " "+videoName+"_part_"+index+".mp4";
		System.out.println(params);
		try {
			Process powerShellProcess = Runtime.getRuntime().exec(params);
  				
			powerShellProcess.waitFor();
  			// once if finished, if possible to add to list of tasks
			Task taskStruct = new Task("1", "", new Video(videoName+"_part_"+index+".mp4"), "toProcess", videoName+"_part_"+index+".mp4");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
