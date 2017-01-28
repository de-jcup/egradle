package de.jcup.egradle.codeassist;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractProposalImpl implements Proposal{

	private String name;
	private String code;
	private String type;
	private String description;
	private int cursorPos=-1; 
	
	void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		if (name==null){
			return StringUtils.EMPTY;
		}
		return name;
	}
	
	void setCode(String code) {
		this.code = code;
	}
	
	void setCursorPos(int cursorOffset) {
		this.cursorPos=cursorOffset;
	}
	
	/**
	 * @return cursor position - if set, otherwise -1
	 */
	public int getCursorPos() {
		return cursorPos;
	}

	public String getCode() {
		if (code==null){
			return StringUtils.EMPTY;
		}
		return code;
	}
	
	void setType(String type) {
		this.type = type;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public final int compareTo(Proposal o) {
		if (o==null){
			return 1;
		}
		int result = getName().compareTo(o.getName());
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName()+"[name=").append(name).append(", code=").append(code).append(", type=")
				.append(type).append(", description=").append(description).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		AbstractProposalImpl other = (AbstractProposalImpl) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
