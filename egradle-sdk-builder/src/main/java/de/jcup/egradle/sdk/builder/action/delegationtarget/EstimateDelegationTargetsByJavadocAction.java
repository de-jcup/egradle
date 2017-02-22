package de.jcup.egradle.sdk.builder.action.delegationtarget;

import static de.jcup.egradle.codeassist.dsl.DSLConstants.*;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.sdk.builder.SDKBuilderContext;
import de.jcup.egradle.sdk.builder.action.SDKBuilderAction;
import de.jcup.egradle.sdk.builder.util.DelegationTargetMethodVisitor;
import de.jcup.egradle.sdk.builder.util.DelegationTargetWalker;

public class EstimateDelegationTargetsByJavadocAction implements SDKBuilderAction {
	private DelegationTargetWalker walker = new DelegationTargetWalker(); 
	private JavadocDelegationTargetMethodVisitor visitor = new JavadocDelegationTargetMethodVisitor();

	@Override
	public void execute(SDKBuilderContext context) throws IOException {
		/* now load the xml files as type data - and inspect all descriptions */
		System.out.println("- estimate still missing estimation targets my javadoc ");
		
		for (String typeName : context.originTypeNameToOriginFileMapping.keySet()) {
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
	void estimateDelegateTargets_by_javdoc(Type type, SDKBuilderContext context, boolean checkOnlyClosures) {
		walker.visitAllMethodInHierarchy(type, visitor, context, checkOnlyClosures);
	}
	
	private class JavadocDelegationTargetMethodVisitor implements DelegationTargetMethodVisitor{

		@Override
		public String getDelegationTargetAsString(Method method) {
			String description = method.getDescription();
			if (description == null) {
				/* not resolvable */
				return null;
			}
			String targetType = inspectTargetTypeByDescription(description);
			return targetType;
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
	
	
	

}
