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
 * Komunikat wysyłany od statku do bazy o zauważeniu wrogiego statku
 * @author karol
 */
public class µAppeared extends Msg {
	/**
	 * Kopia wrogiego statku
	 */
	public Ship shipCopy;

	@Override
	public void processForMothership(Ship ship, RealWorld world, PlayerCacheApi pc) {
		pc.processAppearedMsg(this);
	}

	@Override
	public void processForCommonShip(Ship ship, RealWorld world) {}

	@Override
	public MsgAction getDefaultAction() {
		return MsgAction.FORWARD;
	}
}
