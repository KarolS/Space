/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package space.engine;

import java.util.ArrayList;
import java.util.List;
import vytah.math.Vector3D;

/**
 *
 * @author karol
 */
public class StarMap {

	public List<Star> stars = new ArrayList<Star>();
	private boolean initted = false;

	public synchronized void addStar(Star s) {
		s.setId(stars.size());
		stars.add(s);
	}

	public void addStar(String name, Vector3D location) {
		Star s = new Star();
		s.name = name;
		s.setLocation(location);
		addStar(s);
	}
	public Planet getPlanetById(long id){
		return stars.get((int) (id & 0xffff)).planets[(int)(id>>>32)];
	}

	public synchronized void defaultInit() {
		addStar(new Star("Sol", 0, 0, 0, 0, 0, 0, 0));
		addStar(new Star("Proxima Centauri", 4.2421, 14, 29, 43.0, -62, 40, 46));
		addStar(new Star("Alpha Centauri A", 4.3650, 14, 39, 36.5, -60, 50, 02));
		addStar(new Star("Alpha Centauri B", 4.3650, 14, 39, 35.1, -60, 50, 14));
		addStar(new Star("Velox Barnardi", 5.963, 17, 58, 48.5, +4, 41, 36));
		addStar(new Star("Wolf 359", 7.7825, 10, 56, 29.2, +7, 00, 53));
		addStar(new Star("Lalande 21185", 8.2905, 11, 03, 20.2, +35, 58, 12));
		addStar(new Star("Sirius", 8.5828, 6, 45, 08.9, -16, 42, 58));
		addStar(new Star("Luyten 726-8", 8.728, 1, 39, 01.3, -17, 57, 01));
		addStar(new Star("Ross 154", 9.6813, 18, 49, 49.4, -23, 50, 10));
		addStar(new Star("Ross 248", 10.322, 23, 41, 54.7, +44, 10, 30));
		addStar(new Star("Epsilon Eridani", 10.522, 3, 32, 55.8, -9, 27, 30));
		addStar(new Star("Lacaille 9352", 10.742, 23, 05, 52.0, -35, 51, 11));
		addStar(new Star("Ross 128", 10.919, 11, 47, 44.4, 0, +48, 16));
		addStar(new Star("EZ Aquarii", 11.266, 22, 38, 33.4, -15, 18, 07));
		addStar(new Star("Procyon", 11.402, 7, 39, 18.1, +05, 13, 30));
		addStar(new Star("61 Cygni A", 11.403, 21, 06, 53.9, +38, 44, 58));
		addStar(new Star("61 Cygni B", 11.403, 21, 06, 55.3, +38, 44, 31));
		addStar(new Star("Struve 2398 A", 11.525, 18, 42, 46.7, +59, 37, 49));
		addStar(new Star("Struve 2398 B", 11.525, 18, 42, 46.9, +59, 37, 37));
		addStar(new Star("Groombridge 34", 11.624, 0, 18, 22.9, +44, 01, 23));
		addStar(new Star("Epsilon Indi A", 11.824, 22, 03, 21.7, -56, 47, 10));
		addStar(new Star("Epsilon Indi B", 11.824, 22, 04, 10.5, -56, 46, 58));
		addStar(new Star("DX Cancri", 11.826, 8, 29, 49.5, +26, 46, 37));
		addStar(new Star("Tau Ceti", 11.887, 1, 44, 4.1, -15, 56, 15));
		addStar(new Star("GJ 1061", 11.991, 3, 35, 59.7, -44, 30, 35));
		addStar(new Star("YZ Ceti", 12.132, 1, 12, 30.6, -16, 59, 56));
	}
}
