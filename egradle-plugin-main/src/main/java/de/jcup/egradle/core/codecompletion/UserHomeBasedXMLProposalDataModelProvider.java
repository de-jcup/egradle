package de.jcup.egradle.core.codecompletion;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.core.api.ErrorHandler;
import de.jcup.egradle.core.codecompletion.XMLProposalDataModel;
import de.jcup.egradle.core.codecompletion.XMLProposalDataModel.XMLProposalData;
import de.jcup.egradle.core.codecompletion.XMLProposalDataModel.XMLProposalElement;
import de.jcup.egradle.core.codecompletion.XMLProposalDataModel.XMLProposalValue;
import de.jcup.egradle.core.model.ItemType;
import de.jcup.egradle.core.codecompletion.XMLProposalDataModelProvider;

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

			File[] files = findCodeCompletionFilesInUserHome();
			if (files == null || files.length == 0) {
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

	private File[] findCodeCompletionFilesInUserHome() {
		/*
		 * FIXME albert,06.01.2017: what about change this to changeable
		 * preference value instead of always using user.home?!?!?
		 */
		String userHome = System.getProperty("user.home");
		File file = new File(userHome);
		File subDir = new File(file, ".egradle/codeCompletion");
		if (!subDir.exists()) {
			subDir.mkdirs();

			JAXBContext jc;
			/*
			 * FIXME albert,07.01.2017: improve this. handle version updates
			 * necessary, when egradle version updates having a new release etc.
			 * what about subfolders .egradle/codeCompletion/defaults and
			 * .egradle/codeCompletion/custom ?
			 */
			try {
				jc = JAXBContext.newInstance(XMLProposalDataModel.class);
				javax.xml.bind.Marshaller marshaller = jc.createMarshaller();
				marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

				File defaultFile = new File(subDir, "default.xml");
				FileOutputStream bos = new FileOutputStream(defaultFile);
				XMLProposalDataModel defaultModel = createNewDefaultModel();
				marshaller.marshal(defaultModel, bos);

			} catch (Exception e) {
				if (errorHandler != null) {
					errorHandler.handleError("Was not able to create default xml code completion file!", e);
				}
			}
			return new File[0];
		}

		File[] files = subDir.listFiles(fileFilter);
		return files;
	}
	
	/* FIXME albert,07.01.2017:provide restore default mechanism which is able to setup all default files to origin state */

	private XMLProposalDataModel createNewDefaultModel() {
		/* FIXME albert,07.01.2017: remove this code later, instead use a default xml file provided by plugin installation! */
		XMLProposalDataModel model = new XMLProposalDataModel();
		model.id = "default";
		
		List<XMLProposalData> props = model.getProposals();
		XMLProposalData data = new XMLProposalData();
		List<XMLProposalElement> rootElements = data.getElements();
		XMLProposalElement allProjects = new XMLProposalElement();
		/* FIXME albert,07.01.2017: handle html or support markup inside descriptions - xml does escape... or at least the pretty formatter here?!?!? */
		allProjects.description="Configuration closure for <b>all projects</b>";
		allProjects.name="allProjects";
		allProjects.code="allProjects{ $cursor }";
		rootElements.add(allProjects);
		
		List<XMLProposalValue> rootValues = data.getValues();
		XMLProposalValue applyFromValue = new XMLProposalValue();
		applyFromValue.name="apply from:";
		applyFromValue.code="apply from: '$cursor'";
		rootValues.add(applyFromValue);
		
		
		props.add(data);
		return model;
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
