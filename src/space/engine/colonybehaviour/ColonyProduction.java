/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package space.engine.colonybehaviour;

import space.engine.Colony;
import space.engine.RealWorld;

/**
 *
 * @author karol
 */
public interface ColonyProduction {
    int getNextBuilding(Colony colony, RealWorld world);
    int getNextShip(Colony colony, RealWorld world);
}
