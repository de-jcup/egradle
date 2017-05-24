package de.jcup.egradle.eclipse.ide.wizards;

import de.jcup.egradle.template.FileStructureTemplate;

public interface NewProjectContext {

	FileStructureTemplate getSelectedTemplate();

	void setSelectedTemplate(FileStructureTemplate selectedTemplate);

}
