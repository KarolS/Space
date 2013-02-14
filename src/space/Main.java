package space;

import space.engine.LocalClientsidePlayerApi;
import space.engine.RealWorld;
import space.engine.PlayerCache;
import space.engine.civilizations.AmericanCivilization;
import space.engine.civilizations.JapaneseCivilization;
import space.gui.Gui;
import space.gui.SlickGui;
import vytah.math.Vector3D;

/**
 *
 * @author karol
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        RealWorld realWorld=new RealWorld(new AmericanCivilization(), new JapaneseCivilization());
		realWorld.initStarMap();
		PlayerCache pc0=new PlayerCache();
		//Gui gui = new SwingGui();
		Gui gui = new SlickGui();
		gui.setPlayerCache(pc0);
		LocalClientsidePlayerApi api0=new LocalClientsidePlayerApi();
		LocalClientsidePlayerApi api1=new LocalClientsidePlayerApi();
		realWorld.playerCaches.add(pc0);
		PlayerCache pc1;
		realWorld.playerCaches.add(pc1=new PlayerCache());
		pc0.id=0;
		pc1.id=1;
		realWorld.addShip(new vytah.math.Vector3D(100,100,0), 0,false);
		realWorld.addShip(new Vector3D(100,400,0), 1,false);
		realWorld.addShip(new Vector3D(300,350,0), 0,true);
		realWorld.addShip(new Vector3D(300,200,0), 0,false);
		api0.world=realWorld;
		api1.world=realWorld;
		pc0.api=api0;
		pc1.api=api1;
		/*if(gui instanceof JmeGui){
			JmeGui g= (JmeGui) gui;
			g.setConfigShowMode(ConfigShowMode.AlwaysShow);
		}*/
		gui.startTheGui();
		realWorld.startPlay();
		gui.stopTheGui();
    }

	private Main() {
	}

}
