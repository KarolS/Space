/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.engine.civilizations;

/**
 *
 * @author karol
 */
public class JapaneseCivilization implements Civilization{

	@Override
	public String getDefaultName() {
		return "Japan";
	}

	public String[] IROHA={
		"Iroha", "Rōma", "Hagaki", "Nippon", "Hoken", "Heiwa", "Tōkyō",
		"Chidori", "Ringo", "Numazu", "Rysui", "Owari",
		"Warabi", "Kawase", "Yoshino", "Tabako", "Renge", "Soroban",
		"Tsurukame", "Nezumi", "Nagoya", "Rajio", "Musen",
		"Ueno", "Ido", "Nohara", "Ōsaka", "Kurabu", "Yamato", "Matchi",
		"Keshiki", "Fujisan", "Kodomo", "Eigo", "Tegami",
		"Asahi", "Sakura", "Kitte", "Yumiya", "Meiji", "Mikasa", "Shinbun",
		"Kagi", "Hikōki", "Momiji", "Sekai", "Suzume"
	};

	//TODO: Idiotyczne
	@Override
	public String getShipName(int i) {
		if(i==0){
			return "Tenkoku-sen";
		}
		else{
			return IROHA[(i-1)%IROHA.length]+" "+(i+1);
		}
	}

	@Override
	public String getDefaultShortName() {
		return "JP";
	}

}
