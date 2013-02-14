/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package space.engine;

import space.engine.util.Ph;
import vytah.math.Vector3D;

/**
 * Gwiazda
 * @author karol
 */
public final class Star {

	public static final Vector3D GALAXY_CENTRE = Star.convertCoords(17, 45, 37.224,   -28, 56, 10.23);
	public static final Vector3D GALAXY_ANTICENTRE=  GALAXY_CENTRE.neg();
	public static final Vector3D GALAXY_NORTH = Star.convertCoords(12, 51, 26.282,   27, 07, 42.01);
	public static final Vector3D GALAXY_SOUTH = GALAXY_NORTH.neg();
	public static final Vector3D GALAXY_VELA = GALAXY_NORTH.vecMul(GALAXY_ANTICENTRE).versor(1);
	public static final Vector3D GALAXY_CYGNUS = GALAXY_VELA.neg();

	private Vector3D location;
	public String name;
	private int id;

	public int getId() {
		return id;
	}
	public Planet[] planets;

	public Vector3D getLocation() {
		return location;
	}

	void setId(int i) {
		id = i;
	}

	void setLocation(Vector3D l) {
		location = l;
	}

	public Star() {
		location = Vector3D.V0;
		name = "";
		initRandomPlanets();
	}

	/**
	 * Konweruje współrzędne astronomiczne na współrzędne w grze
	 * @param rah
	 * @param ram
	 * @param ras
	 * @param dd
	 * @param dm
	 * @param ds
	 */
	public static Vector3D convertCoords(int rah, int ram, double ras, int dd, int dm, double ds){
		double ra = rah + ram / 60.0 + ras / 3600.0;
		double d = Math.abs(dd) + Math.abs(dm) / 60.0 + Math.abs(ds) / 3600.0;
		ra *= Math.PI;
		ra /= 12.0;
		d *= Math.PI;
		d /= 180;
		if (dd < 0 || dm < 0 || ds < 0) {
			d *= -1;
		}
		double x, y, z;
		x = Math.cos(d) * Math.cos(ra);
		y = Math.cos(d) * Math.sin(ra);
		z = Math.sin(d);
		return new Vector3D(x,y,z);
	}


	public Star(String n, double dist, int rah, int ram, double ras, int dd, int dm, double ds) {
		name = n;
		location = convertCoords(rah, ram, ras, dd, dm, ds).mul(dist * Ph.LY);
		initRandomPlanets();
	}

	private static final String []PLANET_SUFFIXES=
	{" I", " II", " III", " IV", " V", " VI",
	 " VII", " VIII", " IX", " X"," XI"," XII"};
	private void initRandomPlanets(){
		planets=new Planet[5];
		for(int i=0; i<planets.length; i++){
			planets[i]=new Planet();
			planets[i].name=this.name+PLANET_SUFFIXES[i];
		}
	}
	@Override
	public String toString(){
		return name;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + this.id;
		return hash;
	}
	@Override
	public boolean equals(Object o){
		if(o==null) return false;
		if(o instanceof Star){
			return this.id==((Star)o).id;
		}
		return false;
	}
}
