package de.jcup.egradle.sdk.builder.action.mapping;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import de.jcup.egradle.sdk.builder.SDKBuilderContext;
import de.jcup.egradle.sdk.builder.action.SDKBuilderAction;

public class CreateAlternativeMappingFileAction implements SDKBuilderAction {

	@Override
	public void execute(SDKBuilderContext context) throws IOException {
		if (context.alternativeApiMapping.isEmpty()){
			throw new IllegalStateException("alternative api mapping is empty - another action must fill this before!");
		}
		System.out.println("- generate alternative api mapping file");
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String shortName : context.alternativeApiMapping.keySet()) {
			if (first) {
				first = false;
			} else {
				sb.append("\n");
			}
			sb.append(shortName);
			sb.append(':');
			sb.append(context.alternativeApiMapping.get(shortName));
			sb.append(';');
		}
		File alternativeApiMappingFile = context.alternativeAPiMappingFile;
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(alternativeApiMappingFile))) {
			bw.write(sb.toString());
		}

	}

}
