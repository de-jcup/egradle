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
 package de.jcup.egradle.core.token.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.jcup.egradle.core.token.Token;
import de.jcup.egradle.core.token.TokenImpl;

public class TokenParserResult {
	
	private String description = "NONE";
	private TokenImpl root;
	
	TokenParserResult(TokenImpl root){
		this.root=root;
	}
	
	public Token getRoot() {
		return root;
	}

	@Override
	public String toString() {
		return "TokenParserResult [description=" + description + "]";
	}

	

	private List<Problem> problems = new ArrayList<>();
	
	public boolean hasProblems(){
		return problems.size()>0;
	}
	
	public List<Problem> getProblems() {
		return Collections.unmodifiableList(problems);
	}
	
	public void addProblem(String messge , int offset) {
		Problem problem = new Problem();
		problem.offset=offset;
		problem.messsage=messge;
		problems.add(problem);
	}
	
	public class Problem{
		private String messsage;
		private int offset;
		public String getMesssage() {
			return messsage;
		}
		public int getOffset() {
			return offset;
		}
	}

	public void print() {
		root.print();
	}


}
