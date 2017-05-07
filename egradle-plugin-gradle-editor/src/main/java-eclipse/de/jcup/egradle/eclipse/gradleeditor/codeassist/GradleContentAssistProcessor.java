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
 package de.jcup.egradle.eclipse.gradleeditor.codeassist;

import static de.jcup.egradle.eclipse.gradleeditor.EditorUtil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionListenerExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;

import de.jcup.egradle.codeassist.CodeCompletionRegistry;
import de.jcup.egradle.codeassist.FilterableProposalFactory;
import de.jcup.egradle.codeassist.GradleDSLProposalFactory;
import de.jcup.egradle.codeassist.GradleDSLProposalFactory.ModelProposal;
import de.jcup.egradle.codeassist.GradleDSLProposalFactory.TemplateProposal;
import de.jcup.egradle.codeassist.Proposal;
import de.jcup.egradle.codeassist.ProposalFactory;
import de.jcup.egradle.codeassist.ProposalFactoryContentProvider;
import de.jcup.egradle.codeassist.ProposalFactoryContentProviderException;
import de.jcup.egradle.codeassist.RelevantCodeCutter;
import de.jcup.egradle.codeassist.StaticOffsetProposalFactoryContentProvider;
import de.jcup.egradle.codeassist.UserInputProposalFilter;
import de.jcup.egradle.codeassist.VariableNameProposalFactory;
import de.jcup.egradle.codeassist.dsl.HTMLDescriptionBuilder;
import de.jcup.egradle.codeassist.dsl.LanguageElement;
import de.jcup.egradle.codeassist.dsl.gradle.GradleDSLCodeTemplateBuilder;
import de.jcup.egradle.codeassist.dsl.gradle.GradleDSLTypeProvider;
import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.codeassist.dsl.gradle.estimation.GradleLanguageElementEstimater;
import de.jcup.egradle.core.ModelProvider;
import de.jcup.egradle.core.TextProvider;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Itemable;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.eclipse.api.DocumentTextProvider;
import de.jcup.egradle.eclipse.api.EclipseDevelopmentSettings;
import de.jcup.egradle.eclipse.api.EclipseUtil;
import de.jcup.egradle.eclipse.gradleeditor.EditorActivator;
import de.jcup.egradle.eclipse.gradleeditor.GradleEditor;
import de.jcup.egradle.eclipse.gradleeditor.outline.GradleEditorOutlineLabelProvider;
import de.jcup.egradle.eclipse.gradleeditor.preferences.GradleEditorSyntaxColorPreferenceConstants;

public class GradleContentAssistProcessor implements IContentAssistProcessor, ModelProvider {
	private static final ICompletionProposal[] NO_COMPLETION_PROPOSALS = new ICompletionProposal[0];

	private String errorMessage;

	/**
	 * Caching is only done while code assist sessions is alive!
	 */
	private boolean useCacheBecauseCodeAssistSessionOngoing;

	private IAdaptable adaptable;

	private List<ProposalFactory> proposalFactories;
	private static GradleEditorOutlineLabelProvider labelProvider = new GradleEditorOutlineLabelProvider();

	private RelevantCodeCutter codeCutter;

	private TreeSet<Proposal> cachedProposals;

	private UserInputProposalFilter filter = new UserInputProposalFilter();

	private ICompletionListener completionListener;

	private GradleLanguageElementEstimater estimator;


	private HTMLDescriptionBuilder descriptionBuilder;

	public GradleContentAssistProcessor(IAdaptable adaptable, RelevantCodeCutter codeCutter) {
		if (adaptable == null) {
			throw new IllegalArgumentException("adaptable may not be null!");
		}
		if (codeCutter == null) {
			throw new IllegalArgumentException("codeCutter may not be null!");
		}
		this.adaptable = adaptable;
		this.codeCutter = codeCutter;
		this.proposalFactories = new ArrayList<>();
		this.cachedProposals = new TreeSet<>();
		this.completionListener = new CacheValidListener();
		
		this.descriptionBuilder=new HTMLDescriptionBuilder();
		addFactories();
	}

