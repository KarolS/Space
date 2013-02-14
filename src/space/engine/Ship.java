/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package space.engine;

import space.api.PlayerCacheApi;
import space.engine.messaging.MsgQueue;
import space.engine.util.Ph;
import space.engine.messaging.Msg;
import space.engine.messaging.µInformAboutFire;
import space.engine.messaging.µFire;
import space.engine.messaging.µManœuvre;
import space.engine.messaging.µDisappeared;
import space.engine.messaging.µAppeared;
import java.util.ArrayList;
import java.util.List;
import space.engine.messaging.MsgAction;
import space.engine.messaging.µInformAboutColonyFoundation;
import space.engine.messaging.µInformAboutResourceChange;
import vytah.math.Vector3D;
import vytah.math.equations.QuadraticEquation;

/**
 * Statek lub kolonia
 * @author karol
 */
public class Ship implements Cloneable {

	public int id;
	public int owner;
	public String name;
	/**
	 * Lista manewrów.
	 */
	ArrayList<Manœuvre> manœuvres;
	int processedManœuvresCnt;
	ShipType type;
	/**
	 * Kolejka komunikatów do przetworzenia przez statek
	 */
	public MsgQueue msgQueue;
	/**
	 * Moment stworzenia statku
	 */
	double creationTime;
	/**
	 * Moment zniszczenia statku
	 */
	double destructionTime;
	/**
	 * Moment ostatniego wystrzału
	 */
	double lastFireTime;
	/**
	 * Moment ostatniego bycia trafionym
	 */
	double latestHitTime;
	public Colony colony;
	public Planet planetToColonize;
	private static final double EPSILON = 1 / (64 * 64.0 * 64 * 64 * 64 * 64);

	/**
	 * Zwraca gwiazdę, w której był statek w danym czasie
	 * @param time
	 * @return
	 */
	public Star getStar(double time) {
		return manœuvres.get(getManœuvre(time)).targetStar;
	}

	/**
	 * Przetwarza komunikaty posiadane przez statek. Nie wykonywane przez statki w cache'u.
	 * @param w
	 */
	public synchronized void trimManœvres(double time) {
		int currManœuvreId = getManœuvre(time);
		for (int i = manœuvres.size() - 1; i > currManœuvreId; i--) {
			manœuvres.remove(i); //przycinamy manewry
		}
		Manœuvre penultimate=manœuvres.get(currManœuvreId);
		Manœuvre m=new Manœuvre(penultimate.getLocation(time), penultimate.getVelocity(time), Vector3D.V0, time, penultimate.targetStar);
		manœuvres.add(m);
		if(processedManœuvresCnt>manœuvres.size()-1){
			processedManœuvresCnt=manœuvres.size()-1;
		}
	}

