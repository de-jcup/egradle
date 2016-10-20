package de.jcup.egradle.eclipse.gradleeditor.document;

public enum GradleDocumentIdentifiers implements GradleDocumentIdentifier {
	
	JAVA_KEYWORD,
	
	JAVA_LITERAL,
	
	GROOVY_KEYWORD ,
	
	COMMENT,
	
	STRING,
	
	GRADLE_KEYWORD,
	
	GRADLE_LINK_KEYWORD,
	
	GRADLE_TASK_KEYWORD,
	
	GRADLE_VARIABLE,
	;


	@Override
	public String getId() {
		return name();
	}
	public static String[] allIdsToStringArray(){
		return allIdsToStringArray(null);
	}
	public static String[] allIdsToStringArray(String additionalDefaultId){
		GradleDocumentIdentifiers[] values = values();
		int size = values.length;
		if (additionalDefaultId!=null){
			size+=1;
		}
		String[] data = new String[size];
		int pos=0;
		if (additionalDefaultId!=null){
			data[pos++]=additionalDefaultId;
		}
		for (GradleDocumentIdentifiers d: values){
			data[pos++]=d.getId();
		}
		return data;
	}

}
