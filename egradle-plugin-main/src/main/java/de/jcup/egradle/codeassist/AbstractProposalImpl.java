package de.jcup.egradle.codeassist;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractProposalImpl implements Proposal {

	private String name;
	private String type;
	private String description;
	private String textBeforeColumn;

	private LazyCodeBuilder lazyBuilder;

	void setName(String name) {
		this.name = name;
	}

	public void setTextBefore(String textBeforeColumn) {
		this.textBeforeColumn = textBeforeColumn;
	}

	public String getTextBeforeColumn() {
		if (textBeforeColumn == null) {
			return "";
		}
		return textBeforeColumn;
	}

	@Override
	public String getTemplate() {
		if (lazyBuilder != null) {
			return lazyBuilder.getTemplate();
		}
		return "";
	}

	@Override
	public String getLabel() {
		if (name == null) {
			return StringUtils.EMPTY;
		}
		return name;
	}

	/**
	 * Sets a lazy code builder - so code parts are only build when proposal is
	 * applied. This increases speed and avoids ui slow down..
	 * 
	 * @param builder
	 */
	void setLazyCodeBuilder(LazyCodeBuilder builder) {
		this.lazyBuilder = builder;
	}

	/**
	 * @return lazy code builder or <code>null</code> if not set
	 */
	public LazyCodeBuilder getLazyBuilder() {
		return lazyBuilder;
	}

	/**
	 * @return cursor position - if set, otherwise -1
	 */
	public int getCursorPos() {
		return lazyBuilder.getCursorPos(this, getTextBeforeColumn());
	}

	public String getCode() {
		if (lazyBuilder == null) {
			return StringUtils.EMPTY;
		}
		return lazyBuilder.getCode(this, getTextBeforeColumn());
	}

	void setType(String type) {
		this.type = type;
	}

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
		if (o == null) {
			return 1;
		}
		int result = getLabel().compareTo(o.getLabel());
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName() + "[name=").append(name).append(", type=").append(type)
				.append(", description=").append(description).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
