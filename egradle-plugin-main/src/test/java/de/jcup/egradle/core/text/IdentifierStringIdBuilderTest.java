package de.jcup.egradle.core.text;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.text.DocumentIdentifier.IdentifierStringIdBuilder;

public class IdentifierStringIdBuilderTest {

	private IdentifierStringIdBuilder builderToTest;

	@Before
	public void before(){
		builderToTest = DocumentIdentifier.createStringIdBuilder();
	}
	
	@Test
	public void test_hallo_welt_created_as_array() {
		/* execute */
		String[] created = builderToTest.add("hallo").add("welt").build();
		
		/* test */
		assertArrayEquals(new String[]{"hallo","welt"}, created);
	}
	
	private enum DocumentTestidentifiers1 implements DocumentIdentifier{
		ALPHA,
		
		BETA,
		;

		@Override
		public String getId() {
			return name();
		}
		
	}
	
	private enum DocumentTestidentifiers2 implements DocumentIdentifier{
		GAMMA,
		
		DELTA,
		;

		@Override
		public String getId() {
			return name();
		}
		
	}
	
	@Test
	public void test_document_identifier1() {
		/* execute */
		String[] created = builderToTest.addAll(DocumentTestidentifiers1.values()).build();
		
		/* test */
		assertArrayEquals(new String[]{"ALPHA","BETA"}, created);
	}
	
	@Test
	public void test_document_identifier1_and_2() {
		/* execute */
		String[] created = builderToTest.addAll(DocumentTestidentifiers1.values()).addAll(DocumentTestidentifiers2.values()).build();
		
		/* test */
		assertArrayEquals(new String[]{"ALPHA","BETA","GAMMA","DELTA"}, created);
	}
	
	@Test
	public void test_default_and_document_identifier1_and_2() {
		/* execute */
		String[] created = builderToTest.add("default").addAll(DocumentTestidentifiers1.values()).addAll(DocumentTestidentifiers2.values()).build();
		
		/* test */
		assertArrayEquals(new String[]{"default","ALPHA","BETA","GAMMA","DELTA"}, created);
	}

}
