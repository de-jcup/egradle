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
	public static final RGB COMMENT_DEFAULT_GREEN = new RGB(63, 127, 95); // green
																			// as
																			// java
																			// eclipse
																			// default
																			// for
																			// comments
	public static final RGB LINK_DEFAULT_BLUE = new RGB(63, 63, 191); // blue as
																		// java
																		// eclipse
																		// default
																		// for
																		// links
	public static final RGB KEYWORD_DEFAULT_PURPLE = new RGB(127, 0, 85); // purple
																			// as
																			// java
																			// eclipse
																			// default
																			// for
																			// return
	public static final RGB STRING_DEFAULT_BLUE = new RGB(42, 0, 255); // same
																		// as
																		// java
																		// default
																		// string
	
	public static final RGB OUTLINE_ITEM__TYPE = new RGB(149,125,71); // same as java outline string
	
	public static final RGB DARK_GREEN = new RGB(0, 64, 0);
	public static final RGB TASK_DEFAULT_RED = new RGB(128, 0, 0);

	public static final RGB BLACK = new RGB(0, 0, 0);
	public static final RGB RED = new RGB(170, 0, 0);
	public static final RGB GREEN = new RGB(0, 170, 0);
	public static final RGB BROWN = new RGB(170, 85, 0);
	public static final RGB BLUE = new RGB(0, 0, 170);
	public static final RGB MAGENTA = new RGB(170, 0, 170);
	public static final RGB CYANN = new RGB(0, 170, 170);
	public static final RGB GRAY = new RGB(170, 170, 170);
	public static final RGB DARK_GRAY = new RGB(85, 85, 85);
	public static final RGB BRIGHT_RED = new RGB(255, 85, 85);
	public static final RGB BRIGHT_GREEN = new RGB(85, 255, 85);
	public static final RGB YELLOW = new RGB(255, 255, 85);
	public static final RGB ORANGE = new RGB(255, 165, 0); // http://www.rapidtables.com/web/color/orange-color.htm
	public static final RGB BRIGHT_BLUE = new RGB(85, 85, 255);
	public static final RGB BRIGHT_MAGENTA = new RGB(255, 85, 255);

	public static final RGB BRIGHT_CYAN = new RGB(85, 255, 255);
	public static final RGB WHITE = new RGB(255, 255, 255);
}
