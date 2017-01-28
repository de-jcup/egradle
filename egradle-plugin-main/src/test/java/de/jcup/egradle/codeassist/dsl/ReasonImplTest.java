package de.jcup.egradle.codeassist.dsl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.codeassist.dsl.Plugin;
import de.jcup.egradle.codeassist.dsl.ReasonImpl;

import static org.mockito.Mockito.*;
public class ReasonImplTest {

	private Plugin plugin1;
	private Plugin plugin2;

	@Before
	public void before() {
		plugin1 = mock(Plugin.class);
		plugin2 = mock(Plugin.class);
	}
	
	@Test
	public void two_empty_reasons_are_equal() {
		ReasonImpl impl1 = new ReasonImpl();
		ReasonImpl impl2 = new ReasonImpl();
		
		assertTrue(impl2.equals(impl1));
	}
	@Test
	public void one_reason_with_plugin_another_one_without_are_NOT_equal() {
		ReasonImpl impl1 = new ReasonImpl();
		impl1.setPlugin(plugin1);
		ReasonImpl impl2 = new ReasonImpl();
		
		assertFalse(impl1.equals(impl2));
		assertFalse(impl2.equals(impl1));
	}
	
	@Test
	public void one_reason_with_plugin1_another_one_with_plugin2_are_NOT_equal() {
		ReasonImpl impl1 = new ReasonImpl();
		impl1.setPlugin(plugin1);
		ReasonImpl impl2 = new ReasonImpl();
		impl2.setPlugin(plugin2);
		
		assertFalse(impl1.equals(impl2));
		assertFalse(impl2.equals(impl1));
	}
	
	@Test
	public void one_reason_with_plugin1_another_one_with_plugin1_are_equal() {
		ReasonImpl impl1 = new ReasonImpl();
		impl1.setPlugin(plugin1);
		ReasonImpl impl2 = new ReasonImpl();
		impl2.setPlugin(plugin1);
		
		assertTrue(impl1.equals(impl2));
		assertTrue(impl2.equals(impl1));
	}

}
