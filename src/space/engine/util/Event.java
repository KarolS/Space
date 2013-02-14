/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.engine.util;

import java.util.Collection;
import space.engine.Ship;
import space.engine.messaging.Msg;
import vytah.math.Vector3D;

/**
 *
 * @author karol
 */
public class Event {

	final double time;
	final Vector3D v;
	public Event(Vector3D _v, double _time) {
		v=_v;
		time=_time;
	}
	public static Event ofSending(Msg msg) {
		return new Event(msg.sentLocation, msg.sentTime);
	}
	public static Event ofCreation(Msg msg) {
		return new Event(msg.createdLocation, msg.createdTime);
	}
	public static Event ofShip(Ship ship, double time) {
		return new Event(ship.getLocation(time), time);
	}
	public boolean couldCause(Event that) {
		double dt = this.time-that.time;
		if(dt>=0) return false;
		double sqDist = Vector3D.absSq(v, that.v);
		return dt*dt*Ph.C*Ph.C<sqDist;
	}
	public boolean couldBeCausedBy(Event that){
		double dt = this.time-that.time;
		if(dt<=0) return false;
		double sqDist = Vector3D.absSq(v, that.v);
		return dt*dt*Ph.C*Ph.C<sqDist;
	}
	public boolean couldBeCausedByAny(Collection<Event> those){
		for(Event that: those){
			if(this.couldBeCausedBy(that)) return true;
		}
		return false;
	}
}
