package de.jcup.egradle.core.process;

import de.jcup.egradle.core.domain.CancelStateProvider;

public interface ProcessContext {
	
	public CancelStateProvider getCancelStateProvider();
}
