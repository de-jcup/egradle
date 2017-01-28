package de.jcup.egradle.codeassist;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.jcup.egradle.codeassist.SourceCodeInsertionSupport.InsertionData;
import de.jcup.egradle.codeassist.dsl.CodeBuilder;
import de.jcup.egradle.codeassist.dsl.LanguageElement;
import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Parameter;
import de.jcup.egradle.codeassist.dsl.Plugin;
import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Reason;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.codeassist.dsl.gradle.GradleLanguageElementEstimater;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;

public class GradleDSLProposalFactory extends AbstractProposalFactory {

	private CodeBuilder codeBuilder;
	SourceCodeInsertionSupport insertSupport = new SourceCodeInsertionSupport();


	public Set<Proposal> createProposals(Type identifiedType, String textBeforeColumn) {
		Set<Proposal> proposals = new TreeSet<>();
		Map<String, Type> extensions = identifiedType.getExtensions();
		/*
		 * TODO ATR, 28.01.2017: speed up by not calculating and setting code on
		 * every creation but only when getter is called - refactoring necessary
		 */
		for (String extensionId : extensions.keySet()) {
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
			if (reason != null) {
				Plugin plugin = reason.getPlugin();
				if (plugin != null) {
					description.append("<p>reasoned by plugin:<b>");
					description.append(plugin.getId());
					description.append("</b></p><br><br>");

				}
			}
			description.append(extensionType.getDescription());
			p.setDescription(description.toString());
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
			ModelProposal p = new ModelProposal();
			Reason reason = identifiedType.getReasonForMethod(method);
			String methodLabel = createMethodLabel(method);
			StringBuilder description = new StringBuilder();
			if (reason != null) {
				Plugin plugin = reason.getPlugin();
				if (plugin != null) {
					description.append("<p>reasoned by plugin:<b>");
					description.append(plugin.getId());
					description.append("</b></p><br><br>");

					methodLabel = methodLabel + "(" + plugin.getId() + ")";

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

	public String createMethodLabel(Method method) {
		StringBuilder sb = new StringBuilder();
		sb.append(method.getName());
		List<Parameter> parameters = method.getParameters();
		sb.append("(");
		for (Iterator<Parameter> itp = parameters.iterator(); itp.hasNext();) {
			Parameter param = itp.next();
			param.getName();
			if (itp.hasNext()) {
				sb.append(",");
			}
		}
		sb.append(")");
		return sb.toString();

	}

	private void calculateAndSetCursor(ModelProposal proposal, String textBeforeColumn) {
		String code = proposal.getCode();
		InsertionData insertData = insertSupport.prepareInsertionString(code, textBeforeColumn);

		proposal.setCursorPos(insertData.cursorOffset);
		proposal.setCode(insertData.sourceCode);
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

	private GradleLanguageElementEstimater typeEstimator;
	/*
	 * FIXME ATR, 19.01.2017:check memory foot print of these factories and
	 * think about shared instances
	 */

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
		Type identifiedType = tryToIdentifyParentType(offset, contentProvider);
		if (identifiedType == null) {
			return null;
		}
		String textBeforeColumn = contentProvider.getLineTextBeforeCursorPosition() + 1;
		Set<Proposal> proposals = createProposals(identifiedType, textBeforeColumn);
		return proposals;
	}

	/*
	 * FIXME ATR, 20.01.2017: think about providing multiple types here as
	 * result - its often not clear what can be estimated...
	 */
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
		LanguageElement elementForItem = typeEstimator.estimate(outlineItem, fileType);
		if (elementForItem instanceof Type) {
			Type type = (Type) elementForItem;
			return type;
		}
		if (elementForItem instanceof Method) {
			/*
			 * FIXME ATR, 20.01.2017: HIGH PRIO:problem: when method parameter
			 * is a closure the target type is only available form documentation
			 * - arg!!
			 */
			// something like {@link ObjectConfigurationAction} first occuring
			// seems to be the target where the closure is executed!
			Method m = (Method) elementForItem;
			// List<Parameter> parameters = m.getParameters();
			// for (Parameter p: parameters){
			// Type paramType = p.getType();
			// /* ignore string parameters */
			// if (!isString(paramType)){
			// return p.getType();
			// }
			// }
			/* fall back to return type if no param */
			return m.getReturnType();
		}
		if (elementForItem instanceof Property) {
			Property p = (Property) elementForItem;
			return p.getType();
		}
		if (elementForItem instanceof Type) {
			return (Type) elementForItem;
		}
		/* unresolveable */
		return null;
	}

	private boolean isString(Type paramType) {
		if (paramType == null) {
			return false;
		}
		return "java.lang.String".equals(paramType.getName());
	}

}
