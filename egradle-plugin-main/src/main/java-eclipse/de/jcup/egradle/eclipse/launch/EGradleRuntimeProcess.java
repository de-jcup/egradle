package de.jcup.egradle.eclipse.launch;

import java.util.Map;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.core.model.RuntimeProcess;

public class EGradleRuntimeProcess extends RuntimeProcess implements IStreamListener {

	public EGradleRuntimeProcess(ILaunch launch, Process process, String name, Map<String, String> attributes) {
		super(launch, process, name, attributes);
	}

	@Override
	protected IStreamsProxy createStreamsProxy() {
		return super.createStreamsProxy();
	}

	@Override
	public void streamAppended(String text, IStreamMonitor monitor) {
	}

}