package de.jcup.egradle.sdk.builder.action.delegationtarget;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Parameter;
import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.XMLMethod;
import de.jcup.egradle.codeassist.dsl.XMLType;
import de.jcup.egradle.sdk.builder.SDKBuilderContext;
import de.jcup.egradle.sdk.builder.action.SDKBuilderAction;

public class CalculateDelegationTargetsAction implements SDKBuilderAction {

	@Override
	public void execute(SDKBuilderContext context) throws IOException {
		for (String typeName: context.originTypeNameToOriginFileMapping.keySet()){
			XMLType originType = (XMLType)context.originGradleFilesProvider.getType(typeName);
			calculateStillMissingDelegateTargets(originType, context);
		}

	}
	/**
	 * Very interesting how gradle internal works:
	 * <pre>
	 * 
	 * private EclipseJdt jdt;
	 * 
	 * private EclipseJdt getJdt();
	 * private void setJdt(EclipseJdt jdt);
	 * 
	 * private void jdt(Closure closure);
	 * </pre>
	 * So the closure type is simply always the property type!
	 * <pre>
	 * eclipse{
	 * 	jdt{
	 * 		// do something with jdt
	 *  }
	 * }
	 * </pre>
	 * Is pretty much like:
	 * <pre>
	 * 	project.callClosureWithDelegateTarget(getEclipse()).callClosureWithDelegateTarget(getJdt())...
	 * </pre>
	 * 
	 * Normally typical delegatino targets are done by EGradleAssembleDslTask in gradle fork. But there are some special parts
	 * which where now handled here.
	 * 
	 * @param type
	 * @param context
	 */
	void calculateStillMissingDelegateTargets(Type type, SDKBuilderContext context) {
		int problemCount = 0;
		StringBuilder problems = new StringBuilder();
		for (Method m : type.getMethods()) {
			if (!(m instanceof XMLMethod)) {
				continue;
			}
			context.methodAllCount++;
			XMLMethod method = (XMLMethod) m;
			String delegationTarget = method.getDelegationTargetAsString();
			if (!StringUtils.isBlank(delegationTarget)) {
				continue;// already set
			}
			String targetType = null;
			List<Parameter> parameters = method.getParameters();
			if (parameters.size()!=1){
				continue;
			}
			Parameter firstParam = parameters.iterator().next();
			if (! firstParam.getTypeAsString().equals("groovy.lang.Closure")){
				continue;
			}
			String methodName = m.getName();
			targetType= scanProperties(type, methodName);
			
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

	private String scanProperties(Type type, String methodName) {
		/* try to find a property - in this class or subclass or interfaces etc..*/
		Set<Property> allProperties = type.getProperties();
		for (Property p : allProperties){
			String propName = p.getName();
			if (propName.equals(methodName)){
				String typeAsString = p.getTypeAsString();
				if (typeAsString!=null && !typeAsString.isEmpty()){
					return typeAsString;
				}
			}
		}
		return null;
	}
}
