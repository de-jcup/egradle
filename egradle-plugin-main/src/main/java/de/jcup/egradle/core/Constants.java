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
 package de.jcup.egradle.core;

public class Constants {

	public static final String VIRTUAL_ROOTPROJECT_NAME = "- virtual root project";
	public static final String VIRTUAL_ROOTPROJECT_FOLDERNAME = ".egradle";
	public static final String CONSOLE_FAILED = "[FAILED]";
	public static final String CONSOLE_OK = "[OK]";
	public static final String CONSOLE_WARNING = "[WARNING]";
	/**
	 * Validation output is shrinked to optimize validation performance. The value marks what is the limit of lines necessary to validate
	 */
	public static final int VALIDATION_OUTPUT_SHRINK_LIMIT = 50;
}
