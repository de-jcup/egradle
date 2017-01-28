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

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.jcup.egradle.core.api.FormatConverter;

/**
 * Compresses gradle junit result documents into one single result document -
 * necessary for single imports in eclipse junti view
 * 
 * <pre>
 <?xml version="1.0" encoding="UTF-8"?>
<testsuites disabled="" errors="" failures="" name="" tests="" time="">
    <testsuite disabled="" errors="" failures="" hostname="" id=""
        name="" package="" skipped="" tests="" time="" timestamp="">
        <properties>
            <property name="" value=""/>
            <property name="" value=""/>
        </properties>
        <testcase assertions="" classname="" name="" status="" time="">
            <skipped/>
            <error message="" type=""/>
            <error message="" type=""/>
            <failure message="" type=""/>
            <failure message="" type=""/>
            <system-out/>
            <system-out/>
            <system-err/>
            <system-err/>
        </testcase>
        <testcase assertions="" classname="" name="" status="" time="">
            <skipped/>
            <error message="" type=""/>
            <error message="" type=""/>
            <failure message="" type=""/>
            <failure message="" type=""/>
            <system-out/>
            <system-out/>
            <system-err/>
            <system-err/>
        </testcase>
        <system-out/>
        <system-err/>
    </testsuite>
    <testsuite disabled="" errors="" failures="" hostname="" id=""
        name="" package="" skipped="" tests="" time="" timestamp="">
        <properties>
            <property name="" value=""/>
            <property name="" value=""/>
        </properties>
        <testcase assertions="" classname="" name="" status="" time="">
            <skipped/>
            <error message="" type=""/>
            <error message="" type=""/>
            <failure message="" type=""/>
            <failure message="" type=""/>
            <system-out/>
            <system-out/>
            <system-err/>
            <system-err/>
        </testcase>
        <testcase assertions="" classname="" name="" status="" time="">
            <skipped/>
            <error message="" type=""/>
            <error message="" type=""/>
            <failure message="" type=""/>
            <failure message="" type=""/>
            <system-out/>
            <system-out/>
            <system-err/>
            <system-err/>
        </testcase>
        <system-out/>
        <system-err/>
    </testsuite>
</testsuites>
 * </pre>
 * 
 * @author Albert Tregnaghi
 *
 */
public class JUnitResultsCompressor {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
	private static final String TESTSUITE = "testsuite";
	static final String ATTRIBUTE_TIME = "time";
	static final String ATTRIBUTE_ERRORS = "errors";
	static final String ATTRIBUTE_FAILURES = "failures";
	static final String ATTRIBUTE_TESTS = "tests";
	static final String ATTRIBUTE_DISABLED = "tests";
	FormatConverter converter = new FormatConverter();

	private boolean removeConsoleOutput = true;
	private boolean addEGradlePseudoTestSuite = true;

	public void setRemoveConsoleOutput(boolean removeConsoleOutput) {
		this.removeConsoleOutput = removeConsoleOutput;
	}

	/**
	 * When <code>true</code> EGradle does always add a pseudo testuid with
	 * EGradle information, because the Junit Test Viewer of eclipse show this
	 * always as headline. So its clear that this test results cannot be rerun
	 * by Junit-Plugin but only by EGradle again. When <code>false</code> no additional info is added
	 * 
	 * @param addEGradlePseudoTestSuite
	 */
	public void setAddEGradlePseudoTestSuite(boolean addEGradlePseudoTestSuite) {
		this.addEGradlePseudoTestSuite = addEGradlePseudoTestSuite;
	}

	public Document compress(Collection<InputStream> streams)
			throws IOException, ParserConfigurationException, SAXException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		return compress(streams, dBuilder);
	}

	public Document compress(Collection<InputStream> streams, DocumentBuilder dBuilder)
			throws IOException, ParserConfigurationException, SAXException {
		Document rDocument = dBuilder.newDocument();
		Element testSuites = rDocument.createElement("testsuites");
		rDocument.appendChild(testSuites);

		int errors = 0;
		int failures = 0;
		int tests = 0;
		double time = 0;

		if (addEGradlePseudoTestSuite){
			appendEGradleInformation(rDocument, testSuites);
		}

		for (InputStream stream : streams) {
			Document doc = dBuilder.parse(stream);
			Element testSuite = doc.getDocumentElement();
			String tagName = testSuite.getTagName();
			if (!tagName.equals(TESTSUITE)) {
				throw new IllegalStateException(
						"root type of document not testsuite but " + tagName + ", doc=" + doc.getLocalName());
			}
			errors += converter.convertToInt(testSuite.getAttribute(ATTRIBUTE_ERRORS));
			failures += converter.convertToInt(testSuite.getAttribute(ATTRIBUTE_FAILURES));
			tests += converter.convertToInt(testSuite.getAttribute(ATTRIBUTE_TESTS));
			time += converter.convertToDouble(testSuite.getAttribute(ATTRIBUTE_TIME));

			if (removeConsoleOutput) {
				removeContentOfElement(doc, "system-out");
				removeContentOfElement(doc, "system-err");
			}
			rDocument.adoptNode(testSuite);
			testSuites.appendChild(testSuite);

		}
		testSuites.setAttribute(ATTRIBUTE_ERRORS, "" + errors);
		testSuites.setAttribute(ATTRIBUTE_FAILURES, "" + failures);
		testSuites.setAttribute(ATTRIBUTE_TESTS, "" + tests);
		testSuites.setAttribute(ATTRIBUTE_TIME, "" + time);
		return rDocument;
	}

	private void appendEGradleInformation(Document doc, Element testSuites) {
		Date date = new Date();
		String dateStr = DATE_FORMAT.format(date);
		Element egradlePseudoTestSuite = doc.createElement(TESTSUITE);
		egradlePseudoTestSuite.setAttribute("name", "EGradle imported following JUnit test results on "+dateStr+" :");
		egradlePseudoTestSuite.setAttribute("disabled", "true");
		testSuites.appendChild(egradlePseudoTestSuite);
	}

	private void removeContentOfElement(Document doc, String consoleElement) {
		NodeList nList = doc.getElementsByTagName(consoleElement);
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node item = nList.item(temp);
			item.setTextContent("");
		}
	}

}
