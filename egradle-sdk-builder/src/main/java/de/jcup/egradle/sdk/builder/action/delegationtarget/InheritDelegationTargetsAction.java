package de.jcup.egradle.sdk.builder.action.delegationtarget;

import java.io.IOException;

import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.XMLType;
import de.jcup.egradle.sdk.builder.SDKBuilderContext;
import de.jcup.egradle.sdk.builder.action.SDKBuilderAction;
import de.jcup.egradle.sdk.builder.util.DelegationTargetMethodVisitor;
import de.jcup.egradle.sdk.builder.util.DelegationTargetWalker;

public class InheritDelegationTargetsAction implements SDKBuilderAction {

	private DelegationTargetWalker walker = new DelegationTargetWalker();
	private SimpleDelegationTargetMethodVisitor visitor = new SimpleDelegationTargetMethodVisitor();
	
	@Override
	public void execute(SDKBuilderContext context) throws IOException {
		for (String typeName: context.originTypeNameToOriginFileMapping.keySet()){
			XMLType originType = (XMLType)context.originGradleFilesProvider.getType(typeName);
			walker.visitAllMethodInHierarchy(originType, visitor, context, true);
		}

	}
	
	private class SimpleDelegationTargetMethodVisitor implements DelegationTargetMethodVisitor{

		@Override
		public String getDelegationTargetAsString(Method method) {
			return method.getDelegationTargetAsString();
		}
		
	}
}
