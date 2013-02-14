/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package space.engine.util;

/**
 *
 * @author karol
 */
public final class Ph {

	private Ph() {
	}
	public static final double C = 100;
	public static final double CC = C / 100.0;
	public static final double YR = 1200;
	public static final double LY = C * YR;
	public static final double CC_PER_YEAR = CC / YR;
	public static final double _3C = 0.3 * C;
	public static final double _6C = 0.6 * C;

	public static String str_00_99(int i) {
		if (i < 10) {
			return "0" + (i);
		} else {
			return "" + (i);
		}
	}

	public static String timeToStr(double t) {
		int y = (int) Math.floor(t / YR);
		t -= y * YR;
		int m = (int) Math.floor((t) * 12.0 / YR);
		t -= m * YR / 12.0;
		int cm = (int) Math.floor(t * 1200.0 / YR);
		if (y > 0) {
			return (y) + "y " + (m) + "." + str_00_99(cm) + "m";
		} else {
			return (m) + "." + str_00_99(cm) + "m";
		}
	}
	public static final String[] months = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};

	public static String dateToStr(double t) {
		int y = (int) Math.floor(t / YR);
		t -= y * YR;
		int m = (int) Math.floor((t) * 12.0 / YR);
		t -= m * YR / 12.0;
		int cm = (int) Math.floor(t * 1200.0 / YR);
		return (y + 2250) + " " + months[m] + "/" + str_00_99(cm);
	}
}
