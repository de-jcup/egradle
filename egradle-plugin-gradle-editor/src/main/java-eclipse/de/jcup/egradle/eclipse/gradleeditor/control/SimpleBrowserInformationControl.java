package de.jcup.egradle.eclipse.gradleeditor.control;

import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;

/**
 * EGradles own simple browser information control, inspired by
 * "BrowserInformationControl"}. Because the eclipse control is internal an
 * own implementation was necessary.
 */
public class SimpleBrowserInformationControl extends AbstractInformationControl {

	private static boolean browserAvailabilityChecked;
	private static boolean swtBrowserCanBeUsed;
	private static Point cachedScrollBarSize;
	private String information;
	private Browser browser;

	public SimpleBrowserInformationControl(Shell parentShell) {
		super(parentShell, true);
		create();
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

	public void add(LocationListener locationListener) {
		if (isBrowserNotDisposed()) {
			browser.addLocationListener(locationListener);
		}
	}

	public void remove(LocationListener locationListener) {
		if (isBrowserNotDisposed()) {
			browser.removeLocationListener(locationListener);
		}
	}

	@Override
	public void setInformation(String information) {
		this.information = information;
		if (isBrowserNotDisposed()) {
			StringBuilder htmlSb = new StringBuilder();
			htmlSb.append("<html><body>");
			htmlSb.append(information);
			htmlSb.append("</body></html>");

			boolean success = browser.setText(htmlSb.toString());
			if (!success){
				/* FIXME ATR, 01.02.2017: implement better.. and remove sysout */
				System.out.println("error cannot set text");
			}
		}
	}

	public void redraw(){
		if (isBrowserNotDisposed()){
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
		return information != null && information.length() > 0;
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

		browser.addOpenWindowListener(new OpenWindowListener() {
			@Override
			public void open(WindowEvent event) {
				event.required = true; // Cancel opening of new windows
			}
		});
		/* disable browser menu */
		browser.setMenu(new Menu(getShell(), SWT.NONE));

	}

	@Override
	public void dispose() {
		super.dispose();
		if (isBrowserNotDisposed()) {
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
				/*
				 * TODO ATR: maybe provide another control here in future ?
				 * -with control back /forward ?
				 */
				return new SimpleBrowserInformationControl(parent);
			}
		};
	}
}
