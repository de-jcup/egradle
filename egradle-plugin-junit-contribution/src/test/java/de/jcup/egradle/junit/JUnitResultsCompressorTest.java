/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
 package de.jcup.egradle.junit;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class JUnitResultsCompressorTest {

	private JUnitResultsCompressor compressorToTest;

	@Before
	public void before() {
		compressorToTest = new JUnitResultsCompressor();

	}

	@Test
	public void two_testsuites_with_one_and_two_failures_result_in_testsuites_rootelement_with_3_failures() throws Exception{
		/* prepare */
		Collection<InputStream> streams = new ArrayList<>();
		String text1="<testsuite failures='1'></testsuite>";
		String text2="<testsuite failures='2'></testsuite>";
		
		streams.add(new ByteArrayInputStream(text1.getBytes()));
		streams.add(new ByteArrayInputStream(text2.getBytes()));
		
		/* execute */
		Document result = compressorToTest.compress(streams);
		
		/* test */
		assertNotNull(result);
		Element testResults = result.getDocumentElement();
		assertNotNull(testResults);
		assertEquals("3", testResults.getAttribute(JUnitResultsCompressor.ATTRIBUTE_FAILURES));
	}
	
	@Test
	public void two_testsuites_with_one_and_three_errors_result_in_testsuites_rootelement_with_4_errors() throws Exception{
		/* prepare */
		Collection<InputStream> streams = new ArrayList<>();
		String text1="<testsuite errors='1'></testsuite>";
		String text2="<testsuite errors='3'></testsuite>";
		
		streams.add(new ByteArrayInputStream(text1.getBytes()));
		streams.add(new ByteArrayInputStream(text2.getBytes()));
		
		/* execute */
		Document result = compressorToTest.compress(streams);
		
		/* test */
		assertNotNull(result);
		Element testResults = result.getDocumentElement();
		assertNotNull(testResults);
		assertEquals("4", testResults.getAttribute(JUnitResultsCompressor.ATTRIBUTE_ERRORS));
	}
	
	@Test
	public void two_testsuites_with_two_and_three_tests_result_in_testsuites_rootelement_with_5_tests() throws Exception{
		/* prepare */
		Collection<InputStream> streams = new ArrayList<>();
		String text1="<testsuite tests='2'></testsuite>";
		String text2="<testsuite tests='3'></testsuite>";
		
		streams.add(new ByteArrayInputStream(text1.getBytes()));
		streams.add(new ByteArrayInputStream(text2.getBytes()));
		
		/* execute */
		Document result = compressorToTest.compress(streams);
		
		/* test */
		assertNotNull(result);
		Element testResults = result.getDocumentElement();
		assertNotNull(testResults);
		assertEquals("5", testResults.getAttribute(JUnitResultsCompressor.ATTRIBUTE_TESTS));
	}
	
	@Test
	public void two_testsuites_with_six_seconds_and_three_point2_seconds_result_in_testsuites_rootelement_with_9point2_seconds() throws Exception{
		/* prepare */
		Collection<InputStream> streams = new ArrayList<>();
		String text1="<testsuite time='6'></testsuite>";
		String text2="<testsuite time='3.2'></testsuite>";
		
		streams.add(new ByteArrayInputStream(text1.getBytes()));
		streams.add(new ByteArrayInputStream(text2.getBytes()));
		
		/* execute */
		Document result = compressorToTest.compress(streams);
		
		/* test */
		assertNotNull(result);
		Element testResults = result.getDocumentElement();
		assertNotNull(testResults);
		assertEquals("9.2", testResults.getAttribute(JUnitResultsCompressor.ATTRIBUTE_TIME));
	}
	
	@Test
	public void two_testsuites_with_two_testcases_inside_are_in_result_document_as_well__without_pseudo_testsuite() throws Exception{
		/* prepare */
		Collection<InputStream> streams = new ArrayList<>();
		String text1="<testsuite name='suite1'><testcase name='name1'/></testsuite>";
		String text2="<testsuite name='suite2'><testcase name='name2'/><testcase name='name3'/></testsuite>";
		
		streams.add(new ByteArrayInputStream(text1.getBytes()));
		streams.add(new ByteArrayInputStream(text2.getBytes()));
		compressorToTest.setAddEGradlePseudoTestSuite(false);
		
		/* execute */
		Document result = compressorToTest.compress(streams);
		
		/* test */
		assertNotNull(result);
		NodeList testSuiteElements = result.getElementsByTagName("testsuite");
		assertEquals(2, testSuiteElements.getLength());
		
		NodeList testCaseElements = result.getElementsByTagName("testcase");
		assertEquals(3, testCaseElements.getLength());
		
		
	}
	
	@Test
	public void two_testsuites_with_two_testcases_inside_are_in_result_document_as_well_with_gradle_pseudo_testsuite() throws Exception{
		/* prepare */
		Collection<InputStream> streams = new ArrayList<>();
		String text1="<testsuite name='suite1'><testcase name='name1'/></testsuite>";
		String text2="<testsuite name='suite2'><testcase name='name2'/><testcase name='name3'/></testsuite>";
		
		streams.add(new ByteArrayInputStream(text1.getBytes()));
		streams.add(new ByteArrayInputStream(text2.getBytes()));
		
		/* execute */
		Document result = compressorToTest.compress(streams);
		
		/* test */
		assertNotNull(result);
		NodeList testSuiteElements = result.getElementsByTagName("testsuite");
		assertEquals(3, testSuiteElements.getLength());
		
		NodeList testCaseElements = result.getElementsByTagName("testcase");
		assertEquals(3, testCaseElements.getLength());
		
		
	}

}
