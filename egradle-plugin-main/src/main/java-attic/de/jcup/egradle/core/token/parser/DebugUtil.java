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
 package de.jcup.egradle.core.token.parser;

public class DebugUtil {
	
	public static void debug(String string) {
		System.out.println("DEBUG:"+string);
	}
	public static void trace(String string) {
		System.out.println("TRACE:"+string);
	}

	public static void traceShowRuler() {
		trace("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}
	
	public static void traceHeadline(String headline) {
		trace("------------[[[[ + "+headline+" + ]]]]]----------------");
	}
}
