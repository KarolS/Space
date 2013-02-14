/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.engine.messaging;

import space.api.PlayerCacheApi;
import space.engine.RealWorld;
import space.engine.Ship;
import vytah.math.Vector3D;

/**
 * komunikat wysyłany ze statku do statku-matki, w celu poinformowania o trafieniu przez wroga
 * @author karol
 */
public class µInformAboutFire extends Msg{
	public Vector3D target;

	@Override
	public void processForMothership(Ship ship, RealWorld world, PlayerCacheApi pc) {
		pc.addLaserBeam(createdLocation, target);
	}

	@Override
	public void processForCommonShip(Ship ship, RealWorld world) {}

	@Override
	public MsgAction getDefaultAction() {
		return MsgAction.FORWARD;
	}
}
