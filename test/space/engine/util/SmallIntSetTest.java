/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.engine.util;

import java.util.Collection;
import java.util.Iterator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author karol
 */
public class SmallIntSetTest {

    public SmallIntSetTest() {
    }

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Test
	public void testContains() {
		SmallIntSet set=new SmallIntSet();

		set.add(1);
		set.add(100);
		set.add(230);
		set.add(4);
		assertFalse(set.contains(2));
		assertFalse(set.contains(null));
		assertFalse(set.contains(5));
		assertFalse(set.contains(null));
		assertFalse(set.contains(222));
		assertFalse(set.contains(-8));
		assertFalse(set.contains(34535));
		assertFalse(set.contains(33));
		assertFalse(set.contains(65));
		assertFalse(set.contains(267));
		assertFalse(set.contains(345));
		assertFalse(set.contains(63));
		assertFalse(set.contains(22));
		assertFalse(set.contains(0));
		assertTrue(set.contains(1));
		assertTrue(set.contains(4));
		assertTrue(set.contains(100));
		assertTrue(set.contains(230));
	}

	@Test
	public void testRemove_int() {
	}

	@Test
	public void testSize() {
		SmallIntSet set=new SmallIntSet();
		set.add(1);
		assertEquals(1,set.size());
		set.add(100);
		assertEquals(2,set.size());
		set.add(230);
		assertEquals(3,set.size());
		set.add(4);
		assertEquals(4,set.size());
		
	}

	@Test
	public void testIsEmpty() {
		SmallIntSet set=new SmallIntSet();
		assertTrue(set.isEmpty());
		set.add(1);
		assertFalse(set.isEmpty());
	}

	@Test
	public void testContains_Object() {
		
	}

	@Test
	public void testIterator() {
		SmallIntSet set=new SmallIntSet();
		Iterator<Integer> i=set.iterator();
		assertFalse(i.hasNext());
	}
	@Test
	public void testIterator1() {
		SmallIntSet set=new SmallIntSet();
		set.add(1);
		Iterator<Integer> i=set.iterator();
		assertTrue(i.hasNext());
		assertEquals(1, (int)i.next());
		assertFalse(i.hasNext());
	}

	@Test
	public void testToArray_0args() {
		
	}

	@Test
	public void testToArray_GenericType() {
		
	}

	@Test
	public void testAdd_Integer() {
		
	}

	@Test
	public void testRemove_Object() {
		
	}

	@Test
	public void testContainsAll() {
		
	}

	@Test
	public void testAddAll() {
		
	}

	@Test
	public void testRetainAll() {
		
	}

	@Test
	public void testRemoveAll() {
		
	}

	@Test
	public void testClear() {
		SmallIntSet set=new SmallIntSet();
		set.add(1);
		set.add(2);
		set.clear();
		assertTrue(set.isEmpty());
	}

}