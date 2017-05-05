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
package de.jcup.egradle.eclipse.gradleeditor.outline;


import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;

import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.ItemType;
import de.jcup.egradle.core.model.Modifier;
import de.jcup.egradle.eclipse.api.ColorManager;
import de.jcup.egradle.eclipse.api.EclipseUtil;
import de.jcup.egradle.eclipse.api.EclipseDevelopmentSettings;
import de.jcup.egradle.eclipse.gradleeditor.EditorActivator;
import de.jcup.egradle.eclipse.gradleeditor.GradleEditorColorConstants;

public class GradleEditorOutlineLabelProvider extends BaseLabelProvider
		implements IStyledLabelProvider, IColorProvider {

	private static final String ICON_ALL_PROJECTS = "prj_mode.png";
	private static final String ICON_PROJECT = "prj_obj.png";
	private static final String ICON_ZIP= "zip.png";
	private static final String ICON_ECLIPSE = "eclipse16.png";
	private static final String ICON_CONFIGURE="externalize.png";
	private static final String ICON_APPLY_FROM_PNG = "apply_from.png";
	private static final String ICON_APPLY_PLUGIN_PNG = "plugins.png";
	private static final String ICON_BUILDSCRIPT = "cheatsheet_item_obj.png";
	private static final String ICON_CLASS = "class_obj.png";
	private static final String ICON_INTERFACE = "int_obj.png";
	private static final String ICON_ENUM = "enum_obj.png";
	private static final String ICON_SOURCESET = "source.png";// "impl_co.png";
	private static final String ICON_CLEAN = "clear.png";// "removea_exc.png";
	private static final String ICON_CLOSURE = "closure-parts.png";// "all_sc_obj.png";
	private static final String ICON_CONFIGURATIONS = "th_single.png";
	private static final String ICON_DEPENDENCIES = "module_view.png";
	private static final String ICON_DEPENDENCY = "imp_obj.png";
	private static final String ICON_ASSIGNMENT = "assignment.png";
	private static final String ICON_DO_FIRST = "doFirst.png";
	private static final String ICON_DO_LAST = "doLast.png";
	private static final String ICON_PACKAGE = "package_obj.png";
	private static final String ICON_PRIVATE_CO_PNG = "private_co.png";
	private static final String ICON_PROTECTED_CO_PNG = "protected_co.png";
//	private static final String ICON_DEFAULT_CO_PNG = "default_co.png";
	private static final String ICON_PUBLIC_CO_PNG = "public_co.png";
	
	/* fields */
	private static final String ICON_PRIVATE_FIELD_PNG = "field_private_obj.png";
	private static final String ICON_PROTECTED_FIELD_PNG = "field_protected_obj.png";
	// private static final String ICON_DEFAULT_FIELD_PNG = "field_default_obj.png";
	private static final String ICON_PUBLIC_FIELD_PNG = "field_public_obj.png";
	
	/* other */
	private static final String ICON_REPOSITORIES = "memory_view.png";
	private static final String ICON_REPOSITORY = "imp_obj.png";
	private static final String ICON_SUB_PROJECTS = "prj_mode.png";
	private static final String ICON_TASK_PNG = "gradle-task.png";
	private static final String ICON_TASKS_PNG = "th_single.png";
	private static final String ICON_TEST = "test.png";
	private static final String ICON_MAIN = "runtime_obj.png";
	private static final String ICON_JAR = "jar_obj.png";
	private static final String ICON_WAR = "warFile_obj.gif";
	private static final String ICON_EAR = "ear.gif";
	private static final String ICON_AFTER_EVALUATE = "afterEvaluate.png";
	private static final String ICON_UNKNOWN = "unknown_obj.png";

	
	
	private Styler outlineItemConfigurationStyler = new Styler() {

		@Override
		public void applyStyles(TextStyle textStyle) {
			textStyle.foreground = getColorManager().getColor(GradleEditorColorConstants.DARK_GRAY);
		}
	};

	private Styler outlineItemInfoStyler = new Styler() {

		@Override
		public void applyStyles(TextStyle textStyle) {
			textStyle.foreground = getColorManager().getColor(GradleEditorColorConstants.BRIGHT_BLUE);
		}
	};

	private Styler outlineItemTargetStyler = new Styler() {

		@Override
		public void applyStyles(TextStyle textStyle) {
			textStyle.foreground = getColorManager().getColor(GradleEditorColorConstants.DARK_GRAY);
		}
	};

	private Styler outlineItemTypeStyler = new Styler() {

		@Override
		public void applyStyles(TextStyle textStyle) {
			textStyle.foreground = getColorManager().getColor(GradleEditorColorConstants.OUTLINE_ITEM__TYPE);
		}
	};

	@Override
	public Color getBackground(Object element) {
		return null;
	}

	@Override
	public Color getForeground(Object element) {
		return null;// getColorManager().getColor(GradleEditorColorConstants.BLACK);
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof Item) {
			Item item = (Item) element;
			ItemType type = item.getLastChainedItemType();
			
			if (type==null){
				type = item.getItemType();
			}
			if (type==null){
				return null;
			}
			
			Modifier modifier = item.getModifier();
			String path = null;
			switch (type) {
			case ASSIGNMENT:
				path =  ICON_ASSIGNMENT;
				return getOutlineImage(path);
			case VARIABLE:
				switch (modifier) {
				case PRIVATE:
					path = ICON_PRIVATE_FIELD_PNG;
					break;
				case PROTECTED:
					path = ICON_PROTECTED_FIELD_PNG;
					break;
				case PUBLIC:
					path = ICON_PUBLIC_FIELD_PNG;
					break;
				case DEFAULT:
					path = ICON_PUBLIC_FIELD_PNG;// default in groovy IS PUBLIC!
												// ICON_DEFAULT_CO_PNG;
					break;
				default:
					return null;
				}
				return getOutlineImage(path);
			case CONSTRUCTOR:
			case METHOD:
				switch (modifier) {
				case PRIVATE:
					path = ICON_PRIVATE_CO_PNG;
					break;
				case PROTECTED:
					path = ICON_PROTECTED_CO_PNG;
					break;
				case PUBLIC:
					path = ICON_PUBLIC_CO_PNG;
					break;
				case DEFAULT:
					path = ICON_PUBLIC_CO_PNG;// default in groovy IS PUBLIC!
												// ICON_DEFAULT_FIELD_PNG;
					break;
				default:
					return null;
				}
				return getOutlineImage(path);
			case TASK:
				return getOutlineImage(ICON_TASK_PNG);
			case TASKS:
				return getOutlineImage(ICON_TASKS_PNG);
			case APPLY_FROM:
				return getOutlineImage(ICON_APPLY_FROM_PNG);
			case APPLY_PLUGIN:
				return getOutlineImage(ICON_APPLY_PLUGIN_PNG);
			case REPOSITORIES:
				return getOutlineImage(ICON_REPOSITORIES);
			case JAR:
				return getOutlineImage(ICON_JAR);
			case WAR:
				return getOutlineImage(ICON_WAR);
			case EAR:
				return getOutlineImage(ICON_EAR);
			case AFTER_EVALUATE:
				return getOutlineImage(ICON_AFTER_EVALUATE);
			case ALL_PROJECTS:
				return getOutlineImage(ICON_ALL_PROJECTS);
			case SUB_PROJECTS:
				return getOutlineImage(ICON_SUB_PROJECTS);
			case PROJECT:
				return getOutlineImage(ICON_PROJECT);
			case ECLIPSE:
				return getOutlineImage(ICON_ECLIPSE);
			case CONFIGURE:
				return getOutlineImage(ICON_CONFIGURE);
			case ZIP:
				return getOutlineImage(ICON_ZIP);
			case DEPENDENCIES:
				return getOutlineImage(ICON_DEPENDENCIES);
			case DEPENDENCY:
				return getOutlineImage(ICON_DEPENDENCY);
			case MAIN:
				return getOutlineImage(ICON_MAIN);
			case TEST:
				return getOutlineImage(ICON_TEST);
			case CLEAN:
				return getOutlineImage(ICON_CLEAN);
			case SOURCESETS:
				return getOutlineImage(ICON_SOURCESET);
			case CLOSURE:
				return getOutlineImage(ICON_CLOSURE);
			case BUILDSCRIPT:
				return getOutlineImage(ICON_BUILDSCRIPT);
			case CONFIGURATIONS:
				return getOutlineImage(ICON_CONFIGURATIONS);
			case REPOSITORY:
				return getOutlineImage(ICON_REPOSITORY);
			case CLASS:
				return getOutlineImage(ICON_CLASS);
			case INTERFACE:
				return getOutlineImage(ICON_INTERFACE);
			case ENUM:
				return getOutlineImage(ICON_ENUM);
			case ENUM_CONSTANT:
				return getOutlineImage(ICON_PUBLIC_FIELD_PNG);
			case PACKAGE:
				return getOutlineImage(ICON_PACKAGE);
			case DO_FIRST:
				return getOutlineImage(ICON_DO_FIRST);
			case DO_LAST:
				return getOutlineImage(ICON_DO_LAST);
			default:
				return getOutlineImage(ICON_UNKNOWN);
			}
		}
		return null;
	}

	@Override
	public StyledString getStyledText(Object element) {
		StyledString styled = new StyledString();
		if (element == null) {
			styled.append("null");
		}
		if (element instanceof Item) {
			Item item = (Item) element;

			String configuration = item.getConfiguration();
			if (configuration != null) {
				StringBuilder sb = new StringBuilder();
				sb.append(configuration);
				sb.append(" ");
				styled.append(new StyledString(sb.toString(), outlineItemConfigurationStyler));
			}

			String name = item.getName();
			if (name != null) {
				styled.append(name);
				handleParameters(styled, item);
			}

			String type = item.getType();
			if (type != null) {
				StringBuilder sb = new StringBuilder();
				sb.append(" :");
				sb.append(type);
				StyledString typeString = new StyledString(sb.toString(), outlineItemTypeStyler);
				styled.append(typeString);
			}
			String target = item.getTarget();
			if (target != null) {
				StyledString targetString = new StyledString(":" + target, outlineItemTargetStyler);
				styled.append(targetString);
			}

			String info = item.getInfo();
			if (info != null) {
				StringBuilder sb = new StringBuilder();
				sb.append("  [");
				sb.append(info);
				sb.append(']');

				StyledString infoString = new StyledString(sb.toString(), outlineItemInfoStyler);
				styled.append(infoString);
			}
			if (EclipseDevelopmentSettings.DEBUG_ADD_SPECIAL_TEXTS) {
				StringBuilder sb = new StringBuilder();
				sb.append(" --[");
				sb.append(item.getItemType());
				String[] parameters = item.getParameters();
				if (parameters!=null && parameters.length>0){
					sb.append(", params={");
					boolean first=true;
					for (String param: parameters){
						if (!first){
							sb.append(", ");
						}
						first=false;
						sb.append(param);
					}
					sb.append('}');
				}
				sb.append(']');
				StyledString debugString = new StyledString(sb.toString(), outlineItemConfigurationStyler);
				styled.append(debugString);
			}
		} else {
			return styled.append(element.toString());
		}

		return styled;
	}

	private void handleParameters(StyledString styled, Item item) {
		/* show add parameters if existing */
		boolean paramsMustBeShown=false;
		
		ItemType itemType = item.getItemType();
		paramsMustBeShown=paramsMustBeShown || ItemType.METHOD.equals(itemType);
		paramsMustBeShown=paramsMustBeShown || ItemType.CONSTRUCTOR.equals(itemType);
		
		if (!paramsMustBeShown){
			return;
		}
		String[] parameters = item.getParameters();
		if (parameters == null) {
			return;
		}
		int length = parameters.length;
		styled.append("(");
		if (parameters != null && length > 0) {
			int last = length - 1;
			for (int i = 0; i < length; i++) {
				String parameter = parameters[i];
				if (parameter == null) {
					continue;
				}
				styled.append(parameter);
				if (i != last) {
					styled.append(',');
				}
			}
		}
		styled.append(")");
	}

	public ColorManager getColorManager() {
		EditorActivator editorActivator = EditorActivator.getDefault();
		if (editorActivator == null) {
			return ColorManager.getStandalone();
		}
		return editorActivator.getColorManager();
	}

	
	
	private Image getOutlineImage(String name) {
		return EclipseUtil.getImage("/icons/outline/" + name, EditorActivator.PLUGIN_ID);
	}

}
