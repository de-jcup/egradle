/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
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
