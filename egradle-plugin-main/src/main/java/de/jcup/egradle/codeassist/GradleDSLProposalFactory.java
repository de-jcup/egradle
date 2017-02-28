package de.jcup.egradle.codeassist;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.jcup.egradle.codeassist.dsl.CodeTemplateBuilder;
import de.jcup.egradle.codeassist.dsl.LanguageElement;
import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.MethodUtils;
import de.jcup.egradle.codeassist.dsl.Plugin;
import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Reason;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.codeassist.dsl.gradle.GradleLanguageElementEstimater;
import de.jcup.egradle.codeassist.dsl.gradle.GradleLanguageElementEstimater.EstimationResult;
import de.jcup.egradle.codeassist.dsl.gradle.LanguageElementMetaData;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;

public class GradleDSLProposalFactory extends AbstractProposalFactory {

	private CodeTemplateBuilder codeTemplateBuilder;

	private GradleLanguageElementEstimater typeEstimator;
	private DefaultTypeTemplatesProvider defaultTemplatesProvider = new DefaultTypeTemplatesProvider();
	
	/**
	 * Creates new gradle dsl proposal factory
	 * 
	 * @param codeTemplateBuilder
	 * @param typeEstimator
	 */
	public GradleDSLProposalFactory(CodeTemplateBuilder codeTemplateBuilder, GradleLanguageElementEstimater typeEstimator) {
		if (codeTemplateBuilder == null) {
			throw new IllegalArgumentException("code codeTemplateBuilder may not be null");
		}
		this.codeTemplateBuilder = codeTemplateBuilder;
		if (typeEstimator == null) {
			throw new IllegalArgumentException("typeEstimator may not be null!");
		}
		this.typeEstimator = typeEstimator;
	}
	
	

	@Override
	protected Set<Proposal> createProposalsImpl(int offset, ProposalFactoryContentProvider contentProvider) {
		if (contentProvider == null) {
			return null;
		}
		EstimationResult result = tryToIdentifyContextType(offset, contentProvider);

		if (result == null) {
			return null;
		}
		if (result.getElementType() == null) {
			return null;
		}
		String textBeforeColumn = contentProvider.getLineTextBeforeCursorPosition();
		Set<Proposal> proposals = createProposals(result, textBeforeColumn);
		return proposals;
	}

