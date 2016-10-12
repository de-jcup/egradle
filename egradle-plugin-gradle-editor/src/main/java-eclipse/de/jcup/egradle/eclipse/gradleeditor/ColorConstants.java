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
package de.jcup.egradle.eclipse.gradleeditor;

import org.eclipse.swt.graphics.RGB;

public interface ColorConstants {
	RGB COMMENT = new RGB(128, 128, 128);
	RGB OTHER_KEYWORDS = new RGB(0, 128, 0);
	RGB APPLY = new RGB(128, 128, 0);
	RGB DEFAULT = new RGB(0, 0, 0);

	RGB PROC_INSTR = new RGB(128, 128, 128);
	RGB STRING = new RGB(0, 0, 128);
	RGB TAG = new RGB(0, 0, 128);
}
