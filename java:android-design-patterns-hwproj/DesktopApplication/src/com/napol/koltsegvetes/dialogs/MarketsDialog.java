package com.napol.koltsegvetes.dialogs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class MarketsDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	protected JTextField mkIDField, mkNameField;
	protected JPanel fieldsPanel, buttonsPanel;

	protected boolean cancelled;

	public MarketsDialog(Frame parent) throws Exception {
		super(parent, true);
		this.setSize(550, 150);

		this.setTitle("Adding a new row to Markets table...");

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				actionCancel();
			}
		});

		fieldsPanel = new JPanel();
		GridBagConstraints constraints;
		GridBagLayout layout = new GridBagLayout();
		fieldsPanel.setLayout(layout);

		JLabel mkIDLabel = new JLabel("Market ID:");
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(mkIDLabel, constraints);
		fieldsPanel.add(mkIDLabel);

		mkIDField = new JTextField();
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(5, 5, 0, 0);
		constraints.weightx = 1.0D;
		layout.setConstraints(mkIDField, constraints);
		fieldsPanel.add(mkIDField);

		JLabel mkNameLabel = new JLabel("Market's name:");
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(mkNameLabel, constraints);
		fieldsPanel.add(mkNameLabel);

		mkNameField = new JTextField();
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(5, 5, 0, 0);
		constraints.weightx = 1.0D;
		layout.setConstraints(mkNameField, constraints);
		fieldsPanel.add(mkNameField);

		initializeButtonsPanel();

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(fieldsPanel, BorderLayout.NORTH);
		getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

		setLocationRelativeTo(parent);
	}

	private void initializeButtonsPanel() {
		buttonsPanel = new JPanel();
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionSave();
			}
		});
		buttonsPanel.add(saveButton);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionCancel();
			}
		});
		buttonsPanel.add(cancelButton);
	}

	protected void actionSave() {
		if (mkIDField.getText().trim().length() < 1
				|| mkNameField.getText().trim().length() < 1) {
			JOptionPane.showMessageDialog(this,
					"One or more fields are missing.", "Missing Field(s)",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		dispose();
	}

	protected void actionCancel() {
		cancelled = true;
		dispose();
	}

	public boolean display() {
		setVisible(true);
		return !cancelled;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public String getMarketID() {
		return mkIDField.getText();
	}

	public String getMarketName() {
		return mkNameField.getText();
	}
}
