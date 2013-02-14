/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import javax.swing.JPanel;
import space.engine.Ship;
import space.engine.Star;

/**
 *
 * @author karol
 */
public class InfoPanel extends JPanel implements MouseListener{
	public Object[] objectsToDraw=new Object[100];
	int counter=0;
	static final private int ITEM_WIDTH=100;
	static final private Color COLOUR_NAVY=new Color(0,0,100);
	SlickGui gui;
	InfoPanel(SlickGui aThis) {
		setPreferredSize(new Dimension(100,100));
		setBackground(COLOUR_NAVY);
		gui=aThis;
		addMouseListener(this);
	}
	public synchronized void clearAllObjects(){
		Arrays.fill(objectsToDraw, null);
		counter=0;
	}
	public synchronized void push(Object o){
		if(counter>=objectsToDraw.length) return;
		for (Object oldO: objectsToDraw){
			if(oldO==o) return;
		}
		objectsToDraw[counter]=o;
		counter++;
	}
	@Override
	public void paint(Graphics g){
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, getWidth(), 2);
		g.setColor(COLOUR_NAVY);
		g.fillRect(0, 2, getWidth(), getHeight()-2);
		for(int i=0; i<objectsToDraw.length; i++){
			Object o=objectsToDraw[i];
			if(o==null)continue;
			if(o instanceof Ship){
				paintShip((Ship)o, g, i*ITEM_WIDTH);
			}
			else if(o instanceof Star){
				paintStar((Star)o, g, i*ITEM_WIDTH);
			}
		}
	}

	public void paintShip(Ship ship, Graphics g, int dx){
		g.setColor(Color.WHITE);
		g.drawString(ship.name, dx+2, 12);
		g.drawString(gui.cache.getPlayerName(ship.owner), dx+2, 24);
	}
	public void paintStar(Star star, Graphics g, int dx){
		g.setColor(Color.YELLOW);
		g.drawString(star.name, dx+2, 12);
		g.drawString("Planets: "+star.planets.length, dx+2, 24);

	}

	@Override
	public void mouseClicked(MouseEvent me) {
		int i=me.getX()/ITEM_WIDTH;
		Object o=objectsToDraw[i];
		if(o==null) return;
		if(me.getButton() == MouseEvent.BUTTON1){
			if(o instanceof Star){
				gui.starInfoFrame.show(gui.cache, (Star)o);
			}
		}
		if(me.getButton() == MouseEvent.BUTTON3){
			if(me.isShiftDown()||me.isControlDown()){
				//odznaczamy obiekt
				objectsToDraw[i]=null;
				if(o instanceof Ship){
					gui.selectedShips.remove(((Ship)o).id);
				}
				if(o instanceof Star){
					gui.selectedStars.remove(((Star)o).getId());
				}
			}
			else{
				//zaznaczamy tylko ten obiekt
				clearAllObjects();
				push(o);
				if(o instanceof Ship){
					gui.selectedStars.clear();
					gui.selectedShips.clear();
					gui.selectedShips.add(((Ship)o).id);
				}
				if(o instanceof Star){
					gui.selectedStars.clear();
					gui.selectedShips.clear();
					gui.selectedStars.add(((Star)o).getId());
				}
			}
		}
		if(me.getButton() == MouseEvent.BUTTON2){

			if(o instanceof Ship){
				gui.mainPanel.smoothlyCenterCameraOn(((Ship)o).getLocation(gui.cache.now()));
			}
			if(o instanceof Star){
				gui.mainPanel.smoothlyCenterCameraOn(((Star)o).getLocation());
			}
		}
		this.repaint();
	}

	@Override
	public void mousePressed(MouseEvent me) {}

	@Override
	public void mouseReleased(MouseEvent me) {}

	@Override
	public void mouseEntered(MouseEvent me) {}

	@Override
	public void mouseExited(MouseEvent me) {}
}
