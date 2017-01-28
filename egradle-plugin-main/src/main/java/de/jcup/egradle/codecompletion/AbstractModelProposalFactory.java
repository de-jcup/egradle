package de.jcup.egradle.codecompletion;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.jcup.egradle.codecompletion.SourceCodeInsertionSupport.InsertionData;
import de.jcup.egradle.codecompletion.dsl.CodeBuilder;
import de.jcup.egradle.codecompletion.dsl.Method;
import de.jcup.egradle.codecompletion.dsl.Parameter;
import de.jcup.egradle.codecompletion.dsl.Plugin;
import de.jcup.egradle.codecompletion.dsl.Property;
import de.jcup.egradle.codecompletion.dsl.Reason;
import de.jcup.egradle.codecompletion.dsl.Type;

public abstract class AbstractModelProposalFactory extends AbstractProposalFactory {

	private CodeBuilder builder;
	SourceCodeInsertionSupport insertSupport = new SourceCodeInsertionSupport();

	public AbstractModelProposalFactory(CodeBuilder builder) {
		if (builder == null) {
			throw new IllegalArgumentException("code builder may not be null");
		}
		this.builder = builder;
	}

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
			p.setCode(builder.createClosure(extensionId));

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
			/* FIXME ATR, 28.01.2017: check if mixin does copy properties as well. If so implementation is needed */
			ModelProposal p = new ModelProposal();
			p.type = ModelProposalType.PROPERTY;
			p.setName(property.getName());
			p.setType(identifiedType.getName());
			p.setCode(builder.createClosure(property));
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
					
					methodLabel=methodLabel+"("+plugin.getId()+")";

				}
			}
			p.setName(methodLabel);
			description.append(method.getDescription());
			Type returnType = method.getReturnType();
			if (returnType!=null){
				description.append("<br><br>returns:");
				description.append(returnType.getName());
			}
			p.type = ModelProposalType.METHOD;
			p.setType(identifiedType.getName());
			p.setCode(builder.createClosure(method));
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
}