	void process(RealWorld w) {
		PlayerCacheApi pc = w.getPlayerCacheApi(owner);
		Vector3D location = getLocation(w.now());
		//które statki znikają z zasięgu wzroku?
		for (Ship i : w.ships) {
			double t;
			if (owner == i.owner) {
				continue;
			}
			long img_id = i.id + ((owner != id) ? (0x10000 * id) : 0); //ID obrazu
			if (pc.containsShip(img_id)) {
				t = i.whenSeenInPast(location, w.now());
				if (t > 30 * Ph.YR/*zasięg wzroku*/) {
					µDisappeared m = new µDisappeared();
					m.creatorId = img_id;
					m.createdLocation = w.ships.get(i.id).getLocation(t);//the_world.ships[id].get_location(t);
					m.createdTime = t;
					m.sentTime = t;
					m.setSource(this);
					m.setDistance(w.ships.get(owner).getLocation(w.now()));
					w.ships.get(owner).msgQueue.insert(m);
				}
			}
		}
		//które statki pojawiają się w zasięgu wzroku?
		for (Ship i : w.ships) {
			double t;
			if (owner == i.owner) {
				continue;
			}
			long img_id = i.id + ((owner != id) ? (Msg.ID_MULTIPLER * id) : 0);
			if (pc.containsShip(img_id) == false) {
				t = i.whenSeenInPast(location, w.now());
				if (t < 30 * Ph.YR) {
					µAppeared m = new µAppeared();
					m.createdLocation = w.ships.get(i.id).getLocation(t);
					m.creatorId = img_id;
					m.createdTime = t;
					m.sentTime = t;
					m.setSource(this);
					m.setDistance(w.ships.get(owner).getLocation(w.now()));
					m.shipCopy = i.clone();
					w.ships.get(owner).msgQueue.insert(m);
				}
			}
		}
		//
		//jakie manewry wykonujemy?
		while (processedManœuvresCnt < manœuvres.size() && manœuvres.get(processedManœuvresCnt).getStartTime() <= w.now()) {
			double t = manœuvres.get(processedManœuvresCnt).getStartTime();
			Vector3D l = getLocation(t);
			processedManœuvresCnt++;
			for (Ship i : w.ships) {
				if (i.owner != owner) {
					µManœuvre m = new µManœuvre();
					m.createdLocation = l;
					m.creatorId = id;
					m.createdTime = t;
					m.sentTime = t;
					m.setSource(this);
					m.setDistance(i.getLocation(t));
					m.manœuvre = manœuvres.get(processedManœuvresCnt - 1);
					i.msgQueue.insert(m);
				}
			}
		}
		//Jeśli jest to kolonia...
		if (colony != null) {
			//jeśli jest to statek, który jeszcze lata, to sprawdzamy, czy nie usiadł właśnie koło gwiazdy
			if (colony.planet == null && planetToColonize != null) {
				Star currStar = getStar(w.now());
				if (planetToColonize.parent.equals(currStar)) {
					System.err.println("COLONIZING");
					trimManœvres(w.now());
					for (Ship i : w.ships) {//chwalimy się wszystkim naszą nowozałożoną kolonią
						µInformAboutColonyFoundation m = new µInformAboutColonyFoundation();
						m.createdLocation = planetToColonize.parent.getLocation();
						m.creatorId = id;
						m.createdTime = w.now();
						m.sentTime = w.now();
						m.setSource(this);
						m.setDistance(w.ships.get(owner).getLocation(w.now()));
						m.planetId = planetToColonize.globalId();
						i.msgQueue.insert(m);
					}
				}
				colony.landUponPlanet(planetToColonize);
			}
			if (!colony.resources.isUpToDate()) {
				colony.resources.update(w.now());
				µInformAboutResourceChange m = new µInformAboutResourceChange();
				m.createdLocation = getLocation(w.now());
				m.creatorId = id;
				m.createdTime = w.now();
				m.sentTime = w.now();
				m.setSource(this);
				m.setDistance(w.ships.get(owner).getLocation(w.now()));

				m.newResources = colony.resources.clone();
				w.ships.get(owner).msgQueue.insert(m);
			}
		}
		if (id == owner) {
			//statek-matka
			List<Msg> messages = msgQueue.popAll(w.now());
			for (Msg m : messages) {
				//System.out.println(m);
				//std::cout<<"t="<<the_world.now<<std::endl;
				m.processForMothership(this, w, pc);
			}
		} else {
			//zwykły statek
			List<Msg> messages = msgQueue.popAll(w.now());
			for (Msg m : messages) {
				m.processForCommonShip(this, w);
				if (m.getDefaultAction() == MsgAction.FORWARD) {
					m.creatorId = (m.creatorId & Msg.ID_MASK) + Msg.ID_MULTIPLER * id;
					if (pc.containsShip(m.creatorId)) {
						m.sentTime = w.now;
						m.setSource(this);
						m.setDistance(w.ships.get(owner).getLocation(w.now()));
						w.ships.get(owner).msgQueue.insert(m);
					} else {
						System.out.println("No ej!");
					}
				}
			}
		}
	}

	Ship(Vector3D startPos, double startTime) {
		manœuvres = new ArrayList<Manœuvre>();
		manœuvres.add(new Manœuvre(startPos, Vector3D.V0, Vector3D.V0, startTime, false));
		msgQueue = new MsgQueue();
		msgQueue.owner = this;
		processedManœuvresCnt = 0;
		creationTime = startTime;
		destructionTime = Double.NaN;
		lastFireTime = startTime;
		latestHitTime = Double.NEGATIVE_INFINITY;
	}

