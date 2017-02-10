package de.jcup.egradle.codeassist.dsl;

public interface ModifiableMethod extends Method{

	void setReturnType(Type returnType);

	void setParent(Type parent);

	void setDelegationTargetAsString(String delegationTargetAsString);

	void setDelegationTarget(Type target);

}