package de.jcup.egradle.codeassist;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.jcup.egradle.codeassist.SourceCodeInsertionSupport.InsertionData;
import de.jcup.egradle.codeassist.dsl.CodeBuilder;
import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.MethodUtils;
import de.jcup.egradle.codeassist.dsl.Parameter;
import de.jcup.egradle.codeassist.dsl.Plugin;
import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Reason;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.codeassist.dsl.gradle.GradleLanguageElementEstimater;
import de.jcup.egradle.codeassist.dsl.gradle.GradleLanguageElementEstimater.CreationMode;
import de.jcup.egradle.codeassist.dsl.gradle.GradleLanguageElementEstimater.EstimationResult;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;

public class GradleDSLProposalFactory extends AbstractProposalFactory {

	private CodeBuilder codeBuilder;
	SourceCodeInsertionSupport insertSupport = new SourceCodeInsertionSupport();

	private GradleLanguageElementEstimater typeEstimator;

	/**
	 * Creates new gradle dsl proposal factory
	 * 
	 * @param codeBuilder
	 * @param typeEstimator
	 */
	public GradleDSLProposalFactory(CodeBuilder codeBuilder, GradleLanguageElementEstimater typeEstimator) {
		if (codeBuilder == null) {
			throw new IllegalArgumentException("code codeBuilder may not be null");
		}
		this.codeBuilder = codeBuilder;
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
		/* FIXME ATR, 03.02.2017:  at this point the HTMLDescriptionBuilder like in 
		 * GradleTextHover should be used- so we got only one HTML preview*/
		Type identifiedType = result.getElementType();
		CreationMode mode = result.getMode();
		Set<Proposal> proposals = new TreeSet<>();
		Map<String, Type> extensions = identifiedType.getExtensions();
		/*
		 * TODO ATR, 28.01.2017: speed up by not calculating and setting code on
		 * every creation but only when getter is called - refactoring necessary
		 */
		for (String extensionId : extensions.keySet()) {
			if (extensionId == null) {
				continue;
			}
			Type extensionType = extensions.get(extensionId);
			if (extensionType == null) {
				continue;
			}
			ModelProposal p = new ModelProposal();
			p.type = ModelProposalType.EXTENSION;
			p.setType(extensionType.getName());
			p.setCode(codeBuilder.createClosure(extensionId));

			Reason reason = identifiedType.getReasonForExtension(extensionId);
			StringBuilder description = new StringBuilder();
			StringBuilder name = new StringBuilder();
			name.append(extensionId);
			if (reason != null) {
				Plugin plugin = reason.getPlugin();
				if (plugin != null) {
					// name.append("-");
					// name.append(plugin.getId());
					description.append("<p>reasoned by plugin:<b>");
					description.append(plugin.getId());
					description.append("</b></p><br><br>");

				}
			}
			description.append(extensionType.getDescription());
			description.append("<br><br>Type:");
			description.append(extensionType.getName());
			p.setDescription(description.toString());
			p.setName(name.toString());
			calculateAndSetCursor(p, textBeforeColumn);
			proposals.add(p);
		}
		for (Property property : identifiedType.getProperties()) {
			/*
			 * FIXME ATR, 28.01.2017: check if mixin does copy properties as
			 * well. If so implementation is needed
			 */
			ModelProposal p = new ModelProposal();
			p.type = ModelProposalType.PROPERTY;
			p.setName(property.getName());
			p.setType(identifiedType.getName());
			p.setCode(codeBuilder.createClosure(property));
			p.setDescription(property.getDescription());
			calculateAndSetCursor(p, textBeforeColumn);
			proposals.add(p);
		}
		for (Method method : identifiedType.getMethods()) {
			if (mode == CreationMode.PARENT_TYPE_IS_CONFIGURATION_CLOSURE) {
				if (method.getName().startsWith("get")) {
					continue;
				}
				if (method.getName().startsWith("set")){
					continue;
				}

				List<Parameter> params = method.getParameters();
				if (params.size() == 0) {
					continue;
				}

			}

			ModelProposal p = new ModelProposal();

			Reason reason = identifiedType.getReasonForMethod(method);
			String methodLabel = createMethodLabel(method);
			StringBuilder description = new StringBuilder();
			if (reason != null) {
				Plugin plugin = reason.getPlugin();
				if (plugin != null) {
					description.append("<p>Reasoned by plugin:<b>");
					description.append(plugin.getId());
					description.append("</b></p><br><br>");

					methodLabel = methodLabel + "-" + plugin.getId();

				}
			}
			p.setName(methodLabel);
			description.append(method.getDescription());
			Type returnType = method.getReturnType();
			if (returnType != null) {
				description.append("<br><br>returns:");
				description.append(returnType.getName());
			}
			p.type = ModelProposalType.METHOD;
			p.setType(identifiedType.getName());
			p.setCode(codeBuilder.createClosure(method));
			p.setDescription(description.toString());
			calculateAndSetCursor(p, textBeforeColumn);
			proposals.add(p);
		}
		return proposals;
	}

	/*
	 * FIXME ATR, 20.01.2017: think about providing multiple types here as
	 * result - its often not clear what can be estimated...
	 */
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

	private void calculateAndSetCursor(ModelProposal proposal, String textBeforeColumn) {
		String code = proposal.getCode();
		InsertionData insertData = insertSupport.prepareInsertionString(code, textBeforeColumn);

		proposal.setCursorPos(insertData.cursorOffset);
		proposal.setCode(insertData.sourceCode);
	}

	private boolean isString(Type paramType) {
		if (paramType == null) {
			return false;
		}
		return "java.lang.String".equals(paramType.getName());
	}

	public enum ModelProposalType {
		METHOD, PROPERTY, EXTENSION,
	}

	public class ModelProposal extends AbstractProposalImpl {

		private ModelProposalType type;

		public boolean isMethod() {
			return ModelProposalType.METHOD.equals(type);
		}

		public boolean isProperty() {
			return ModelProposalType.PROPERTY.equals(type);
		}
	}

}
