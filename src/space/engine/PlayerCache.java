/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package space.engine;

import space.api.PlayerCacheApi;
import space.engine.messaging.µInformAboutColonyFoundation;
import space.engine.util.Ph;
import space.engine.messaging.µCreation;
import space.engine.messaging.µMoveOrder;
import space.engine.messaging.µDisappeared;
import space.engine.messaging.µAppeared;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import space.api.ClientSidePlayerApi;
import space.engine.messaging.Msg;
import space.engine.messaging.µColonyFoundationOrder;
import space.engine.util.Event;
import space.engine.util.SmallIntSet;
import vytah.math.Vector3D;

/**
 * Pośrednik między interfejsem/AI a API klienta
 * @author karol
 */
public class PlayerCache implements PlayerCacheApi {

	public ClientSidePlayerApi api;

	@Override
	public List<Star> getStars() {
		return api.getStars();
	}

	public double now() {
		return api.getNow();
	}
	public Map<Long, Ship> shipCache = new HashMap<Long, Ship>();
	public LaserBeamQueue laserBeamQueue = new LaserBeamQueue();
	public int id;

	public void queueMovement(long shipId, Vector3D target) {
		queueMovement(shipId, target, null);
	}

	public void queueMovement(long shipId, Vector3D target, Star star) {
		//nie można ruszać nieistniejącym ani cudzym statkiem
		if (shipCache.containsKey(shipId) == false) {
			return;
		}
		Ship cachedShip = shipCache.get(shipId);
		if (cachedShip.owner != id) {
			return;
		}
		cachedShip.planetToColonize=null;

		µMoveOrder m = new µMoveOrder();
		m.createdLocation = shipCache.get((long) id).getLocation(now());
		m.creatorId = id;
		m.createdTime = now();
		m.sentTime = now();
		m.setSource(shipCache.get((long) id));
		m.setDistance(api.getShipLocation((int) (shipId&Msg.ID_MASK), now()));
		m.maxVelocity = 0.7 * Ph.C;
		double wsif = api.getWhenSeenInFuture((int) (shipId&Msg.ID_MASK), m.createdLocation, now());
		m.when = now() + wsif;
		//std::cout<<"signal will go "<<wsif<<std::endl;
		if (m.when < cachedShip.manœuvres.get(cachedShip.manœuvres.size() - 1).getStartTime()) {
			m.when = cachedShip.manœuvres.get(cachedShip.manœuvres.size() - 1).getStartTime();
		}
		m.when += 1.2;
		m.target = target;
		m.player = id;
		m.targetStar = star.getId();
		api.send((int) (shipId&Msg.ID_MASK), m);
		cachedShip.queueMovement(m.when, m.maxVelocity, m.target, star);
	}

	public void queueMovementToStar(long shipId, Star star) {
		queueMovement(shipId, star.getLocation(), star);
	}

	public void addLaserBeam(Vector3D fromLocation, Vector3D toLocation) {
		laserBeamQueue.insert(fromLocation, toLocation, now());
	}

	public PlayerCacheApi getPlayerCacheApi(int owner) {
		return this;
	}

	public Iterable<Ship> getShips() {
		return shipCache.values();
	}

	@Override
	public boolean containsShip(long id) {
		return shipCache.containsKey(id) && shipCache.get(id) != null;
	}

	public Vector3D getShipLocation(int id, double t) {
		return api.getShipLocation(id, t);
	}

	@Override
	public void pushManœuvre(long shipId, Manœuvre manœuvre) {
		//System.out.println("Player "+id+" sees ship "+Long.toHexString(shipId)+" doïng "+manœuvre);
		Ship ship = shipCache.get(shipId);
		//System.out.println(ship);
		//System.out.flush();
		if (!containsShip(shipId)) {
			//System.out.println("Brak statku w cache");
			return;
		}
		ship.manœuvres.add(manœuvre);
	}

	@Override
	public void processAppearedMsg(µAppeared m) {
		//System.out.println("Player "+id+" knows now ship #0x"+Long.toHexString(m.creatorId));
		m.shipCopy.id = (int) (m.creatorId & Msg.ID_MASK);
		m.shipCopy.manœuvres = new ArrayList<Manœuvre>(Arrays.<Manœuvre>asList(m.shipCopy.manœuvres.get(m.shipCopy.getManœuvre(m.createdTime))));
		shipCache.put(m.creatorId, m.shipCopy);
	}

	@Override
	public void processDisappearedMsg(µDisappeared m) {
		shipCache.put(m.creatorId, null);
	}

