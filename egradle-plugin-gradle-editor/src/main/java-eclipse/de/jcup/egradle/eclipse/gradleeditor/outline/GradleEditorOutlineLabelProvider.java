package de.jcup.egradle.eclipse.gradleeditor.outline;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

import de.jcup.egradle.core.outline.OutlineItem;
import de.jcup.egradle.core.outline.OutlineItemType;
import de.jcup.egradle.core.outline.OutlineModifier;
import de.jcup.egradle.core.token.Token;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.gradleeditor.Activator;

public class GradleEditorOutlineLabelProvider extends BaseLabelProvider implements ILabelProvider {

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return super.isLabelProperty(element, property);
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof OutlineItem) {
			OutlineItem item = (OutlineItem) element;
			OutlineItemType type = item.getItemType();
			switch (type) {
			case VARIABLE:
				OutlineModifier modifier = item.getModifier();
				String path = null;
				switch(modifier){
				case PRIVATE:
					path = "/icons/outline/private_co.png";
					break;
				case PROTECTED:
					path = "/icons/outline/protected_co.png";
					break;
				case PUBLIC:
					path = "/icons/outline/public_co.png";
					break;
				case DEFAULT:
					path = "/icons/outline/default_co.png";
					break;
					default:
						return null;
				}
				return EGradleUtil.getImage(path, Activator.PLUGIN_ID);
			case CLOSURE:
				
			default:
				return null;
			}
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element == null) {
			return "null";
		}
		if (element instanceof OutlineItem) {
			StringBuilder sb = new StringBuilder();
			OutlineItem outlineItem = (OutlineItem) element;
			sb.append(outlineItem.getName());
			
			String type = outlineItem.getType();
			if (type!=null){
				sb.append(":");
				sb.append(type);
			}
			
			String info = outlineItem.getInfo();
			if (info == null) {
				return sb.toString();
			}
			sb.append('[');
			sb.append(info);
			sb.append(']');
			return sb.toString();
		} else {
			if (element instanceof Token) {
				Token gelement = (Token) element;
				return gelement.getValue();
			}
			return element.toString();
		}
	}

}
