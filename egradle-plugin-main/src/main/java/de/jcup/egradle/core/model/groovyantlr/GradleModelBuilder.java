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

import java.io.InputStream;

/**
 * Builds a outline model containing gradle specific outline items
 * 
 * @author Albert Tregnaghi
 *
 */
public class GradleModelBuilder extends AbstractGroovyModelBuilder {
	public GradleModelBuilder(InputStream is) {
		this.is = is;
	}

	@Override
	protected AbstractGroovyModelBuilderSupport createModelBuilderSupport() {
		return new GradleModelBuilderSupport();
	}

	

}
