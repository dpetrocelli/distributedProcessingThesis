package ImageCreationAndProcessing;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

public class PicObtainer {

	public static void main(String[] args) {
			
		while (true) {
			String folder = "c:/Users/l12082/Desktop/captures/";
		// ADD LINUX FOLDER /tmp
		    BufferedImage captura = null;
			try {
				int x, y, width,height;
				x = 0;
				y = 0;
				width = 100;
				height = 100;
				captura = new Robot().createScreenCapture(new Rectangle(120, 280, 60, 25) );
			} catch (HeadlessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		     // Guardar Como JPEG
		     Date dNow = new Date( );
		     SimpleDateFormat ft = new SimpleDateFormat ("dd-MM-yyyy.hh-mm-ss");

		     String date = ft.format(dNow);
		     File file = new File(folder+date+".jpg");
		     try {
				ImageIO.write(captura, "jpg", file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		     try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	
	}
}
