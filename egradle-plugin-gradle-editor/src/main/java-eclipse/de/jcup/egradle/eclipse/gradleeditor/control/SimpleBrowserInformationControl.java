/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, VersionData 2.0 (the "License");
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
 package de.jcup.egradle.eclipse.gradleeditor.control;

import static de.jcup.egradle.codeassist.dsl.DSLConstants.*;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.jcup.egradle.core.util.History;
import de.jcup.egradle.core.util.StringUtilsAccess;
import de.jcup.egradle.eclipse.gradleeditor.EditorActivator;
import de.jcup.egradle.eclipse.util.EclipseDevelopmentSettings;
import de.jcup.egradle.eclipse.util.EclipseUtil;

/**
 * EGradles own simple browser information control, inspired by
 * "BrowserInformationControl"}. Because the eclipse control is internal an own
 * implementation was necessary.
 */
public class SimpleBrowserInformationControl extends AbstractInformationControl {

	private static boolean browserAvailabilityChecked;
	private static boolean swtBrowserCanBeUsed;
	private static Point cachedScrollBarSize;
	private String currentHTML;
	private Browser browser;
	private BrowserEGradleLinkListener browserEGradleLinkListener;
	private boolean linksEnabled;
	private OpenWindowListener windowListener;
	private LocationListener locationListener;
	private History<String> history;
	private Action browsBackitem;
	private OpenInExternalBrowserAction openInBrowserAction;

	/**
	 * Creates an simple browser information control being resizable, providing
	 * NO toolbar and does NOT support hyper link listening
	 * 
	 * @param parentShell
	 */
	public SimpleBrowserInformationControl(Shell parentShell) {
		super(parentShell, true);
		this.history = new History<>(0);
		this.linksEnabled = false;
		create();
	}

	/**
	 * Creates an simple browser information control being resizable, providing
	 * a toolbar and uses hyperlink listener
	 * 
	 * @param parentShell
	 * @param maximumHistory
	 *            maximum amount of history items
	 */
	public SimpleBrowserInformationControl(Shell parentShell, int maximumHistory) {
		super(parentShell, new ToolBarManager());
		this.history = new History<>(maximumHistory);
		this.linksEnabled = true;
		create();
	}

	public void setBrowserEGradleLinkListener(BrowserEGradleLinkListener browserEGradleLinkListener) {
		this.browserEGradleLinkListener = browserEGradleLinkListener;
	}

	@Override
	public void setBackgroundColor(Color background) {
		super.setBackgroundColor(background);
		if (isBrowserNotDisposed()) {
			browser.setBackground(background);
		}
	}

	@Override
	public void setForegroundColor(Color foreground) {
		super.setForegroundColor(foreground);
		if (isBrowserNotDisposed()) {
			browser.setForeground(foreground);
		}

	}

	@Override
	public void setInformation(String information) {
		if (isDebugEnabled()) {
			debug("set information=" + StringUtilsAccess.abbreviate(information, 40));
		}
		if (information == null) {
			information = "";
		}
		if (isBrowserNotDisposed()) {
			boolean hasHtmlElementInside = information.startsWith("<html");
			StringBuilder htmlSb = new StringBuilder();
			if (!hasHtmlElementInside) {
				htmlSb.append("<html><body>");
			}
			htmlSb.append(information);
			if (!hasHtmlElementInside) {
				htmlSb.append("</body></html>");
			}
			this.currentHTML = htmlSb.toString();
			if (history.isEmpty()) {
				if (isDebugEnabled()) {
					debug("add to history intial");
				}
				/*
				 * this means it's the initial page. To go back to the page by
				 * history it is necessary to add current HTML to history too!
				 * All other history entries are done by link mechanism.
				 */
				history.add(currentHTML);
				if (isDebugEnabled()) {
					debug("now:" + history.toString());
				}
			}
			browser.setText(information);
		}
	}

	public void redraw() {
		if (isBrowserNotDisposed()) {
			browser.redraw();
		}
	}

