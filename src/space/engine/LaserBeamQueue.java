/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package space.engine;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import vytah.math.Vector3D;

/**
 * Klasa zarządzająca impulsami lasera
 * @author karol
 */
public class LaserBeamQueue {

	public Queue<LaserBeam> l = new PriorityQueue<LaserBeam>();

	/**
	 * Usuwa wszystkie impulsy, które już przestały istnieć
	 * @param now
	 */
	public void clean(double now) {
		lock.lock();
		try {
			while (!l.isEmpty() && l.element().endTime < now) {
				l.remove();
			}
		} finally {
			lock.unlock();
		}
	}
	private Lock lock = new ReentrantLock(true);

	/**
	 * blokuje dostęp do kolejki
	 */
	public void lock() {
		lock.lock();
	}

	/**
	 * Odbolowuje kolejkę
	 */
	public void unlock() {
		lock.unlock();
	}

	/**
	 * Wstawia nowy impuls do kolejki.
	 * Znaczenie parametrów: patrz konstruktor LaserBeam
	 * @see LaserBeam
	 * @param from_loc
	 * @param to_loc
	 * @param now
	 */
	public void insert(Vector3D from_loc, Vector3D to_loc, double now) {
		lock.lock();
		try {
			//System.out.println("Inserting a new laser beam");
			LaserBeam lb = new LaserBeam(from_loc, to_loc, now);
			l.add(lb);
		} finally {
			lock.unlock();
		}
	}
}
