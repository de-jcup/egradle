package de.jcup.egradle.sdk.builder.action.delegationtarget;

import static de.jcup.egradle.codeassist.dsl.DSLConstants.*;

import java.io.IOException;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.MethodUtils;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.TypeReference;
import de.jcup.egradle.codeassist.dsl.XMLMethod;
import de.jcup.egradle.sdk.builder.SDKBuilderContext;
import de.jcup.egradle.sdk.builder.action.SDKBuilderAction;

public class EstimateDelegationTargetsByJavadocAction implements SDKBuilderAction {

	@Override
	public void execute(SDKBuilderContext context) throws IOException {
		/* now load the xml files as type data - and inspect all descriptions */
		System.out.println("- estimate still missing estimation targets my javadoc ");
		
		for (String typeName : context.allTypes.keySet()) {
			Type type=context.originGradleFilesProvider.getType(typeName);
			estimateDelegateTargets_by_javdoc(type,context);
		}
		
	}
	
	/**
	 * Estimates delegation targets by javadoc. will be only done for method having a closure inside!
	 * @param type
	 * @param context
	 */
	void estimateDelegateTargets_by_javdoc(Type type, SDKBuilderContext context) {
		estimateDelegateTargets_by_javdoc(type, context,true);
	}
	/**
	 * Estimates delegation targets by javadoc. 
	 * @param type
	 * @param context
	 * @param checkOnlyClosures when <code>true</code> only methods with containing closures inside will be estimated
	 */
	void estimateDelegateTargets_by_javdoc(Type type, SDKBuilderContext context, boolean checkOnlyClosures) {
		int problemCount = 0;
		StringBuilder problems = new StringBuilder();
		for (Method m : type.getDefinedMethods()) {
			if (!(m instanceof XMLMethod)) {
				continue;
			}
			context.methodAllCount++;
			XMLMethod method = (XMLMethod) m;
			String delegationTarget = m.getDelegationTargetAsString();
			if (!StringUtils.isBlank(delegationTarget)) {
				continue;// already set
			}
			if (checkOnlyClosures && !MethodUtils.hasGroovyClosureAsParameter(method)){
				continue;
			}
			String description = method.getDescription();
			if (description == null) {
				/* not resolvable */
				problemCount++;
				problems.append(method.getName());
				problems.append(" ");
				continue;
			}
			if (description.indexOf(HYPERLINK_TYPE_PREFIX)==-1){
				/* fetch interfaces */
				description = fetchDescriptionFromInterfaces(type, method);
			}
			String targetType = inspectTargetTypeByDescription(description);
			if (targetType != null) {
				method.setDelegationTargetAsString(targetType);
			}
		}
		if (problemCount > 0) {
			// System.out.println("- WARN: type:" + type.getName()
			// + " has following method without descriptions: has no description
			// " + problems.toString());
			context.methodWithOutDescriptionCount += problemCount;
		}

	}
	
	private String fetchDescriptionFromInterfaces(Type type, XMLMethod method) {
		Set<TypeReference> interfaces = type.getInterfaces();
		for (TypeReference interfaceReference: interfaces){
			Type _interface = interfaceReference.getType();
			if (_interface==null){
				/* can happen - e.g. for java.util.Set...*/
				continue;
			}
			for (Method m2 :_interface.getDefinedMethods()){
				if (!MethodUtils.haveSameSignatures(m2,method)){
					continue;
				}
				String description = m2.getDescription();
				if (description == null) {
					continue;
				}
				if (description.indexOf("@inheritDoc")!=-1){
					continue;
				}
				if (description.indexOf(HYPERLINK_TYPE_PREFIX)==-1){
					continue;
				}
				/* description found - use it!*/
				return description;
			}
		}
		return null;
	}
	
	private String inspectTargetTypeByDescription(String description) {
		if (description==null){
			return null;
		}
		String targetType =null;
		int index = 0;
		while (targetType == null && index != -1) {
			int from = index + 1;
			index = StringUtils.indexOf(description, HYPERLINK_TYPE_PREFIX, from);
			if (index != -1) {
				targetType = inspect(index, description);
			}
		}
		return targetType;
	}
	
	private String inspect(int index, String description) {
		StringBuilder sb = new StringBuilder();
		index = index + HYPERLINK_TYPE_PREFIX.length();
		int length = description.length();
		for (int i = index; i < length; i++) {
			char c = description.charAt(i);
			if (Character.isLetterOrDigit(c) || c == '.') {
				sb.append(c);
			} else {
				if (c == '#') {
					return null;
				}
				break;
			}
		}
		String targetType = sb.toString();
		return targetType;

	}

}
