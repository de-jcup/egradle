/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
 package de.jcup.egradle.eclipse.ide.execution.validation;

import org.eclipse.jface.preference.FieldEditor;
/**
 * Abstract base implementation which does nothing per default and handles no property changes
 * @author Albert Tregnaghi
 *
 */
public abstract class RootProjectValidationAdapter implements RootProjectValidationHandler{

		@Override
		public void addFieldEditor(FieldEditor field) {
			
		}

		@Override
		public void handleValidationResult(boolean b) {
			
		}

		@Override
		public void handleValidationRunning(boolean running) {
			
		}

		@Override
		public void onValidationStateChanged(boolean valid) {
			
		}

		@Override
		public boolean isHandlingPropertyChanges() {
			return false;
		}
		
	}