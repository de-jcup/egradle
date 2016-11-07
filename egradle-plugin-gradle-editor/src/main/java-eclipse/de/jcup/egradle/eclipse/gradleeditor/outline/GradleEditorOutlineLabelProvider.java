package de.jcup.egradle.eclipse.gradleeditor.outline;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

import de.jcup.egradle.core.model.OutlineModel.Item;
import de.jcup.egradle.core.token.Token;
import de.jcup.egradle.core.token.TokenType;

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
			return getItemText(item);
		} else {
			if (element instanceof Token) {
				Token gelement = (Token) element;
				return gelement.getName();
			}
			return element.toString();
		}
	}

	private String getItemText(Item item) {
		StringBuilder sb = new StringBuilder();
		Token token = item.getToken();
		if (TokenType.BRACE_OPENING.equals(token.getType())) {

			if (token.canGoBackward()) {
				Token tokenBeforeBrace = token.goBackward();
				if (TokenType.BRACKET_OPENING.equals(tokenBeforeBrace.getType())) {
					if (tokenBeforeBrace.canGoBackward()) {
						tokenBeforeBrace = tokenBeforeBrace.goBackward();
					}
				}
				if (TokenType.BRACKET_CLOSING.equals(tokenBeforeBrace.getType())) {
					if (tokenBeforeBrace.canGoBackward()) {
						tokenBeforeBrace = tokenBeforeBrace.goBackward();
					}
				}
				if (tokenBeforeBrace.canGoBackward()) {
					Token before = tokenBeforeBrace.goBackward();
					String beforeName = before.getName();
					if (beforeName.equals("task")) {
						sb.append("task ");
					}
				}
				sb.append(tokenBeforeBrace.getName());
			} else {
				sb.append("|-" + token.getName());
			}
		} else if (TokenType.GSTRING.equals(token.getType()) || TokenType.GSTRING.equals(token.getType())) {
			/* string */
			if (token.canGoBackward()) {
				Token before = token.goBackward();
				if (TokenType.BRACKET_CLOSING.equals(before.getType())){
					return token.getName();
				}
				/* FIXME ATR, 08.11.2016: Some Strings must be filtered in outline model creation! not all strings should appear in outline*/
				String beforeName = before.getName();
				if (beforeName.equals("from:") || beforeName.equals("plugin:")) {
					if (before.canGoBackward()) {
						Token expectedApply = before.goBackward();
						if ("apply".equals(expectedApply.getName())){
							sb.append("apply ");
							sb.append(beforeName);
							sb.append(" ");
						}
					}
				}else{
					/* normally a variable def*/
					if (before.canGoBackward()) {
						Token maybeDef = before.goBackward();
						if ("def".equals(maybeDef.getName())){
							sb.append("def");
						}
					}
					sb.append(beforeName);
					sb.append(" ");
				}
			}

			sb.append(token.getName());
		}
		if (sb.length() == 0) {
			sb.append(token.getName());
		}
		return sb.toString();
	}

}
