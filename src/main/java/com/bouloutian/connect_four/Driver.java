package com.bouloutian.connect_four;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Driver {

	public static void main(String[] args) {
		System.out.println("Starting Connect Four Analysis");
		try {
			// 622 457
			BufferedImage image = ImageIO.read(new File("res/GlassImage2.jpg"));
			int bestMove = ConnectFourVision.getMoveForImage(image);
			System.out.println("The best move is to go in column " + bestMove
					+ ".");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (VisionException e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}

}