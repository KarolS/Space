/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package space.gui;

import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import space.engine.LaserBeam; 
import space.engine.Ship;
import space.engine.PlayerCache;
import space.engine.util.Ph;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ConcurrentModificationException;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import space.engine.Star;
import vytah.math.Matrix3x3;
import vytah.math.Vector3D;

/**
 * @deprecated
 * @author karol
 */
@Deprecated
public class SwingGui extends JFrame implements MouseListener, ActionListener, MouseMotionListener, MouseWheelListener, KeyListener, Gui {

	JPanel mainPanel;
	public PlayerCache cache;
	Set<Integer> selectedShips = new TreeSet<Integer>();
	Set<Integer> selectedStars = new TreeSet<Integer>();
	
	volatile double rotation1,rotation2;
	//volatile V rotatedX=new V(1,0,0), rotatedY=new V(0,1,0), rotatedZ=new V(0,0,1);
	//volatile V unrotatedX=new V(1,0,0), unrotatedY=new V(0,1,0), unrotatedZ=new V(0,0,1);
	volatile Matrix3x3 rotated=Matrix3x3.I;
	volatile Matrix3x3 unrotated=Matrix3x3.I;
	volatile Vector3D centreV=new Vector3D(0,0,0);
	volatile double centreCoërtion=0;
	volatile Vector3D sourceCentreV=new Vector3D(0,0,0);
	volatile Vector3D targetCentreV=new Vector3D(0,0,0);
	volatile double zoom = 1;
	volatile boolean drawCoordinates=true;
	StarPopupMenu starPopupMenu;
	StarInfoFrame starInfoFrame;
	InfoPanel infoPanel;

	public Point convert(Vector3D v) {
		v=v.sub(centreV);
		Vector3D tmp = rotated.mul(v);
		return new Point((int) (tmp.x() / zoom)+mainPanel.getWidth()/2, (int) (tmp.y() / zoom)+mainPanel.getHeight()/2);
	}

	public Vector3D convert(Point p) {
		return unrotated.mul((p.x-mainPanel.getWidth()/2) * zoom,(p.y-mainPanel.getHeight()/2) * zoom,0).add(centreV);
	}

