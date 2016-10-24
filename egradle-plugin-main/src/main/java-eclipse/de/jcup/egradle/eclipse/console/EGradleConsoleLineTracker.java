package de.jcup.egradle.eclipse.console;

import java.util.List;

import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import de.jcup.egradle.core.process.RememberLastLinesOutputHandler;
import de.jcup.egradle.eclipse.api.EGradleUtil;

public class EGradleConsoleLineTracker implements IConsoleLineTracker {

	private IDocument document;
	private RememberLastLinesOutputHandler rememberOutputHandler;

	public EGradleConsoleLineTracker() {
	}

	@Override
	public void init(IConsole console) {
		EGradleUtil.removeAllValidationErrorsOfConsoleOutput();
		
		rememberOutputHandler = EGradleUtil.createOutputHandlerForValidationErrorsOnConsole();
		document = console.getDocument();
	}

	@Override
	public void lineAppended(IRegion line) {
		if (rememberOutputHandler==null){
			return;
		}
		if (document == null) {
			return;
		}
		try {
			String lineStr = document.get(line.getOffset(), line.getLength());
			System.out.println("line appended:" + lineStr);
			if (lineStr.startsWith("Total time")) {
				/* ok . time to validate */
				List<String> list = rememberOutputHandler.createOutputToValidate();
				EGradleUtil.showValidationErrorsOfConsoleOutput(list);
				rememberOutputHandler=null;
			}else{
				rememberOutputHandler.output(lineStr);
			}
		} catch (BadLocationException e) {
			/* ignore */
		}
	}

	@Override
	public void dispose() {
		document = null;
	}

}
