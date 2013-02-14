/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.engine;

import space.engine.util.Event;
import vytah.math.Vector3D;


/**
 *
 * @author karol
 */
public class TechnologySourceInfo {
	final Event event;
	final int technologyId;
	public TechnologySourceInfo(Vector3D _location, double _time, int _technologyId){
		technologyId=_technologyId;
		event = new Event(_location,_time);
	}
}
