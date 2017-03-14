package de.jcup.egradle.codeassist;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.ItemType;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.core.model.ModelInspector;

/**
 * This factory creates proposals by fetching all available variables AND assignments at given offset
 * @author albert
 *
 */
public class VariableNameProposalFactory extends AbstractProposalFactory {

	@Override
	protected Set<Proposal> createProposalsImpl(int offset, ProposalFactoryContentProvider contentProvider) {
		Model model = contentProvider.getModel();
		if (model==null){
			return null;
		}
		Item modelNode = model.getItemAt(offset);
		if (modelNode==null){
			return null;
		}
		Set<Proposal> proposals = new LinkedHashSet<>();
		
		/* very easy (silly) first approach - just collect all variables without handling visibility etc.*/
		ModelInspector inspector = new ModelInspector();
		List<Item> allVariables = inspector.findAllItemsOfType(ItemType.VARIABLE, modelNode);
		for (Item variableItem: allVariables){
			proposals.add(new ItemProposalImpl(variableItem));
		}
		List<Item> allAssignments = inspector.findAllItemsOfType(ItemType.ASSIGNMENT, modelNode);
		for (Item assignmentItem: allAssignments){
			proposals.add(new ItemProposalImpl(assignmentItem));
		}
		List<Item> allDefinedTasks = inspector.findAllItemsOfType(ItemType.TASK,modelNode);
		for (Item definedTaskItem: allDefinedTasks){
			proposals.add(new ItemProposalImpl(definedTaskItem));
		}
		return proposals;
	}

	
	
}
