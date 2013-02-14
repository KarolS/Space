/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.engine;

import java.util.ArrayList;
import java.util.List;
import space.engine.civilizations.Civilization;
import space.engine.util.Event;
import space.engine.util.ShipNameGenerator;

/**
 *
 * @author karol
 */
public class Player {
	String name;
	String shortName;
	int noofGeneratedShips=0;
	public Civilization civilization;
	List<Event>[] technologySources;
	@SuppressWarnings("unchecked")
	public Player(Civilization civ){
		civilization=civ;
		name=civilization.getDefaultName();
		shortName=civilization.getDefaultShortName();
		technologySources = new List[TechTree.TECH_COUNT];
		for(int i=0; i<TechTree.TECH_COUNT; i++){
			technologySources[i] = new ArrayList<Event>();
		}
	}
	public String getNewShipName(){
		String r=civilization.getShipName(noofGeneratedShips);
		noofGeneratedShips++;
		return r;
	}
}
