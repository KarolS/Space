/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.engine;

import vytah.math.Vector3D;


/**
 *
 * @author karol
 */
public final class Manœuvre implements Cloneable{
	final private boolean isRotational;
	final private Vector3D x,v,a;
	final private double startTime;
	final public Star targetStar;
	
	public Manœuvre() {
		this(Vector3D.V0,Vector3D.V0,Vector3D.V0,0,false);
	}
	/**
	 *
	 * @param _x położenie obiektu w chwili _t
	 * @param _v prędkość obiektu w chwili _t
	 * @param _a przyspieszenie obiektu w chwili _t / przy ruchu po okręgu normalna do okręgu (?)
	 * @param _t czas
	 * @param _rot czy ruch po okręgu
	 */
	public Manœuvre(Vector3D _x, Vector3D _v, Vector3D _a, double _t, boolean _rot){
		this(_x, _v, _a, _t, _rot, null);
	}
	/**
	 *
	 * @param _x położenie obiektu w chwili _t
	 * @param _v prędkość obiektu w chwili _t
	 * @param _a przyspieszenie obiektu w chwili _t / przy ruchu po okręgu normalna do okręgu (?)
	 * @param _t czas
	 * @param _rot czy ruch po okręgu
	 * @param _targetStar
	 */
	public Manœuvre(Vector3D _x, Vector3D _v, Vector3D _a, double _t, Star _targetStar){
		this(_x, _v, _a, _t, false, _targetStar);
	}
	/**
	 *
	 * @param _x położenie obiektu w chwili _t
	 * @param _v prędkość obiektu w chwili _t
	 * @param _a przyspieszenie obiektu w chwili _t / przy ruchu po okręgu normalna do okręgu (?)
	 * @param _t czas
	 * @param _rot czy ruch po okręgu
	 * @param _targetStar
	 */
	public Manœuvre(Vector3D _x, Vector3D _v, Vector3D _a, double _t, boolean _rot, Star _targetStar){
		x=_x;v=_v;a=_a;
		startTime=_t;
		isRotational=_rot;
		targetStar=_targetStar;
	}

	public Manœuvre(Vector3D _x, Vector3D _v, Vector3D _a, double _t) {
		this(_x,_v,_a,_t,false);
	}
	/**
	 * Zwraca położenie obiektu w chwili t
	 * @param t
	 * @return
	 */
	public Vector3D getLocation(double t){
		t-=startTime;
		return Vector3D.sum(1, x, t, v, t*t/2, a);
	}

	public Vector3D getVelocity(double t){
		t-=startTime;
		return Vector3D.sum(1, v, t, a);
	}
	public Vector3D getAcceleration(double T){
		return a;
	}

	public double getStartTime(){
		return startTime;
	}
	/**
	 * Zwraca położenie na początku manewru
	 * @return
	 */
	public Vector3D location(){
		return x;
	}

	@Override
	public String toString(){
		return x+" t="+startTime+" v="+v+" a="+a+(isRotational?" (rot)":"");
	}
}
