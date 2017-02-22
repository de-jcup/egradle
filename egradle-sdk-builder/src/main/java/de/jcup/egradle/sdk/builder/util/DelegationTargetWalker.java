package de.jcup.egradle.sdk.builder.util;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.MethodUtils;
import de.jcup.egradle.codeassist.dsl.ModifiableMethod;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.TypeReference;
import de.jcup.egradle.sdk.builder.SDKBuilderContext;

public class DelegationTargetWalker {

	public void visitAllMethodInHierarchy(Type type, DelegationTargetMethodVisitor visitor, SDKBuilderContext context,
			boolean checkOnlyClosures) {
		for (Method m : type.getDefinedMethods()) {

			String targetType = m.getDelegationTargetAsString();
			if (StringUtils.isNotBlank(targetType)) {
				continue;
			}
			targetType = findDelegationTargetTypeForMethod(m, visitor, type, checkOnlyClosures);

			if (StringUtils.isNotBlank(targetType)) {
				if (m instanceof ModifiableMethod) {
					ModifiableMethod method = (ModifiableMethod) m;
					method.setDelegationTargetAsString(targetType);
				} else {
					context.addWarning("cannot set target type:" + targetType + " to method:" + m);
				}
			}
		}
	}

	private String findDelegationTargetTypeForMethod(Method methodToSearch, DelegationTargetMethodVisitor visitor,
			Type currentType, boolean checkOnlyClosures) {
		if (checkOnlyClosures && !MethodUtils.hasGroovyClosureAsParameter(methodToSearch)) {
			return null;
		}

		/* scan methods of type */
		for (Method m : currentType.getDefinedMethods()) {
			if (!MethodUtils.haveSameSignatures(methodToSearch, m)) {
				continue;
			}
			String targetType = visitor.getDelegationTargetAsString(m);
			if (StringUtils.isBlank(targetType)) {
				continue;
			}
			return targetType;
		}

		/* not found in methods so scan super type where possible */
		Type superType = currentType.getSuperType();
		if (superType != null) {
			String targetType = findDelegationTargetTypeForMethod(methodToSearch, visitor, superType,
					checkOnlyClosures);
			if (StringUtils.isNotBlank(targetType)) {
				return targetType;
			}
		}
		/* still not found so inspect interfaces */
		Set<TypeReference> interfaces = currentType.getInterfaces();
		for (TypeReference interfaceReference : interfaces) {
			Type _interface = interfaceReference.getType();
			if (_interface == null) {
				/* can happen - e.g. for java.util.Set... */
				continue;
			}
			String targetType = findDelegationTargetTypeForMethod(methodToSearch, visitor, _interface,
					checkOnlyClosures);
			if (StringUtils.isBlank(targetType)) {
				continue;
			}
			return targetType;
		}
		return null;
	}

}
