/*
 * Copyright 2017 Albert Tregnaghi
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
package de.jcup.egradle.template;

import java.util.Comparator;

public class FileStructureTemplatePriorityComparator implements Comparator<FileStructureTemplate> {

	@Override
	public int compare(FileStructureTemplate o1, FileStructureTemplate o2) {
		if (o1 == null) {
			if (o2 == null) {
				return 0;
			}
			return -1;
		}
		if (o2 == null) {
			return 1;
		}
		int diff = o1.getPriority() - o2.getPriority();
		if (diff == 0) {
			return 0;
		}
		if (diff > 0) {
			return 1;
		}
		return -1;
	}

}
