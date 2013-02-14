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
 *
 * @author karol
 */
public class µDisappeared extends Msg{

	@Override
	public void processForMothership(Ship ship, RealWorld world, PlayerCacheApi pc) {
		pc.processDisappearedMsg(this);
	}

	@Override
	public void processForCommonShip(Ship ship, RealWorld world) {}

	@Override
	public MsgAction getDefaultAction() {
		return MsgAction.FORWARD;
	}

}