	private void addFactories() {
		CodeCompletionRegistry codeCompletionRegistry = EditorActivator.getDefault().getCodeCompletionRegistry();
		GradleDSLTypeProvider typeProvider = codeCompletionRegistry.getService(GradleDSLTypeProvider.class);

		estimator = new GradleLanguageElementEstimater(typeProvider);
		proposalFactories.add(new GradleDSLProposalFactory(new GradleDSLCodeTemplateBuilder(), estimator));
		proposalFactories.add(new VariableNameProposalFactory());
	}

	public GradleLanguageElementEstimater getEstimator() {
		return estimator;
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		if (!isCodeCompletionEnabled()) {
			errorMessage = "EGradle editor code completion is disabled. Change your preferences!";
			return NO_COMPLETION_PROPOSALS;
		}
		// waitForOutlineModelRefreshed();
		errorMessage = null;

		IDocument document = viewer.getDocument();
		if (document==null){
			return NO_COMPLETION_PROPOSALS;
		}
		ProposalFactoryContentProvider contentProvider = null;
		try {
			GradleFileType fileType = adaptable.getAdapter(GradleFileType.class);
			TextProvider textProvider = new DocumentTextProvider(document);
			ModelProvider modelProvider = GradleContentAssistProcessor.this;
			contentProvider = new StaticOffsetProposalFactoryContentProvider(fileType, modelProvider, textProvider, codeCutter, offset);

		} catch (ProposalFactoryContentProviderException e) {
			return NO_COMPLETION_PROPOSALS;
		}

		if (DEBUG) {
			debugCacheState("proposal computing-1");
		}
		if (!useCacheBecauseCodeAssistSessionOngoing) {
			if (DEBUG) {
				debugCacheState("proposal computing-2");
			}
			cachedProposals.clear();
			boolean filterGetterAndSetter = getPreferences().isCodeAssistNoProposalsForGetterOrSetter();
			for (ProposalFactory proposalFactory : proposalFactories) {
				if (proposalFactory instanceof FilterableProposalFactory){
					FilterableProposalFactory fpropFactory = (FilterableProposalFactory) proposalFactory;
					fpropFactory.setFilterGetterAndSetter(filterGetterAndSetter);
				}
				Set<Proposal> proposalsOfCurrentFactory = proposalFactory.createProposals(offset, contentProvider);
				cachedProposals.addAll(proposalsOfCurrentFactory);
			}
			useCacheBecauseCodeAssistSessionOngoing = true;
			if (DEBUG) {
				debugCacheState("proposal computing-3");
			}
		}
		if (DEBUG) {
			debugCacheState("proposal computing-4");
		}
		Set<Proposal> filteredProposals = filter.filter(cachedProposals, contentProvider);
		List<ICompletionProposal> list = createEclipseProposals(offset, filteredProposals, contentProvider);
		return list.toArray(new ICompletionProposal[list.size()]);
	}

	public Model getModel() {
		return adaptable.getAdapter(Model.class);
	}

	private boolean isCodeCompletionEnabled() {
		return getPreferences().isCodeAssistProposalsEnabled();
	}