	/**
	 * Zwraca maksymale przyspieszenie statku
	 * @return
	 */
	double getMaxAcc() {
		return 0.5;/*pół ale czego?*/
	}

// <editor-fold defaultstate="collapsed" desc="Szalona matma, nie dotykać!!!">
	/**
	 * Zwraca id manewru wykonywanego przez statek w danej chwili
	 * @param time
	 * @return
	 */
	synchronized int getManœuvre(double time) {
		int first = 0, last = manœuvres.size() - 1;
		if (last < 0) {
			return 0;
		}
		while (last - first > 1) {
			int middle = (first + last) / 2;
			if (manœuvres.get(middle).getStartTime() <= time) {
				first = middle;
			} else {
				last = middle;
			}
		}
		if (manœuvres.get(last).getStartTime() <= time) {
			return last;
		}
		return first;
	}

	/**
	 * Zwraca wiek obrazu. Do większości zastosowań, należy odjąć go od now.
	 * @param x0 położenie obserwatora
	 * @param t0 chwila
	 * @return nieujemny wiek obrazu statku
	 */
	public synchronized double whenSeenInPast(Vector3D x0, double t0) {
		int first = 0, last = manœuvres.size() - 1;
		while (last - first > 1) {
			int middle = (first + last) / 2;
			double tm = manœuvres.get(middle).getStartTime();
			if (Vector3D.absSq(manœuvres.get(middle).location(), x0) <= Ph.C * Ph.C * (t0 - tm) * (t0 - tm) && tm <= t0) {
				first = middle;
			} else {
				last = middle;
			}
		}
		double tm = manœuvres.get(last).getStartTime();
		if (Vector3D.absSq(manœuvres.get(last).location(), x0) <= Ph.C * Ph.C * (t0 - tm) * (t0 - tm) && tm <= t0) {
			first = last;
		}
		double min_t = manœuvres.get(first).getStartTime();
		if (min_t > t0) {
			min_t = t0;
		}
		double max_t = t0;
		if (first < manœuvres.size() - 1) {
			max_t = manœuvres.get(first + 1).getStartTime();
			if (max_t > t0) {
				max_t = t0;
			}
		}

		/*std::cout<<"man_id: "<<first<<" man_st: "<<manœuvres.get(first].start_time<<" n_man_st: ";
		if(first<last) std::cout<<manœuvres.get(first].start_time;
		else std::cout<<"null";
		std::cout<<" range: ["<<min_t<<","<<max_t<<"]"<<std::endl;*/

		Manœuvre m = manœuvres.get(first);
		for (int i = 0; i < 200 && max_t - min_t > EPSILON; i++) {//TODO: inna pętla
			double med_t = (min_t + max_t) / 2.0;
			double dx1 = Vector3D.abs(m.getLocation(min_t), x0);
			double dx2 = Vector3D.abs(m.getLocation(max_t), x0);
			double r1 = dx1 / Ph.C + min_t - t0;
			double r2 = dx2 / Ph.C + max_t - t0;
			if (Math.abs(r1) < 0.00001) {
				return t0 - min_t;
			}
			if (Math.abs(r2) < 0.00001) {
				return t0 - max_t;
			}
			if (r1 * r2 > 0) {
				//System.err.println("bad cut in PAST");//TODO
				//Thread.dumpStack();
			}
			double dx3 = Vector3D.abs(m.getLocation(med_t), x0);
			double r3 = dx3 / Ph.C + med_t - t0;
			if (r3 * r1 > 0) {
				min_t = med_t;
			} else {
				max_t = med_t;
			}
		}
		return t0 - (min_t + max_t) / 2.0;
	}

	/**
	 * Zwraca za ile czasu sygnał od nadawcy dotrze do statku. Do większości zastosowań, należy dodać go do now.
	 * Wartość tę należy traktować jako prognozę na podstawie istniejącej wiedzy, przyszłość jesz przecię nieprzewidywalna...
	 * @param x0 położenie nadawct
	 * @param t0 chwila
	 * @return nieujemny czas przebiegu sygnału
	 */
	public synchronized double whenSeenInFuture(Vector3D x0, double t0) {
		return whenToAim(x0, t0, manœuvres.size() - 1);
	}

