package com.napol.koltsegvetes.dialogs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class ClustersDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	protected JTextField clNameField, clDescriptionField;
	@SuppressWarnings("rawtypes")
	protected JComboBox clDirectionBox;
	protected JPanel fieldsPanel, buttonsPanel;

	protected boolean cancelled;

	public ClustersDialog(Frame parent) throws Exception {
		super(parent, true);
		this.setSize(550, 150);

		this.setTitle("Adding a new row to Clusters table");

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				actionCancel();
			}
		});

		fieldsPanel = new JPanel();
		GridBagConstraints constraints;
		GridBagLayout layout = new GridBagLayout();
		fieldsPanel.setLayout(layout);

		JLabel clNameLabel = new JLabel("Cluster name:");
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(clNameLabel, constraints);
		fieldsPanel.add(clNameLabel);

		clNameField = new JTextField();
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(5, 5, 0, 0);
		constraints.weightx = 1.0D;
		layout.setConstraints(clNameField, constraints);
		fieldsPanel.add(clNameField);

		JLabel clDescriptionLabel = new JLabel("Cluster's description:");
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(clDescriptionLabel, constraints);
		fieldsPanel.add(clDescriptionLabel);

		clDescriptionField = new JTextField();
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(5, 5, 0, 0);
		constraints.weightx = 1.0D;
		layout.setConstraints(clDescriptionField, constraints);
		fieldsPanel.add(clDescriptionField);

		// Integer[] directionValues = new Integer(2);

		JLabel clDirectionLabel = new JLabel("Cluster Direction: ");
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(clDirectionLabel, constraints);
		fieldsPanel.add(clDirectionLabel);

		String[] directionValues = { "", "Income", "Expense" };
		clDirectionBox = new JComboBox<String>(directionValues);

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(clDirectionBox, constraints);
		fieldsPanel.add(clDirectionBox);

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
		if (clNameField.getText().trim().length() < 1
				|| clDescriptionField.getText().trim().length() < 1) {
			JOptionPane.showMessageDialog(this,
					"One or more fields are missing.", "Missing Field(s)",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (clDirectionBox.getSelectedIndex() == 0) {
			JOptionPane
					.showMessageDialog(
							this,
							"Direction is not selected! Please select cluster direction!",
							"Direction is not selected...",
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

	public String getClusterName() {
		return clNameField.getText();
	}

	public String getDescription() {
		return clDescriptionField.getText();
	}

	public String getDirection() {
		if (clDirectionBox.getSelectedItem().toString().equals("Income"))
			return "1";
		else
			return "-1";
	}
}
