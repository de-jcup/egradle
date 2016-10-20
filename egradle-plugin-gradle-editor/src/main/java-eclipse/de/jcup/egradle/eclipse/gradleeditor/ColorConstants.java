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
	RGB GRAY = new RGB(128, 128, 128); 
	RGB COMMENT_DEFAULT_GREEN = new RGB(63, 127,95); // green as java eclipse default for comments
	RGB ORANGE = new RGB(128, 128, 0);
	RGB BLACK = new RGB(0, 0, 0);
	RGB LINK_DEFAULT_BLUE = new RGB(63,63,191); // blue as java eclipse default for links
	RGB KEYWORD_DEFAULT_PURPLE = new RGB(127,0,85); // purple as java eclipse default for return
	RGB STRING_DEFAULT_BLUE = new RGB(42, 0, 255); // same as java default string
	RGB DARK_GREEN = new RGB(0,64,0);
	RGB TASK_DEFAULT_RED = new RGB(128,0,0);
}
