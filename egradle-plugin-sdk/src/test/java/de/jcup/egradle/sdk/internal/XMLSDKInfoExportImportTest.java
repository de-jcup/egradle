package de.jcup.egradle.sdk.internal;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class XMLSDKInfoExportImportTest {

	private XMLSDKInfoExporter exporter;
	private XMLSDKInfoImporter importer;

	@Before
	public void before(){
		exporter= new XMLSDKInfoExporter();
		importer = new XMLSDKInfoImporter();
	}

	@Test
	public void test_export_import_same_content() throws IOException {
		/* prepare */
		Date creationDate = new Date(1000);
		Date installDate = new Date(2000);
		String gradleVersion = "a.b";
		String sdkVersion="x.y.z";
		
		XMLSDKInfo exportedInfo = new XMLSDKInfo();
		exportedInfo.setCreationDate(creationDate);
		exportedInfo.setInstallationDate(installDate);
		exportedInfo.setGradleVersion(gradleVersion);
		exportedInfo.setSdkVersion(sdkVersion);
		
		File testFile = File.createTempFile(getClass().getSimpleName(), "importExport.xml");

		XMLSDKInfo importedInfo = null;

		/* execute */
		try(FileOutputStream stream = new FileOutputStream(testFile)){
			exporter.exportSDKInfo(exportedInfo, stream);
		}
		try(FileInputStream stream = new FileInputStream(testFile)){
			importedInfo = importer.importSDKInfo(stream);
		}
		
		/* test */
		assertEquals(creationDate, importedInfo.getCreationDate());
		assertEquals(installDate, importedInfo.getInstallationDate());
		assertEquals(gradleVersion, importedInfo.getGradleVersion());
		assertEquals(sdkVersion, importedInfo.getSdkVersion());
		
	}

}
