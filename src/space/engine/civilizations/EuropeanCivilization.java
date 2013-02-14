/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.engine.civilizations;

/**
 *
 * @author karol
 */
public class EuropeanCivilization implements Civilization{

	@Override
	public String getDefaultName() {
		return "Europe";
	}

	//TODO: uzupełnić!
	public String[] MALES={
		"Abbt", "Bacon", "Cook", "Diderot", "Goethe", "Hume", "de Jovellanos", "Kant", "Montesquieu",
		"Novikov", "Obradović", "Poniatowski", "Quesney", "Rousseau", "Smith", "Voltaire"
	};
	public String[] FEMALES={
		"Goeppert-Mayer", "Meitner","Skłodowska-Curie",
	};
	@Override
	public String getShipName(int i) {
		if(i==0){
			return "Navis Princeps";
		}
		else{
			if(i%2==0) return MALES[((i/2)-1)%MALES.length]+" "+(i+1);
			else return FEMALES[(i/2)%FEMALES.length]+" "+(i+1);
		}
	}

	@Override
	public String getDefaultShortName() {
		return "EU";
	}

}
