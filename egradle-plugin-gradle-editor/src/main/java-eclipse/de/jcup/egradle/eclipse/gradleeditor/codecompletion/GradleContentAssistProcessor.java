package de.jcup.egradle.eclipse.gradleeditor.codecompletion;

import static de.jcup.egradle.eclipse.gradleeditor.preferences.GradleEditorPreferences.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;

import de.jcup.egradle.core.codecompletion.Proposal;
import de.jcup.egradle.core.codecompletion.ProposalFactory;
import de.jcup.egradle.core.codecompletion.ProposalFactoryContentProvider;
import de.jcup.egradle.core.codecompletion.RelevantCodeCutter;
import de.jcup.egradle.core.codecompletion.VariableNameProposalFactory;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Itemable;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.gradleeditor.Activator;
import de.jcup.egradle.eclipse.gradleeditor.outline.GradleEditorOutlineLabelProvider;
import de.jcup.egradle.eclipse.gradleeditor.preferences.GradleEditorPreferenceConstants;

public class GradleContentAssistProcessor implements IContentAssistProcessor {
	private static final ICompletionProposal[] NO_COMPLETION_PROPOSALS = new ICompletionProposal[0];

	private String errorMessage;

	private IAdaptable adaptable;

	private List<ProposalFactory> proposalFactories = new ArrayList<>();
	private static GradleEditorOutlineLabelProvider labelProvider =new GradleEditorOutlineLabelProvider();
	
	private RelevantCodeCutter codeCutter;
	
	public GradleContentAssistProcessor(IAdaptable adaptable, RelevantCodeCutter codeCutter) {
		if (adaptable==null){
			throw new IllegalArgumentException("adaptable may not be null!");
		}
		if (codeCutter==null){
			throw new IllegalArgumentException("codeCutter may not be null!");
		}
		this.adaptable=adaptable;
		this.codeCutter=codeCutter;
		
		proposalFactories.add(new VariableNameProposalFactory());
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		if (! isCodeCompletionEnabled()){
			errorMessage="EGradle editor code completion is disabled. Change your preferences!";
			return NO_COMPLETION_PROPOSALS;
		}
		errorMessage=null;
		IDocument document = viewer.getDocument();
		
		ProposalFactoryContentProvider contentProvider=null;
		try {
			int line = document.getLineOfOffset(offset);
			int offsetOfFirstCharacterInLine = document.getLineOffset(line);

			
			contentProvider = new ProposalFactoryContentProvider() {
				private String relevant;
				@Override
				public Model getModel() {
					return adaptable.getAdapter(Model.class);
				}

				@Override
				public String getEditorSourceEnteredAt(int cursorOffset) {
					/* the content provider is only used one time per cursor offset - so we
					 * simply cache the relevant calculation iniside internal string to
					 * speed up...
					 */
					if (relevant==null){
						String code = document.get();
						relevant = codeCutter.getRelevantCode(code, cursorOffset);
					}
					return relevant;
						
				}

				@Override
				public int getLineAt(int offset) {
					return line;
				}

				@Override
				public int getOffsetOfFirstCharacterInLine(int line) {
					return offsetOfFirstCharacterInLine;
				}
			};
		} catch (BadLocationException e) {
			return NO_COMPLETION_PROPOSALS;
		}
		
		
		Set<Proposal> allProposals = new TreeSet<>();
		for (ProposalFactory proposalFactory: proposalFactories){
			Set<Proposal> proposalsOfCurrentFactory = proposalFactory.createProposals(offset, contentProvider);
			allProposals.addAll(proposalsOfCurrentFactory);
		}
		List<ICompletionProposal> list = createEclipseProposals(offset, allProposals, contentProvider);
		return list.toArray(new ICompletionProposal[list.size()]);
	}

	private boolean isCodeCompletionEnabled() {
		return EDITOR_PREFERENCES.getBooleanPreference(GradleEditorPreferenceConstants.P_EDITOR_CODECOMPLETION_ENABLED);
	}

	private List<ICompletionProposal> createEclipseProposals(int offset, Set<Proposal> allProposals,  ProposalFactoryContentProvider contentProvider) {
		List<ICompletionProposal> list = new ArrayList<>();
		for (Proposal p: allProposals){
			Image image = null;
			if (p instanceof Itemable){
				Itemable a = (Itemable) p;
				Item item = a.getItem();
				if (item!=null){
					image = labelProvider.getImage(item);
				}
			}
			if (image==null){
				image =EGradleUtil.getImage("/icons/gradle-og.png", Activator.PLUGIN_ID); 
			}
			IContextInformation contextInformation =null;
			StringBuilder sb = new StringBuilder();
			if (p.getDescription()==null){
				sb.append("<html>");
				sb.append(p.getDescription());
				sb.append("<html>");
			}
			String additionalProposalInfo = sb.toString();
			String alreadyEntered=contentProvider.getEditorSourceEnteredAt(offset);
			int length = p.getCode().length();
			int alreadyEnteredChars = alreadyEntered.length();
			int cursorOffset=offset-alreadyEnteredChars;
			int replacementLength=alreadyEnteredChars;
			int cursorMovement=length;
			GradleCompletionProposal proposal = new GradleCompletionProposal(p.getCode(), cursorOffset, replacementLength, cursorMovement ,image,p.getName(),contextInformation,additionalProposalInfo);
			list.add(proposal);
			
		}
		return list;
		
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		/* TODO ATR, 04.01.2017: implement or remove next lines:*/
//		ContextInformation info = new ContextInformation("contextDisplayString:"+offset, "informationDisplayString");
//		return new IContextInformation[]{info};
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

}
