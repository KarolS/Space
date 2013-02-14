/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.engine;

import java.util.Arrays;

/**
 *
 * @author karol
 */
public class ResourceStatus implements Cloneable {
	private double time;
	private double[] stockpiles=new double[Resource.NOOF_RESOURCES];
	private double[] income=new double[Resource.NOOF_RESOURCES];
	private double[] parameters=new double[Parameter.NOOF_PARAMETERS];
	private boolean upToDate;

	public boolean isUpToDate(){
		return upToDate;
	}
	@Override
	public ResourceStatus clone(){
		upToDate=true;
		ResourceStatus r=new ResourceStatus();
		r.time=time;
		r.income=Arrays.copyOf(income, income.length);
		r.parameters=Arrays.copyOf(parameters, parameters.length);
		r.stockpiles=Arrays.copyOf(stockpiles, stockpiles.length);
		r.upToDate=true;
		return r;
	}
	public ResourceStatus getForecast(double now){
		ResourceStatus r=new ResourceStatus();
		r.time=now;
		r.income=Arrays.copyOf(income, income.length);
		for(int i=0; i<income.length; i++){
			r.stockpiles[i]+=(now-time)*income[i];
		}
		r.parameters=Arrays.copyOf(parameters, parameters.length);
		r.stockpiles=Arrays.copyOf(stockpiles, stockpiles.length);
		r.upToDate=true;
		return r;
	}
	public void update(double now){
		for(int i=0; i<income.length; i++){
			stockpiles[i]+=(now-time)*income[i];
		}
		time=now;
		upToDate=false; //TAK, TAK MA BYĆ!
	}
	public double getTime(){
		return time;
	}
	public double getStockpile(int i){
		return stockpiles[i];
	}
	public double getIncome(int i){
		return income[i];
	}
	public void setIncome(int i, double value){
		income[i]=value;
		upToDate=false;
	}
	public void touch(){
		upToDate=false;
	}
	public double getParameter(int i){
		return parameters[i];
	}
	public void setParameter(int i, double value){
		parameters[i]=value;
		upToDate=false;
	}
}
