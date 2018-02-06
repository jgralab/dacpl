package de.uni_koblenz.ist.manesh.phd.dac_event_generator.gui;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import de.uni_koblenz.ist.manesh.phd.dac_event_generator.driver.EvaluationListener;
import de.uni_koblenz.ist.manesh.phd.dac_event_generator.driver.EvaluatorThread;
import de.uni_koblenz.ist.manesh.phd.dac_event_generator.driver.EventScript;
import de.uni_koblenz.ist.manesh.phd.dac_event_generator.driver.SyntaxError;
import de.uni_koblenz.ist.manesh.phd.dac_swt_host.ChildShell;
import de.uni_koblenz.ist.manesh.phd.dac_swt_host.ScriptLoader;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.EventBus;

public class EventEditorMainWindow implements EvaluationListener, ChildShell {
	final private static Logger log = Logger.getLogger("MainWindow");
	private StyledText scriptTextBox;
	private StyledText outputTextBox;
	private final EvaluatorThread evaluator;
	private Display display;

	public EventEditorMainWindow(EventBus bus) {
		evaluator = new EvaluatorThread();
		evaluator.setEventBus(bus);
		evaluator.setEvaluationListener(this);
		evaluator.start();
	}

	@Override
	public void onEventReady(
			final de.uni_koblenz.ist.manesh.phd.dac_event_generator.driver.Event event) {
		// int start = scriptTextBox.getOffsetAtLine(event.lineNumber);
		// scriptTextBox.setSelection(start, start+10);
		// scriptTextBox.setLineBackground(event.lineNumber, 1,
		// display.getSystemColor(SWT.COLOR_YELLOW));
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				scriptTextBox.setLineBackground(event.lineNumber - 1, 1,
						display.getSystemColor(SWT.COLOR_YELLOW));
			}
		});
	}

	private void output(String string) {
		outputTextBox.append(string);
		outputTextBox.append(System.getProperty("line.separator"));
		outputTextBox.setTopIndex(outputTextBox.getLineCount() - 1);
	}

	private void reloadScript() {
		scriptTextBox.setLineBackground(0, scriptTextBox.getLineCount(),
				display.getSystemColor(SWT.COLOR_WHITE));

		try {
			final EventScript script = EventScript.compile(scriptTextBox
					.getText());
			evaluator.setScript(script);
			output("OK.");
		} catch (final SyntaxError e) {
			log.severe(e.getMessage());
			output(e.getMessage());
		}
	}

	private void runStep() {
		if (evaluator.step()) {
			output("Next step...");
		} else {
			output("Still busy...");
		}
	}

	@Override
	public Shell createShell(Shell parent) {
		display = Display.getDefault();
		final Shell shell = new Shell(parent, SWT.RESIZE | SWT.TITLE);
		shell.setSize(450, 450);
		shell.setText("Event Generator");
		shell.setLayout(new GridLayout(2, false));
		final Label lblScript = new Label(shell, SWT.NONE);
		lblScript.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		lblScript.setText("Script");

		scriptTextBox = new StyledText(shell, SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		scriptTextBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 2, 1));
		scriptTextBox.setText(ScriptLoader.load(getClass(), "events.script"));

		final Label lblOutput = new Label(shell, SWT.NONE);
		lblOutput.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		lblOutput.setText("Output");

		outputTextBox = new StyledText(shell, SWT.BORDER);
		outputTextBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 2, 1));
		outputTextBox.setEditable(false);

		final Button btnReload = new Button(shell, SWT.NONE);
		btnReload.setText("Reload");
		btnReload.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event event) {
				reloadScript();
			}
		});

		final Button btnPlay = new Button(shell, SWT.NONE);
		btnPlay.setText("Play");
		btnPlay.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event event) {
				runStep();
			}
		});
		shell.open();
		shell.layout();
		reloadScript();

		return shell;
	}
}
