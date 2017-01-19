package de.jcup.egradle.codecompletion;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.core.api.ErrorHandler;
import de.jcup.egradle.core.api.FileHelper;

/**
 * Data model provider which simply reads all xml files from
 * user.home/.egradle/codeCompletion.
 * 
 * @author albert
 *
 */
public class UserHomeBasedXMLProposalDataModelProvider implements XMLProposalDataModelProvider {

	public static final UserHomeBasedXMLProposalDataModelProvider INSTANCE = new UserHomeBasedXMLProposalDataModelProvider();

	private CodeCompletionFileFilter fileFilter = new CodeCompletionFileFilter();
	private List<XMLProposalDataModel> cachedDataModels = new ArrayList<>();
	private Object monitor = new Object();
	private boolean loaded;
	private ErrorHandler errorHandler;

	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public void reload() {
		synchronized (monitor) {
			loaded = true;
			cachedDataModels.clear();

			Set<File> files = findCodeCompletionFilesInUserHome();
			if (files == null || files.size() == 0) {
				return;
			}
			JAXBContext jc;
			try {
				jc = JAXBContext.newInstance(XMLProposalDataModel.class);
				Unmarshaller unmarshaller = jc.createUnmarshaller();
				for (File file : files) {
					try {
						XMLProposalDataModel loadedModel = (XMLProposalDataModel) unmarshaller
								.unmarshal(new FileInputStream(file));
						cachedDataModels.add(loadedModel);
					} catch (IOException e) {
						if (errorHandler != null) {
							errorHandler.handleError(
									"Was not able to create xml code completion model for file:" + file, e);
						}
					}
				}
			} catch (JAXBException e) {
				if (errorHandler != null) {
					errorHandler.handleError(
							"Was not able to create JAXB context - so no xml code completion from user home base by xmls possible!",
							e);
				}
				return;
			}
		}
	}

	private Set<File> findCodeCompletionFilesInUserHome() {
		Set<File> fileSet = new HashSet<>();
		/*
		 * FIXME albert,06.01.2017: what about change this to changeable
		 * preference value instead of always using user.home?!?!?
		 */
		File defaultsSubDir = ensureDefaultsTargetDirectory();
		File customSubDir = ensureTargetDirectory("custom");
		
		File[] defaultFiles = defaultsSubDir.listFiles(fileFilter);
		if (defaultFiles==null || defaultFiles.length==0){
			installDefaultFiles(defaultsSubDir);
			 defaultFiles = defaultsSubDir.listFiles(fileFilter);
		}
		File[] customFiles = customSubDir.listFiles(fileFilter);
		addFiles(defaultFiles,fileSet);
		addFiles(customFiles,fileSet);
		return fileSet;
	}

	private File ensureDefaultsTargetDirectory() {
		return ensureTargetDirectory("defaults/"+getVersion());
	}

	private void addFiles(File[] files, Set<File> fileSet) {
		if (files==null){
			return;
		}
		for (File file: files){
			if (file==null){
				continue;
			}
			fileSet.add(file);
		}
	}
	
	public String getVersion() {
		return "1.3";
	}

	private File ensureTargetDirectory(String subDirName) {
		String userHome = System.getProperty("user.home");
		File file = new File(userHome);
		File subDir = new File(file, ".egradle/codeCompletion/"+subDirName);
		if (!subDir.exists()) {
			subDir.mkdirs();
		}
		return subDir;
	}
	
	public void restoreDefaults() {
		File dir = ensureDefaultsTargetDirectory();
		deleteOldDefaultFiles(dir);
		installDefaultFiles(dir);
		reload();
	}

	private void deleteOldDefaultFiles(File dir) {
		File[] files = dir.listFiles(fileFilter);
		for (File file: files){
			if (!file.delete()){
				if (errorHandler!=null){
					errorHandler.handleError("Cannot delete old default file:"+file);
				}
			}
		}
	}
	
	private void installDefaultFiles(File dir) {
		installNewDefaultFiles("/codecompletion/defaults/core.xml", dir, "core.xml");
	}
	
	private void installNewDefaultFiles(String resPath, File dir, String fileName) {
		FileHelper helper = getFileHelper();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resPath);
		try {
			StringBuffer sb = helper.read(new InputStreamReader(in));
			
			getFileHelper().createTextFile(dir, fileName, sb.toString());
		} catch (IOException e) {
			if (errorHandler!=null){
				errorHandler.handleError(e);
			}
		}
	}
	
	private FileHelper getFileHelper(){
		return FileHelper.DEFAULT;
	}

	private class CodeCompletionFileFilter implements FileFilter {

		@Override
		public boolean accept(File file) {
			if (file == null) {
				return false;
			}
			String name = file.getName();
			if (StringUtils.isEmpty(name)) {
				return false;
			}
			if (name.endsWith(".xml")) {
				return true;
			}
			return false;
		}

	}

	@Override
	public List<XMLProposalDataModel> getDataModels() {
		synchronized (monitor) {
			if (!loaded) {
				reload();
			}
			return cachedDataModels;
		}
	}

	

}