	/**
	 * Zwraca za ile czasu sygnał od nadawcy dotrze do statku. Do większości zastosowań, należy dodać go do now.
	 * Tę metodę używa się do celowania
	 * @param x0 położenie nadawcy
	 * @param t0 chwila
	 * @param maxManœuvreId id ostatniego znanego (tj. mającego być branym pod uwagę) manewru statku
	 * @return nieujemny czas przebiegu sygnału
	 */
	double whenToAim(Vector3D x0, double t0, int maxManœuvreId) {
		int first = 0, last = maxManœuvreId;
		while (last - first > 1) {
			int middle = (first + last) / 2;
			double tm = manœuvres.get(middle + 1).getStartTime();
			if (Vector3D.absSq(manœuvres.get(middle + 1).location(), x0) < Ph.C * Ph.C * (t0 - tm) * (t0 - tm) && tm > t0) {
				last = middle;
			} else {
				first = middle;
			}
		}
		if (first != last) {
			double tm = manœuvres.get(last).getStartTime();
			if ((Vector3D.absSq(manœuvres.get(last).location(), x0) >= Ph.C * Ph.C * (t0 - tm) * (t0 - tm)
					&& tm >= t0)
					|| manœuvres.get(last).getStartTime() < t0) {
				first = last;
			}
		}
		double min_t = manœuvres.get(first).getStartTime();
		if (min_t < t0) {
			min_t = t0;
		}
		double max_t = t0;
		last = maxManœuvreId;
		if (first < last) {
			max_t = manœuvres.get(first + 1).getStartTime();
		} else {
			if (manœuvres.get(first).getStartTime() < t0) {
				max_t = t0 + 2 * Vector3D.abs(manœuvres.get(first).location(), x0) / Ph.C;
			} else {
				max_t = manœuvres.get(first).getStartTime() + 2 * Vector3D.abs(manœuvres.get(first).location(), x0) / Ph.C;
			}
		}
		/*std::cout<<"man_id: "<<first<<" man_st: "<<manœuvres.get(first].start_time<<" n_man_st: ";
		if(first<last) std::cout<<manœuvres.get(first].start_time;
		else std::cout<<"null";
		std::cout<<" range: ["<<min_t<<","<<max_t<<"]"<<std::endl;*/
		Manœuvre m = manœuvres.get(first);
		for (int i = 0; i < 1000 && max_t - min_t > EPSILON / 256; i++) {//TODO: inna pętla
			double med_t = (min_t + max_t) / 2.0;
			double dx1 = -Vector3D.abs(m.getLocation(min_t), x0);
			double dx2 = -Vector3D.abs(m.getLocation(max_t), x0);
			double r1 = dx1 / Ph.C + min_t - t0;
			double r2 = dx2 / Ph.C + max_t - t0;
			if (r1 * r2 > EPSILON) {
				System.err.println("bad cut in FUTURE in iteration "+i);
				System.err.println(String.format("maxManoeuvre: %s thisManoeuvre: %s", maxManœuvreId, first));
				return min_t - t0;//?
				//Thread.dumpStack();
			}
			if (Math.abs(r1) < 0.00001) {
				return min_t - t0;
			}
			if (Math.abs(r2) < 0.00001) {
				return max_t - t0;
			}
			double dx3 = -Vector3D.abs(m.getLocation(med_t), x0);
			double r3 = dx3 / Ph.C + med_t - t0;
			if (r3 * r1 > EPSILON) {
				min_t = med_t;
			} else {
				max_t = med_t;
			}
		}
		return (min_t + max_t) / 2.0 - t0;
	}

	/**
	 * Zwraca położenie statku w danej chwili
	 * @param time
	 * @return
	 */
	public Vector3D getLocation(double time) {
		return manœuvres.get(getManœuvre(time)).getLocation(time);
	}

	/**
	 * Zwraca prędkość statku w danej chwili
	 * @param time
	 * @return
	 */
	public Vector3D getVelocity(double time) {
		return manœuvres.get(getManœuvre(time)).getVelocity(time);
	}

	/**
	 * Zwraca przyspieszenie statku w danej chwili
	 * @param time
	 * @return
	 */
	public Vector3D getAcceleration(double time) {
		return manœuvres.get(getManœuvre(time)).getAcceleration(time);
	}

	/**
	 * Czy statek jest w danej chwili widoczny z danego miejsca
	 * @param fromWhere
	 * @param when
	 * @return
	 */
	public synchronized boolean isTheoreticallyVisible(Vector3D fromWhere, double when) {
		//if(true)return true;
		if (creationTime > when) {
			return false;
		}
		Vector3D creationLocation = manœuvres.get(0).location();
		double how_old = when - creationTime;
		how_old *= Ph.C;
		how_old *= how_old;
		if (how_old < Vector3D.absSq(creationLocation, fromWhere)) {
			return false;
		}
		if (destructionTime < 0) {
			return true;
		}
		if (destructionTime > when) {
			return true;
		}
		Vector3D destructionLocation = getLocation(destructionTime);
		how_old = when - destructionTime;
		how_old *= Ph.C;
		how_old *= how_old;
		if (how_old > Vector3D.absSq(destructionLocation, fromWhere)) {
			return false;//TODO: ?
		}
		return true;
	}
	// </editor-fold>

