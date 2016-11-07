package org.freejava.browser;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;
import org.freejava.browser.log.ILogger;
import org.freejava.browser.model.Model;

public class View extends ViewPart {
	public static final String ID = "org.freejava.browser.view";

	private FormToolkit toolkit;
	private ScrolledForm form;

	// form controls
	private Text input1Text;
	private Text input2Text;
	private Button okButton;
	//boolean error = false;

	public String getInput1(){
		return input1Text.getText();
	}
	public String getInput2(){
		return input2Text.getText();
	}
	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);

		// EmployeeDetail form title
		form.setText("Please fill in the form.");
		toolkit.decorateFormHeading(form.getForm());

		// EmployeeSearch form content
		fillBody(form, toolkit);

		// Make sure the borders are drawn
		toolkit.paintBordersFor(form.getBody());
	}

	private void fillBody(ScrolledForm form, FormToolkit toolkit) {
		TableWrapLayout layout = new TableWrapLayout();
		form.getBody().setLayout(layout);
		layout.numColumns = 2;

		Composite parent = form.getBody();

		// Input 1
		toolkit.createLabel(parent, "Input 1");
		input1Text = toolkit.createText(parent, ""); //$NON-NLS-1$
		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		input1Text.setLayoutData(td);

		// Input 2
		toolkit.createLabel(parent, "Input 2");
		input2Text = toolkit.createText(parent, ""); //$NON-NLS-1$
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		input2Text.setLayoutData(td);


		// Search button
		okButton = toolkit.createButton(parent, "OK", SWT.PUSH);
		td = new TableWrapData(TableWrapData.CENTER);
		td.colspan = 2;
		okButton.setLayoutData(td);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ILogger logger = new RCPLogger();
				logger.logInfo("Processing...", null);
				Model model = new Model();
				try {
					Map<String, String> allProperties = new HashMap<String, String>(BeanUtils.describe(View.this));
					String[] ignores = new String[] {
							"site",
							"contentDescription",
							"orientation",
							"partName",
							"titleToolTip",
							"class",
							"viewSite",
                            "partProperties",
                            "title",
                            "titleImage"
					};
					for (String excludeKey : ignores) allProperties.remove(excludeKey);
					model.setProperties(allProperties);
				} catch (Exception e) {
					logger.logError("Critical error!", e);
				}
				model.execute();
				logger.logInfo("Result:" + model.getResult(), null);
				logger.logInfo("Finished.", null);
			}
		});

	}

	public void setErrorStatusLine(final String message) {
    	Activator.getDefault().getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				getViewSite().getActionBars().getStatusLineManager().setErrorMessage(message);
			}
		});
    }

	public void setMessageStatusLine(final String message) {
    	Activator.getDefault().getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				getViewSite().getActionBars().getStatusLineManager().setMessage(message);
			}
		});
    }

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		input1Text.setFocus();
	}
}