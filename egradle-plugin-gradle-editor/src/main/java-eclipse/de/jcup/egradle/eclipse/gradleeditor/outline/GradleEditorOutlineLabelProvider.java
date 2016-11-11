package de.jcup.egradle.eclipse.gradleeditor.outline;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;

import de.jcup.egradle.core.outline.OutlineItem;
import de.jcup.egradle.core.outline.OutlineItemType;
import de.jcup.egradle.core.outline.OutlineModifier;
import de.jcup.egradle.core.token.Token;
import de.jcup.egradle.eclipse.api.ColorManager;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.gradleeditor.Activator;
import de.jcup.egradle.eclipse.gradleeditor.ColorConstants;

public class GradleEditorOutlineLabelProvider extends BaseLabelProvider
		implements IStyledLabelProvider, IColorProvider {

	private static final String ICONS_OUTLINE_DEFAULT_CO_PNG = "/icons/outline/default_co.png";
	private static final String ICONS_OUTLINE_PUBLIC_CO_PNG = "/icons/outline/public_co.png";
	private static final String ICONS_OUTLINE_PROTECTED_CO_PNG = "/icons/outline/protected_co.png";
	private static final String ICONS_OUTLINE_PRIVATE_CO_PNG = "/icons/outline/private_co.png";

	@Override
	public Image getImage(Object element) {
		if (element instanceof OutlineItem) {
			OutlineItem item = (OutlineItem) element;
			OutlineItemType type = item.getItemType();
			switch (type) {
			case VARIABLE:
				OutlineModifier modifier = item.getModifier();
				String path = null;
				switch (modifier) {
				case PRIVATE:
					path = ICONS_OUTLINE_PRIVATE_CO_PNG;
					break;
				case PROTECTED:
					path = ICONS_OUTLINE_PROTECTED_CO_PNG;
					break;
				case PUBLIC:
					path = ICONS_OUTLINE_PUBLIC_CO_PNG;
					break;
				case DEFAULT:
					path = ICONS_OUTLINE_DEFAULT_CO_PNG;
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
	private Styler outlineItemTypeStyler = new Styler() {
		
		@Override
		public void applyStyles(TextStyle textStyle) {
			textStyle.foreground = getColorManager().getColor(ColorConstants.OUTLINE_ITEM__TYPE);
		}
	};
	
	private Styler outlineItemInfoStyler = new Styler() {

		@Override
		public void applyStyles(TextStyle textStyle) {
			textStyle.foreground = getColorManager().getColor(ColorConstants.BRIGHT_BLUE);
		}
	};

	@Override
	public StyledString getStyledText(Object element) {
		StyledString styled = new StyledString();
		if (element == null) {
			styled.append("null");
		}
		if (element instanceof OutlineItem) {
			OutlineItem outlineItem = (OutlineItem) element;
			String name = outlineItem.getName();
			if (name!=null){
				styled.append(name);
			}

			String type = outlineItem.getType();
			if (type != null) {
				StringBuilder sb = new StringBuilder();
				sb.append(":");
				sb.append(type);
				StyledString typeString = new StyledString(sb.toString(), outlineItemTypeStyler);
				styled.append(typeString);
			}

			String info = outlineItem.getInfo();
			if (info != null) {
				StringBuilder sb = new StringBuilder();
				sb.append("  [");
				sb.append(info);
				sb.append(']');
				
				StyledString infoString = new StyledString(sb.toString(), outlineItemInfoStyler);
				styled.append(infoString);
			}
		} else {
			if (element instanceof Token) {
				Token gelement = (Token) element;
				return styled.append(gelement.getValue());
			}
			return styled.append(element.toString());
		}
		return styled;
	}


	private ColorManager getColorManager() {
		return Activator.getDefault().getColorManager();
	}

	@Override
	public Color getForeground(Object element) {
		return getColorManager().getColor(ColorConstants.BLACK);
	}

	@Override
	public Color getBackground(Object element) {
		return null;
	}

}
