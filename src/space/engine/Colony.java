package space.engine;

import space.engine.colonybehaviour.ColonyBehaviour;

/**
 *
 * @author karol
 */
public class Colony implements Cloneable{

	public String name;
	public Planet planet;
	public ResourceStatus resources=new ResourceStatus();
  public ColonyBehaviour behaviour;
  
	@Override
	public Colony clone(){
		Colony c=new Colony();
		c.name=name;
		c.planet=planet;
		c.resources=resources;
		c.behaviour=behaviour;
		return c;
	}
	public void landUponPlanet(Planet aPlanet){
		planet=aPlanet;
		resources.touch();//touchujemy, by się wysłało
		//TODO - tu będzie ciekawy kod
	}
}
