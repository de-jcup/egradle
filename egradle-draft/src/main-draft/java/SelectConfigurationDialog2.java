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
 package de.jcup.egradle.eclipse.ui;
//Send questions, comments, bug reports, etc. to the authors:

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import de.jcup.egradle.core.text.AbstractTextMatcher;
import de.jcup.egradle.core.text.FilterPatternMatcher;
import de.jcup.egradle.eclipse.Activator;

public class SelectConfigurationDialog2 extends AbstractFilterableTreeQuickDialog<String> {

	private String configuration;

	public SelectConfigurationDialog2(Shell parent, String infoText) {
		super(null, parent, "Select configuration", 400,200,infoText);
	}

	@Override
	protected ITreeContentProvider createTreeContentProvider(IAdaptable adaptable) {
		return new ConfigurationTreeContentProvider();
	}

	@Override
	protected void openSelectionImpl(ISelection selection, String filterText) {
		if (selection instanceof StructuredSelection){
			StructuredSelection ss = (StructuredSelection) selection;
			Object firstElement = ss.getFirstElement();
			if (firstElement!=null){
				configuration=firstElement.toString();
			}
		}else{
			configuration=filterText;
		}
	}

	@Override
	protected String getInitialSelectedItem() {
		Object input = getInput();
		if (input ==null){
			return null;
		}
		return input.toString();
	}

	@Override
	protected FilterPatternMatcher<String> createItemMatcher() {
		return new StringTextMatcher();
	}

	@Override
	protected IBaseLabelProvider createLabelProvider() {
		return null;
	}

	@Override
	protected AbstractTreeViewerFilter<String> createFilter() {
		return new SelectConfigurationTreeViewerFilter();
	}

	@Override
	protected AbstractUIPlugin getUIPlugin() {
		return Activator.getDefault();
	}

	
	private class SelectConfigurationTreeViewerFilter extends AbstractTreeViewerFilter<String>{
		
	}
	
	private class StringTextMatcher extends AbstractTextMatcher<String>{

		@Override
		protected String createItemText(String item) {
			return item;
		}
		
	}
	
	private class ConfigurationTreeContentProvider implements ITreeContentProvider{


		public ConfigurationTreeContentProvider() {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (! (inputElement instanceof List)){
				return new Object[]{};
			}
			List<?> configurations = (List<?>)inputElement;
			return configurations.toArray();
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return null;
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return false;
		}
		
	}

	public String getConfigurationResult() {
		return configuration;
	}
	
}