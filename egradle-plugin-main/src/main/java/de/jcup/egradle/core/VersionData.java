package de.jcup.egradle.core;

public class VersionData {

	private static final String TEXT__UNKNOWN = "unknown";

	public static final de.jcup.egradle.core.VersionData UNKNOWN = new VersionData(TEXT__UNKNOWN);
	
	private String asText;
	
	public VersionData(String version){
		if (version==null){
			version=TEXT__UNKNOWN;
		}else if (version.trim().length()<1){
			version=TEXT__UNKNOWN;
		}
		this.asText=version;
	}

	public String getAsText() {
		return asText;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((asText == null) ? 0 : asText.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VersionData other = (VersionData) obj;
		if (asText == null) {
			if (other.asText != null)
				return false;
		} else if (!asText.equals(other.asText))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VersionData [" + asText + "]";
	}
}
