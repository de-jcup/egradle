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

/**
 * Observes validation running on EGradle root project validation
 * 
 * @author Albert Tregnaghi
 *
 */
public interface RootProjectValidationObserver {

    /**
     * Handle valdiation is running or not
     * 
     * @param running
     */
    public void handleValidationRunning(boolean running);

    /**
     * Handle validation result
     * 
     * @param valid - validation result
     */
    public void handleValidationResult(boolean valid);

}