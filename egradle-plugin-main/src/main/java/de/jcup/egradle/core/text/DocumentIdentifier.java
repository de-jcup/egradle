package de.jcup.egradle.core.text;

import java.util.ArrayList;
import java.util.List;

public interface DocumentIdentifier {

	public String getId();
	
	public static IdentifierStringIdBuilder createStringIdBuilder(){
		return new IdentifierStringIdBuilder();
	}
	
	public class IdentifierStringIdBuilder{
		private List<String> list= new ArrayList<>();
		
		private IdentifierStringIdBuilder(){
			
		}
		public String[] build(){
			return list.toArray(new String[list.size()]);
		}
		
		public IdentifierStringIdBuilder addAll(DocumentIdentifier[] identifiers){
			if (identifiers==null){
				return this;
			}
			for (DocumentIdentifier identifier: identifiers){
				add(identifier);
			}
			return this;
		}
		
		
		public IdentifierStringIdBuilder add(DocumentIdentifier identifier){
			if (identifier==null){
				return this;
			}
			return add(identifier.getId());
		}
		
		public IdentifierStringIdBuilder add(String identifierAsString){
			if (identifierAsString==null){
				return this;
			}
			list.add(identifierAsString);
			return this;
		}
		
		
	}
	
}
