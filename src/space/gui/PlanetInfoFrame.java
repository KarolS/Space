/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.gui;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Color;
import javax.swing.JDialog;
import javax.swing.JLabel;
import space.engine.Colony;
import space.engine.Planet;
import space.engine.PlayerCache;
import space.engine.Star;

/**
 *
 * @author karol
 */
public class PlanetInfoFrame extends JDialog {
	JLabel planetName=new JLabel();
	public PlanetInfoFrame(){
		setModal(true);
		setLayout(new FormLayout("5dlu,pref,5dlu,5dlu:grow", "5dlu,pref,5dlu"));
		setSize(600,400);
		setResizable(false);
		CellConstraints cc=new CellConstraints();
		add(planetName,cc.xy(2, 2));
		planetName.setForeground(Color.yellow);
		getContentPane().setBackground(new Color(0,0,100));
	}
	public void show(PlayerCache cache, Planet planet, Colony colony){
		planetName.setText(planet.name);
		setVisible(true);
	}
}
