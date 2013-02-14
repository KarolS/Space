/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.engine;

/**
 *
 * @author karol
 */
public class Planet {
	public Star parent;
	public String name;
	private int innerId;
	public long globalId(){
		return ((long)innerId)<<32 + parent.getId();
	}

	public boolean isColonizable(int shipId) {
		return true;///TODO
	}
}
