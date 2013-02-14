package space.engine;

import java.util.Collections;
import java.util.List;
import space.api.ClientSidePlayerApi;
import space.engine.messaging.Msg;
import vytah.math.Vector3D;

/**
 *
 * @author karol
 */
public class LocalClientsidePlayerApi implements ClientSidePlayerApi {
	public RealWorld world;
	@Override
	public double getNow() {
		return world.now()+world.getTimePerMillisecond()*(world.getLocalTime()-world.getLastUpdateTime());
	}

	@Override
	public Vector3D getShipLocation(int shipId, double time) {
		return world.ships.get(shipId).getLocation(time);
	}

	@Override
	public double getWhenSeenInFuture(int shipId, Vector3D fromWhere, double t0) {
		return world.ships.get(shipId).whenSeenInFuture(fromWhere, t0);
	}
	public double getWhenSeenInPast(int shipId, Vector3D fromWhere, double t0) {
		return world.ships.get(shipId).whenSeenInPast(fromWhere, t0);
	}

	@Override
	public void send(int shipId, Msg m) {
		world.send(shipId,m);
	}

	@Override
	public void endGame() {
		world.running=false;
	}

	@Override
	public double getTimePerMillisecond() {
		return world.getTimePerMillisecond();
	}

	@Override
	public List<Star> getStars() {
		return Collections.unmodifiableList(world.starmap.stars);
	}

	@Override
	public Long getPlanetIdIfColony(int shipId) {
		Ship ship=world.ships.get(shipId);
		if(ship.colony!=null && ship.colony.planet!=null){
			Planet that=ship.colony.planet;
			long res= that.globalId();
			//assert?
			return res;
		}
		return null;
	}

	@Override
	public void setGameSpeed(double d) {
		world.setGameSpeed(d);
	}

	@Override
	public Planet getPlanet(long id) {
		return world.starmap.getPlanetById(id);
	}

	@Override
	public String getPlayerName(int playerId) {
		return world.players[playerId].name;
	}
	@Override
	public String getPlayerShortName(int playerId) {
		return world.players[playerId].shortName;
	}


}
