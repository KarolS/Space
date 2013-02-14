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
 * komunikat o niewywołanym przez nasz rozkaz zatrzymaniu się statku, tzn.:
 * w tym o wszelkim zatrzymaniu się wrogich statków
 * i o zatrzymaniu awaryjnym naszych statków
 * @author karol
 */
public class µEmergencyStop extends Msg{
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
