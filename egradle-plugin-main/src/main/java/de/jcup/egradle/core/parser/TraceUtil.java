package de.jcup.egradle.core.parser;

public class TraceUtil {
	public static void traceLine(String string) {
		System.out.println("TRACE:"+string);

	}

	public static void trace(String string) {
		System.out.print(string);

	}

	public static void traceShowRuler() {
		traceLine("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}
}
