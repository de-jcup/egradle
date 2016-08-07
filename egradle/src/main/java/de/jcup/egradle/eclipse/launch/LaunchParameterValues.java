package de.jcup.egradle.eclipse.launch;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.IParameterValues;

public class LaunchParameterValues implements IParameterValues {

	private Map<Object, Object> map = new HashMap<>();

	@Override
	public Map<Object,Object> getParameterValues() {
		return map;
	}

}
