package com.bouloutian.connect_four;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import java.awt.image.DataBufferByte;

public class ConnectFourVision {

	private static final int NUM_THRESHOLDS = 2;
	private static Mat originalBoardImage;

	public static int getMoveForImage(BufferedImage image) {
		// Load the OpenCV Library
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// Load the connect four original image
		originalBoardImage = bufferedImageToMat(image);

		// Create a copy of the original image to use
		Mat img = originalBoardImage.clone();

		// Apply thresholding techniques the board image
		Mat boardThreshold = generateBoardThreshold(img);
		showResult(boardThreshold);

		// Generate a mask from the thresholded board image
		//Mat projection = generateBoardProjection(boardThreshold.clone());
		//showResult(projection);

		// Find red tokens in the image
		//LinkedList<Circle> redTokens = findTokens(projection, Color.RED);

		// Find yellow tokens in the image
		//LinkedList<Circle> yellowTokens = findTokens(projection, Color.YELLOW);

		// Calculate the supposed positions of the pieces
		//Board board = calculateTokenPositions(projection.size(), redTokens,
		//		yellowTokens);
		//board.display();

		// Initialize the minimax structure to find a good move
		//Minimax minimax = new Minimax(board, 8);
		//int bestMove = minimax.alphaBeta(Board.MARK_RED);

		// Create and display a drawing for debugging and visualization purposes
		//Mat drawing = Mat.zeros(projection.size(), projection.type());
		//for (Circle circle : redTokens) {
		//	Core.circle(drawing, circle.getCenter(), circle.getRadius(),
		//			new Scalar(0, 0, 255), -1);
		//}
		//for (Circle circle : yellowTokens) {
		//	Core.circle(drawing, circle.getCenter(), circle.getRadius(),
		//			new Scalar(0, 255, 255), -1);
		//}
		//Size boardBounds = projection.size();
//		final float BLOCK_WIDTH = (float) boardBounds.width / Board.COLUMNS;
//		final float BLOCK_HEIGHT = (float) boardBounds.height / Board.ROWS;
//		for (int row = 0; row < Board.ROWS; row++) {
//			for (int col = 0; col < Board.COLUMNS; col++) {
//				Point point = new Point(col * BLOCK_WIDTH + BLOCK_WIDTH / 2,
//						(Board.ROWS - row - 1) * BLOCK_HEIGHT + BLOCK_HEIGHT
//								/ 2);
//				Core.circle(drawing, point, 1, new Scalar(255, 255, 255), 1);
//			}
//		}
//		showResult(drawing);
//
		return 0;
	}

