/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package space.engine.messaging;

import java.util.Comparator;
import space.api.PlayerCacheApi;
import space.engine.RealWorld;
import space.engine.util.Ph;
import space.engine.Ship;
import vytah.math.Vector3D;

/**
 *
 * @author karol
 */
public abstract class Msg {
	public static final long ID_MASK=      0x0ffffffffl;
	public static final long ID_MULTIPLER= 0x100000000l;

	/**
	 * Czas stworzenia komunikatu
	 */
	public double createdTime;
	/**
	 * Czas wysłania komunikatu
	 */
	public double sentTime;
	/**
	 * Połowa odległości, jaką maksymalnie musi przebyć komunikat, by dotrzeć do celu, gdy ten by od niego uciekał
	 * Mierzona w czasie
	 */
	//public double distanceOnEvent;
	/**
	 * Miejsce powstania komunikatu
	 */
	public Vector3D createdLocation;
	/**
	 * Miejsce stworzenia komunikatu
	 */
	public Vector3D sentLocation;
	/**
	 * Statek, który wysłał komunikat
	 */
	public int senderId;
	/**
	 * Statek, który zapoczątkował komunikat (długie id);
	 */
	public long creatorId;

	/*
	 * Najwcześniejsza chwila, w której może przybyć sygnał, tj.
	 * gdyby cel do źródła leciał z prędkością c
	 */
	public double fastestPossibleArrivalTime;

	/**
	 * Ustawia miejsce wysłania i id nadawcy na położenie statku s w chwili sentTime.
	 * sentTime musi być ustawiony wcześniej!
	 * @param s
	 */
	public void setSource(Ship s) {
		sentLocation = s.getLocation(sentTime);
		senderId = s.id;
	}

	/**
	 * Ustawia odległość do celu na podstawie współrzędnych celu i miejsca wysłania.
	 * sentLocation musi być ustawione wcześniej, bezpośrednio lub przez setSource(Ship)!
	 * @param target współrzędne celu
	 */
	public void setDistance(Vector3D target) {
		//distanceOnEvent = V.abs(target, sentLocation) / Ph.C;
		fastestPossibleArrivalTime = (Vector3D.abs(target, sentLocation))/(2.0*Ph.C)+sentTime;
	}

	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder(this.getClass().getSimpleName());
		sb.append(" sent from ").append(sentLocation).append(" t=").append(sentTime)
				.append(" and won\'t arrive earlier than ").append(fastestPossibleArrivalTime);
		return sb.toString();
	}

	public static final Comparator<Msg> COMPARATOR=new Comparator<Msg>(){

		public int compare(Msg o1, Msg o2) {
			int r=Double.compare(o1.sentTime, o2.sentTime);
			if(r!=0){
				return r;
			}
			if(o1 instanceof µAppeared || o1 instanceof µCreation) return 1;
			if(o2 instanceof µAppeared || o2 instanceof µCreation) return -1;
			return 0;
		}


	};

	public abstract void processForMothership(Ship ship, RealWorld world, PlayerCacheApi pc);
	public abstract void processForCommonShip(Ship ship, RealWorld world);
	public abstract MsgAction getDefaultAction();

}