	private BufferedImage buffer=new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	public void renderFrame(Graphics gg) {
		BufferedImage tmp;
		if(buffer.getWidth()!=mainPanel.getWidth() || buffer.getHeight()!=mainPanel.getHeight()){
			buffer=new BufferedImage(mainPanel.getWidth(), mainPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
		}
		Graphics g=buffer.createGraphics();
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());

		double extraZoom=Math.min(getWidth(),mainPanel.getHeight())*0.35;
		if(drawCoordinates){
			g.setColor(Color.green);
			Point viewCentre=convert(centreV);
			Point galaxyCentre=convert(centreV.add(zoom*extraZoom,Star.GALAXY_CENTRE));
			Point galaxyAntiCentre=convert(centreV.add(zoom*extraZoom,Star.GALAXY_ANTICENTRE));
			Point galaxyCygnus=convert(centreV.add(zoom*extraZoom,Star.GALAXY_CYGNUS));
			Point galaxyVela=convert(centreV.add(zoom*extraZoom,Star.GALAXY_VELA));
			Point galaxyNorth=convert(centreV.add(zoom*extraZoom,Star.GALAXY_NORTH));
			Point galaxySouth=convert(centreV.add(zoom*extraZoom,Star.GALAXY_SOUTH));
			g.drawLine(viewCentre.x, viewCentre.y, galaxyCentre.x, galaxyCentre.y);
			g.drawLine(viewCentre.x, viewCentre.y, galaxyAntiCentre.x, galaxyAntiCentre.y);
			g.drawLine(viewCentre.x, viewCentre.y, galaxyCygnus.x, galaxyCygnus.y);
			g.drawLine(viewCentre.x, viewCentre.y, galaxyVela.x, galaxyVela.y);
			g.drawLine(viewCentre.x, viewCentre.y, galaxyNorth.x, galaxyNorth.y);
			g.drawLine(viewCentre.x, viewCentre.y, galaxySouth.x, galaxySouth.y);
			g.drawString("Centre", galaxyCentre.x, galaxyCentre.y);
			g.drawString("Anticentre", galaxyAntiCentre.x, galaxyAntiCentre.y);
			g.drawString("Cygnus", galaxyCygnus.x, galaxyCygnus.y);
			g.drawString("Vela", galaxyVela.x, galaxyVela.y);
			g.drawString("North", galaxyNorth.x, galaxyNorth.y);
			g.drawString("South", galaxySouth.x, galaxySouth.y);
		}

		double now = cache.now(), t, tf;
		//if(true)return;
		if (cache.shipCache.get((long) cache.id) == null) {
			return;
		}
		Vector3D home = cache.shipCache.get((long) cache.id).getLocation(now);
		for (Star star : cache.getStars()) {
			Point seenAt = convert(star.getLocation());
			g.setColor(Color.YELLOW);
			g.fillOval(seenAt.x - 3, seenAt.y - 3, 7, 7);
			g.drawString(star.name, seenAt.x + 5, seenAt.y);
			synchronized (this) {
				if (selectedStars.contains(Integer.valueOf(star.getId()))) {
					g.setColor(Color.PINK);
					g.drawOval(seenAt.x - 5, seenAt.y - 5, 11, 11);
				}
			}
		}
		try{
			for (Ship ship : cache.shipCache.values()) {
				if (ship == null) {
					continue;
				}
				if (ship.isTheoreticallyVisible(home, now) == false) {
					continue;
				}
				t = ship.whenSeenInPast(home, now);
				tf = ship.whenSeenInFuture(home, now);
				Point seenAt = convert(ship.getLocation(now - t));
				Point expectedAt = convert(ship.getLocation(now));
				Point listensAt = convert(ship.getLocation(now + tf));
				if (ship.isColony()) {
					g.setColor(Color.GREEN);
					g.fillOval(expectedAt.x - 3, expectedAt.y - 3, 7, 7);
					//g.drawString((ship.owner == cache.id) ? "Colony " : "Enemy colony ", seenAt.x, seenAt.y);
					//TODO
				} else {
					g.setColor(Color.RED);
					g.fillOval(seenAt.x - 3, seenAt.y - 3, 7, 7);
					//tmp=ImageFactory.getImage(ship, rotated, now-t, ImageFactory.TYPE_VISIBLE);
					//g.drawImage(tmp, seenAt.x - tmp.getWidth()/2, seenAt.y-tmp.getHeight()/2, null);
					g.drawString(((ship.owner == cache.id) ? "" : "["+cache.getPlayerShortName(ship.owner)+"] ") + (!ship.canColonize() ? "" : "COL ") + ship.name + ", dist: " + Ph.timeToStr(t), seenAt.x, seenAt.y);
					g.setColor(Color.WHITE);
					g.fillOval(expectedAt.x - 3, expectedAt.y - 3, 7, 7);
					//tmp=ImageFactory.getImage(ship, rotated, now, ImageFactory.TYPE_NOW);
					//g.drawImage(tmp, expectedAt.x - tmp.getWidth()/2, expectedAt.y-tmp.getHeight()/2, null);
					g.drawString(((ship.owner == cache.id) ? "" : "["+cache.getPlayerShortName(ship.owner)+"] ") + (!ship.canColonize() ? "" : "COL ") + ship.name + " now.", expectedAt.x, expectedAt.y);
					if (ship.owner == cache.id) {
						g.setColor(Color.BLUE);
						g.fillOval(listensAt.x - 3, listensAt.y - 3, 7, 7);
						//tmp=ImageFactory.getImage(ship, rotated, now+tf, ImageFactory.TYPE_FUTURE);
						//g.drawImage(tmp, listensAt.x - tmp.getWidth()/2, listensAt.y-tmp.getHeight()/2, null);
						g.drawString("", listensAt.x, listensAt.y);
					}
				}
				synchronized (this) {
					if (selectedShips.contains(Integer.valueOf(ship.id))) {
						g.setColor(Color.PINK);
						g.drawOval(seenAt.x - 5, seenAt.y - 5, 11, 11);
					}
				}
				g.setColor(Color.CYAN);
				//System.out.println("Lasers: "+cache.laserBeamQueue.l.size());
				cache.laserBeamQueue.clean(now);
				cache.laserBeamQueue.lock();
				for (LaserBeam lb : cache.laserBeamQueue.l) {
					Vector3D v = lb.getLocation(now);
					//System.out.println(v);
					Point p = convert(v);
					g.fillRect(p.x, p.y, 1, 1);
				}
				cache.laserBeamQueue.unlock();
			}
		}
		catch(ConcurrentModificationException cex){
			//IGNORE MWAHAHAHA
		}
		g.setColor(Color.WHITE);
		if(dragging){
			g.drawRect(dragX1, dragY1, dragX2-dragX1, dragY2-dragY1);
		}
		g.drawString(Ph.dateToStr(now), 10, 10);
		gg.drawImage(buffer, 0, 0, null);
	}
	/*
	 * Czy LPM jest wciśnięty
	 */
	boolean dragging = false;
	/**
	 * Początek prostokąta zakreślanego LPM
	 */
	int dragX1, dragY1, dragX2, dragY2;
	/*
	 * Ostatnie położnie myszy przy wciśniętym ŚPM
	 */
	int middleButtonPressX = 0, middleButtonPressY = 0;
	/*
	 * Ostatnie położnie myszy przy wciśniętym PPM
	 */
	int rightButtonPressX = 0, rightButtonPressY = 0;

