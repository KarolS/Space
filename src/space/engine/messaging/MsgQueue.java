/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package space.engine.messaging;

import space.engine.util.Ph;
import space.engine.messaging.Msg;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import space.engine.Ship;
import vytah.math.Vector3D;

/**
 *
 * @author karol
 */
public class MsgQueue {

	public void insert(Msg m) {
		int i = v.size();
		v.add(m);
		while (i != 0 && v.get(parent(i)).fastestPossibleArrivalTime > v.get(i).fastestPossibleArrivalTime) {
			swap(i, parent(i));
			i = parent(i);
		}
		//System.out.println(this);
		verify(0);
	}

	@Deprecated
	public Msg peek(double now) {
		if (v.isEmpty()) {
			return null;
		}
		Msg m = v.get(0);
		if (surelyCannotArrive(m, now)) {
			return null;
		}
		return m;
	}
	//TODO: nie do końca działa jak trzeba, patrz laserowe chluśnięcia

	public Msg pop(double now) {
		return pop(0, now);
	}
	public List<Msg> popAll(double now) {
		List<Msg> l = new ArrayList<Msg>();
		Msg m2;
		while ((m2 = pop(now)) != null) {
			l.add(m2);
		}
		Collections.sort(l, Msg.COMPARATOR);
		return l;
	}

	public synchronized Msg pop(int root, double now) {
		//System.out.println("Popping at "+root);
		if (root >= v.size()) {
			return null;
		}
		if (surelyCannotArrive(v.get(root), now)) {
			return null;
		}
		if (hasArrived(v.get(root), owner, now)) {
			Msg result = v.get(root);
			v.set(root, v.get(v.size() - 1));
			v.remove(v.size() - 1);
			heapify(root);
			verify(0);
			return result;
		} else {
			Msg result = pop(left(root), now);
			if (result != null) {
				return result;
			} else {
				return pop(right(root), now);
			}
		}
	}

	boolean surelyCannotArrive(Msg m, double now) {
		return m.fastestPossibleArrivalTime >= now;
	}

	boolean hasArrived(Msg m, Ship target, double now) {
		//V vv = target.getLocation(world.now) - m.sentLocation;
		double t = now - m.sentTime;
		if (t < 0) {
			return false;
		}
		t *= Ph.C;
		t *= t;
		if (t >= Vector3D.absSq(target.getLocation(now), m.sentLocation)) {
			return true;
		}
		return false;
	}
	/**
	 * Statek, w którego posiadaniu jest ta kolejka
	 */
	public Ship owner;

	private static int parent(int i) {
		return (i - 1) / 2;
	}

	private static int left(int i) {
		return 2 * i + 1;
	}

	private static int right(int i) {
		return 2 * i + 2;
	}

	private void heapify(int root) {
		if (root >= v.size() || left(root) >= v.size()) {
			return;
		}
		if (v.get(root).fastestPossibleArrivalTime <= v.get(left(root)).fastestPossibleArrivalTime
				&& (right(root) >= v.size() || v.get(root).fastestPossibleArrivalTime <= v.get(right(root)).fastestPossibleArrivalTime)) {
			return;
		}
		int target = left(root);
		if (right(root) < v.size() && v.get(right(root)).fastestPossibleArrivalTime <= v.get(left(root)).fastestPossibleArrivalTime) {
			target = right(root);
		}
		swap(root, target);
		heapify(target);
	}
	private List<Msg> v = new ArrayList<Msg>();

	private void swap(int root, int target) {
		Msg tmp = v.get(root);
		v.set(root, v.get(target));
		v.set(target, tmp);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Queue:\n");
		int indent = 0;
		for (int i = 0; i < v.size(); i++) {
			for (int j = 0; j < indent; j++) {
				sb.append(' ');
			}
			sb.append(v.get(i).toString());
			sb.append('\n');
			if (((i + 1) & (i + 2)) == 0) {
				indent++;
			}
		}
		return sb.toString();
	}

	public void verify(int root) {
		if (root >= v.size() || left(root) >= v.size()) {
			return;
		}
		if (v.get(left(root)).fastestPossibleArrivalTime < v.get(root).fastestPossibleArrivalTime) {
			throw new RuntimeException("Queue skewed");
		}
		if (right(root) < v.size() && v.get(right(root)).fastestPossibleArrivalTime < v.get(root).fastestPossibleArrivalTime) {
			throw new RuntimeException("Queue skewed");
		}
		verify(left(root));
		verify(right(root));
	}
}
