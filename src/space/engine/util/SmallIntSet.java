/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.engine.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author karol
 */
public class SmallIntSet implements Set<Integer>{

	public static final int MAX_SIZE=256;
	private boolean pry(long container, int bitNo){
		return (container&(1l<<bitNo))!=0;
	}
	private long d0,d1,d2,d3,d4,d5,d6,d7;
	public boolean contains(int i){
		if(i<0||i>=MAX_SIZE){
			return false;
		}
		int j=i%64;
		switch(i/64){
			case 0: return pry(d0,j);
			case 1: return pry(d1,j);
			case 2: return pry(d2,j);
			case 3: return pry(d3,j);
			case 4: return pry(d4,j);
			case 5: return pry(d5,j);
			case 6: return pry(d6,j);
			case 7: return pry(d7,j);
		}
		return false;
	}
	private long jam(long where, int bitNo, boolean value){
		return value
				?(where|(1l<<bitNo))
				:(where&~(1l<<bitNo));
	}
	private long quickhash(){
		return d0^d1^d2^d3^d4^d5^d6^d7;
	}
	private void innerAdd(int i){
		int j=i%64;
		switch(i/64){
			case 0: d0=jam(d0,j,true); break;
			case 1: d1=jam(d1,j,true); break;
			case 2: d2=jam(d2,j,true); break;
			case 3: d3=jam(d3,j,true); break;
			case 4: d4=jam(d4,j,true); break;
			case 5: d5=jam(d5,j,true); break;
			case 6: d6=jam(d6,j,true); break;
			case 7: d7=jam(d7,j,true); break;
		}
	}
	public boolean add(int i){
		long old=quickhash();
		if(i<0||i>=256){
			throw new IllegalArgumentException(i+" is not from range [0.."+(MAX_SIZE-1)+"]");
		}
		innerAdd(i);
		return old!=quickhash();
	}

	private void innerRemove(int i){
		int j=i%64;
		switch(i/64){
			case 0: d0=jam(d0,j,false); break;
			case 1: d1=jam(d1,j,false); break;
			case 2: d2=jam(d2,j,false); break;
			case 3: d3=jam(d3,j,false); break;
			case 4: d4=jam(d4,j,false); break;
			case 5: d5=jam(d5,j,false); break;
			case 6: d6=jam(d6,j,false); break;
			case 7: d7=jam(d7,j,false); break;
		}
	}
	public boolean remove(int i){
		long old=quickhash();
		if(i<0||i>=256){
			throw new IllegalArgumentException(i+" is not from range [0.."+(MAX_SIZE-1)+"]");
		}
		innerRemove(i);
		return old!=quickhash();
	}

	private int bitcount(long l){
		long x=l;
		final long m1  = 0x5555555555555555l; //binary: 0101...
		final long m2  = 0x3333333333333333l; //binary: 00110011..
		final long m4  = 0x0f0f0f0f0f0f0f0fl; //binary:  4 zeros,  4 ones ...
		final long m8  = 0x00ff00ff00ff00ffl; //binary:  8 zeros,  8 ones ...
		final long m16 = 0x0000ffff0000ffffl; //binary: 16 zeros, 16 ones ...
		final long m32 = 0x00000000ffffffffl; //binary: 32 zeros, 32 ones
		x = (x & m1 ) + ((x >>>  1) & m1 ); //put count of each  2 bits into those  2 bits
		x = (x & m2 ) + ((x >>>  2) & m2 ); //put count of each  4 bits into those  4 bits
		x = (x & m4 ) + ((x >>>  4) & m4 ); //put count of each  8 bits into those  8 bits
		x = (x & m8 ) + ((x >>>  8) & m8 ); //put count of each 16 bits into those 16 bits
		x = (x & m16) + ((x >>> 16) & m16); //put count of each 32 bits into those 32 bits
		x = (x & m32) + ((x >>> 32) & m32); //put count of each 64 bits into those 64 bits
		return (int)x;
	}
	@Override
	public int size() {
		return bitcount(d0)
				+ bitcount(d1)
				+ bitcount(d2)
				+ bitcount(d3)
				+ bitcount(d4)
				+ bitcount(d5)
				+ bitcount(d6)
				+ bitcount(d7);
	}

	@Override
	public boolean isEmpty() {
		return
				(d0==0)&&
				(d1==0)&&
				(d2==0)&&
				(d3==0)&&
				(d4==0)&&
				(d5==0)&&
				(d6==0)&&
				(d7==0);
	}

	@Override
	public boolean contains(Object o) {
		if(o instanceof Long || o instanceof Integer || o instanceof Short || o instanceof Byte){
			Number n=(Number)o;
			return contains(n.intValue());
		}
		return false;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>(){
			int next=0;
			int lastReturned=-1;
			{
				findNext();
			}
			private void findNext(){
				next++;
				while(next<MAX_SIZE && !SmallIntSet.this.contains(next)){
					next++;
				}
			}
			@Override
			public boolean hasNext() {
				return next<MAX_SIZE;
			}

			@Override
			public Integer next() {
				lastReturned=next;
				findNext();
				return lastReturned;
			}

			@Override
			public void remove() {
				if(lastReturned<0){
					throw new IllegalStateException();
				}
				SmallIntSet.this.remove(lastReturned);
				lastReturned=-1;
			}

		};
	}

	@Override
	public Object[] toArray() {
		Integer[] res=new Integer[size()];
		int j=0;
		for(int i=0; i<MAX_SIZE; i++){
			if(contains(i)){
				res[j]=i;
				j++;
			}
		}
		return res;
	}

	@Override
	public <T> T[] toArray(T[] ts) {
		T[] res=(T[])Array.newInstance(ts.getClass(), size());
		int j=0;
		for(int i=0; i<MAX_SIZE; i++){
			if(contains(i)){
				res[j]=(T)Integer.valueOf(i);
				j++;
			}
		}
		return res;
	}

	@Override
	public boolean add(Integer e) {
		return add(e.intValue());
	}

	@Override
	public boolean remove(Object o) {
		long old=quickhash();
		if(o instanceof Long || o instanceof Integer || o instanceof Short || o instanceof Byte){
			Number n=(Number)o;
			return remove(n.intValue());
		}
		return old!=quickhash();
	}

	@Override
	public boolean containsAll(Collection<?> clctn) {
		for(Object o: clctn){
			if(!contains(o)){
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends Integer> clctn) {
		long old=quickhash();
		for(Integer i: clctn){
			innerAdd(i.intValue());
		}
		return old!=quickhash();
	}

	@Override
	public boolean retainAll(Collection<?> clctn) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean removeAll(Collection<?> clctn) {
		long old=quickhash();
		for(Object o: clctn){
			if(o instanceof Long || o instanceof Integer || o instanceof Short || o instanceof Byte){
				Number n=(Number)o;
				innerRemove(n.intValue());
			}
		}
		return old!=quickhash();
	}

	@Override
	public void clear() {
		d0=0;
		d1=0;
		d2=0;
		d3=0;
		d4=0;
		d5=0;
		d6=0;
		d7=0;
	}
}
