/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.gui;

import java.awt.Component;
import java.awt.Point;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.CanvasGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.MouseListener;
import org.newdawn.slick.SlickException;
import space.engine.LaserBeam;
import space.engine.PlayerCache;
import space.engine.Ship;
import space.engine.Star;
import vytah.math.Matrix3x3;
import space.engine.util.Ph;
import vytah.math.Vector3D;
 
/**
 *
 * @author karol
 */
public class SlickPanel extends BasicGame implements Gui{

	MouseListener inputListener = new MouseListener() {

		@Override
		public void mouseWheelMoved(int i) {
			zoom /= Math.exp(i / 100.0);
		}

		@Override
		public void mouseClicked(int i, int x, int y, int times) {
			switch(i){
				case Input.MOUSE_LEFT_BUTTON:
					break;
				case Input.MOUSE_MIDDLE_BUTTON:
					break;
				case Input.MOUSE_RIGHT_BUTTON:
					Star bestStar = null;
					int minDist = 17;
					for (Star star : cache.getStars()) {
						Point seenAt = convert(star.getLocation());
						int currDist = Math.abs(x - seenAt.x) + Math.abs(y - seenAt.y);
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
						for (int iii : selectedShips) {
							cache.queueMovement((long) iii, convert(new Point(x,y)));
						}
					} else {//tylko od gwiazdy do gwiazdy!!
						parent.starPopupMenu.setUp(selectedShips, selectedStars, bestStar);
						if (true) {
							parent.starPopupMenu.show(getComponent(), x,y);
						}
					}
			}
		}

		private boolean l,m,r;
		@Override
		public void mousePressed(int i, int x, int y) {
			switch(i){
				case Input.MOUSE_LEFT_BUTTON:
					dragging = true;
					dragX1 = x;
					dragY1 = y;
					dragX2 = dragX1;
					dragY2 = dragY1;
					l=true;
					break;
				case Input.MOUSE_MIDDLE_BUTTON:
					middleButtonPressX = x;
					middleButtonPressY = y;
					m=true;
					break;
				case Input.MOUSE_RIGHT_BUTTON:
					rightButtonPressX = x;
					rightButtonPressY = y;
					r=true;
					break;

			}
		}

		@Override
		public void mouseReleased(int i, int x, int y) {
			switch(i){
				case Input.MOUSE_LEFT_BUTTON:
					l=false;
					synchronized (this) {
						dragging = false;
						dragX2 = x;
						dragY2 = y;
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
						//if(e.isControlDown()||e.isShiftDown()){}
						else{
							selectedShips.clear();
							selectedStars.clear();
							parent.infoPanel.clearAllObjects();
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
								parent.infoPanel.push(ship);
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
								parent.infoPanel.push(star);
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
						parent.repaint();
					}
					break;
				case Input.MOUSE_MIDDLE_BUTTON:
					m=false;
					break;
				case Input.MOUSE_RIGHT_BUTTON:
					r=false;
					break;

			}
		}

		@Override public void mouseMoved(int i, int i1, int x, int y) {
			mouseDragged(i, i1, x, y);
		}

		@Override
		public void mouseDragged(int ox, int oy, int x, int y) {
			if(l){
					dragX2 = x;
					dragY2 = y;
			}
			if(m){
					sourceCentreV = targetCentreV = centreV = centreV
							.sub(
								unrotated.mul(
									(x - middleButtonPressX)*zoom,
									(y - middleButtonPressY)*zoom,
									0
								)
							);
					middleButtonPressX = x;
					middleButtonPressY = y;
			}
			if(r){
					rotation1 += x - rightButtonPressX;
					rotation2 += y - rightButtonPressY;
					rightButtonPressX = x;
					rightButtonPressY = y;
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

		private Input input ;
		@Override
		public void setInput(Input input) { this.input = input; }

		@Override
		public boolean isAcceptingInput() {
			return true;
		}

		@Override public void inputEnded() { }

		@Override public void inputStarted() {}
	};

	private SlickGui parent;
	public SlickPanel(SlickGui _parent){
		super("Space");
		parent = _parent;
		try {
			app = new CanvasGameContainer(this);
		} catch (SlickException ex) {
			Logger.getLogger(SlickPanel.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	@Override
	public void init(GameContainer gc) throws SlickException {
		gc.getInput().addMouseListener(inputListener);
		gc.setAlwaysRender(true);
	}

	@Override
	public void update(GameContainer gc, int i) throws SlickException {
		centreCoërtion+=0.1;
		if(centreCoërtion>1)centreCoërtion=1;
		centreV=Vector3D.between(sourceCentreV,centreCoërtion,targetCentreV);
		/*Input input = gc.getInput();
		if(input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) && !leftMousePressed) {
			dragging = true;
			dragX1 = input.getMouseX();
			dragY1 = input.getMouseY();
			dragX2 = dragX1;
			dragY2 = dragY1;
		}
		if(input.isMouseButtonDown(Input.MOUSE_MIDDLE_BUTTON) && !middleMousePressed) {
			middleButtonPressX = input.getMouseX();
			middleButtonPressY = input.getMouseY();
		}
		if(input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON) && !rightMousePressed) {
			rightButtonPressX = input.getMouseX();
			rightButtonPressY = input.getMouseY();
		}
		if(!input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) && leftMousePressed) {
			synchronized (this) {
				dragging = false;
				dragX2 = input.getMouseX();
				dragY2 = input.getMouseY();
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
				//if(e.isControlDown()||e.isShiftDown()){}
				else{
					selectedShips.clear();
					selectedStars.clear();
					//infoPanel.clearAllObjects(); //TODO
				}
				double now = cache.now();
				int minDistShip = 17;
				int minDistStar = 17;
				Ship mothership = cache.shipCache.get((long) cache.id);
				if(mothership==null) return ;
				V home = mothership.getLocation(now);
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
						//infoPanel.push(ship); //TODO
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
						//infoPanel.push(star); //TODO
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
		if(!input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON) && rightMousePressed) {
			Star bestStar = null;
			int minDist = 17;
			for (Star star : cache.getStars()) {
				Point seenAt = convert(star.getLocation());
				int currDist = Math.abs(input.getMouseX() - seenAt.x) + Math.abs(input.getMouseY() - seenAt.y);
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
				for (int ii : selectedShips) {
					cache.queueMovement((long) ii, convert(new Point(input.getMouseX(),input.getMouseY())));
				}
			} else {//tylko od gwiazdy do gwiazdy!!
				starPopupMenu.setUp(selectedShips, selectedStars, bestStar);
				if (true||e.isPopupTrigger()) {
					starPopupMenu.show(mainPanel, e.getX(), e.getY());
				}
			}
		}
		leftMousePressed = input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON);
		middleMousePressed = input.isMouseButtonDown(Input.MOUSE_MIDDLE_BUTTON);
		rightMousePressed = input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON);
		if(leftMousePressed) {
			dragX2 = input.getMouseX();
			dragY2 = input.getMouseY();
		}
		if(middleMousePressed) {
			sourceCentreV = targetCentreV = centreV = centreV
					.sub(
						unrotated.mul(
							(input.getMouseX() - middleButtonPressX)*zoom,
							(input.getMouseY() - middleButtonPressY)*zoom,
							0
						)
					);
			middleButtonPressX = input.getMouseX();
			middleButtonPressY = input.getMouseY();
		}
		if(rightMousePressed){
			rotation1 += input.getMouseX() - rightButtonPressX;
			rotation2 += input.getMouseY() - rightButtonPressY;
			rightButtonPressX = input.getMouseX();
			rightButtonPressY = input.getMouseY();
			{
				//magic trig!
				double θ=rotation1/250;
				double ψ=rotation2/100;
				double sinψ = Math.sin(ψ), sinθ=Math.sin(θ);
				double cosψ = Math.cos(ψ), cosθ=Math.cos(θ);
				rotated = new M(
						cosθ,     -sinθ,       0,
						cosψ*sinθ, cosψ*cosθ, -sinψ,
						sinψ*sinθ, sinψ*cosθ,  cosψ
						);
				unrotated=rotated.transpose();
			}

		}*/
		Input input = gc.getInput();
		if(input.isKeyDown(Input.KEY_SPACE)){
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
		if(input.isKeyDown(Input.KEY_H)){
			smoothlyCenterCameraOn(cache.getShipLocation(cache.id, cache.now()));
		}

	}

	boolean leftMousePressed, middleMousePressed, rightMousePressed;

	Set<Integer> selectedShips = new HashSet<Integer>();
	Set<Integer> selectedStars = new HashSet<Integer>();
	volatile double rotation1,rotation2;
	volatile Matrix3x3 rotated=Matrix3x3.I;
	volatile Matrix3x3 unrotated=Matrix3x3.I;
	volatile Vector3D centreV=new Vector3D(0,0,0);
	volatile double centreCoërtion=0;
	volatile Vector3D sourceCentreV=new Vector3D(0,0,0);
	volatile Vector3D targetCentreV=new Vector3D(0,0,0);
	volatile double zoom = 1;
	volatile boolean drawCoordinates=true;
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


	public Point convert(Vector3D v) {
		v=v.sub(centreV);
		Vector3D tmp = rotated.mul(v);
		return new Point((int) (tmp.x() / zoom)+app.getWidth()/2, (int) (tmp.y() / zoom)+app.getHeight()/2);
	}

	public Vector3D convert(Point p) {
		return unrotated.mul((p.x-app.getWidth()/2) * zoom,(p.y-app.getHeight()/2) * zoom,0).add(centreV);
	}
	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		g.setColor(Color.darkGray);
		g.fillRect(0, 0, app.getWidth(), app.getHeight());

		double extraZoom=Math.min(app.getWidth(),app.getHeight())*0.35;
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
			g.setColor(Color.yellow);
			g.fillOval(seenAt.x - 3, seenAt.y - 3, 7, 7);
			g.drawString(star.name, seenAt.x + 5, seenAt.y);
			synchronized (this) {
				if (selectedStars.contains(Integer.valueOf(star.getId()))) {
					g.setColor(Color.pink);
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
					g.setColor(Color.green);
					g.fillOval(expectedAt.x - 3, expectedAt.y - 3, 7, 7);
					//g.drawString((ship.owner == cache.id) ? "Colony " : "Enemy colony ", seenAt.x, seenAt.y);
					//TODO
				} else {
					g.setColor(Color.red);
					g.fillOval(seenAt.x - 3, seenAt.y - 3, 7, 7);
					//tmp=ImageFactory.getImage(ship, rotated, now-t, ImageFactory.TYPE_VISIBLE);
					//g.drawImage(tmp, seenAt.x - tmp.getWidth()/2, seenAt.y-tmp.getHeight()/2, null);
					g.drawString(((ship.owner == cache.id) ? "" : "["+cache.getPlayerShortName(ship.owner)+"] ") + (!ship.canColonize() ? "" : "COL ") + ship.name + ", dist: " + Ph.timeToStr(t), seenAt.x, seenAt.y);
					g.setColor(Color.white);
					g.fillOval(expectedAt.x - 3, expectedAt.y - 3, 7, 7);
					//tmp=ImageFactory.getImage(ship, rotated, now, ImageFactory.TYPE_NOW);
					//g.drawImage(tmp, expectedAt.x - tmp.getWidth()/2, expectedAt.y-tmp.getHeight()/2, null);
					g.drawString(((ship.owner == cache.id) ? "" : "["+cache.getPlayerShortName(ship.owner)+"] ") + (!ship.canColonize() ? "" : "COL ") + ship.name + " now.", expectedAt.x, expectedAt.y);
					if (ship.owner == cache.id) {
						g.setColor(Color.blue);
						g.fillOval(listensAt.x - 3, listensAt.y - 3, 7, 7);
						//tmp=ImageFactory.getImage(ship, rotated, now+tf, ImageFactory.TYPE_FUTURE);
						//g.drawImage(tmp, listensAt.x - tmp.getWidth()/2, listensAt.y-tmp.getHeight()/2, null);
						g.drawString("", listensAt.x, listensAt.y);
					}
				}
				synchronized (this) {
					if (selectedShips.contains(Integer.valueOf(ship.id))) {
						g.setColor(Color.pink);
						g.drawOval(seenAt.x - 5, seenAt.y - 5, 11, 11);
					}
				}
				g.setColor(Color.cyan);
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
			cex.printStackTrace();
		}
		g.setColor(Color.white);
		if(dragging){
			g.drawRect(dragX1, dragY1, dragX2-dragX1, dragY2-dragY1);
		}
		g.drawString(Ph.dateToStr(now), 10, 35);
	}

	volatile private PlayerCache cache;
	@Override
	public void setPlayerCache(PlayerCache pc) {
		cache = pc;
	}
	private CanvasGameContainer app;
	public Component getComponent() {
		return app;
	}
	private Thread guiThread = new Thread() {
		public void run(){
			try {
				//app.setDisplayMode(800, 600, false);
				app.start();
			} catch (SlickException ex) {
				Logger.getLogger(SlickPanel.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	};
	@Override
	public void startTheGui() {
		guiThread.start();
	}

	@Override
	public void stopTheGui() {
		//app.exit(); //TODO
	}
	public void smoothlyCenterCameraOn(Vector3D location){
		targetCentreV=location;
		sourceCentreV=centreV;
		centreCoërtion=0;
	}

}