	@Override
	public void processCreationMsg(µCreation m) {
		m.shipCopy.id = (int) (m.creatorId & Msg.ID_MASK);
		m.shipCopy.manœuvres = new ArrayList<Manœuvre>(Arrays.<Manœuvre>asList(m.shipCopy.manœuvres.get(m.shipCopy.getManœuvre(m.createdTime))));
		shipCache.put(m.creatorId, m.shipCopy);
	}

	@Override
	public void addExplosion(long shipId) {
		if (shipCache.containsKey(shipId)) {
			shipCache.get(shipId).latestHitTime = now();
		}
	}

	public void endGame() {
		api.endGame();
	}
	List<TechnologySourceInfo> technologySources = new ArrayList<TechnologySourceInfo>();
	SmallIntSet knownTechnologies = new SmallIntSet();

	@Override
	public void addTechnologySource(Vector3D source, int technologyId, double time) {
		technologySources.add(new TechnologySourceInfo(source, time, technologyId));
		knownTechnologies.add(technologyId);
	}

	@Override
	public ResourceStatus getColonyResourceStatus(long id) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public SmallIntSet getKnownTechnologies(long shipId) {
		SmallIntSet result = new SmallIntSet();
		double now = now();
		Vector3D location = getShipLocation((int) shipId, now);
		Event shipNow = new Event(location, now);
		for (TechnologySourceInfo technologySource : technologySources) {
			if (shipNow.couldBeCausedBy(technologySource.event)) {
				result.add(technologySource.technologyId);
			}
		}
		return result;
	}

	@Override
	public void colonyResourceChanged(int shipId, ResourceStatus newResources) {
		if (shipCache.containsKey((long) id)) {
			Ship ship = shipCache.get((long) id);
			if (ship.colony != null) {
				ship.colony.resources = newResources;
			}
		}
	}

	@Override
	public void processInformAboutColonyFoundationMessage(µInformAboutColonyFoundation aThis) {
		System.err.println("processInformAboutColonyFoundationMessage");
		Ship ship=shipCache.get(aThis.creatorId);
		if(ship==null) return;
		ship.trimManœvres(aThis.createdTime);
		if(ship.colony==null){
			ship.colony=new Colony();
		}
		ship.colony.landUponPlanet(api.getPlanet(aThis.planetId));
		assert ship.colony.planet!=null;
	}

	public boolean isOurColonyShip(long shipId) {
		Ship ship=shipCache.get(shipId);
		if(ship==null) return false;
		if(ship.owner!=id)return false;
		if (ship.canColonize()&&ship.planetToColonize!=null){
			ship.colony.landUponPlanet(ship.planetToColonize);
		}
		return ship.canColonize();
	}

	public void forceMovementToColonize(long shipId, Planet planet) {
		Ship cachedShip=shipCache.get(shipId);
		if(cachedShip==null) return;
		if(cachedShip.owner!=id)return;
		µColonyFoundationOrder m = new µColonyFoundationOrder();
		m.createdLocation = shipCache.get((long) id).getLocation(now());
		m.creatorId = id;
		m.createdTime = now();
		m.sentTime = now();
		m.setSource(shipCache.get((long) id));
		m.setDistance(api.getShipLocation((int) (shipId&Msg.ID_MASK), now()));
		m.maxVelocity = 0.7 * Ph.C;
		double wsif = api.getWhenSeenInFuture((int) (shipId&Msg.ID_MASK), m.createdLocation, now());
		m.when = now() + wsif;
		//std::cout<<"signal will go "<<wsif<<std::endl;
		if (m.when < cachedShip.manœuvres.get(cachedShip.manœuvres.size() - 1).getStartTime()) {
			m.when = cachedShip.manœuvres.get(cachedShip.manœuvres.size() - 1).getStartTime();
		}
		m.when += 1.2;
		m.player = id;
		m.targetPlanet = planet.globalId();
		api.send((int) (shipId&Msg.ID_MASK), m);
		cachedShip.planetToColonize=planet;
		cachedShip.forceMovement(m.when, m.maxVelocity, planet.parent.getLocation(), planet.parent);
	}

	@Override
	public void setGameSpeed(double d) {
		api.setGameSpeed(d);
	}

	@Override
	public Planet getPlanet(long id) {
		return api.getPlanet(id);
	}

	@Override
	public String getPlayerName(int playerId) {
		return api.getPlayerName(playerId);
	}
	@Override
	public String getPlayerShortName(int playerId) {
		return api.getPlayerShortName(playerId);
	}
}
