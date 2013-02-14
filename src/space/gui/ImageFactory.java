/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package space.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import space.engine.Ship;
import vytah.math.Matrix3x3;
import vytah.math.Vector3D;

/**
 *
 * @author karol
 */
public class ImageFactory {

	public static final int SHIP_FRAMES = 8;
	static BufferedImage[] shipVisible = new BufferedImage[SHIP_FRAMES];
	static BufferedImage[] shipNow = new BufferedImage[SHIP_FRAMES];
	static BufferedImage[] shipFuture = new BufferedImage[SHIP_FRAMES];
	static BufferedImage[] colonyShipNow = new BufferedImage[SHIP_FRAMES];
	static BufferedImage[] colonyShipVisible = new BufferedImage[SHIP_FRAMES];
	static BufferedImage[] colonyShipFuture = new BufferedImage[SHIP_FRAMES];
	static BufferedImage[] mothershipNow = new BufferedImage[SHIP_FRAMES];
	static BufferedImage[] mothershipVisible = new BufferedImage[SHIP_FRAMES];
	static BufferedImage[] mothershipFuture = new BufferedImage[SHIP_FRAMES];

	static BufferedImage empty;
	public static final int TYPE_FUTURE=0;
	public static final int TYPE_NOW=1;
	public static final int TYPE_VISIBLE=2;
	public static BufferedImage getImage(Ship ship, Matrix3x3 rotation, double time, int type){
		Vector3D v=rotation.mul(ship.getVelocity(time));
		double angle=Math.atan2(v.y(), v.x());
		int normalizedAngle=10+((int) Math.round(angle/(Math.PI/4)));
		normalizedAngle%=8;
		if(ship.isColony()){
			return empty;
		}
		if(ship.canColonize()){
			switch(type){
				case TYPE_FUTURE: return colonyShipFuture[normalizedAngle];
				case TYPE_NOW: return colonyShipNow[normalizedAngle];
				case TYPE_VISIBLE: return colonyShipVisible[normalizedAngle];
			}
		}
		if(ship.id==ship.owner){
			switch(type){
				case TYPE_FUTURE: return mothershipFuture[normalizedAngle];
				case TYPE_NOW: return mothershipNow[normalizedAngle];
				case TYPE_VISIBLE: return mothershipVisible[normalizedAngle];
			}
		}
		switch(type){
				case TYPE_FUTURE: return shipFuture[normalizedAngle];
				case TYPE_NOW: return shipNow[normalizedAngle];
				case TYPE_VISIBLE: return shipVisible[normalizedAngle];
			}
		return empty;
	}
	static {
		try {
			empty=new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			for (int i = 0;i < 8; i++) {
				shipVisible[i] = ImageIO.read(ImageFactory.class.getResource("/space/img/ship-" + i * 45 + ".png"));
				shipNow[i] = ImageIO.read(ImageFactory.class.getResource("/space/img/shipn-" + i * 45 + ".png"));
				shipFuture[i] = ImageIO.read(ImageFactory.class.getResource("/space/img/shipf-" + i * 45 + ".png"));

				colonyShipNow[i] = ImageIO.read(ImageFactory.class.getResource("/space/img/colonyn-" + i * 45 + ".png"));
				colonyShipVisible[i] = ImageIO.read(ImageFactory.class.getResource("/space/img/colony-" + i * 45 + ".png"));
				colonyShipFuture[i] = ImageIO.read(ImageFactory.class.getResource("/space/img/colonyf-" + i * 45 + ".png"));

				mothershipNow[i] = ImageIO.read(ImageFactory.class.getResource("/space/img/mothern-" + i * 45 + ".png"));
				mothershipFuture[i] = ImageIO.read(ImageFactory.class.getResource("/space/img/motherf-" + i * 45 + ".png"));
				mothershipVisible[i] = ImageIO.read(ImageFactory.class.getResource("/space/img/mother-" + i * 45 + ".png"));
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
}
