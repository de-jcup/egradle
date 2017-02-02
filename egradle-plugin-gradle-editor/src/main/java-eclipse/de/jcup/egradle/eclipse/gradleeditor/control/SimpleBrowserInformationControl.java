package de.jcup.egradle.eclipse.gradleeditor.control;

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

import de.jcup.egradle.core.api.History;

/**
 * EGradles own simple browser information control, inspired by
 * "BrowserInformationControl"}. Because the eclipse control is internal an own
 * implementation was necessary.
 */
public class SimpleBrowserInformationControl extends AbstractInformationControl {

	private static final String REAL_HTML_SITE_IDENTIFIER = "<REAL_HTML_SITE/>";
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
	 * @param style
	 */
	SimpleBrowserInformationControl(Shell parentShell, int maximumHistory) {
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
		browser = new Browser(parent, SWT.FILL);// , SWT.H_SCROLL | SWT.V_SCROLL
												// | SWT.RESIZE);
		browser.setJavascriptEnabled(false);

		if (linksEnabled) {
			Action browsBackitem = new Action() {
				@Override
				public void run() {
					if (isBrowserNotDisposed()) {
						String backContent = history.goBack();
						if (backContent == null) {
							return;
						}
						if (REAL_HTML_SITE_IDENTIFIER.equals(backContent)){
							if (browser.isBackEnabled()) {
								/*
								 * okay, a real website was shown, so try to go back
								 */
								browser.back();
								return;
							}
						}
						setInformation(backContent);
					}
				}

			};

			ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
			ImageDescriptor back = sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK);
			browsBackitem.setText("back");
			browsBackitem.setImageDescriptor(back);
			ToolBarManager toolBarManager = getToolBarManager();
			toolBarManager.add(browsBackitem);
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
				if (event.location == "about:blank") {
					event.doit = false;
				}
			}

			@Override
			public void changed(LocationEvent event) {
				if (!linksEnabled) {
					return;
				}
				String newLocation = event.location;
				if (newLocation == null) {
					return;
				}
				if (browserEGradleLinkListener != null) {
					SimpleBrowserInformationControl control = SimpleBrowserInformationControl.this;
					if (browserEGradleLinkListener.isAcceptingHyperlink(newLocation)) {
						if (isBrowserNotDisposed()) {
							history.add(currentHTML);
							browserEGradleLinkListener.onEGradleHyperlinkClicked(control, newLocation);
						}
					} else if (newLocation.startsWith("http")) {
						/*
						 * a real html site - so add currentHTML to history if
						 * not already present
						 */
						history.add(REAL_HTML_SITE_IDENTIFIER);
					}
				}
			}
		};

		browser.addOpenWindowListener(windowListener);
		browser.addLocationListener(locationListener);

		/* disable browser menu */
		browser.setMenu(new Menu(getShell(), SWT.NONE));

	}

	@Override
	public void dispose() {
		super.dispose();
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
				SimpleBrowserInformationControl newControl = new SimpleBrowserInformationControl(parent, 5);
				newControl.setBrowserEGradleLinkListener(browserEGradleLinkListener);
				return newControl;
			}
		};
	}
}
