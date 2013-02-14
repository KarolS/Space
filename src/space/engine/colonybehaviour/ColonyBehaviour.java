package space.engine.colonybehaviour;

import space.engine.Colony;
import space.engine.RealWorld;
import space.engine.Star;

/**
 *
 * @author karol
 */
public class ColonyBehaviour {

    private final ColonyProduction peaceProduction;
    private final ColonyProduction warProduction;
    private final ColonyRallyPoint warRallyPoint;
    private final ColonyRallyPoint peaceRallyPoint;

    private boolean isAtWar() {
        return false;
    }

    public ColonyBehaviour(ColonyProduction peaceProduction, ColonyRallyPoint peaceRallyPoint, ColonyProduction warProduction, ColonyRallyPoint warRallyPoint) {
        this.peaceProduction = peaceProduction;
        this.peaceRallyPoint = peaceRallyPoint;
        this.warProduction = warProduction;
        this.warRallyPoint = warRallyPoint;
    }

    public int getNextBuilding(Colony colony, RealWorld world) {
        return (isAtWar() ? warProduction : peaceProduction).getNextBuilding(colony, world);

    }

    public int getNextShip(Colony colony, RealWorld world) {
        return (isAtWar() ? warProduction : peaceProduction).getNextShip(colony, world);
    }

    public Star getRallyPoint(Colony colony, RealWorld world) {
        return (isAtWar() ? warRallyPoint : peaceRallyPoint).getRallyPoint(colony, world);
    }

    /**
     * @return the peaceProduction
     */
    public ColonyProduction getPeaceProduction() {
        return peaceProduction;
    }

    /**
     * @return the warProduction
     */
    public ColonyProduction getWarProduction() {
        return warProduction;
    }

    /**
     * @return the warRallyPoint
     */
    public ColonyRallyPoint getWarRallyPoint() {
        return warRallyPoint;
    }

    /**
     * @return the peaceRallyPoint
     */
    public ColonyRallyPoint getPeaceRallyPoint() {
        return peaceRallyPoint;
    }
}
