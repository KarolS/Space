/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.engine;

import space.engine.util.Ph;
import vytah.math.Vector3D;

/**
 * Impuls lasera.
 * @author karol
 */
public final class LaserBeam implements Comparable<LaserBeam>{
	/**
	 * Czas, w którym promień zniknie
	 */
    final public double endTime;
	/**
	 * Cel lasera
	 */
    final public Vector3D target;
	/**
	 * Kierunek od celu do źródła lasera
	 */
    final public Vector3D directionToSource;
	/**
	 *
	 * @param from_loc źródło
	 * @param to_loc cel
	 * @param now bieżący czas
	 */
    public LaserBeam(Vector3D from_loc,Vector3D to_loc, double now){
		if(to_loc.isNaN()) throw new RuntimeException("NaNbelievable!");
		if(from_loc.isNaN()) throw new RuntimeException("NaNbelievable!");
		if(from_loc.equals(to_loc)){
			throw new IllegalArgumentException("Locations have to be different");
		}
        target=to_loc;
        endTime=now+Vector3D.abs(from_loc,to_loc)/Ph.C;
        directionToSource=from_loc.sub(to_loc).versor(1);
		if(directionToSource.isNaN())throw new RuntimeException("NaNbelievable!");
		//System.out.println("New lazor beam: "+from_loc+"->"+to_loc+" || "+directionToSource+" t="+endTime);
    }
	/**
	 * Położenie impulsu lasera w chwili when
	 * @param when
	 * @return
	 */
    public Vector3D getLocation(double when){
        return Vector3D.sum((endTime-when)*Ph.C,directionToSource,1,target);
    }
	public int compareTo(LaserBeam o) {
		return Double.compare(endTime, o.endTime);
	}

	public boolean equals(Object o){
		if(o==null||!(o instanceof LaserBeam)) return false;
		return endTime==((LaserBeam)o).endTime;
	}
}
