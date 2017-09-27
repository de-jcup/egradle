package de.jcup.egradle.eclipse.document;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.ui.texteditor.MarkerAnnotation;

public class GroovyEditorAnnotationHoover extends DefaultAnnotationHover {
	@Override
	protected boolean isIncluded(Annotation annotation) {
		if (annotation instanceof MarkerAnnotation) {
			return true;
		}
		/* we do not support other annotations */
		return false;
	}
}