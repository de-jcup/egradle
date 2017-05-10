/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.egradle.integration;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.junit.rules.ExternalResource;

import de.jcup.egradle.codeassist.CodeCompletionRegistry;
import de.jcup.egradle.codeassist.GradleDSLProposalFactory;
import de.jcup.egradle.codeassist.ProposalFactoryContentProvider;
import de.jcup.egradle.codeassist.ProposalFactoryContentProviderException;
import de.jcup.egradle.codeassist.RelevantCodeCutter;
import de.jcup.egradle.codeassist.StaticOffsetProposalFactoryContentProvider;
import de.jcup.egradle.codeassist.dsl.ApiMappingImporter;
import de.jcup.egradle.codeassist.dsl.FilesystemFileLoader;
import de.jcup.egradle.codeassist.dsl.XMLPluginsImporter;
import de.jcup.egradle.codeassist.dsl.XMLTypeImporter;
import de.jcup.egradle.codeassist.dsl.gradle.GradleDSLCodeTemplateBuilder;
import de.jcup.egradle.codeassist.dsl.gradle.GradleDSLPluginLoader;
import de.jcup.egradle.codeassist.dsl.gradle.GradleDSLTypeProvider;
import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.codeassist.dsl.gradle.estimation.GradleLanguageElementEstimater;
import de.jcup.egradle.codeassist.hover.HoverSupport;
import de.jcup.egradle.core.ModelProvider;
import de.jcup.egradle.core.TestUtil;
import de.jcup.egradle.core.TextProvider;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.core.model.ModelBuilder.ModelBuilderException;
import de.jcup.egradle.core.model.groovyantlr.GradleModelBuilder;
import de.jcup.egradle.core.util.ErrorHandler;

/**
 * IntegrationTestComponents is the central point for integration tests
 * 
 * @author Albert Tregnaghi
 *
 */
public class IntegrationTestComponents extends ExternalResource {

	private static IntegrationTestComponents INSTANCE = new IntegrationTestComponents();
	private CodeCompletionRegistry codeCompletionRegistry;
	private ErrorHandler errorHandler;
	private GradleDSLTypeProvider gradleDslProvider;
	private HoverSupport hoverSupport;
	private RelevantCodeCutter relevantCodeCutter;
	private GradleLanguageElementEstimater estimator;
	private GradleDSLCodeTemplateBuilder gradleDslCodeBuilder;
	private GradleDSLProposalFactory gradleDSLProposalFactory;
	private XMLPluginsImporter pluginsImporter;
	private FilesystemFileLoader fileLoader;
	private static boolean showFullStacktraces;
	static {
		String property = System.getProperty("egradle.integration.test.stacktrace");
		showFullStacktraces = Boolean.parseBoolean(property);

	}

	private IntegrationTestComponents() {
		startSDKParts();
	}

	public static IntegrationTestComponents initialize() {
		return INSTANCE;
	}

	public RelevantCodeCutter getRelevantCodeCutter() {
		return relevantCodeCutter;
	}

	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	public HoverSupport getHoverSupport() {
		return hoverSupport;
	}

	public GradleDSLTypeProvider getGradleDslProvider() {
		return gradleDslProvider;
	}

	private void startSDKParts() {

		relevantCodeCutter = new RelevantCodeCutter();
		hoverSupport = new HoverSupport();
		errorHandler = new ErrorHandler() {

			@Override
			public void handleError(String message) {
				System.err.println(message);

			}

			@Override
			public void handleError(String message, Throwable t) {
				if (showFullStacktraces) {
					handleError(message);
					t.printStackTrace();
					System.err.println(message + " - " + t.getMessage());
				}
			}

		};
		codeCompletionRegistry = new CodeCompletionRegistry();
		XMLTypeImporter typeImporter = new XMLTypeImporter();
		pluginsImporter = new XMLPluginsImporter();
		ApiMappingImporter apiMappingImporter = new ApiMappingImporter();
		fileLoader = new FilesystemFileLoader(typeImporter, pluginsImporter, apiMappingImporter);
		fileLoader.setDSLFolder(new File(TestUtil.SDK__SRC_MAIN_RES_FOLDER, "sdk"));
		gradleDslProvider = new GradleDSLTypeProvider(fileLoader);
		gradleDslProvider.setErrorHandler(errorHandler);

		GradleDSLPluginLoader pluginLoader = new GradleDSLPluginLoader(fileLoader);

		/*
		 * install dsl type provider as service, so it must be definitely used
		 * shared...
		 */
		codeCompletionRegistry.registerService(GradleDSLTypeProvider.class, gradleDslProvider);
		codeCompletionRegistry.registerService(GradleDSLPluginLoader.class, pluginLoader);

		estimator = new GradleLanguageElementEstimater(gradleDslProvider);
		gradleDslCodeBuilder = new GradleDSLCodeTemplateBuilder();
		gradleDSLProposalFactory = new GradleDSLProposalFactory(gradleDslCodeBuilder, estimator);

		codeCompletionRegistry.init();

	}

	public XMLPluginsImporter getPluginsImporter() {
		return pluginsImporter;
	}

	public FilesystemFileLoader getFileLoader() {
		return fileLoader;
	}

	public GradleDSLProposalFactory getGradleDSLProposalFactory() {
		return gradleDSLProposalFactory;
	}

	public GradleDSLCodeTemplateBuilder getGradleDslCodeBuilder() {
		return gradleDslCodeBuilder;
	}

	public Model buildModel(String text) {
		InputStream is = new ByteArrayInputStream(text.getBytes());
		GradleModelBuilder builder = new GradleModelBuilder(is);
		try {
			return builder.build(null);
		} catch (ModelBuilderException e) {
			throw new IllegalStateException("Cannot build test model:\nReason:" + e.getMessage() + "\nText=" + text, e);
		}
	}

	public GradleLanguageElementEstimater getEstimator() {
		return estimator;
	}

	public String loadTestFile(String path) {
		File file = new File(TestUtil.SRC_TEST_RES_FOLDER, path);
		if (!file.exists()) {
			throw new IllegalStateException("Testfile does not exist:" + file.getAbsolutePath());
		}
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line = "";
			boolean firstLine = true;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				if (!firstLine) {
					sb.append("\n");
				}
				firstLine = false;
				sb.append(line);
			}
			return sb.toString();
		} catch (IOException e) {
			throw new IllegalStateException("Testfile reading failed:" + file.getAbsolutePath(), e);
		}
	}

	public ProposalFactoryContentProvider buildContentProvider(String text, int offset) {
		Model model = buildModel(text);

		ModelProvider modelProvider = new ModelProvider() {

			@Override
			public Model getModel() {
				return model;
			}
		};
		TextProvider textProvider = new IntegrationTestTextProvider(text);
		try {
			StaticOffsetProposalFactoryContentProvider provider = new StaticOffsetProposalFactoryContentProvider(
					GradleFileType.GRADLE_BUILD_SCRIPT, modelProvider, textProvider, relevantCodeCutter, offset);
			return provider;
		} catch (ProposalFactoryContentProviderException e) {
			throw new IllegalStateException("Should not happen", e);
		}
	}
}