	@Override 
	public void mouseClicked(MouseEvent e) {
		if (cache == null) {
			return;
		}
		if (e.getButton() == MouseEvent.BUTTON3) {
			Star bestStar = null;
			int minDist = 17;
			for (Star star : cache.getStars()) {
				Point seenAt = convert(star.getLocation());
				int currDist = Math.abs(e.getX() - seenAt.x) + Math.abs(e.getY() - seenAt.y);
				if (currDist < minDist) {
					currDist = minDist;
					bestStar = star;
				}
			}

			if (bestStar == null) {
				System.out.println("I po co, i tak nie poleci");
				if (true) {
					return;
				}
				for (int i : selectedShips) {
					cache.queueMovement((long) i, convert(e.getPoint()));
				}
			} else {//tylko od gwiazdy do gwiazdy!!
				starPopupMenu.setUp(selectedShips, selectedStars, bestStar);
				if (true||e.isPopupTrigger()) {
					starPopupMenu.show(mainPanel, e.getX(), e.getY());
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			dragging = true;
			dragX1 = e.getX();
			dragY1 = e.getY();
			dragX2 = dragX1;
			dragY2 = dragY1;
		}
		if (e.getButton() == MouseEvent.BUTTON2) {
			middleButtonPressX = e.getX();
			middleButtonPressY = e.getY();
		}
		if (e.getButton() == MouseEvent.BUTTON3) {
			rightButtonPressX = e.getX();
			rightButtonPressY = e.getY();
		}
	}

	@Override public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			synchronized (this) {
				dragging = false;
				dragX2 = e.getX();
				dragY2 = e.getY();
				if (dragX1 > dragX2) {
					int tmp = dragX1;
					dragX1 = dragX2;
					dragX2 = tmp;
				}
				if (dragY1 > dragY2) {
					int tmp = dragY1;
					dragY1 = dragY2;
					dragY2 = tmp;
				}
				if(e.isControlDown()||e.isShiftDown()){}
				else{
					selectedShips.clear();
					selectedStars.clear();
					infoPanel.clearAllObjects();
				}
				double now = cache.now();
				int minDistShip = 17;
				int minDistStar = 17;
				Vector3D home = cache.shipCache.get((long) cache.id).getLocation(now);
				Ship onlyShip = null;
				Star onlyStar = null;
				
				for (Ship ship : cache.shipCache.values()) {
					if (ship == null) {
						continue;
					}
					if (ship.isTheoreticallyVisible(home, now) == false) {
						continue;
					}
					Point seenAt = convert(ship.getLocation(now - (ship.whenSeenInPast(home, now))));
					Point expectedAt = convert(ship.getLocation(now));
					if ((expectedAt.x <= dragX2
							&& expectedAt.x >= dragX1
							&& expectedAt.y <= dragY2
							&& expectedAt.y >= dragY1)
							|| (seenAt.x <= dragX2
							&& seenAt.x >= dragX1
							&& seenAt.y <= dragY2
							&& seenAt.y >= dragY1)) {
						selectedShips.add(ship.id);
						infoPanel.push(ship);
						int currDist = Math.min(Math.abs(seenAt.x - dragX2) + Math.abs(seenAt.y - dragY2),
								Math.abs(expectedAt.x - dragX2) + Math.abs(expectedAt.y - dragY2));
						if (currDist <= minDistShip) {
							onlyShip = ship;
							minDistShip = currDist;
						}

					}
				}
				if (selectedShips.isEmpty()) {
					if (onlyShip != null) {
						selectedShips.add(onlyShip.id);
					}
				}
				for (Star star : cache.getStars()) {
					if (star == null) {
						continue;
					}
					Point seenAt = convert(star.getLocation());
					if ((seenAt.x <= dragX2
							&& seenAt.x >= dragX1
							&& seenAt.y <= dragY2
							&& seenAt.y >= dragY1)) {
						selectedStars.add(star.getId());
						infoPanel.push(star);
						int currDist = Math.abs(seenAt.x - dragX2) + Math.abs(seenAt.y - dragY2);
						if (currDist <= minDistStar) {
							onlyStar = star;
							minDistStar = currDist;
						}

					}
				}
				if (selectedStars.isEmpty()) {
					if (null != onlyStar) {
						selectedStars.add(onlyStar.getId());
					}
				}
			}
		}
	}

