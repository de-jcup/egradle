package de.jcup.egradle.sdk.builder.action.type;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.XMLType;
import de.jcup.egradle.sdk.builder.SDKBuilderContext;
import de.jcup.egradle.sdk.builder.action.SDKBuilderAction;

public class SaveTypesToSDKTargetFolder implements SDKBuilderAction {

	@Override
	public void execute(SDKBuilderContext context) throws IOException {
		if (context.originTypeNameToOriginFileMapping.isEmpty()){
			throw new IllegalArgumentException("all types empty!!??!");
		}
		for (String typeName : context.originTypeNameToOriginFileMapping.keySet()) {
			Type type=context.originGradleFilesProvider.getType(typeName);
			File originFile = context.originTypeNameToOriginFileMapping.get(typeName);
			
			String subPath = extractSubPathFromFile(originFile, context.sourceParentDirectory);
			File file = new File(context.targetPathDirectory,subPath);
			file.getParentFile().mkdirs();
			try(FileOutputStream fos = new FileOutputStream(file)){
				context.typeExporter.exportType((XMLType) type, fos);
			}
		}
		System.out.println("exported types to:"+context.targetPathDirectory);

	}

	String extractSubPathFromFile(File file, File parent) throws IOException{
		StringBuilder sb = new StringBuilder();
		File current = file;
		boolean parentFound=false;
		
		while(current!=null){
			if (current.equals(parent)){
				parentFound=true;
				break;
			}
			sb.insert(0, current.getName());
			sb.insert(0, '/');
			current=current.getParentFile();
		}
		if (!parentFound){
			throw new IllegalArgumentException("Parent:"+parent+"\ndoes not contain\nFile:"+file);
		}
		String result = sb.toString();
		if (result.startsWith("/")){
			result = StringUtils.substring(result, 1);
		}
		return result;
	}
}
