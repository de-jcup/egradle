/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, VersionData 2.0 (the "License");
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
 package de.jcup.egradle.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuildContext {
	
	private List<Error> errors = new ArrayList<>();
	
	public void add(Error error){
		if (error==null){
			return;
		}
		errors.add(error);
	}
	
	/**
	 * @return an unmodifiable list of outline errors, never <code>null</code>
	 */
	public List<Error> getErrors(){
		return Collections.unmodifiableList(errors);
	}

	public boolean hasErrors() {
		return errors.size()>0;
	}
	
}
