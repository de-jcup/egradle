package de.jcup.egradle.core.outline;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class OutlineModelImplTest {

	private OutlineModelImpl modelToTest;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void before() {
		modelToTest = new OutlineModelImpl();
	}

	@Test
	public void empty_model_returns_not_null_on_getRoot() {
		assertNotNull(modelToTest.getRoot());
	}

	@Test
	public void empty_model_returns_not_null_getRoot_getChildrenUnmodifable() {
		assertNotNull(modelToTest.getRoot().getChildren());
	}

	@Test
	public void getItemAt_works_for_exact_positions() {
		OutlineItem child1 = new OutlineItem();
		child1.setOffset(10);
		OutlineItem child2 = new OutlineItem();
		child2.setOffset(20);
		modelToTest.getRoot().add(child1);
		modelToTest.getRoot().add(child2);
		
		assertEquals(child1, modelToTest.getItemAt(10));
		assertEquals(child2, modelToTest.getItemAt(20));
	}
	
	@Test
	public void getItemAt_pos_between_child1_and_child2__returns_child1() {
		OutlineItem child1 = new OutlineItem();
		child1.setOffset(10);
		OutlineItem child2 = new OutlineItem();
		child2.setOffset(20);
		modelToTest.getRoot().add(child1);
		modelToTest.getRoot().add(child2);
		
		assertEquals(child1, modelToTest.getItemAt(15));
	}
}