	private List<ICompletionProposal> createEclipseProposals(int offset, Set<Proposal> allProposals,
			ProposalFactoryContentProvider contentProvider) {
		
		GradleEditor editor = adaptable.getAdapter(GradleEditor.class);
		
		String bgColor = null;
		String fgColor = null;
		if (editor != null) {
			bgColor = editor.getBackGroundColorAsWeb();
			fgColor = editor.getForeGroundColorAsWeb();
		}
		String commentColorWeb = getPreferences().getWebColor(GradleEditorSyntaxColorPreferenceConstants.COLOR_COMMENT);
		
		List<ICompletionProposal> list = new ArrayList<>();
		for (Proposal p : allProposals) {
			Image image = null;
			if (p instanceof Itemable) {
				Itemable a = (Itemable) p;
				Item item = a.getItem();
				if (item != null) {
					image = labelProvider.getImage(item);
				}
			} else if (p instanceof ModelProposal) {
				ModelProposal mp = (ModelProposal) p;
				if (mp.isMethod()) {
					image = EclipseUtil.getImage("/icons/codecompletion/public_co.png", EditorActivator.PLUGIN_ID);
				} else if (mp.isProperty()) {
					image = EclipseUtil.getImage("/icons/codecompletion/hierarchicalLayout.png", EditorActivator.PLUGIN_ID);
				}
			} else if (p instanceof TemplateProposal) {
				image = EclipseUtil.getImage("/icons/codecompletion/source.png", EditorActivator.PLUGIN_ID);
			}
			if (image == null) {
				image = EclipseUtil.getImage("/icons/gradle-og.png", EditorActivator.PLUGIN_ID);
			}
			IContextInformation contextInformation = null;
			String alreadyEntered = contentProvider.getEditorSourceEnteredAtCursorPosition();

			int alreadyEnteredChars = alreadyEntered.length();
			int cursorOffset = offset - alreadyEnteredChars;

			int replacementLength = alreadyEnteredChars;
			String additionalProposalInfo = null;
			LazyLanguageElementHTMLDescriptionBuilder lazyBuilder = null;
			if (p instanceof ModelProposal) {
				ModelProposal mp = (ModelProposal) p;
				LanguageElement element = mp.getElement();
				if (element != null) {
					lazyBuilder = new LazyLanguageElementHTMLDescriptionBuilder(fgColor, bgColor, commentColorWeb, element,mp, descriptionBuilder);
				}
			}else if (p instanceof TemplateProposal){
				/* do nothing - description currently not supported*/
			}
			/* create eclipse completion proposal */
			GradleCompletionProposal proposal = new GradleCompletionProposal(p,cursorOffset,
					replacementLength, image, contextInformation, additionalProposalInfo, lazyBuilder);
			list.add(proposal);

		}
		return list;

	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	public ICompletionListener getCompletionListener() {
		return completionListener;
	}

	private static boolean DEBUG = EclipseDevelopmentSettings.DEBUG_ADD_SPECIAL_LOGGING;

	/**
	 * As long as the code assistent session is alive we do not bother about
	 * outline model changes at all, we do not rebuild proposals by factory.
	 * Filtering is done always.
	 * 
	 * @author Albert Tregnaghi
	 *
	 */
	private class CacheValidListener implements ICompletionListener, ICompletionListenerExtension2 {

		@Override
		public void assistSessionStarted(ContentAssistEvent event) {
			useCacheBecauseCodeAssistSessionOngoing = false;
			if (DEBUG) {
				debugCacheState("assistSessionStarted");
			}

		}

		@Override
		public void assistSessionEnded(ContentAssistEvent event) {
			useCacheBecauseCodeAssistSessionOngoing = false;
			if (DEBUG) {
				debugCacheState("assistSessionEnded");
			}
		}

		@Override
		public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {

		}

		@Override
		public void applied(ICompletionProposal proposal) {
			if (DEBUG) {
				debugCacheState("applied proposal-1");
			}
			/*
			 * after apply the model must be changed and cursor position
			 * normally changes as well! This often made problems
			 */
			IEditorPart activeEditor = EclipseUtil.getActiveEditor();
			if (activeEditor instanceof GradleEditor) {
				GradleEditor ge = (GradleEditor) activeEditor;
				if (DEBUG) {
					debugCacheState("applied proposal-2");
				}
				ge.rebuildOutline();
				if (DEBUG) {
					debugCacheState("applied proposal-3");
				}
			}

		}

	}

	private void debugCacheState(String message) {
		EclipseUtil.logInfo(getClass().getSimpleName() + ":" + message + ", useCacheBecauseCodeAssistSessionOngoing="
				+ useCacheBecauseCodeAssistSessionOngoing);
	}
}
