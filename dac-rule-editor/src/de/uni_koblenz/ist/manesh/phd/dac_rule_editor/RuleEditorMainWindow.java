package de.uni_koblenz.ist.manesh.phd.dac_rule_editor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import de.uni_koblenz.ist.manesh.phd.case_studies.dac.facades.eca_rules.RulesFacade;
import de.uni_koblenz.ist.manesh.phd.dac_swt_host.ChildShell;
import de.uni_koblenz.ist.manesh.phd.dac_swt_host.ScriptLoader;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Entity;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.EventBus;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.EventListener;
import de.uni_koblenz.ist.manesh.phd.dac_system_state_monitor.event.Verb;

public class RuleEditorMainWindow implements EventListener, ChildShell {
	final private static Logger log = Logger.getLogger("MainWindow");
	private StyledText scriptTextBox;
	private StyledText outputTextBox;
	private final RulesFacade mCompilerDecompiler;
	private Shell shlRuleEditor;
	private final EventBus mEventBus;

	public RuleEditorMainWindow(RulesFacade rulesFacade, EventBus bus) {
		mCompilerDecompiler = rulesFacade;
		mEventBus = bus;
		mEventBus.registerListenerForVerb(Verb.ADAPTS, this);
	}

	@Override
	public boolean onEvent(Entity subject, String subjectId, Verb verb,
			Entity object, String objectId) {
		final String code = mCompilerDecompiler.decompile();
		scriptTextBox.setText(code);

		return false;
	}

	private void loadScript() {
		final String script = ScriptLoader.load(getClass(), "rules.script");
		scriptTextBox.setText(script);
		reloadScript();
	}

	private void output(String string) {
		outputTextBox.append(string);
		outputTextBox.append(System.getProperty("line.separator"));
		outputTextBox.setTopIndex(outputTextBox.getLineCount() - 1);
	}

	private void reloadScript() {
		final InputStream is = new ByteArrayInputStream(scriptTextBox.getText()
				.getBytes());
		try {
			mCompilerDecompiler.compile(is, "UTF-8");
			output("OK");
		} catch (final Exception e) {
			output(e.getMessage());
		} finally {
			try {
				is.close();
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public Shell createShell(Shell parent) {
		shlRuleEditor = new Shell(parent, SWT.RESIZE | SWT.TITLE);
		shlRuleEditor.addListener(SWT.CLOSE, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (mEventBus != null) {
					mEventBus.unregisterListener(RuleEditorMainWindow.this);
				}
			}
		});
		shlRuleEditor.setSize(450, 600);
		shlRuleEditor.setText("Rule Editor");
		shlRuleEditor.setLayout(new GridLayout(2, false));
		final Label lblScript = new Label(shlRuleEditor, SWT.NONE);
		lblScript.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		lblScript.setText("Script");

		scriptTextBox = new StyledText(shlRuleEditor, SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		scriptTextBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 2, 1));

		final Label lblOutput = new Label(shlRuleEditor, SWT.NONE);
		lblOutput.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		lblOutput.setText("Output");

		outputTextBox = new StyledText(shlRuleEditor, SWT.BORDER);
		outputTextBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 2, 1));
		outputTextBox.setEditable(false);

		final Button btnReload = new Button(shlRuleEditor, SWT.NONE);
		btnReload.setText("Reload");
		new Label(shlRuleEditor, SWT.NONE);
		btnReload.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event event) {
				reloadScript();
			}
		});
		shlRuleEditor.open();
		shlRuleEditor.layout();
		loadScript();

		return shlRuleEditor;
	}
}
