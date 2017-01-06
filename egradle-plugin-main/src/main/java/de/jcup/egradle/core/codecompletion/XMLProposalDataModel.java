package de.jcup.egradle.core.codecompletion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "model")
public class XMLProposalDataModel {

	@XmlAttribute(name = "id")
	String id;
	
	private boolean prepared;

	public String getId() {
		return id;
	}

	/**
	 * Ensure model is prepared (pathes calculated etc.) - if already prepared, nothing happens
	 */
	public void ensurePrepared(){
		if (!prepared){
			prepare();
		}
	}
	
	private Map<String, Set<XMLProposalElement>> elementsByPathMap = new TreeMap<>();
	
	/**
	 * Returns a set containing all suitable {@link XMLProposalElement} instances
	 * @param path
	 * @return set of proposal elements, never <code>null</code>
	 */
	public Set<XMLProposalElement> getElementsByPath(String path) {
		Set<XMLProposalElement> set = elementsByPathMap.get(path);
		if (set==null){
			return Collections.emptySet();
		}
		return set;
	}

	private void prepare() {
		prepared=true;
		/* calulate Ids */
		for (XMLProposalData data: proposals){
			List<XMLProposalRootPathEntry> rootPathEntries = data.getContext().getRootPathEntries();
			calculateAndAddPathesToMap(data, null, rootPathEntries, data.getElements());
		}
		
	}

	private void calculateAndAddPathesToMap(XMLProposalData data ,StringBuilder parentSB, List<XMLProposalRootPathEntry> rootPathEntries, List<XMLProposalElement> elements) {
		for (XMLProposalElement element:elements){
			StringBuilder sb = new StringBuilder();
			if (parentSB!=null){
				sb.append(parentSB);
				sb.append('.');
			}
			sb.append(element.getName());
			calculateAndAddPathesToMap(data, sb, rootPathEntries, element.getElements());
			for (XMLProposalRootPathEntry rootPath : rootPathEntries){
				StringBuilder uidSb = new StringBuilder();
				uidSb.append(rootPath.path);
				uidSb.append(sb);
				String uid = uidSb.toString();
				
				Set<XMLProposalElement> set = elementsByPathMap.get(uid);
				if (set == null){
					set = new LinkedHashSet<>();
					elementsByPathMap.put(uid, set);
				}
				set.add(element);
			}
		}
	}

	@XmlElement(name = "proposal")
	private List<XMLProposalData> proposals = new ArrayList<>();

	public List<XMLProposalData> getProposals() {
		return proposals;
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
