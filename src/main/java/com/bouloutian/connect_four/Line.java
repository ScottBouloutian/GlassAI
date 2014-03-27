package com.bouloutian.connect_four;
import org.opencv.core.Point;
import org.opencv.core.Size;

public class Line implements Comparable{
	private static final double RANGE = Math.PI / 8;
	private Point pt1;
	private Point pt2;
	private double slope;
	private LineType lineType;

	public Line(LineType lineType) {
		pt1 = new Point(0, 0);
		pt2 = new Point(0, 0);
		slope=0;
		this.lineType = lineType;
	}

	public Line(int x1, int y1, int x2, int y2) {
		pt1 = new Point(x1, y1);
		pt2 = new Point(x2, y2);
		slope=(double)(y1-y2)/(x1-x2);
		lineType = LineType.LINE_UNKNOWN;
	}

	public Line(double rho, double theta) {
		pt1 = new Point();
		pt2 = new Point();
		double a = Math.cos(theta);
		double b = Math.sin(theta);
		double x0 = a * rho;
		double y0 = b * rho;
		pt1.x = Math.round(x0 + 1000 * (-b));
		pt1.y = Math.round(y0 + 1000 * (a));
		pt2.x = Math.round(x0 - 1000 * (-b));
		pt2.y = Math.round(y0 - 1000 * (a));
		slope=(pt1.y-pt2.y)/(pt1.x-pt2.x);
		if (isInRange(theta, 0, RANGE)
				|| isInRange(theta, Math.PI - RANGE, Math.PI)) {
			orderEndpointsY();
			lineType = LineType.LINE_VERTICAL;
		} else if (isInRange(theta, Math.PI / 2 - RANGE, Math.PI / 2 + RANGE)) {
			orderEndpointsX();
			lineType = LineType.LINE_HORIZONTAL;
		} else if (isInRange(theta, Math.PI / 4 - RANGE, Math.PI / 4 + RANGE)
				|| isInRange(theta, 3 * Math.PI / 4 - RANGE, 3 * Math.PI / 4
						+ RANGE)) {
			lineType = LineType.LINE_DIAGONAL;
		} else {
			lineType = LineType.LINE_UNKNOWN;
		}
	}

	public void adjustHorizontalLineForWidth(int width){
		if(pt1.x<0){
			pt1.y-=(int)(slope*pt1.x);
			pt1.x=0;
		}
		if(pt2.x<width){
			pt2.y+=(int)(slope*(width-pt2.x));
			pt2.x=width;
		}
	}
	
	public void adjustVerticalLineForHeight(int height){
		if(pt1.y<0){
			pt1.x-=(int)(1/slope*pt1.y);
			pt1.y=0;
		}
		if(pt2.y<height){
			pt2.x+=(int)(1/slope*(height-pt2.y));
			pt2.y=height;
		}
	}
	
	private void orderEndpointsX() {
		if (pt2.x < pt1.x) {
			Point temp = pt1;
			pt1 = pt2;
			pt2 = temp;
		}
	}

	private void orderEndpointsY() {
		if (pt2.y < pt1.y) {
			Point temp = pt1;
			pt1 = pt2;
			pt2 = temp;
		}
	}

	public void addLine(Line line) {
		pt1.x += line.pt1.x;
		pt1.y += line.pt1.y;
		pt2.x += line.pt2.x;
		pt2.y += line.pt2.y;
	}

	public void divideLineBy(int n) {
		pt1.x /= n;
		pt1.y /= n;
		pt2.x /= n;
		pt2.y /= n;
	}

	public Point getPt1() {
		return pt1;
	}

	public Point getPt2() {
		return pt2;
	}

	public LineType getLineType() {
		return lineType;
	}

	public double getSlope(){
		return slope;
	}
	
	private boolean isInRange(double val, double lowerBound, double upperBound) {
		return (val >= lowerBound && val < upperBound);
	}

	public String toString(){
		return pt1.toString() + " " + pt2.toString();
	}
	
	public Point intersectionWith(Line line){
		double x=(slope*pt1.x-pt1.y+line.slope*line.pt1.x+line.pt1.y)/(slope-line.getSlope());
		double y=slope*(x-pt1.x)+pt1.y;
		return new Point((int)x,(int)y);
	}
	
	public int compareTo(Object object){
		Line line=(Line)object;
		if(line.lineType==lineType){
			switch(lineType){
			case LINE_VERTICAL:
				if((pt1.x+pt2.x)<(line.pt1.x+line.pt2.x)){
					return -1;
				}else{
					return 1;
				}
			case LINE_HORIZONTAL: 
				if((pt1.y+pt2.y)<(line.pt1.y+line.pt2.y)){
					return -1;
				}else{
					return 1;
				}
			default:
				return 0;
			}
		}else{
			return line.lineType.compareTo(lineType);
		}
	}
}
