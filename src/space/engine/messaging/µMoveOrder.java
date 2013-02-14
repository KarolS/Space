/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package space.engine.messaging;

import space.api.PlayerCacheApi;
import space.engine.PlayerCache;
import space.engine.RealWorld;
import space.engine.Ship;
import vytah.math.Vector3D;

/**
 *
 * @author karol
 */
public final class µMoveOrder extends Msg {

	public int player;
	public Vector3D target;
	public Integer targetStar;
	public double when;
	public double maxVelocity;

	@Override
	public void processForMothership(Ship ship, RealWorld world, PlayerCacheApi pc) {
		processForCommonShip(ship, world);
	}

	@Override
	public void processForCommonShip(Ship ship, RealWorld world) {
		if(ship.colony!=null && ship.colony.planet!=null){
			return; //sorry, kolonia się nie ruszy...
		}
		if (when < world.now()) {
			when = world.now();
			//TODO: Log "late"
		}
		if (player == ship.owner) {
			assert targetStar!=null;
			ship.queueMovement(when, maxVelocity, target,
					(targetStar == null
					? null
					: world.starmap.stars.get(targetStar)));
		}
	}

	@Override
	public MsgAction getDefaultAction() {
		return MsgAction.USE_LOCALLY;
	}
}
