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
 package de.jcup.egradle.library.access;
import org.codehaus.groovy.ast.ASTNode;

public class EGradleBuildscriptResult {

	private ASTNode node;
	
	private Exception exception;
	
	public EGradleBuildscriptResult(Exception exception) {
		this.exception = exception;
	}
	
	public EGradleBuildscriptResult(ASTNode node) {
		this.node = node;
	}
	
	public Exception getException() {
		return exception;
	}
	
	public ASTNode getNode() {
		return node;
	}
}

