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
 package de.jcup.egradle.core.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class FormatConverterTest {

	private FormatConverter converterToTest;

	@Before
	public void before() {
		converterToTest = new FormatConverter();
	}

	@Test
	public void convert_null_to_int_results_in_zero() {
		assertEquals(0, converterToTest.convertToInt(null));
	}

	@Test
	public void convert_empty_string_to_int_results_in_zero() {
		assertEquals(0, converterToTest.convertToInt(""));
	}

	@Test
	public void convert_10000_to_int_results_in_10000() {
		assertEquals(10000, converterToTest.convertToInt("10000"));
	}

	@Test
	public void convert_null_to_double_results_in_zero() {
		assertEquals(0, converterToTest.convertToDouble(null),0.0);
	}

	@Test
	public void convert_empty_string_to_double_results_in_zero() {
		assertEquals(0, converterToTest.convertToDouble(""),0.0);
	}

	@Test
	public void convert_100point71_to_double_results_in_100point71() {
		assertEquals(100.71, converterToTest.convertToDouble("100.71"), 0.0);
	}

}
