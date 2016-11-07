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
