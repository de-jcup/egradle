package de.jcup.egradle.eclipse.gradleeditor.outline;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.token.Token;

public class GradleEditorOutlineLabelProvider extends BaseLabelProvider implements ILabelProvider {

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return super.isLabelProperty(element, property);
	}

	@Override
	public Image getImage(Object element) {
		// if (element instanceof Closure){
		// Closure c = (Closure) element;
		// if (c.getName().indexOf("task ")!=-1){
		// return EGradleUtil.getImage("icons/gradle-og.gif");
		// }
		// }
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element == null) {
			return "null";
		}
		if (element instanceof Item) {
			Item item = (Item) element;
			return item.getText();
		} else {
			if (element instanceof Token) {
				Token gelement = (Token) element;
				return gelement.getValue();
			}
			return element.toString();
		}
	}


}
