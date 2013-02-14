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
import space.engine.PlayerCache;
import space.engine.Star;

/**
 *
 * @author karol
 */
public class StarInfoFrame extends JDialog {
	JLabel starName=new JLabel();
	public StarInfoFrame(){
		setModal(true);
		setLayout(new FormLayout("5dlu,pref,5dlu,5dlu:grow", "5dlu,pref,5dlu"));
		setSize(600,400);
		setResizable(false);
		CellConstraints cc=new CellConstraints();
		add(starName,cc.xy(2, 2));
		starName.setForeground(Color.yellow);
		getContentPane().setBackground(new Color(0,0,100));
	}
	public void show(PlayerCache cache, Star star){
		starName.setText(star.name);
		setVisible(true);
	}
}
