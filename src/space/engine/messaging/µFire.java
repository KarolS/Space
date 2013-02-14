/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.engine.messaging;

import space.api.PlayerCacheApi;
import space.engine.RealWorld;
import space.engine.Ship;
import space.engine.util.Ph;
import vytah.math.Vector3D;

/**
 *
 * @author karol
 */
public class µFire extends Msg{
	public Vector3D target;

	@Override
	public void processForMothership(Ship ship, RealWorld world, PlayerCacheApi pc) {
		processForCommonShip(ship, world);
	}
	/**
	 * Procedura uruchamiana w celu obliczenia reakcji statku na dolatujący strzał z wrogiego lasera
	 * i przesłania informacji o zagrożeniu do bazy.
	 * @param ship statek
	 * @param w świat
	 */
	@Override
	public void processForCommonShip(Ship ship, RealWorld w) {
		//TODO: sprawdzić, czy rzeczywiście trafiony
		Vector3D currentVelocity = ship.getVelocity(w.now());
		Vector3D laserVersor = this.target.sub(this.createdLocation).versor(1);
		double corellation = laserVersor.scMul(currentVelocity.versor(1 / Ph.C));
		corellation += 1;
		corellation *= corellation;
		corellation += 1;
		double sqDistanceFromperfectHit = Vector3D.absSq(this.target, ship.getLocation(w.now()));
		if (sqDistanceFromperfectHit > corellation) {
			System.out.println("Missed by " + sqDistanceFromperfectHit + " when " + corellation + " would suffice...");
			return; //Ha! You missed!
		}
		if (ship.id != ship.owner) {
			µHitByFire m = new µHitByFire();
			m.createdLocation = this.target;
			m.creatorId = ship.id;
			m.createdTime = w.now();
			m.sentTime = w.now();
			m.setSource(ship);
			m.setDistance(w.ships.get(ship.owner).getLocation(w.now()));
			m.enemyLocation = this.createdLocation;
			w.ships.get(ship.owner).msgQueue.insert(m);
		}
	}

	@Override
	public MsgAction getDefaultAction() {
		return MsgAction.USE_LOCALLY;
	}
}
