/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, VersionData 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
 package de.jcup.egradle.codeassist.dsl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
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
