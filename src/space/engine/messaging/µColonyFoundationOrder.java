/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.engine.messaging;

import space.api.PlayerCacheApi;
import space.engine.Planet;
import space.engine.RealWorld;
import space.engine.Ship;

/**
 *
 * @author karol
 */
public class µColonyFoundationOrder extends Msg{

	public int player;
	public long targetPlanet;
	public double when;
	public double maxVelocity;

	@Override
	public void processForMothership(Ship ship, RealWorld world, PlayerCacheApi pc) {}

	@Override
	public void processForCommonShip(Ship ship, RealWorld world) {
		//TODO: sprawdzenie, czy umie kolonizować...
		if(!ship.canColonize()){
			return; // sorry.
		}
		if (when < world.now()) {
			when = world.now();
			//TODO: Log "late"
		}
		if (player == ship.owner) {
			System.err.println("µColonyFoundationOrder.processForCommonShip");
			Planet planet=world.starmap.getPlanetById(targetPlanet);
			assert planet!=null;
			ship.planetToColonize=planet;
			ship.forceMovement(when, maxVelocity, planet.parent.getLocation(),planet.parent);
		}
	}

	@Override
	public MsgAction getDefaultAction() {
		return MsgAction.USE_LOCALLY;
	}

}