	Set<Proposal> createProposals(EstimationResult result, String textBeforeColumn) {
		Type identifiedType = result.getElementType();
		if (identifiedType==null){
			return Collections.emptySet();
		}
		/* when this is a documented type we show only documented methods etc. otherwise we support
		 * all methods
		 */
		boolean filterUndocumented = identifiedType.isDocumented();
		Set<Proposal> proposals = new TreeSet<>();
		
		Map<String, Type> extensions = identifiedType.getExtensions();
		for (String extensionId : extensions.keySet()) {
			if (extensionId == null) {
				continue;
			}
			Type extensionType = extensions.get(extensionId);
			if (extensionType == null) {
				continue;
			}
			ModelProposal proposal = createModelProposal(result);
			proposal.type = ModelProposalType.EXTENSION;
			proposal.setType(extensionType.getName());
			proposal.setLazyCodeBuilder(new ClosureByStringCodeBuilder(extensionId, codeTemplateBuilder));

			Reason reason = identifiedType.getReasonForExtension(extensionId);
			StringBuilder name = new StringBuilder();
			name.append(extensionId);
			if (reason!=null){
				Plugin plugin = reason.getPlugin();
				if (plugin!=null){
					name.append("-"+plugin.getId());
				}
			}
			proposal.setName(name.toString());
			proposal.setTextBefore(textBeforeColumn);

			proposal.setExtensionId(extensionId);
			proposal.setElement(extensionType);

			proposals.add(proposal);
		}
		for (Property property : identifiedType.getProperties()) {
			if (filterUndocumented && ! property.isDocumented()){
				continue;
			}
			/*
			 * FIXME ATR, 28.01.2017: check if mixin does copy properties as
			 * well. If so implementation is needed
			 */
			ModelProposal proposal = createModelProposal(result);
			proposal.type = ModelProposalType.PROPERTY;
			proposal.setName(property.getName());
			proposal.setType(identifiedType.getName());
			proposal.setLazyCodeBuilder(new PropertyAssignmentCodeBuilder(property, codeTemplateBuilder));
			proposal.setDescription(property.getDescription());
			proposal.setTextBefore(textBeforeColumn);

			proposal.setElement(property);

			proposals.add(proposal);
		}
		for (Method method : identifiedType.getMethods()) {
			if (filterUndocumented && ! method.isDocumented()){
				continue;
			}
			if (isIgnoreGetterOrSetter()){
				if (method.getName().startsWith("get")) {
					continue;
				}
				if (method.getName().startsWith("set")) {
					continue;
				}
			}
			/* TODO ATR, 09.02.2017: what about marking properties as read or write only?
			 * could be done here, or by xml generation so in xml as attribute available...
			 */
			ModelProposal proposal = createModelProposal(result);

			Reason reason = identifiedType.getReasonFor(method);
			String methodLabel = createMethodLabel(method);
			StringBuilder descSb = new StringBuilder();
			if (reason != null) {
				Plugin plugin = reason.getPlugin();
				if (plugin != null) {
					descSb.append("<p>Reasoned by plugin:<b>");
					descSb.append(plugin.getId());
					descSb.append("</b></p><br><br>");

					methodLabel = methodLabel + "-" + plugin.getId();

				}
			}
			proposal.setName(methodLabel);
			descSb.append(method.getDescription());
			Type returnType = method.getReturnType();
			if (returnType != null) {
				descSb.append("<br><br>returns:");
				descSb.append(returnType.getName());
			}
			proposal.type = ModelProposalType.METHOD;
			proposal.setType(identifiedType.getName());
			proposal.setLazyCodeBuilder(new SmartMethodCodeBuilder(method, codeTemplateBuilder));
			proposal.setDescription(descSb.toString());
			proposal.setTextBefore(textBeforeColumn);

			proposal.setElement(method);

			proposals.add(proposal);
		}
		
		/* apply defined templates as proposals */
		List<Template> templates = defaultTemplatesProvider.getTemplatesForType(identifiedType.getName());
		for (Template template: templates){
			TemplateProposal proposal = new TemplateProposal();
			proposal.setName(template.getName());
			proposal.setLazyCodeBuilder(new TemplateCodeBuilder(template));
			proposal.setTextBefore(textBeforeColumn);
			proposal.setDescription(template.getContent());

			proposals.add(proposal);
		}
		return proposals;
	}

	private ModelProposal createModelProposal(EstimationResult result) {
		ModelProposal proposal = new ModelProposal();
		proposal.reason=result;
		return proposal;
	}

	private EstimationResult tryToIdentifyContextType(int offset, ProposalFactoryContentProvider contentProvider) {
		Model model = contentProvider.getModel();
		if (model == null) {
			return null;
		}
		Item outlineItem = model.getParentItemOf(offset);
		if (outlineItem == null) {
			return null;
		}
		GradleFileType fileType = contentProvider.getFileType();
		EstimationResult result = typeEstimator.estimate(outlineItem, fileType);
		return result;
	}

	private String createMethodLabel(Method method) {
		return MethodUtils.createSignature(method);

	}

	public enum ModelProposalType {
		METHOD, PROPERTY, EXTENSION,
	}
	
	public class TemplateProposal extends AbstractProposalImpl{
		
	}

	public class ModelProposal extends AbstractProposalImpl implements LanguageElementMetaData {

		private ModelProposalType type;
		private LanguageElement element;
		private String extensionId;
		private EstimationResult reason;
		
		/**
		 * Returns estimation result which was the reason for this proposal.
		 * Only for test and debug purposes
		 * @return estimation result or <code>null</code> if not set
		 */
		public EstimationResult getReason() {
			return reason;
		}
		
		public void setElement(LanguageElement element) {
			this.element = element;
		}

		

		public void setExtensionId(String extensionId) {
			this.extensionId = extensionId;
		}

		public LanguageElement getElement() {
			return element;
		}

		public boolean isMethod() {
			return ModelProposalType.METHOD.equals(type);
		}

		public boolean isProperty() {
			return ModelProposalType.PROPERTY.equals(type);
		}

		@Override
		public boolean isTypeFromExtensionConfigurationPoint() {
			return extensionId != null;
		}

		@Override
		public String getExtensionName() {
			return extensionId;
		}
		
	}

}
