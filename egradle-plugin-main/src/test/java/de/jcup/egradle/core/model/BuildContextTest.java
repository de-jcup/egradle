package de.jcup.egradle.core.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.model.BuildContext;
import de.jcup.egradle.core.model.Error;

public class BuildContextTest {

	private BuildContext contextToTest;
	
	@Before
	public void before(){
		contextToTest = new BuildContext();
	}
	
	@Test
	public void test_nothing_added__has_errors_returns_false() {
		assertFalse(contextToTest.hasErrors());
	}

	@Test
	public void test_one_error_added__has_errors_returns_true() {
		contextToTest.add(new Error());
		assertTrue(contextToTest.hasErrors());
	}
}
