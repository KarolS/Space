/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.engine.civilizations;

/**
 *
 * @author karol
 */
public class RussianCivilization implements Civilization{

	@Override
	public String getDefaultName() {
		return "Russia";
	}

	public String[] BUKVA_SLOVO={"Anna", "Boris", "Vasilij","Grigorij","Dmitrij","Elena", "Ženâ",
	"Zinaida", "Ivan", "Konstantin","Leonid", "Mihail", "Nikolaj", "Ol\'ga", "Pavel", "Roman", "Semën",
	"Tat\'âna","Ul\'âna","Fëdor","Hariton", "Caplâ","Čelovek","Šura", "Ŝuka","Èho","Ûrij","Âkov"};
	@Override
	public String getShipName(int i) {
		if(i==0){
			return "Kreml'";
		}
		else{
			return BUKVA_SLOVO[(i-1)%BUKVA_SLOVO.length]+" "+(i+1);
		}
	}

	@Override
	public String getDefaultShortName() {
		return "RU";
	}

}
