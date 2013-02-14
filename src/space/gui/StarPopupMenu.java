/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package space.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import space.engine.PlayerCache;
import space.engine.Star;

/**
 *
 * @author karol
 */
public class StarPopupMenu extends JPopupMenu implements ActionListener {

	public int MAX_PLANETS_SUPPORTED_BY_GUI=10;
	JMenuItem starInfo = new JMenuItem("Star info...");
	JMenuItem queueMovementTo = new JMenuItem("Move to this star");
	List<JMenuItem> colonizeOrders = new ArrayList<JMenuItem>();
	List<JMenuItem> planetInfo = new ArrayList<JMenuItem>();
	public PlayerCache cache;
	public Star star = null;
	public Set<Integer> selectedShips = null;
	SlickGui gui;

	public StarPopupMenu(SlickGui agui) {
		super();
		gui=agui;
		add(starInfo);
		starInfo.addActionListener(this);
		add(new JSeparator());
		add(queueMovementTo);
		queueMovementTo.addActionListener(this);
		for(int i=0; i<MAX_PLANETS_SUPPORTED_BY_GUI;++i){
			JMenuItem m=new JMenuItem("Colonize planet #"+i);
			colonizeOrders.add(m);
			add(m);
			m.addActionListener(this);
		}
		add(new JSeparator());
		for(int i=0; i<MAX_PLANETS_SUPPORTED_BY_GUI;++i){
			JMenuItem m=new JMenuItem("Planet info #"+i);
			planetInfo.add(m);
			add(m);
			m.addActionListener(this);
		}
	}
	public void setUp(Set<Integer> ships, Set<Integer>stars, Star star){
		this.selectedShips=ships;
		this.star=star;
		boolean colonyships=false;
		int colonyshipId=-1;
		for(int ship : ships){
			if(cache.isOurColonyShip(ship)){
				colonyships=true;
				colonyshipId=ship;
				break;
			}
		}
		for(int i=0; i<MAX_PLANETS_SUPPORTED_BY_GUI;i++){
			planetInfo.get(i).setVisible(i<star.planets.length);
		}
		for(int i=0; i<star.planets.length;i++){
			planetInfo.get(i).setText("Planet info: "+star.planets[i].name);
			colonizeOrders.get(i).setText("Colonize "+star.planets[i].name);
		}
		for(int i=0; i<MAX_PLANETS_SUPPORTED_BY_GUI;i++){
			colonizeOrders.get(i).setVisible(
					colonyships &&
					i<star.planets.length &&
					star.planets[i].isColonizable(colonyshipId)
					);
		}
		//TODO
	}
	@Override
	public void actionPerformed(ActionEvent ae) {
		if(!(ae.getSource() instanceof JMenuItem)) return;
		JMenuItem src = (JMenuItem) ae.getSource();
		if (src == starInfo) {
			gui.starInfoFrame.show(cache, star);
		}
		if (src == queueMovementTo) {
			if (selectedShips != null && star != null) {
				for (int i : selectedShips) {
					cache.queueMovementToStar((long) i, star);
				}
			}
		}
		if(planetInfo.contains(src)){
			int p=planetInfo.indexOf(src);
			//TODO
		}
		if(colonizeOrders.contains(src)){
			int p=planetInfo.indexOf(src);
			if (selectedShips != null && star != null) {
				if(star.planets.length>=p) return; // !
				boolean first=true;
				for (int i : selectedShips) {
					if (cache.isOurColonyShip(i) && first) {
						first = false;
						cache.forceMovementToColonize((long) i, star.planets[p]);
					}
				}
			}
		}
	}
}
