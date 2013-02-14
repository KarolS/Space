/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package space.engine.colonybehaviour;

import space.engine.Colony;
import space.engine.RealWorld;
import space.engine.Star;

/**
 *
 * @author karol
 */
public interface ColonyRallyPoint {
    Star getRallyPoint(Colony colony, RealWorld world);
}