	public synchronized void forceMovement(double when, double maxVelocity, Vector3D target, Star star) {
		//TODO: co robić, jak urywamy w środku manewru o niezerowej prędkości?
		trimManœvres(when);
		queueMovement(when, maxVelocity, target, star);
	}
	/**
	 * Dodaje do kolejki manewrów kilka manewrów, których celem jest dotarcie do danego celu i zatrzymanie się w nim
	 * @param time chwila rozpoczęcia manewru
	 * @param maxVelocity maksymalna prędkość
	 * @param target cel
	 * @param targetStar docelowa gwiazda
	 */
	public synchronized void queueMovement(double time, double maxVelocity, Vector3D target, Star targetStar) {
		if(isColony()) {
			return; //to nie Gwiazda Śmierci...
		}
		System.out.println("Movement to " + targetStar+" by ship "+id);
		double max_acc = getMaxAcc();
		assert max_acc>EPSILON;
		int last = manœuvres.size() - 1;
		if (time < manœuvres.get(last).getStartTime()) {
			time = manœuvres.get(last).getStartTime();
			System.err.println("invalid movement order, requeuing");
		}
		Vector3D from = getLocation(time);
		Vector3D now_at = from;
		Vector3D direction = target.sub(from);
		double distance = direction.abs();
		if(distance<=EPSILON){
			return;
		} //nie będziemy stać w miejscu, nie?
		assert distance>EPSILON;
		double now = time;
		int noof_phases = 1;
		if (maxVelocity > Ph._3C) {
			noof_phases++;
		}
		if (maxVelocity > Ph._6C) {
			noof_phases++;
		}
		double dist_travelled = 0.0;
		double velocity_to_achieve, duration;
		double acc_now, v_now = 0.0;
		System.out.println("Distance travelled=" + dist_travelled + ", universe time=" + now + ", turning engines on, accelerating");
		for (int i = 0; i < noof_phases; i++) {
			velocity_to_achieve = i == 0 ? Ph._3C : (i == 1 ? Ph._6C : Ph.C);
			acc_now = (3 - i) * max_acc / 3.0;
			assert acc_now>EPSILON;
			if (velocity_to_achieve > maxVelocity) {
				velocity_to_achieve = maxVelocity;
			}
			manœuvres.add(new Manœuvre(now_at, direction.versor(v_now), direction.versor(acc_now), now, false));
			duration = (velocity_to_achieve - v_now) / acc_now;
			assert (velocity_to_achieve>v_now);
			if (dist_travelled + duration * (v_now + duration * acc_now / 2) > distance / 2) {
				QuadraticEquation q = new QuadraticEquation(acc_now / 2, v_now, dist_travelled - distance / 2);
				System.err.println(q);
				duration = q.smallestPositive();
				noof_phases = i + 1;
				assert duration>EPSILON;
				//TODO: nie działa
			}
			 //TODO - niekoniecznie?
			now += duration;
			dist_travelled += duration * (v_now + duration * acc_now / 2);
			v_now += acc_now * duration;
			now_at = from.add(direction.versor(dist_travelled));
		}
		System.out.println("Distance travelled=" + dist_travelled + ", universe time=" + now + ", turning engines off");
		manœuvres.add(new Manœuvre(now_at, direction.versor(v_now), Vector3D.V0, now));
		assert v_now>EPSILON;
		duration = (distance - 2 * dist_travelled) / v_now;
		now += duration;
		dist_travelled += duration * v_now;
		now_at = from.add(direction.versor(dist_travelled));
		System.out.println("Distance travelled=" + dist_travelled + ", universe time=" + now + ", turning engines on, decelerating");
		for (int i = noof_phases - 1; i >= 0; i--) {
			velocity_to_achieve = i == 0 ? 0 : (i == 1 ? Ph._3C : Ph._6C);
			acc_now = (3 - i) * max_acc / 3.0;
			manœuvres.add(new Manœuvre(now_at, direction.versor(v_now), (direction.versor(-acc_now)), now));
			duration = (v_now - velocity_to_achieve) / acc_now;
			now += duration;
			dist_travelled += duration * (v_now - duration * acc_now / 2);
			v_now -= acc_now * duration;
			now_at = from.add(direction.versor(dist_travelled));
		}
		System.out.println("Distance travelled=" + dist_travelled + ", universe time=" + now + ", target reached");
		Manœuvre man=new Manœuvre(now_at, Vector3D.V0, Vector3D.V0, now, targetStar);
		manœuvres.add(man);
		//System.err.print("Now having "+manœuvres.size()+ " manoeuvres, the current one is #");
		//System.err.println(getManœuvre(now+5));
		//assert getStar(now+5)!=null;
	}

