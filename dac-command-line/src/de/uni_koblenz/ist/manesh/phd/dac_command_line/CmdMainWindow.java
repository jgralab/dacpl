package de.uni_koblenz.ist.manesh.phd.dac_command_line;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.BundleException;

import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.features.IllegalFeatureSelectionException;
import de.uni_koblenz.ist.manesh.phd.dac_config.port.function.IFeatureSelect;
import de.uni_koblenz.ist.manesh.phd.dac_controller.ports.manage.IAdapation;
import de.uni_koblenz.ist.manesh.phd.dac_swt_host.ChildShell;

public class CmdMainWindow implements ChildShell {
	final private static Logger log = Logger.getLogger("MainWindow");
	private Text cmdText;
	private StyledText outputTextBox;
	// private Display display;
	private Shell shell;
	private final IAdapation mManage;
	private final IFeatureSelect mFeatureSel;

	public CmdMainWindow(IAdapation manage, IFeatureSelect featureSel) {
		mManage = manage;
		mFeatureSel = featureSel;
	}

	/**
	 * Open the window.
	 */
	@Override
	public Shell createShell(Shell parent) {
		shell = new Shell(parent, SWT.RESIZE | SWT.TITLE);
		shell.setSize(450, 600);
		shell.setText("Command Line");
		shell.setLayout(new GridLayout(2, false));
		final Label lblScript = new Label(shell, SWT.NONE);
		lblScript.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		lblScript.setText("Command");

		cmdText = new Text(shell, SWT.BORDER | SWT.SINGLE);
		cmdText.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == '\r') {
					executeCmd();
				}
			}
		});
		cmdText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2,
				1));

		final Label lblOutput = new Label(shell, SWT.NONE);
		lblOutput.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		lblOutput.setText("Output");

		outputTextBox = new StyledText(shell, SWT.BORDER | SWT.V_SCROLL);
		outputTextBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 2, 1));
		outputTextBox.setEditable(false);

		final Button btnReload = new Button(shell, SWT.NONE);
		btnReload.setText("Execute");
		new Label(shell, SWT.NONE);
		btnReload.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event event) {
				executeCmd();
			}
		});
		shell.open();
		shell.layout();

		return shell;
	}

	private void executeCmd() {
		final String line = cmdText.getText().trim();
		final String[] chunks = line.split(" ");
		if (chunks.length == 0)
			return;
		final String cmd = chunks[0];

		if ("pushModelState".equalsIgnoreCase(cmd)) {
			mManage.pushModelState();
			output("Model state pushed.");
		} else if ("popModelState".equalsIgnoreCase(cmd)) {
			mManage.popModelState();
			output("Model state popped.");
		} else if ("plotModel".equalsIgnoreCase(cmd)) {
			mManage.plotModelState();
			output("Model plotted.");
		} else if ("addPerson".equalsIgnoreCase(cmd)) {
			if (chunks.length < 3) {
				output("Not enough arguments.");
			} else {
				final String name = chunks[1];
				final String role = chunks[2];
				mManage.addPerson(name, role);
				output("Person added.");
			}
		} else if ("addRole".equalsIgnoreCase(cmd)) {
			if (chunks.length < 2) {
				output("Not enough arguments.");
			} else {
				final String name = chunks[1];
				mManage.addRole(name);
				output("Role added.");
			}
		} else if ("removePerson".equalsIgnoreCase(cmd)) {
			if (chunks.length < 2) {
				output("Not enough arguments.");
			} else {
				final String name = chunks[1];
				if (mManage.removePerson(name)) {
					output("Success");
				} else {
					output("Nothing changed.");
				}
			}
		} else if ("removeRole".equalsIgnoreCase(cmd)) {
			if (chunks.length < 2) {
				output("Not enough arguments.");
			} else {
				final String name = chunks[1];
				if (mManage.removeRole(name)) {
					output("Success");
				} else {
					output("Nothing changed.");
				}
			}
		} else if ("adaptRulesForVIP".equalsIgnoreCase(cmd)) {
			mManage.adaptRulesForVIP();
			output("Model adapted for VIP.");
		} else if("selectFeatures".equalsIgnoreCase(cmd)) {
			String[] args = new String[chunks.length - 1];
			System.arraycopy(chunks, 1, args, 0, args.length);
			try {
				mFeatureSel.selectFeatures(args);
				output("Success");
			} catch (IllegalFeatureSelectionException e) {
				output(e.getMessage());
			} catch (BundleException e) {
				output(e.getMessage());
			}
		} else if("listPersons".equalsIgnoreCase(cmd)) {
			output(mManage.listPersons());
		} else if("listRoles".equalsIgnoreCase(cmd)) {
			output(mManage.listRoles());
		} else {
			printHelp();
		}

		cmdText.setText("");
	}

	private void printHelp() {
		for (Method m : IAdapation.class.getMethods()) {
			output(m.getName());
		}

		for (Method m : IFeatureSelect.class.getMethods()) {
			output(m.getName());
		}
	}

	private void output(String string) {
		outputTextBox.append(string);
		outputTextBox.append(System.lineSeparator());
		outputTextBox.setTopIndex(outputTextBox.getLineCount() - 1);
	}

}
