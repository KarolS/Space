/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.engine.messaging;

import space.api.PlayerCacheApi;
import space.engine.PlayerCache;
import space.engine.RealWorld;
import space.engine.Ship;

/**
 * Komunikat z rozkazem natychmiastowego zatrzymania się
 * @author karol
 */
public class µEmergencyStopOrder extends Msg {

	@Override
	public void processForMothership(Ship ship, RealWorld world, PlayerCacheApi pc) {
		processForCommonShip(ship, world);
	}

	@Override
	public void processForCommonShip(Ship ship, RealWorld world) {
		//TODO
	}

	@Override
	public MsgAction getDefaultAction() {
		return MsgAction.USE_LOCALLY;
	}
	//TODO
}
