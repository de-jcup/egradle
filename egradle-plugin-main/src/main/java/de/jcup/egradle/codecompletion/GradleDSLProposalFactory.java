package de.jcup.egradle.codecompletion;

import java.util.Set;

import de.jcup.egradle.codecompletion.dsl.CodeBuilder;
import de.jcup.egradle.codecompletion.dsl.Type;
import de.jcup.egradle.codecompletion.dsl.gradle.GradleTypeEstimater;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;

public class GradleDSLProposalFactory extends AbstractModelProposalFactory {

	private GradleTypeEstimater typeEstimator;
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
			GradleTypeEstimater typeEstimator) {
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

		Set<Proposal> proposals = super.createProposals(identifiedType);
		return proposals;
	}

	private Type tryToIdentifyParentType(int offset, ProposalFactoryContentProvider contentProvider) {
		Model model = contentProvider.getModel();
		if (model == null) {
			return null;
		}
		Item outlineItem = model.getParentItemOf(offset);
		if (outlineItem == null) {
			return null;
		}
		Type parentType = typeEstimator.estimateFromGradleProjectAsRoot(outlineItem);

		return parentType;
	}

}
