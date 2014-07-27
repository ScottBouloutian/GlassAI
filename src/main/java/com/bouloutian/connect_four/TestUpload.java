package com.bouloutian.connect_four;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TestUpload {

	private static final String TEST_IMAGE = "src/main/resources/connect_four.jpg";
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		File imageFile = new File(TEST_IMAGE);
		try {
			BufferedImage image = ImageIO.read(imageFile);
			String result = AmazonInterface.upload(image);
			System.out.println(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println(endTime-startTime);
	}

}
