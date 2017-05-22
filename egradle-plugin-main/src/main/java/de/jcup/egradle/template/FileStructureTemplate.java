package de.jcup.egradle.template;

import static org.apache.commons.lang3.Validate.*;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

import de.jcup.egradle.core.util.FileUtil;
import de.jcup.egradle.core.util.Transformer;

public class FileStructureTemplate {
	
	static final String PROP_NAME = "name";
	static final String PROP_DESCRIPTION = "description";
	
	
	private Properties properties;
	private File pathToContent;

	public FileStructureTemplate(File contentRootFolder, Properties properties){
		notNull(contentRootFolder, "'pathToContent' may not be null");
		notNull(properties, "'properties' may not be null");
		
		this.pathToContent=contentRootFolder;
		this.properties=properties;
		
	}
	
	public String getName(){
		return properties.getProperty(PROP_NAME);
	}
	
	public File getPathToContent() {
		return pathToContent;
	}
	
	public String getDescription() {
		return properties.getProperty(PROP_DESCRIPTION);
	}
	
	public void applyTo(File targetFolder) throws IOException{
		/* FIXME ATR, 22.05.2017: integrate into wizard! */
		/* FIXME ATR, 22.05.2017: write more test cases! e.g. for content replacment*/
		/* FIXME ATR, 22.05.2017: implement! */
		FileUtil.copyDirectories(pathToContent, targetFolder,new FileNameTransformer());
	}
	
	private static class FileNameTransformer implements Transformer<String>{
		private static Pattern FILENAME_REPLACER_UNDERSCORE=Pattern.compile("_*");

		@Override
		public String transform(String source) {
			if (source==null){
				return "null";
			}
			source = FILENAME_REPLACER_UNDERSCORE.matcher(source).replaceAll("");
			
			return source;
		}
		
	}
}
