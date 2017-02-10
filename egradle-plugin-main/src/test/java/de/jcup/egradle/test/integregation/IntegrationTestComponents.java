package de.jcup.egradle.test.integregation;

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
import de.jcup.egradle.codeassist.dsl.XMLDSLPluginsImporter;
import de.jcup.egradle.codeassist.dsl.XMLDSLTypeImporter;
import de.jcup.egradle.codeassist.dsl.gradle.GradleDSLCodeTemplateBuilder;
import de.jcup.egradle.codeassist.dsl.gradle.GradleDSLTypeProvider;
import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.codeassist.dsl.gradle.GradleLanguageElementEstimater;
import de.jcup.egradle.codeassist.hover.HoverSupport;
import de.jcup.egradle.core.ModelProvider;
import de.jcup.egradle.core.TestUtil;
import de.jcup.egradle.core.TextProvider;
import de.jcup.egradle.core.api.ErrorHandler;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.core.model.ModelBuilder.ModelBuilderException;
import de.jcup.egradle.core.model.groovyantlr.GradleModelBuilder;

/**
 * IntegrationTestComponents is the central point for integration tests
 * @author Albert Tregnaghi
 *
 */
public class IntegrationTestComponents extends ExternalResource{
	
	private static IntegrationTestComponents INSTANCE = new IntegrationTestComponents();
	private CodeCompletionRegistry codeCompletionRegistry;
	private ErrorHandler errorHandler;
	private GradleDSLTypeProvider gradleDslProvider;
	private HoverSupport hoverSupport;
	private RelevantCodeCutter relevantCodeCutter;
	private GradleLanguageElementEstimater estimator;
	private GradleDSLCodeTemplateBuilder gradleDslCodeBuilder;
	private GradleDSLProposalFactory gradleDSLProposalFactory;

	private IntegrationTestComponents() {
		startSDKParts();
	}
	
	public static IntegrationTestComponents initialize(){
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
			public void handleError(Throwable t) {
				t.printStackTrace();

			}

			@Override
			public void handleError(String message) {
				System.err.println(message);

			}

			@Override
			public void handleError(String message, Throwable t) {
				handleError(message);
				handleError(t);
			}

		};
		codeCompletionRegistry = new CodeCompletionRegistry();
		XMLDSLTypeImporter typeImporter = new XMLDSLTypeImporter();
		XMLDSLPluginsImporter pluginsImporter = new XMLDSLPluginsImporter();
		ApiMappingImporter apiMappingImporter = new ApiMappingImporter();
		FilesystemFileLoader loader = new FilesystemFileLoader(typeImporter, pluginsImporter, apiMappingImporter);
		loader.setDSLFolder(new File(TestUtil.SRC_MAIN_RES_FOLDER,"sdk/gradle/3.0")); // FIXME ATR, 07.02.2017: make this changeable!
		gradleDslProvider = new GradleDSLTypeProvider(loader);
		gradleDslProvider.setErrorHandler(errorHandler);
		/*
		 * install dsl type provider as service, so it must be definitely used
		 * shared...
		 */
		codeCompletionRegistry.registerService(GradleDSLTypeProvider.class, gradleDslProvider);

		estimator=new GradleLanguageElementEstimater(gradleDslProvider);
		gradleDslCodeBuilder = new GradleDSLCodeTemplateBuilder();
		gradleDSLProposalFactory = new GradleDSLProposalFactory(gradleDslCodeBuilder,estimator);
		
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
			throw new IllegalStateException("Cannot build test model:\nReason:"+e.getMessage()+"\nText="+text,e);
		}
	}

	public GradleLanguageElementEstimater getEstimator() {
		return estimator;
	}

	public String loadTestFile(String path) {
		File file = new File(TestUtil.SRC_TEST_RES_FOLDER,path);
		if (!file.exists()){
			throw new IllegalStateException("Testfile does not exist:"+file.getAbsolutePath());
		}
		try(BufferedReader br = new BufferedReader(new FileReader(file))){
			String line = "";
			boolean firstLine = true;
			StringBuilder sb = new StringBuilder();
			while ((line=br.readLine())!=null){
				if (!firstLine){
					sb.append("\n");
				}
				firstLine=false;
				sb.append(line);
			}
			return sb.toString();
		}catch(IOException e){
			throw new IllegalStateException("Testfile reading failed:"+file.getAbsolutePath(),e);
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
			StaticOffsetProposalFactoryContentProvider provider = new StaticOffsetProposalFactoryContentProvider(GradleFileType.GRADLE_BUILD_SCRIPT, modelProvider, textProvider, relevantCodeCutter, offset);
			return provider;
		} catch (ProposalFactoryContentProviderException e) {
			throw new IllegalStateException("Should not happen",e);
		}
	}
}
