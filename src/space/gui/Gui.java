/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.gui;

import space.engine.PlayerCache;

/**
 *
 * @author karol
 */
public interface Gui {
	void setPlayerCache(PlayerCache pc);

	public void startTheGui();
	void stopTheGui();
}
