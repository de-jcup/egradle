package de.jcup.egradle.template;

import static org.apache.commons.lang3.Validate.*;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.core.util.DirectoryCopySupport;
import de.jcup.egradle.core.util.FileSupport;

public class FileStructureTemplate {

	static final String PROP_NAME = "name";
	static final String PROP_DESCRIPTION = "description";

	private File pathToContent;
	DirectoryCopySupport copySupport;
	private String name;
	private String description;
	FileSupport fileSupport;
	TemplateContentTransformerFactory contentTransformerFactory;

	public FileStructureTemplate(String name, File contentRootFolder, String description) {
		notNull(contentRootFolder, "'pathToContent' may not be null");
		if (name == null) {
			name = contentRootFolder.getName();
		}
		if (description == null) {
			description = "No description available";
		}
		this.pathToContent = contentRootFolder;
		this.copySupport = new DirectoryCopySupport();
		this.fileSupport = new FileSupport();
		this.name = name;
		this.description = description;
		
		/* own internal factory so easier to test */
		this.contentTransformerFactory=new TemplateContentTransformerFactory(); 
	}

	public String getName() {
		return name;
	}

	public File getPathToContent() {
		return pathToContent;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Applies content of template to given target folder. The target folder
	 * name itself is NOT replaced by variables etc. <br>
	 * <br>
	 * But sub directories and file names will be replaced in following way:
	 * <ul>
	 * <li>_xy will be replaced to xy</li>
	 * <li>$my.property will be tried to be replaced by property key</li>
	 * </ul>
	 * 
	 * @param targetFolder
	 * @param properties
	 *            - properties containing data for replacement
	 * @throws IOException
	 */
	public void applyTo(File targetFolder, Properties properties) throws IOException {
		if (targetFolder==null){
			throw new IllegalArgumentException("target folder may not be null!");
		}
		if (! targetFolder.isDirectory()){
			throw new IOException("Given file is no folder:"+targetFolder);
		}
		if (properties == null) {
			properties = new Properties();
		}
		
		copyFiles(targetFolder, properties);

		transformFiles(targetFolder, properties);

	}
	/* FIXME ATR, 22.05.2017: integrate into wizard! */

	private void copyFiles(File targetFolder, Properties properties) throws IOException {
		TemplateFileNameTransformer targetFileNameTransformer = new TemplateFileNameTransformer(properties);
		copySupport.copyDirectories(pathToContent, targetFolder, targetFileNameTransformer, true);
	}

	private void transformFiles(File targetFolder, Properties properties) throws IOException {
		TemplateContentTransformer templateContentTransformer = contentTransformerFactory.createTemplateContentTransformer(properties);

		if (templateContentTransformer==null){
			throw new IllegalStateException("Factory wrong implemented, returns null for transformer:"+contentTransformerFactory.getClass());
		}
		
		transformContentRecursive(targetFolder, templateContentTransformer);
	}

	private void transformContentRecursive(File file, TemplateContentTransformer transformer) throws IOException {
		if (file.isFile()) {
				String source = fileSupport.readTextFile(file);
				String transformed = transformer.transform(source);
				if (StringUtils.equals(source,transformed)){
					// ignore - same content as before
					return;
				}
				fileSupport.writeTextFile(file, transformed);
		} else if (file.isDirectory()) {
			for (File subFile : file.listFiles()) {
				transformContentRecursive(subFile, transformer);
			}
		}

	}

	static final FileFilter ONLY_DIRECTORIES = new FileFilter() {

		@Override
		public boolean accept(File file) {
			if (file == null) {
				return false;
			}
			return file.isDirectory();
		}
	};
	
	static class TemplateContentTransformerFactory {

		public TemplateContentTransformer createTemplateContentTransformer(Properties properties){
			return new TemplateContentTransformer(properties);
		}
	}


}
