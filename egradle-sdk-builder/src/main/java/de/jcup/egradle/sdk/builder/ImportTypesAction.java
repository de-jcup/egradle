package de.jcup.egradle.sdk.builder;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.XMLType;

public class ImportTypesAction implements SDKBuilderAction {

	@Override
	public void execute(SDKBuilderContext context) throws IOException {
		
		TypeImportFilter filter = new TypeImportFilter();
		scanTypes(context.sourceParentDirectory, filter, context);
	
		if (context.allTypes.isEmpty()){
			throw new IllegalStateException("no types found!");
		}
		
		/* now types are scanned, so start importing all types */
		for (String typeName: context.allTypes.keySet()){
			/* simply call the provider, it will resolve the type*/
			Type buildtype = context.originGradleFilesProvider.getType(typeName);
			if (buildtype==null){
				throw new IllegalArgumentException("Cannot build type:"+typeName);
			}
		}
	}
	
	private void scanTypes(File file, FileFilter filter, SDKBuilderContext context) throws IOException{
		if (file.isDirectory()){
			File[] listFiles = file.listFiles(filter);
			for (File childFile: listFiles){
				scanTypes(childFile, filter,context);
			}
		}else if (file.isFile()){
			try(InputStream stream=new FileInputStream(file)){
				XMLType tempType = context.typeImporter.importType(stream);
				String typeName = tempType.getName();
				context.allTypes.put(typeName, file);
			}
		}
	}
	
	private class TypeImportFilter implements FileFilter {

		@Override
		public boolean accept(File file) {
			if (file == null) {
				return false;
			}
			if (file.isDirectory()) {
				return true;
			}
			String name = file.getName();
			if (name.equals("plugins.xml")){
				return false;
			}
			return name.endsWith(".xml");
		}
	};

}
