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
 package de.jcup.egradle.codecompletion;

import java.util.List;

/**
 * A provider to support {@link XMLProposalDataModel}. Does also 
 * caching reload etc.
 * 
 * @author albert
 *
 */
public interface XMLProposalDataModelProvider {
	
	/**
	 * Returns a list of xml proposal data models. If the models are reloaded or 
	 * cached depends on implementation.
	 * @return list with data models, never <code>null</code>
	 */
	public List<XMLProposalDataModel> getDataModels();
	
	

}
