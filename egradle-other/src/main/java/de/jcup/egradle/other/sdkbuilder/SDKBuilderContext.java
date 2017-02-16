package de.jcup.egradle.other.sdkbuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.sdk.internal.XMLSDKInfo;
import de.jcup.egradle.sdk.internal.XMLSDKInfoExporter;

public class SDKBuilderContext {
	File sdkInfoFile;
	XMLSDKInfo sdkInfo = new XMLSDKInfo();
	File sourceParentDirectory;
	File targetPathDirectory;
	Map<String, Type> tasks = new TreeMap<>();
	int methodWithOutDescriptionCount;
	int methodAllCount;

	Set<String> allTypes = new TreeSet<>();

	public String getInfo() {
		double missingDescriptionPercent = 0;
		if (methodWithOutDescriptionCount != 0 && methodAllCount != 0) {
			double onePercent = methodAllCount / 100;
			missingDescriptionPercent = methodWithOutDescriptionCount / onePercent;
		}
		return "Methods all:" + methodAllCount + " - missing descriptions:" + methodWithOutDescriptionCount + " ="
				+ missingDescriptionPercent + "%";
	}
	
	public File createTargetFile(File targetRootDirectory) {
		return new File(targetRootDirectory, "sdk/");
	}
	
	public void writeSDKInfo() throws IOException{
		
		try(FileOutputStream stream = new FileOutputStream(sdkInfoFile)){

			XMLSDKInfoExporter exporter = new XMLSDKInfoExporter();
			exporter.exportSDKInfo(sdkInfo, stream);
			System.out.println("- written sdk info file:"+sdkInfoFile);
		}
	}
}