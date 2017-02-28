package de.jcup.egradle.codeassist.dsl;

public class ReasonImpl implements Reason {

	private Plugin plugin;
	private Type superType;

	public ReasonImpl() {
	}

	public ReasonImpl setPlugin(Plugin plugin) {
		this.plugin = plugin;
		return this;
	}

	
	public Plugin getPlugin() {
		return plugin;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((plugin == null) ? 0 : plugin.hashCode());
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
		ReasonImpl other = (ReasonImpl) obj;
		if (plugin == null) {
			if (other.plugin != null)
				return false;
		} else if (!plugin.equals(other.plugin))
			return false;
		return true;
	}

	@Override
	public Type getSuperType() {
		return superType;
	}

	public void setSuperType(Type superType) {
		this.superType = superType;
	}
}