	/**
	 * Tells whether this control is available for given parent composite
	 * 
	 * @param parent
	 *            the parent component used for checking or <code>null</code> if
	 *            none
	 * @return <code>true</code> if this control is available
	 */
	public static boolean isAvailableFor(Composite parent) {
		if (!browserAvailabilityChecked) {
			try {
				Browser browser = new Browser(parent, SWT.NONE);
				browser.dispose();
				swtBrowserCanBeUsed = true;

				/* compute scrollbar size */
				Slider sliderV = new Slider(parent, SWT.VERTICAL);
				Slider sliderH = new Slider(parent, SWT.HORIZONTAL);

				int width = sliderV.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
				int height = sliderH.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;

				cachedScrollBarSize = new Point(width, height);

				sliderV.dispose();
				sliderH.dispose();

			} catch (SWTError er) {
				swtBrowserCanBeUsed = false;
			} finally {
				browserAvailabilityChecked = true;
			}
		}

		return swtBrowserCanBeUsed;
	}

	@Override
	public boolean hasContents() {
		return currentHTML != null && currentHTML.length() > 0;
	}

	int getToolbarWidth() {
		assertAvailable();
		return cachedScrollBarSize.x;
	}

	int getToolbarHeight() {
		assertAvailable();
		return cachedScrollBarSize.x;
	}

	@Override
	protected void createContent(Composite parent) {
		assertAvailable();
		browser = new Browser(parent, SWT.FILL);
		browser.setJavascriptEnabled(false);

		if (linksEnabled) {

			openInBrowserAction = new OpenInExternalBrowserAction();
			openInBrowserAction.setImageDescriptor(EclipseUtil.createImageDescriptor("icons/codecompletion/external_browser.png", EditorActivator.PLUGIN_ID));
			openInBrowserAction.setToolTipText("Open this page in external browser");
			
			browsBackitem = new Action() {
				@Override
				public void run() {
					goBack();
					updateActions();
					if (isDebugEnabled()) {
						debug("go back-after:\n" + history.toString());
					}
				}

				private void goBack() {
					if (isDebugEnabled()) {
						debug("go back-before:\n" + history.toString());
					}
					if (isBrowserNotDisposed()) {
						if (history.isEmpty()) {
							return;
						}
						String current = history.goBack();
						if (isDebugEnabled()) {
							debug("history.current was " + current);
						}
						String backContent = history.goBack();
						if (isDebugEnabled()) {
							debug("history.back=" + backContent);
						}
						if (backContent == null) {
							return;
						}
						if (browserEGradleLinkListener.isAcceptingHyperlink(backContent)) {
							if (isDebugEnabled()) {
								debug("history detected for type, so use link handler");
							}
							browserEGradleLinkListener.onEGradleHyperlinkClicked(SimpleBrowserInformationControl.this,
									backContent);
							history.add(backContent);// add types again to
														// history so still
														// current again.
							return;

						} else if (backContent.startsWith(HYPERLINK_HTTP_PREFIX)) {
							if (isDebugEnabled()) {
								debug("history detected for real html page, so use browser history");
							}
							if (browser.isBackEnabled()) {
								/*
								 * okay, a real website was shown, so try to go
								 * back
								 */
								browser.back();
								return;
							} else {
								if (isDebugEnabled()) {
									debug("browser back not enabled!");
								}
								/*
								 * retry - can happen when first html page shown
								 */
								goBack();
								return;
							}
						} else {
							if (isDebugEnabled()) {
								debug("no type or http protocoll, so simply set information");
							}
							setInformation(backContent);
						}
					}
				}

			};

			ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
			ImageDescriptor back = sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK);
			browsBackitem.setImageDescriptor(back);
			updateActions();
			ToolBarManager toolBarManager = getToolBarManager();
			toolBarManager.add(browsBackitem);
			toolBarManager.add(openInBrowserAction);
			toolBarManager.update(true);
		}

