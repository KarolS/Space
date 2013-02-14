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
 * Komunikat o awarii silników w naszym statku.
 * Powinien powodować skasowanie wszystkich niewykonanych manwewrów z cache
 * @author karol
 */
public class µEngineFailure extends Msg {
	//TODO
	@Override
	public void processForMothership(Ship ship, RealWorld world, PlayerCacheApi pc) {
		//pc.engineFailure(ship.id,world.now());
	}

	@Override
	public void processForCommonShip(Ship ship, RealWorld world) {}

	@Override
	public MsgAction getDefaultAction() {
		return MsgAction.FORWARD;
	}

}
