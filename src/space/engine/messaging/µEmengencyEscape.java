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
 * Komunikat od statku do właściciela o automatycznej ucieczce
 * @author karol
 */
public class µEmengencyEscape extends Msg{

	//TODO
	@Override
	public void processForMothership(Ship ship, RealWorld world, PlayerCacheApi pc) {
		//TODO
	}

	@Override
	public void processForCommonShip(Ship ship, RealWorld world) {}

	@Override
	public MsgAction getDefaultAction() {
		return MsgAction.FORWARD;
	}
}
