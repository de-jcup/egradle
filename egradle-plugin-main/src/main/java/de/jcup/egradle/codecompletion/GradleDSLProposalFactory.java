package de.jcup.egradle.codecompletion;

import java.util.Set;

import de.jcup.egradle.codecompletion.dsl.CodeBuilder;
import de.jcup.egradle.codecompletion.dsl.LanguageElement;
import de.jcup.egradle.codecompletion.dsl.Method;
import de.jcup.egradle.codecompletion.dsl.Property;
import de.jcup.egradle.codecompletion.dsl.Type;
import de.jcup.egradle.codecompletion.dsl.gradle.GradleFileType;
import de.jcup.egradle.codecompletion.dsl.gradle.GradleLanguageElementEstimater;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;

public class GradleDSLProposalFactory extends AbstractModelProposalFactory {

	private GradleLanguageElementEstimater typeEstimator;
	/*
	 * FIXME ATR, 19.01.2017:check memory foot print of these factories and think about shared instances
	 */

	/**
	 * Creates new gradle dsl proposal factory
	 * 
	 * @param codeBuilder
	 * @param typeEstimator 
	 */
	public GradleDSLProposalFactory(CodeBuilder codeBuilder,
			GradleLanguageElementEstimater typeEstimator) {
		super(codeBuilder);
		if (typeEstimator == null) {
			throw new IllegalArgumentException("typeEstimator may not be null!");
		}
		this.typeEstimator = typeEstimator;
	}

	@Override
	public Set<Proposal> createProposalsImpl(int offset, ProposalFactoryContentProvider contentProvider) {
		if (contentProvider == null) {
			return null;
		}
		Type identifiedType = tryToIdentifyParentType(offset, contentProvider);
		if (identifiedType == null) {
			return null;
		}
		String textBeforeColumn = contentProvider.getLineTextBeforeCursorPosition()+1;
		Set<Proposal> proposals = super.createProposals(identifiedType,textBeforeColumn);
		return proposals;
	}

	/* FIXME ATR, 20.01.2017:  think about providing multiple types here as result - its often not clear what can be estimated...*/
	private Type tryToIdentifyParentType(int offset, ProposalFactoryContentProvider contentProvider) {
		Model model = contentProvider.getModel();
		if (model == null) {
			return null;
		}
		Item outlineItem = model.getParentItemOf(offset);
		if (outlineItem == null) {
			return null;
		}
		GradleFileType fileType = contentProvider.getFileType();
		LanguageElement elementForItem = typeEstimator.estimate(outlineItem,fileType);
		if (elementForItem instanceof Type){
			Type type = (Type) elementForItem;
			return type;
		}
		if (elementForItem instanceof Method){
			/* FIXME ATR, 20.01.2017:  HIGH PRIO:problem: when method parameter is a closure the target type is only available form documentation - arg!! */
			// something like {@link ObjectConfigurationAction} first occuring seems to be the target where the closure is executed!
			Method m = (Method) elementForItem;
//			List<Parameter> parameters = m.getParameters();
//			for (Parameter p: parameters){
//				Type paramType = p.getType();
//				/* ignore string parameters */
//				if (!isString(paramType)){
//					return p.getType();
//				}
//			}
			/* fall back to return type if no param */
			return m.getReturnType();
		}
		if (elementForItem instanceof Property){
			Property p  =(Property) elementForItem;
			return p.getType();
		}
		if (elementForItem instanceof Type){
			return (Type) elementForItem;
		}
		/* unresolveable */
		return null;
	}
	
	private boolean isString(Type paramType) {
		if (paramType==null){
			return false;
		}
		return "java.lang.String".equals(paramType.getName());
	}

}
