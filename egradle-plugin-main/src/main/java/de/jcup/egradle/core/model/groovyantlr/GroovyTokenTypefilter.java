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
 package de.jcup.egradle.core.model.groovyantlr;

import antlr.collections.AST;
import de.jcup.egradle.core.api.Filter;

class GroovyTokenTypefilter implements Filter{

		private int filteredType;

		GroovyTokenTypefilter(int type){
			this.filteredType=type;
		}
	
		@Override
		public boolean isFiltered(Object obj) {
			boolean isAst = obj instanceof AST;
			if (!isAst){
				return false;
			}
			AST ast = (AST) obj;
			int type = ast.getType();
			if (filteredType==type){
				return true;
			}
			return false;
		}
		
	}