package space.api;

import java.util.List;
import space.engine.Manœuvre;
import space.engine.Planet;
import space.engine.ResourceStatus;
import space.engine.Star;
import space.engine.messaging.µInformAboutColonyFoundation;
import space.engine.messaging.µCreation;
import space.engine.messaging.µDisappeared;
import space.engine.messaging.µAppeared;
import space.engine.util.SmallIntSet;
import vytah.math.Vector3D;

/**
 * Api cache gracza, używane od strony serwera
 * @author karol
 */
public interface PlayerCacheApi {

	/**
	 * dodaje ewent wynalezienia wynalazku w danym punkcie
	 * @param source punkt wynalezienia wynalazku
	 * @param technologyId id wynalazku
	 * @param time moment wynalezienia wynalazku
	 */
	void addTechnologySource(Vector3D source, int technologyId, double time);
	/**
	 * Czy cache zawiera statek o danym długim id.
	 * @param id
	 * @return
	 */
	boolean containsShip(long id);

	ResourceStatus getColonyResourceStatus(long id);
	SmallIntSet getKnownTechnologies(long shipId);

	/**
	 * dodaje manewr do kolejki statku o danym długim id
	 * @param shipId
	 * @param manœuvre
	 */
	public void pushManœuvre(long shipId, Manœuvre manœuvre);

	/**
	 * Przetwarza komunikat o zobaczonym wrogim statku
	 * @param m
	 */
	public void processAppearedMsg(µAppeared m);

	/**
	 * Przetwarza komunikat o znikniętym wrogim statku
	 * @param m
	 */
	public void processDisappearedMsg(µDisappeared m);

	/**
	 * Przetwarza komunikat o stworzonym statku
	 * @param m
	 */
	public void processCreationMsg(µCreation m);

	/**
	 * Informuje cache o eksplozji na statku o danym długim id
	 * @param shipId
	 */
	public void addExplosion(long shipId);

	/**
	 * Dodaje impuls lasera lecący od jednego miejsca do drugiego
	 * @param createdLocation
	 * @param target
	 */
	public void addLaserBeam(Vector3D createdLocation, Vector3D target);

	/**
	 * Zwraca listę gwiazd
	 * @return
	 */
	List<Star> getStars();

	Planet getPlanet(long id);

	public void colonyResourceChanged(int shipId, ResourceStatus newResources);

	public void processInformAboutColonyFoundationMessage(µInformAboutColonyFoundation aThis);

	void setGameSpeed(double speed);

	String getPlayerName(int playerId);
	String getPlayerShortName(int playerId);
}