	private static Mat bufferedImageToMat(BufferedImage bufferedImage) {
		BufferedImage image = bufferedImage;
		byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer())
				.getData();
		Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		mat.put(0, 0, data);
		return mat;
	};

	private static Board calculateTokenPositions(Size boardBounds,
			LinkedList<Circle> redTokens, LinkedList<Circle> yellowTokens) {
		Board result = new Board();
		final float BLOCK_WIDTH = (float) boardBounds.width / Board.COLUMNS;
		final float BLOCK_HEIGHT = (float) boardBounds.height / Board.ROWS;
		for (int row = 0; row < Board.ROWS; row++) {
			for (int col = 0; col < Board.COLUMNS; col++) {
				Point point = new Point(col * BLOCK_WIDTH + BLOCK_WIDTH / 2,
						(Board.ROWS - row - 1) * BLOCK_HEIGHT + BLOCK_HEIGHT
								/ 2);
				for (Circle token : redTokens) {
					if (token.containsPoint(point)) {
						result.set(col, Board.MARK_RED);
					}
				}
				for (Circle token : yellowTokens) {
					if (token.containsPoint(point)) {
						result.set(col, Board.MARK_BLACK);
					}
				}
			}
		}
		return result;
	}

	private static LinkedList<Circle> findTokens(Mat sourceImage,
			Color tokenColor) {
		Mat boardDist = distanceMatrixFromColor(sourceImage, tokenColor);
		Mat boardThreshold = new Mat();
		Imgproc.threshold(boardDist, boardThreshold, 50, 255,
				Imgproc.THRESH_BINARY_INV);
		LinkedList<MatOfPoint> contours = new LinkedList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(boardThreshold, contours, hierarchy,
				Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
		LinkedList<Circle> minCircles = new LinkedList<Circle>();
		for (int i = 0; i < contours.size(); i++) {
			double area = Imgproc.contourArea(contours.get(i));
			if (area > 100) {
				Point center = new Point();
				float[] radius = new float[1];
				Imgproc.minEnclosingCircle(new MatOfPoint2f(contours.get(i)
						.toArray()), center, radius);
				Circle circle = new Circle(center, (int) radius[0] + 5);
				minCircles.push(circle);
			}
		}
		return minCircles;
	}

	// Generates a thresholded image of the board based on each pixel's distance
	// from the board color
	// Repeated thresholding is used to try to get as much of the board as
	// possible
	// The resultant image should be white where teh board is lcoated and black
	// everywhere else
	private static Mat generateBoardThreshold(Mat originalImage) {
		Mat boardDist;
		Mat boardThreshold = new Mat();
		Color color = new Color(0, 0, 255);
		for (int i = 0; i < NUM_THRESHOLDS; i++) {
			System.out.println("Thresholding current color is: ("
					+ color.getRed() + "," + color.getGreen() + ","
					+ color.getBlue() + ")");
			boardDist = distanceMatrixFromColor(originalImage, color);
			Imgproc.threshold(boardDist, boardThreshold, 50, 255,
					Imgproc.THRESH_BINARY_INV);
			// Imgproc.blur(boardThreshold, boardThreshold, new Size(3,3));
			if (i < NUM_THRESHOLDS - 1) {
				int n = 0;
				int r = 0;
				int g = 0;
				int b = 0;
				double[] data = new double[1];
				double[] data2 = new double[1];
				for (int row = 0; row < originalImage.rows(); row++) {
					for (int col = 0; col < originalImage.cols(); col++) {
						data = boardThreshold.get(row, col);
						data2 = originalImage.get(row, col);
						if (data[0] > 0) {
							n++;
							b += data2[0];
							g += data2[1];
							r += data2[2];
						}
					}
				}
				color = new Color(r / n, g / n, b / n);
			}
		}

		// Dilate the image to remove small defects in the threshold
		for (int i = 0; i < 3; i++) {
			Imgproc.dilate(boardThreshold, boardThreshold, new Mat());
		}

		return boardThreshold;
	}

	private static Mat generateBoardProjection(Mat boardThreshold) {

		// Find the polygon enclosing the blue connect four board
		LinkedList<MatOfPoint> contours = new LinkedList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(boardThreshold, contours, hierarchy,
				Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
		int contourIndex = 0;
		for (int i = 0; i < contours.size(); i++) {
			double area = Imgproc.contourArea(contours.get(i));
			if (area > 1000) {
				contourIndex = i;
			}
		}

		// Find possible border lines of the enclosing polygon
		Mat newImage = Mat.zeros(boardThreshold.size(), boardThreshold.type());
		Mat debugImage = originalBoardImage.clone();
		Imgproc.drawContours(newImage, contours, contourIndex, new Scalar(255));
		Imgproc.drawContours(debugImage, contours, contourIndex, new Scalar(0,
				0, 255), 3);
		Mat lines = new Mat();
		Imgproc.HoughLines(newImage, lines, 1, Math.PI / 180, 75);
		LinkedList<Line> verticalLines = new LinkedList<Line>();
		LinkedList<Line> horizontalLines = new LinkedList<Line>();
		for (int i = 0; i < lines.cols(); i++) {
			double[] info = lines.get(0, i);
			Line line = new Line(info[0], info[1]);
			Core.line(debugImage, line.getPt1(), line.getPt2(), new Scalar(0,
					255, 0), 3);
			switch (line.getLineType()) {
			case LINE_VERTICAL:
				line.adjustVerticalLineForHeight(boardThreshold.height());
				verticalLines.push(line);
				break;
			case LINE_HORIZONTAL:
				line.adjustHorizontalLineForWidth(boardThreshold.width());
				horizontalLines.push(line);
				break;
			default:
				break;
			}
		}
		System.out.println("There are " + lines.cols()
				+ " lines that were detected.");
		showResult(debugImage);

		// Get the corners of the polygon and apply the transform
		Mat corners1 = calculateBorderFromLines(verticalLines, horizontalLines);
		Size resultSize = originalBoardImage.size();
		double[] data1 = { 0, 0 };
		double[] data2 = { resultSize.width, 0 };
		double[] data3 = { resultSize.width, resultSize.height };
		double[] data4 = { 0, resultSize.height };
		Mat corners2 = new Mat(new Size(4, 1), CvType.CV_32FC2);
		corners2.put(0, 0, data1);
		corners2.put(0, 1, data2);
		corners2.put(0, 2, data3);
		corners2.put(0, 3, data4);
		Mat transform = Imgproc.getPerspectiveTransform(corners1, corners2);
		Mat dst = new Mat(resultSize, originalBoardImage.type());
		Imgproc.warpPerspective(originalBoardImage, dst, transform, dst.size());
		return dst;

	}

	// Given lists of horizontal and vertical lines on the border of the image,
	// determine the border rectangle of the image
	private static Mat calculateBorderFromLines(LinkedList<Line> verticalLines,
			LinkedList<Line> horizontalLines) {

		// Create a list of every intersection
		LinkedList<Point> points = new LinkedList<Point>();
		for (Line vertLine : verticalLines) {
			for (Line horiLine : horizontalLines) {
				points.add(vertLine.intersectionWith(horiLine));
			}
		}

		// Find the average of all the points
		Point average = averagePoints(points);

		// Categorize the points
		LinkedList<Point> topLeftPoints = new LinkedList<Point>();
		LinkedList<Point> topRightPoints = new LinkedList<Point>();
		LinkedList<Point> bottomLeftPoints = new LinkedList<Point>();
		LinkedList<Point> bottomRightPoints = new LinkedList<Point>();
		for (Point point : points) {
			if (point.x < average.x) {
				if (point.y < average.y) {
					topLeftPoints.add(point);
				} else {
					bottomLeftPoints.add(point);
				}
			} else {
				if (point.y < average.y) {
					topRightPoints.add(point);
				} else {
					bottomRightPoints.add(point);
				}
			}
		}

		// Average the points in each of the categories
		Point pt1 = averagePoints(topLeftPoints);
		Point pt2 = averagePoints(topRightPoints);
		Point pt3 = averagePoints(bottomRightPoints);
		Point pt4 = averagePoints(bottomLeftPoints);
		Mat corners = new Mat(new Size(4, 1), CvType.CV_32FC2);

		// Return the matrix of the corners
		double[] data1 = { pt1.x, pt1.y };
		double[] data2 = { pt2.x, pt2.y };
		double[] data3 = { pt3.x, pt3.y };
		double[] data4 = { pt4.x, pt4.y };
		corners.put(0, 0, data1);
		corners.put(0, 1, data2);
		corners.put(0, 3, data4);
		corners.put(0, 2, data3);
		return corners;
	}

	public static Point averagePoints(LinkedList<Point> points) {
		Point result = new Point(0, 0);
		for (Point point : points) {
			result.x += point.x;
			result.y += point.y;
		}
		result.x /= points.size();
		result.y /= points.size();
		return result;
	}

	private static Mat distanceMatrixFromColor(Mat boardImage, Color color) {
		Mat boardDist = new Mat(boardImage.size(), CvType.CV_8U);
		for (int row = 0; row < boardImage.rows(); row++) {
			for (int col = 0; col < boardImage.cols(); col++) {
				double[] values = boardImage.get(row, col);
				double distRGB = Math.sqrt((Math.pow(color.getBlue()
						- values[0], 2)
						+ Math.pow(color.getGreen() - values[1], 2) + Math.pow(
						color.getRed() - values[2], 2)) / 3);
				boardDist.put(row, col, Math.round(distRGB));
			}
		}
		return boardDist;
	}

	// Displays in a window an image as specified by the parameter
	private static void showResult(Mat image) {
		Mat resizedImage = new Mat();
		Imgproc.resize(image, resizedImage,
				new Size(image.cols(), image.rows()));
		MatOfByte matOfByte = new MatOfByte();
		Highgui.imencode(".jpg", resizedImage, matOfByte);
		byte[] byteArray = matOfByte.toArray();
		BufferedImage bufImage = null;
		try {
			InputStream in = new ByteArrayInputStream(byteArray);
			bufImage = ImageIO.read(in);
			JFrame frame = new JFrame();
			frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
