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
