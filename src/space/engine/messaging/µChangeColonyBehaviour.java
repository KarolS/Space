package space.engine.messaging;
import space.api.PlayerCacheApi;
import space.engine.Colony;
import space.engine.RealWorld;
import space.engine.Ship;
import space.engine.Star;
import space.engine.colonybehaviour.ColonyBehaviour;

public class µChangeColonyBehaviour extends Msg {

  private ColonyBehaviour behaviour;

  @Override
  public void processForMothership(Ship ship, RealWorld world, PlayerCacheApi pc) {
    processForCommonShip(ship, world);
  }

  @Override
  public void processForCommonShip(Ship ship, RealWorld world) {
    Colony colony = ship.colony;
    if(colony != null && senderId == ship.owner) {
      colony.behaviour = behaviour;
    }
  }

  @Override
  public MsgAction getDefaultAction() {
    return MsgAction.USE_LOCALLY;
  }
  
  public µChangeColonyBehaviour(ColonyBehaviour beh){
    this.behaviour = beh;
  }
}
