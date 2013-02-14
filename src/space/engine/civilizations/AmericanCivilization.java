/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.engine.civilizations;

/**
 *
 * @author karol
 */
public class AmericanCivilization implements Civilization{

	@Override
	public String getDefaultName() {
		return "United States";
	}

	String[]NATO={"Alpha", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot", "Golf", "Hotel", "Juliett", "Kilo","Lima","Mike",
	"November", "Oscar", "Papa", "Quebec", "Romeo", "Sierra", "Tango", "Uniform", "Victor", "Whisky", "X-ray", "Yankee", "Zulu"};
	@Override
	public String getShipName(int i) {
		if(i==0){
			return "Space Force One";
		}
		else{
			return NATO[(i-1)%NATO.length]+" "+(i+1);
		}
	}

	@Override
	public String getDefaultShortName() {
		return "US";
	}

}
