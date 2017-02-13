package de.jcup.egradle.codeassist.dsl;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

public class XMLTypeSchemaGenerator {
	public static void main(String[] args) throws Exception {
		new XMLTypeSchemaGenerator().generate();
	}

	private void generate() throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance(XMLType.class);
		SchemaOutputResolver sor = new TypeSchemaOutputResolver();
		jaxbContext.generateSchema(sor);
	}

	private class TypeSchemaOutputResolver extends SchemaOutputResolver {

		@Override
		public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
			File file = new File(suggestedFileName);
			StreamResult result = new StreamResult(file);
			result.setSystemId(file.toURI().toURL().toString());
			return result;
		}

	}
}
