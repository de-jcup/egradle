package de.jcup.egradle.eclipse.ide;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import de.jcup.egradle.core.process.EGradleShellType;
import de.jcup.egradle.eclipse.ide.execution.validation.RootProjectValidationAdapter;
import de.jcup.egradle.eclipse.ide.ui.RootProjectConfigMode;
import de.jcup.egradle.eclipse.ide.ui.RootProjectConfigUIDelegate;
import de.jcup.egradle.eclipse.preferences.EGradleCallType;

public class EGradleProjectPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

	private RootProjectConfigUIDelegate configUIDelegate;
	private EGradleProjectPropertyPageDataSupport support;

	public EGradleProjectPropertyPage() {
		this(null);
		
	}

	public EGradleProjectPropertyPage(EGradleProjectPropertyPageDataSupport support) {
		if (support==null){
			support = new PersistedEGradleProjectPropertyPageDataSupport();
		}
		configUIDelegate = new RootProjectConfigUIDelegate(new RootProjectValidationAdapter() {
			@Override
			public void addFieldEditor(FieldEditor field) {
			}
		},RootProjectConfigMode.PREDEFINED_ROOTPROJECT_ONLY);
		
		this.support=support;
	}

	@Override
	public void setElement(IAdaptable element) {
		super.setElement(element);
		
		if (support instanceof PersistedEGradleProjectPropertyPageDataSupport){
			PersistedEGradleProjectPropertyPageDataSupport spx = (PersistedEGradleProjectPropertyPageDataSupport) support;
			IProject project = null;
			if (element!=null){
				project = element.getAdapter(IProject.class);
			}
			spx.setProject(project);
		}
		
	}
	
	@Override
	protected Control createContents(Composite parent) {
		/* main composite*/
		Composite mainComposite = new Composite(parent, SWT.NULL);
		GridData mainCompositeLayoutData = new GridData(SWT.FILL,SWT.FILL,true,true);
		mainComposite.setLayoutData(mainCompositeLayoutData);

		GridLayout mainLayout = new GridLayout(1, false);
		mainComposite.setLayout(mainLayout);
		
		Composite configComposite = new Composite(parent, SWT.NULL);
		
		GridLayout configLayout = new GridLayout(1, false);
		configLayout.horizontalSpacing = 5;
		configLayout.verticalSpacing = 5;		
		
		/* config composite */
		configComposite.setLayout(configLayout);
		configComposite.setLayoutData(mainCompositeLayoutData);
		
		configUIDelegate.createConfigUI(configComposite);
		EGradleCallType callType = support.getCallType();
		String callTypeId = null;
		if (callType==null){
			callType=EGradleCallType.getOSDependentDefaultCallType();
		}
		callTypeId=callType.getId();
		
		configUIDelegate.setRootProjectPath(support.getRootProjectPath());
		configUIDelegate.setGlobalJavaHomePath(support.getJavaHomePath());
		configUIDelegate.setGradleCallCommand(support.getGradleCallCommand());
		configUIDelegate.setGradleBinInstallFolder(support.getGradleBinInstallFolder());
		
		EGradleShellType shellType = support.getShellType();
		if (shellType==null){
			shellType=EGradleShellType.NONE;
		}
		configUIDelegate.setShellId(shellType.getId());
		
		configUIDelegate.setGradleCallTypeId(callTypeId);
		
		return configComposite;
	}

}
