package ch.bbv.control;

import java.util.List;

import static org.junit.Assert.*;
import org.junit.Test;

import ch.bbv.bo.Condiment;

import com.google.common.collect.Lists;


public class CondimentConverterTest {
	
	@Test
	public void fromString_stringNotNull_newCondiment(){
		List<Condiment> data = Lists.newArrayList();
 		CondimentConverter testee = new CondimentConverter(data);
 		Condiment result = testee.fromString("Test");
 		assertNotNull("A Condiment must be created", result);
 		assertTrue("Condiment must be added to the List", data.contains(result));
 		assertEquals("Name expected", "Test", result.getName());
	}
	
	@Test
	public void toString_condimentNull_null(){
		List<Condiment> data = Lists.newArrayList();
 		CondimentConverter testee = new CondimentConverter(data);
 		String result = testee.toString(null);
 		assertNull("null must result in a null String", result);
	}
	
	@Test
	public void toString_condimentNotNull_name(){
		List<Condiment> data = Lists.newArrayList();
		CondimentConverter testee = new CondimentConverter(data);
		String result = testee.toString(new Condiment("Test"));
		assertEquals("Name expected", "Test", result);
	}

}
