/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package space.engine;

import space.api.PlayerCacheApi;
import space.engine.messaging.Msg;
import space.engine.messaging.µCreation;
import java.util.ArrayList;
import java.util.List;
import space.engine.civilizations.Civilization;
import vytah.math.Vector3D;

/**
 *
 * @author karol
 */
public class RealWorld {

	public StarMap starmap;
	protected double now = 0;
	public List<PlayerCacheApi> playerCaches = new ArrayList<PlayerCacheApi>();
	public List<Ship> ships = new ArrayList<Ship>();
	public Player[] players;
	private long lastUpdateTime;

	public RealWorld(Civilization... civilizations){
		players=new Player[civilizations.length];
		for(int i=0; i<players.length; i++){
			players[i]=new Player(civilizations[i]);
		}
	}
	public double now() {
		return now;
	}

	void send(int shipId, Msg m) {
		ships.get(shipId).msgQueue.insert(m);
	}

	public PlayerCacheApi getPlayerCacheApi(int id) {
		return playerCaches.get(id);
	}

	public Iterable<Ship> getShips() {
		return ships;
	}

	public Vector3D getShipLocation(int id, double t) {
		return ships.get(id).getLocation(t);
	}
	public volatile boolean running = true;
	public volatile double speed=1.0;
	public double getTimePerMillisecond(){
		return 0.01*speed;
	}
	public long getLocalTime(){
		return System.currentTimeMillis();
	}
	public long getLastUpdateTime(){
		return lastUpdateTime;
	}
	public void play() {
		while (running) {
			now += getTimePerMillisecond()*(getLocalTime()-lastUpdateTime);
			lastUpdateTime=System.currentTimeMillis();
			for (Ship ship : ships) {
				ship.process(this);
				//if(((int)now)%5==0)System.err.println(ship.id+" @ "+ship.getLocation(now)+" @ "+ship.getStar(now));
				ship.aimAndShoot(this, ships, now);
				try {
					Thread.sleep(100);
				} catch (InterruptedException ex) {
					running=false;
					return;
				}
			}
		}
	}

	public void startPlay() {
		lastUpdateTime=System.currentTimeMillis();
		new Thread(new Runnable() {

			public void run() {
				play();
			}
		}).start();
	}

	public void addShip(Vector3D location, int owner,boolean isColonyShip) {
		Ship ship = new Ship(location, now);
		ship.owner = owner;
		ship.id = ships.size();
		ship.name=players[owner].getNewShipName();
		if(isColonyShip){
			ship.colony=new Colony();
		}
		ships.add(ship);
		µCreation m = new µCreation();
		m.createdLocation = location;
		m.creatorId = ship.id;
		m.createdTime = now;
		m.sentTime = now;
		m.setSource(ship);
		m.shipCopy=ship.clone();
		m.setDistance(ships.get(owner).getLocation(now));
		ships.get(owner).msgQueue.insert(m);
	}

	public void initStarMap() {
		starmap=new StarMap();
		starmap.defaultInit();
	}

	void setGameSpeed(double d) {
		speed=d;
	}
}
