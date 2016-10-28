package de.jcup.egradle.eclipse.execution.validation;

import org.eclipse.jface.preference.FieldEditor;
/**
 * Abstract base implementation which does nothing per default and handles no property changes
 * @author Albert Tregnaghi
 *
 */
public abstract class ExecutionConfigDelegateAdapter implements ExecutionConfigDelegate{

		@Override
		public void handleCheckState() {
			
		}

		@Override
		public void handleFieldEditorAdded(FieldEditor field) {
			
		}

		@Override
		public void handleOriginRootProject(String stringValue) {
			
		}

		@Override
		public void handleValidationResult(boolean b) {
			
		}

		@Override
		public void handleValidationRunning(boolean running) {
			
		}

		@Override
		public void handleValidationStateChanges(boolean valid) {
			
		}

		@Override
		public boolean isHandlingPropertyChanges() {
			return false;
		}
		
	}