	@Override public void mouseEntered(MouseEvent e) {}

	@Override public void mouseExited(MouseEvent e) {}

	@Override public void actionPerformed(ActionEvent e) {}


	Thread repaintThread;

	TechTreeViewer techTreeViewer;
	public SwingGui() {
		/////////////////////////////starPopupMenu=new StarPopupMenu(this);
		starInfoFrame=new StarInfoFrame();
		mainPanel = new JPanel() {

			@Override
			public void paint(Graphics g) {
				renderFrame(g);
			}
		};
		mainPanel.addMouseListener(this);
		mainPanel.addMouseMotionListener(this);
		mainPanel.addMouseWheelListener(this);
		this.addKeyListener(this);//?
		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);
		///////////////////////////////////infoPanel=new InfoPanel(this);
		add(infoPanel, BorderLayout.SOUTH);
		repaintThread = new Thread(new Runnable() {

			@Override public void run() {
				Runnable renderFrame = new Runnable() {

					@Override public void run() {
						repaint();
					}
				};
				while (true) {
					try {
						//System.out.println("Invoking...");
						SwingUtilities.invokeAndWait(renderFrame);
						//System.out.println("Invoked");
						centreCoërtion+=0.1;
						if(centreCoërtion>1)centreCoërtion=1;
						centreV=Vector3D.between(sourceCentreV,centreCoërtion,targetCentreV);
						Thread.sleep(50);
					} catch (InterruptedException ex) {
						return;
					} catch (InvocationTargetException ex) {
						return;
					}
				}
			}
		});
		techTreeViewer=new TechTreeViewer();
		{
			JMenuBar menuBar=new JMenuBar();
			add(menuBar,BorderLayout.NORTH);
			JMenu game=new JMenu("Game");
			menuBar.add(game);
				JMenuItem speed0 = new JMenuItem("Pause");
				speed0.addActionListener(new ActionListener() {
					@Override public void actionPerformed(ActionEvent ae) {
						SwingGui.this.cache.setGameSpeed(0.0);
					}
				});
				game.add(speed0);
				JMenuItem speed01 = new JMenuItem("10% Speed");
				speed01.addActionListener(new ActionListener() {
					@Override public void actionPerformed(ActionEvent ae) {
						SwingGui.this.cache.setGameSpeed(0.1);
					}
				});
				game.add(speed01);
				JMenuItem speed1 = new JMenuItem("Normal Speed");
				speed1.addActionListener(new ActionListener() {
					@Override public void actionPerformed(ActionEvent ae) {
						SwingGui.this.cache.setGameSpeed(1.0);
					}
				});
				game.add(speed1);
				JMenuItem speed3 = new JMenuItem("Double Speed");
				speed3.addActionListener(new ActionListener() {
					@Override public void actionPerformed(ActionEvent ae) {
						SwingGui.this.cache.setGameSpeed(2.0);
					}
				});
				game.add(speed3);
			JMenu view=new JMenu("View");
			menuBar.add(view);
				JMenuItem toggleCoord = new JMenuItem("Toggle coordinates");
				toggleCoord.addActionListener(new ActionListener() {
					@Override public void actionPerformed(ActionEvent ae) {
						SwingGui.this.drawCoordinates^=true;
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
		repaintThread.start();
		setSize(400, 400);
	}

	@Override
	public void dispose() {
		repaintThread.interrupt();
		cache.endGame();
		super.dispose();
	}

	@Override 
	public void mouseDragged(MouseEvent e) {
		if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
			dragX2 = e.getX();
			dragY2 = e.getY();
		}
		if ((e.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) != 0) {
			sourceCentreV = targetCentreV = centreV = centreV
					.sub(
						unrotated.mul(
							(e.getX() - middleButtonPressX)*zoom,
							(e.getY() - middleButtonPressY)*zoom,
							0
						)
					);
			middleButtonPressX = e.getX();
			middleButtonPressY = e.getY();
		}
		if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0) {
			rotation1 += e.getX() - rightButtonPressX;
			rotation2 += e.getY() - rightButtonPressY;
			rightButtonPressX = e.getX();
			rightButtonPressY = e.getY();
			{
				//magic trig!
				double θ=rotation1/250;
				double ψ=rotation2/100;
				double sinψ = Math.sin(ψ), sinθ=Math.sin(θ);
				double cosψ = Math.cos(ψ), cosθ=Math.cos(θ);
				rotated = new Matrix3x3(
						cosθ,     -sinθ,       0,
						cosψ*sinθ, cosψ*cosθ, -sinψ,
						sinψ*sinθ, sinψ*cosθ,  cosψ
						);
				unrotated=rotated.transpose();
			}
		}
	}

	@Override public void mouseMoved(MouseEvent e) {
	}

	@Override public void mouseWheelMoved(MouseWheelEvent e) {
		zoom /= Math.exp(e.getWheelRotation() / 5.0);
	}

	@Override
	public void setPlayerCache(PlayerCache pc) {
		cache = pc;
		starPopupMenu.cache=pc;
	}

	@Override
	public void startTheGui() {
		setVisible(true);
	}

	@Override
	public void stopTheGui() {
		//IGNORE ME
	}

	@Override
	public void keyTyped(KeyEvent ke) {
		
	}

	public void smoothlyCenterCameraOn(Vector3D location){
		targetCentreV=location;
		sourceCentreV=centreV;
		centreCoërtion=0;
	}
	@Override public void keyPressed(KeyEvent ke) {
		if(ke.getKeyChar()=='h'){
			smoothlyCenterCameraOn(cache.getShipLocation(cache.id, cache.now()));
		}
		if(ke.getKeyChar()==' '){
			if(selectedStars.size()==1){
				for(int s: selectedStars){
					smoothlyCenterCameraOn(cache.getStars().get(s).getLocation());
				}
			}
			else{
				if(selectedStars.isEmpty() && selectedShips.size()==1){
					for(int s: selectedShips){
						smoothlyCenterCameraOn(cache.getShipLocation(s, cache.now()));
					}
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent ke) {}
}
