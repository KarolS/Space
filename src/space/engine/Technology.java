/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.engine;

import java.util.Arrays;
import space.engine.util.Event;
import space.engine.util.SmallIntSet;

/**
 *
 * @author karol
 */
public class Technology {
	public int[]cost=new int[Resource.NOOF_RESOURCES];
	public String name;
	public SmallIntSet prerequisites=new SmallIntSet();
	public boolean canResearch(RealWorld world, Ship colonyShip){
		if(!colonyShip.isColony()){
			return false;
		}
		for(int i=0; i<Resource.NOOF_RESOURCES; i++){
			if(colonyShip.colony.resources.getStockpile(i)<cost[i]){
				return false;
			}
		}
		Event now = Event.ofShip(colonyShip, world.now);
		for(int reqTech:prerequisites){
			if(!now.couldBeCausedByAny(world.players[colonyShip.owner].technologySources[reqTech])){
				return false;
			}
		}
		return true;
	}
	public Technology(String name, int[] cost, int...prereqisites){
		this.name=name;
		this.cost=Arrays.copyOf(cost, cost.length);
		for(int prerequisite: prereqisites){
			this.prerequisites.add(prerequisite);
		}

	}
}