		windowListener = new OpenWindowListener() {
			@Override
			public void open(WindowEvent event) {
				event.required = true; // Cancel opening of new windows
			}
		};
		locationListener = new LocationAdapter() {

			@Override
			public void changing(LocationEvent event) {
				if (isDebugEnabled()) {
					debug("changing location:" + event);
				}
				if (event.location == null) {
					event.doit = false;
				} else if (event.location == "about:blank") {
					event.doit = false;
				} else if (event.location.startsWith(HYPERLINK_TYPE_PREFIX)) {
					event.doit = false;
				}
				if (isDebugEnabled()) {
					debug("changed location(1):" + event);
				}
				if (!linksEnabled) {
					if (isDebugEnabled()) {
						debug("changed location(1b): no linksEnabled - so guard closing");
					}
					return;
				}
				if (isDebugEnabled()) {
					debug("changed location(2)-links are enabled");
				}
				String newLocation = event.location;
				if (newLocation == null) {
					if (isDebugEnabled()) {
						debug("changed location(2b): no new location set - guard close");
					}
					return;
				}
				if (browserEGradleLinkListener != null) {
					SimpleBrowserInformationControl control = SimpleBrowserInformationControl.this;
					if (browserEGradleLinkListener.isAcceptingHyperlink(newLocation)) {
						if (isBrowserNotDisposed()) {
							if (isDebugEnabled()) {
								debug("changed location(3)-add to history:" + newLocation);
							}
							history.add(newLocation);
							if (isDebugEnabled()) {
								debug("now:" + history.toString());
							}
							if (isDebugEnabled()) {
								debug("changed location(3b)-calling gradle hyperlink listener");
							}
							browserEGradleLinkListener.onEGradleHyperlinkClicked(control, newLocation);
						}
					} else if (newLocation.startsWith(HYPERLINK_HTTP_PREFIX)) {
						if (isDebugEnabled()) {
							debug("changed location(4)-add history:" + newLocation);
						}
						history.add(newLocation);
						if (isDebugEnabled()) {
							debug("now:" + history.toString());
						}
					}
				}
				updateActions();
			}
		};

		browser.addOpenWindowListener(windowListener);
		browser.addLocationListener(locationListener);

		/* disable browser menu - except when development set*/
		boolean disableBrowserMenu = !EclipseDevelopmentSettings.DEBUG_ADD_SPECIAL_MENUS;

		if (disableBrowserMenu){
			browser.setMenu(new Menu(getShell(), SWT.NONE));
		}

	}

	protected void updateActions() {
		String potentialtarget = history.current();
		if (potentialtarget!=null && potentialtarget.startsWith("http")){
			openInBrowserAction.setTarget(potentialtarget);
			openInBrowserAction.setEnabled(true);
		}else{
			openInBrowserAction.setEnabled(false);
			openInBrowserAction.setTarget(null);
		}
		updateBrowsBackAction();
	}

	private void updateBrowsBackAction() {
		if (browsBackitem == null) {
			return;
		}
		if (history == null) {
			return;
		}
		browsBackitem.setEnabled(history.getCount() > 1);
		browsBackitem.setText("back (" + history.getCount() + "/" + history.getMax() + ")");
	}

	private void debug(String text) {
		System.out.println(getClass().getSimpleName() + ":" + text);
	}

	private boolean isDebugEnabled() {
		return EclipseDevelopmentSettings.DEBUG_ADD_SPECIAL_LOGGING;
	}

	@Override
	public void dispose() {
		super.dispose();
		history.clear();
		if (isBrowserNotDisposed()) {
			browser.removeOpenWindowListener(windowListener);
			browser.removeLocationListener(locationListener);

			browser.dispose();
		}
	}

	private boolean isBrowserNotDisposed() {
		return browser != null && !browser.isDisposed();
	}

	private void assertAvailable() {
		if (!browserAvailabilityChecked) {
			throw new IllegalStateException("Availability not checked before!");
		}
		if (!swtBrowserCanBeUsed) {
			throw new IllegalStateException("Availibility was checked but SWT browser cannot be used!");
		}
	}

	@Override
	public IInformationControlCreator getInformationPresenterControlCreator() {
		/*
		 * Its necessary to implement this - otherwise leaving the origin
		 * tooltip area, which does not contain the browser control, will hide
		 * the control. It would be nicer if eclipse would have an easier
		 * mechanism to reuse the origin control!
		 */
		return new IInformationControlCreator() {
			@Override
			public IInformationControl createInformationControl(Shell parent) {
				SimpleBrowserInformationControl newControl = new SimpleBrowserInformationControl(parent, 20);
				newControl.setBrowserEGradleLinkListener(browserEGradleLinkListener);
				return newControl;
			}
		};
	}
}
