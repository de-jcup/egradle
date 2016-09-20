package de.jcup.egradle.eclipse.launch;

/**
 * A key/value pair for property tables
 */
public class PropertyVariable {
	private String name;

	private String value;

	public PropertyVariable(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object obj) {
		boolean equal = false;
		if (obj instanceof PropertyVariable) {
			PropertyVariable var = (PropertyVariable) obj;
			equal = var.getName().equals(name);
		}
		return equal;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