	/**
	 * Procedura wyboru celu i oddania strzału do celu przez ten statek.
	 * Procedura nie zrobi nic, jeśli:
	 * - nie ma wrogich statków w zasięgu;
	 * - nie minął niezbędny czas od oddania ostatniego strzału;
	 * - statek nie może strzelać z przyczyn wewnętrznych;
	 * - statek nie zobaczył jeszcze wrogich statków w swoim zasięgu
	 * @param w świat
	 * @param allShips wszystkie statki
	 * @param when chwila
	 */
	synchronized void aimAndShoot(RealWorld w, List<Ship> allShips, double when) {
		if (when < lastFireTime + getFireRate()) {
			return;
		}
		lastFireTime = when;
		Ship target = null;
		double target_time_distance = Double.POSITIVE_INFINITY; //TODO: może dać tu maksymalny zasięg broni?
		int target_id = 0;
		Vector3D this_now_location = getLocation(when);
		for (Ship i : allShips) {
			if (i == this) {
				continue; //Nie wolno strzelać do siebie bo to głupie
			}
			if (i.owner != owner &&//TODO: w testach naparzamy we wszystko
					/* && wojna TODO*/ //&&
					i.isTheoreticallyVisible(this_now_location, when)) {
				if (target == null) {
					target = i;
					target_id = i.id;
					target_time_distance = target.whenSeenInPast(this_now_location, when);
				} else {
					Ship new_target = i;
					double new_time = new_target.whenSeenInPast(this_now_location, when);
					if (new_time < target_time_distance && new_target != this) {
						target = new_target;
						target_id = i.id;
						target_time_distance = new_time;
					}
				}
			}
		}
		if (target != null) {
			int mid = target.getManœuvre(when);
			Vector3D aim = target.manœuvres.get(mid).getLocation(when + target.whenToAim(this_now_location, when, mid));
			{
				µFire m = new µFire();
				m.createdLocation = w.ships.get(id).getLocation(when);
				m.creatorId = id;
				m.createdTime = when;
				m.sentTime = when;
				m.setSource(this);
				m.setDistance(w.ships.get(target_id).getLocation(w.now));
				m.target = aim;
				w.ships.get(target_id).msgQueue.insert(m);
			}
			{
				µInformAboutFire m = new µInformAboutFire();
				m.createdLocation = w.ships.get(id).getLocation(when);
				m.creatorId = id;
				m.createdTime = when;
				m.sentTime = when;
				m.setSource(this);
				m.setDistance(w.ships.get(owner).getLocation(w.now));
				m.target = aim;
				w.ships.get(owner).msgQueue.insert(m);
			}
			//TODO
		}
	}
	private static final double SOME_CRAZY_CONSTANT_I_HAVE_NO_NAME_FOR = 1;

	private double getFireRate() {
		return 0.1;
	}

	/**
	 * Tworzy kopię statku, pozbawioną tajnych danych typu pełna kolejka komunikatów czy manewrów
	 * @return kopia
	 */
	@Override
	public Ship clone() {
		try {
			Ship copy = (Ship) super.clone();
			copy.msgQueue = new MsgQueue();
			copy.manœuvres = new ArrayList<Manœuvre>();
			copy.manœuvres.add(manœuvres.get(0));
			if (colony != null) {
				copy.colony = colony.clone();
			}
			return copy;
		} catch (CloneNotSupportedException ex) {
			throw new RuntimeException("This shouldn\'t happen...");
		}

	}

	@Override
	public String toString() {
		return "Ship #0x" + Long.toHexString(id) + " owned by " + owner + " created t=" + creationTime;
	}

	public boolean canColonize(){
		return colony!=null && colony.planet==null;
	}
	public boolean isColony(){
		return colony!=null && colony.planet!=null;
	}

}
