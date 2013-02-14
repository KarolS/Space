/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package space.gui;

import java.awt.event.KeyEvent;
import space.engine.PlayerCache;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 *
 * @author karol
 */
public class SlickGui extends JFrame implements KeyListener, Gui {

	public static final long serialVersionUID = 21345678;
	public PlayerCache cache;
	Set<Integer> selectedShips = new TreeSet<Integer>();
	Set<Integer> selectedStars = new TreeSet<Integer>();

	TechTreeViewer techTreeViewer;
	final StarPopupMenu starPopupMenu;
	final StarInfoFrame starInfoFrame;

	SlickPanel mainPanel = new SlickPanel(this);
	final InfoPanel infoPanel;
	public SlickGui() {
		starPopupMenu=new StarPopupMenu(this);
		starInfoFrame=new StarInfoFrame();
		
		this.addKeyListener(this);
		
		setLayout(new BorderLayout());
		add(mainPanel.getComponent(), BorderLayout.CENTER);
		infoPanel=new InfoPanel(this);
		add(infoPanel, BorderLayout.SOUTH);
		techTreeViewer=new TechTreeViewer();
		{
			JMenuBar menuBar=new JMenuBar();
			add(menuBar,BorderLayout.NORTH);
			JMenu game=new JMenu("Game");
			menuBar.add(game);
				JMenuItem speed0 = new JMenuItem("Pause");
				speed0.addActionListener(new ActionListener() {
					@Override public void actionPerformed(ActionEvent ae) {
						SlickGui.this.cache.setGameSpeed(0.0);
					}
				});
				game.add(speed0);
				JMenuItem speed01 = new JMenuItem("10% Speed");
				speed01.addActionListener(new ActionListener() {
					@Override public void actionPerformed(ActionEvent ae) {
						SlickGui.this.cache.setGameSpeed(0.1);
					}
				});
				game.add(speed01);
				JMenuItem speed1 = new JMenuItem("Normal Speed");
				speed1.addActionListener(new ActionListener() {
					@Override public void actionPerformed(ActionEvent ae) {
						SlickGui.this.cache.setGameSpeed(1.0);
					}
				});
				game.add(speed1);
				JMenuItem speed3 = new JMenuItem("Double Speed");
				speed3.addActionListener(new ActionListener() {
					@Override public void actionPerformed(ActionEvent ae) {
						SlickGui.this.cache.setGameSpeed(2.0);
					}
				});
				game.add(speed3);
			JMenu view=new JMenu("View");
			menuBar.add(view);
				JMenuItem toggleCoord = new JMenuItem("Toggle coordinates");
				toggleCoord.addActionListener(new ActionListener() {
					@Override public void actionPerformed(ActionEvent ae) {
						SlickGui.this.mainPanel.drawCoordinates^=true;
					}
				});
				view.add(toggleCoord);			JMenu help=new JMenu("Help");
			menuBar.add(help);
				JMenuItem techtree = new JMenuItem("Techtree");
				techtree.addActionListener(new ActionListener() {
					@Override public void actionPerformed(ActionEvent ae) {
						techTreeViewer.setVisible(true);
					}
				});
				help.add(techtree);

		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400, 400);
	}

	@Override
	public void dispose() {
		cache.endGame();
		super.dispose();
	}

	@Override
	public void setPlayerCache(PlayerCache pc) {
		cache = pc;
		starPopupMenu.cache=pc;
		mainPanel.setPlayerCache(pc);
	}

	@Override
	public void startTheGui() {
		setVisible(true);
		mainPanel.startTheGui();
	}

	@Override
	public void stopTheGui() {
		mainPanel.stopTheGui();
	}

	@Override
	public void keyTyped(KeyEvent ke) {
		
	}

	/*public void smoothlyCenterCameraOn(V location){
		targetCentreV=location;
		sourceCentreV=centreV;
		centreCoërtion=0;
	}*/
	@Override public void keyPressed(KeyEvent ke) {
		if(ke.getKeyChar()=='h'){
			mainPanel.smoothlyCenterCameraOn(cache.getShipLocation(cache.id, cache.now()));
		}
		if(ke.getKeyChar()==' '){
			if(selectedStars.size()==1){
				for(int s: selectedStars){
					mainPanel.smoothlyCenterCameraOn(cache.getStars().get(s).getLocation());
				}
			}
			else{
				if(selectedStars.isEmpty() && selectedShips.size()==1){
					for(int s: selectedShips){
						mainPanel.smoothlyCenterCameraOn(cache.getShipLocation(s, cache.now()));
					}
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent ke) {}
}
