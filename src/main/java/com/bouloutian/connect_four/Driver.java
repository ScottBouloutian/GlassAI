package com.bouloutian.connect_four;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Driver {

	public static void main(String[] args) {
		System.out.println("Starting Connect Four Analysis");
		try {
			BufferedImage image = ImageIO.read(new File("res/GlassImage.png"));
			ConnectFourVision.getMoveForImage(image);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done");
	}

}