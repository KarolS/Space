package space.api;

import java.util.List;
import space.engine.Planet;
import space.engine.Star;
import space.engine.messaging.Msg;
import vytah.math.Vector3D;

/**
 *
 * @author karol
 */
public interface ClientSidePlayerApi {
	/**
	 * Zwraca używaną przez serwer prędkość gry
	 * @return
	 */
	double getTimePerMillisecond();
	/**
	 * Zwraca bieżacy czas na serwerze, z uwzględnieniem poprawek
	 * @return
	 */
	double getNow();
	/**
	 * Zwraca położenie statku o danym id w danej chwili
	 * @param shipId
	 * @param time
	 * @return
	 */
	Vector3D getShipLocation(int shipId, double time);

	double getWhenSeenInFuture(int shipId, Vector3D fromWhere, double t0);

	/**
	 * Wysyła dowolny komunikat do danego statku
	 * @param shipId
	 * @param m
	 */
	void send(int shipId, Msg m);
	/**
	 * Wysyła żądanie zakończenia gry
	 */
	public void endGame();

	/**
	 * Zwraca niemodyfikowalną listę wszystkich gwiazd.
	 * @return
	 */
	public List<Star> getStars();
	public Planet getPlanet(long id);

	public Long getPlanetIdIfColony(int shipId);

	public void setGameSpeed(double d);

	public String getPlayerName(int playerId);

	public String getPlayerShortName(int playerId);
}
