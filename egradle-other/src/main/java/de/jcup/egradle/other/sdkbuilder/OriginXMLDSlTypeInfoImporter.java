package de.jcup.egradle.other.sdkbuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.jcup.egradle.codeassist.dsl.XMLDSLMethodInfo;
import de.jcup.egradle.codeassist.dsl.XMLDSLPropertyInfo;
import de.jcup.egradle.codeassist.dsl.XMLDSLTypeDocumentation;

public class OriginXMLDSlTypeInfoImporter {

	/* Example:org.gradle.api.artifacts.ComponentSelection.xml content:
	 *  @formatter:off
		<section>
	    <section>
	        <title>Properties</title>
	        <table>
	            <thead>
	                <tr>
	                    <td>Name</td>
	                </tr>
	                <tr>
	                    <td>requested</td>
	                </tr>
	                <tr>
	                    <td>candidate</td>
	                </tr>
	            </thead>
	        </table>
	    </section>
	    <section>
	        <title>Methods</title>
	        <table>
	            <thead>
	                <tr>
	                    <td>Name</td>
	                </tr>
	            </thead>
	            <tr>
	                <td>reject</td>
	            </tr>
	        </table>
	    </section>
		</section>
 		@formatter:off*/
	public XMLDSLTypeDocumentation collectDSL(File file) throws IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		File lastParsedFile = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();

				lastParsedFile = file;
				try (FileInputStream fis = new FileInputStream(file)) {

					XMLDSLTypeDocumentation target = new XMLDSLTypeDocumentation();
					
					Document document = builder.parse(fis);

					NodeList section1s = document.getElementsByTagName("section");
					for (int i = 0; i < section1s.getLength(); i++) {
						Element section1 = (Element) section1s.item(i);
						NodeList section2s = section1.getElementsByTagName("section");
						for (int j = 0; j < section2s.getLength(); j++) {
							Element section2 = (Element)section2s.item(j);
							if (section2!=null){
								scanSecton2(section2,target);
							}else{
								System.err.println("- no section2 found in"+file);
							}
						}
					}
					return target;
				}
		} catch (SAXException e) {
			throw new IOException("Parsing failed - last parsed file:" + lastParsedFile);
		} catch (ParserConfigurationException e) {
			throw new IOException("Parsing failed because parser wrong configured");
		}
	}

	private void scanSecton2(Element element, XMLDSLTypeDocumentation target) {
		
		NodeList titles = element.getElementsByTagName("title");
		Element titleElement = (Element)titles.item(0);
		if (titleElement==null){
			System.err.println("no title found!!!");
		}
		String title = titleElement.getTextContent();
		title = title.toLowerCase();
		if ("properties".equals(title)){
			NodeList tdElements = element.getElementsByTagName("td");
			for (int i = 0; i < tdElements.getLength(); i++) {
				Element tdElement = (Element) tdElements.item(i);
				String textContent = tdElement.getTextContent();
				if (StringUtils.isBlank(textContent)){
					continue;
				}
				if (textContent.startsWith("Default")){
					continue;
				}
				if (textContent.equals("Name")){
					continue;
				}
				XMLDSLPropertyInfo propInfo= new XMLDSLPropertyInfo();
				propInfo.setName(textContent);
				target.getProperties().add(propInfo);
			}
		}else if ("methods".equals(title)){
			NodeList tdElements = element.getElementsByTagName("td");
			for (int i = 0; i < tdElements.getLength(); i++) {
				Element tdElement = (Element) tdElements.item(i);
				XMLDSLMethodInfo methodInfo= new XMLDSLMethodInfo();
				String textContent = tdElement.getTextContent();
				if (StringUtils.isBlank(textContent)){
					continue;
				}
				if (textContent.startsWith("Default")){
					continue;
				}
				if (textContent.equals("Name")){
					continue;
				}
				methodInfo.setName(textContent);
				target.getMethods().add(methodInfo);
			}
		}
		
	}

}
