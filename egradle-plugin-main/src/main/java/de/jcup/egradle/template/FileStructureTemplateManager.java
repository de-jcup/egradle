package de.jcup.egradle.template;

import static de.jcup.egradle.template.FileStructureTemplate.*;
import static org.apache.commons.lang3.Validate.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import de.jcup.egradle.core.RootFolderProvider;
import de.jcup.egradle.core.util.LogAdapter;

public class FileStructureTemplateManager {

	private boolean useCache;
	private List<FileStructureTemplate> fileStructureTemplates;
	private RootFolderProvider provider;
	private LogAdapter logAdapter;

	public FileStructureTemplateManager(RootFolderProvider provider, LogAdapter logAdapter) {
		notNull(provider, "'provider' may not be null");
		notNull(logAdapter, "'logAdapter' may not be null");

		fileStructureTemplates = new ArrayList<>();
		this.provider = provider;
		this.logAdapter = logAdapter;
	}

	public List<FileStructureTemplate> getTemplates() {
		if (useCache) {
			return fileStructureTemplates;
		}
		try {
			loadFileStructureTemplates();
		} catch (IOException e) {
			logAdapter.logError("Was not able to load templates", e);
		}

		useCache = true;
		return fileStructureTemplates;
	}

	private void loadFileStructureTemplates() throws IOException {
		fileStructureTemplates.clear();
		File rootFolder = provider.getRootFolder();
		if (rootFolder==null){
			return;
		}
		/* inside root folder the template folders exist */
		File[] templateFolders = rootFolder.listFiles(FileStructureTemplate.ONLY_DIRECTORIES);
		if (templateFolders == null) {
			return;
		}
		for (File templateFolder : templateFolders) {
			if (templateFolder == null) {
				continue;
			}
			addTemplateFolder(templateFolder);
		}
	}

	private void addTemplateFolder(File templateFolder) {
		Properties p = getSafeProperties(templateFolder);
		FileStructureTemplate template = new FileStructureTemplate(p.getProperty(PROP_NAME),templateFolder,p.getProperty(PROP_DESCRIPTION));
		for (Feature f: Features.values()){
			String value = p.getProperty(f.getId());
			if (Boolean.valueOf(value)){
				template.enableFeature(f);
			}
			
		}
		
		fileStructureTemplates.add(template);
	}

	
	private Properties getSafeProperties(File templateFolder){
		 Properties p = new Properties();
		 tryToLoadProperties(p, templateFolder);
		 return p;

	}
	
	private Properties tryToLoadProperties( Properties p, File templateFolder) {
		
		 File templatePropertyFile = new File(templateFolder,"template.properties");
		 if (! templatePropertyFile.exists()){
			 return p;
		 }
		 try (InputStream is = new FileInputStream(templatePropertyFile)){
			 p.load(is);
		 } catch (RuntimeException | IOException e) {
			logAdapter.logError("Was not able to load template properties for:"+templateFolder, e);
		}
		return p;
	}

}
