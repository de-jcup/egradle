package de.jcup.egradle.core.ast.statement;

public class DefStatement extends AbstractStatement{

	
	private String variableName;
	private String variableValue;
	private boolean methodDefinition;
	private boolean variableDefinition;
	private String methodName;
	
	public void setMethodName(String methodName) {
		this.methodName = methodName;
		this.methodDefinition=true;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public boolean isVariableDefinition() {
		return variableDefinition;
	}
	
	public boolean isMethodDefinition() {
		return methodDefinition;
	}
	
	
	public void setVariableName(String variableName) {
		this.variableName = variableName;
		variableDefinition=true;
	}
	
	public String getVariableName() {
		return variableName;
	}

	
	public void setVariableValue(String value) {
		this.variableValue=value;
		variableDefinition=true;
	}

	public String getVariableValue() {
		return variableValue;
	}
}
