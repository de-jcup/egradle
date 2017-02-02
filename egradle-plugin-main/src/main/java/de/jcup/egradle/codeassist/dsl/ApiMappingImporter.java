package de.jcup.egradle.codeassist.dsl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

public class ApiMappingImporter {

	/**
	 * Imports mapping 
	 * @param stream
	 * @return map never <code>null</code>. Map keys are shortnames, values are long names
	 * @throws IOException 
	 */
	public Map<String,String> importMapping(InputStream stream) throws IOException{
		Map<String,String> map = new TreeMap<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String line = "";
		while ((line=reader.readLine())!=null){
			/* file contains as following "shortName:longName;" */
			String[] parts = StringUtils.split(line, ":;");
			if (parts==null){
				continue;
			}
			if (parts.length<2){
				continue;
			}
			map.put(parts[0],parts[1]);
		}
		return map;
	}

}
