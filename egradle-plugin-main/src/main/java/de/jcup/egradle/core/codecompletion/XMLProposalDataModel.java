package de.jcup.egradle.core.codecompletion;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "model")
public class XMLProposalDataModel {

	@XmlAttribute(name = "id")
	String id;

	public String getId() {
		return id;
	}

	@XmlElement(name = "proposal")
	private List<XMLProposalData> proposals = new ArrayList<>();

	public List<XMLProposalData> getProposals() {
		return proposals;
	}

	public static class XMLProposalContext {

		@XmlElement(name = "rootPathEntry")
		private List<XMLProposalRootPathEntry> rootPathEntries = new ArrayList<>();

		public List<XMLProposalRootPathEntry> getRootPathEntries() {
			return rootPathEntries;
		}

	}

	public static class XMLProposalRootPathEntry {
		@XmlValue
		String path;

		public String createRootPathEntrygetPath() {
			return path;
		}
	}

	/**
	 * Represents an item of outline view inside xml
	 * 
	 * @author albert
	 *
	 */
	public static class XMLProposalElement implements XMLProposalDescribed {
		@XmlAttribute(name = "name", required=true)
		String name;

		public String getName() {
			return name;
		}

		@XmlElement(name = "element")
		private List<XMLProposalElement> elements = new ArrayList<>();
		
		public List<XMLProposalElement> getElements() {
			return elements;
		}

		@XmlElement(name = "value")
		private List<XMLProposalValue> values = new ArrayList<>();

		public List<XMLProposalValue> getValues() {
			return values;
		}


		@XmlElement(name = "description")
		String description;
		

		@Override
		public String getDescription() {
			return description;
		}

	}

	public static class XMLProposalData {

		@XmlElement(name = "context")
		private XMLProposalContext context = new XMLProposalContext();

		public XMLProposalContext getContext() {
			return context;
		}

		@XmlElement(name = "element")
		private List<XMLProposalElement> elements = new ArrayList<>();

		public List<XMLProposalElement> getElements() {
			return elements;
		}

		@XmlElement(name = "value")
		private List<XMLProposalValue> values = new ArrayList<>();

		public List<XMLProposalValue> getValues() {
			return values;
		}

	}

	/**
	 * Represents a child entry (a leave)
	 * 
	 * @author albert
	 *
	 */
	public static class XMLProposalValue implements XMLProposalDescribed {

		@XmlAttribute(name = "code")
		String code;

		public String getCode() {
			return code;
		}

		@XmlAttribute(name = "maxOccurrence")
		Integer maxOccurrence ;

		public Integer getMaxOccurrence() {
			return maxOccurrence;
		}

		@XmlElement(name = "description")
		String description;

		@Override
		public String getDescription() {
			return description;
		}

	}

	public static interface XMLProposalDescribed {
		public String getDescription();
	}

}
