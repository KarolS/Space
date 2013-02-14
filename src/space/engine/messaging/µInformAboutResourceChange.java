/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.engine.messaging;

import space.api.PlayerCacheApi;
import space.engine.RealWorld;
import space.engine.ResourceStatus;
import space.engine.Ship;

/**
 *
 * @author karol
 */
public class µInformAboutResourceChange  extends Msg{
	public ResourceStatus newResources;

	@Override
	public void processForMothership(Ship ship, RealWorld world, PlayerCacheApi pc) {
		pc.colonyResourceChanged(ship.id, newResources);
	}

	@Override
	public void processForCommonShip(Ship ship, RealWorld world) {}

	@Override
	public MsgAction getDefaultAction() {
		return MsgAction.USE_LOCALLY;
	}
}
