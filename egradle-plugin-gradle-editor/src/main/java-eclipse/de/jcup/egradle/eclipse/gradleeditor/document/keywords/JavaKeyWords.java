package de.jcup.egradle.eclipse.gradleeditor.document.keywords;

// see http://docs.oracle.com/javase/tutorial/java/nutsandbolts/_keywords.html
public enum JavaKeyWords implements DocumentKeyWord {

	ABSTRACT("abstract"),
	
	CONTINUE("continue"),
	
	FOR("for"),

	NEW("new"),
	
	SWITCH("switch"),
	
	ASSERT("assert"),
	
	DEFAULT("default"),
	
	GOTO("goto"),
	
	PACKAGE("package"),
	
	SYNCHRONIZED("synchronized"),
	
	BOOLEAN("boolean"),
	
	DO("do"),
	
	IF("if"),
	
	PRIVATE("private"),
	
	THIS("this"),
	
	BREAK("break"),
	
	DOUBLE("double"),
	
	IMPLEMENTS("implements"),
	
	PROTECTED("protected"),
	
	THROW("throw"),
	
	BYTE("byte"),
	
	ELSE("else"),
	
	IMPORT("import"),
	
	PUBLIC("public"),
	
	THROWS("throws"),
	
	CASE("case"),
	
	ENUM("enum"), 
	
	INSTANCE_OF("instanceof"),
			
	RETURN("return"),
	
	TRANSIENT("transient"),
	
	CATCH("catch"),
	
	EXTENDS("extends"),
	
	INT("int"),
	
	SHORT("short"),
	
	TRY("try"),
	
	CHAR("char"),
	
	FINAL("final"),
	
	INTERFACE("interface"),
	
	STATIC("static"),
	
	VOID("void"),
	
	CLASS("class"),
	
	FINALLY("finally"),
	
	LONG("long"),
			
	STRICTFP("strictfp"),
	
	VOLATILE("volatile"),
	
	CONST("const"),
	
	FLOAT("float"),
	
	NATIVE("native"),
	
	SUPER("super"),
	
	WHILE("while")

	;

	private String text;

	private JavaKeyWords(String text) {
		this.text = text;
	}


	@Override
	public String getText() {
		return text;
	}
}
