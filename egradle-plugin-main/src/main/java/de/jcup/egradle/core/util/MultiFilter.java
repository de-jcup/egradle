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
 package de.jcup.egradle.core.util;

import java.util.ArrayList;
import java.util.List;

public class MultiFilter implements Filter{

	private List<Filter> filters = new ArrayList<>();
	
	@Override
	public boolean isFiltered(Object object) {
		if (object==null){
			return true;
		}
		/* use other filters */
		for (Filter filter: filters){
			if (filter.isFiltered(object)){
				return true;
			}
		}
		return false;
	}

	public void add(Filter otherFilter) {
		if (otherFilter==null){
			return;
		}
		if (filters.contains(otherFilter)){
			return;
		}
		filters.add(otherFilter);
	}

	

}
