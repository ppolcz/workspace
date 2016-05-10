package com.napol.koltsegvetes.dialogs;

import java.awt.*;
import java.awt.event.*;
import java.util.GregorianCalendar;
import javax.swing.*;

public class TransactionDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	protected JTextField trRemarkField, amountField;
	@SuppressWarnings("rawtypes")
	protected JComboBox caNameBox, clNameBox, hourBox, minuteBox, mkIDBox,
			yearBox, monthBox, dayBox;
	protected JPanel dateTimePanel, buttonsPanel;
	protected static String[] months = { "January", "February", "March",
			"April", "May", "June", "July", "August", "September", "October",
			"November", "December" };

	protected boolean cancelled;

	public TransactionDialog(Frame parent, String[] caName, String[] clName,
			String[] mkID, int actyear, int actmonth, int actday)
			throws Exception {
		super(parent, true);
		this.setSize(550, 250);
		String name = "";
		String sum = "";

		this.setTitle("Adding a new row to Transactions table");

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				actionCancel();
			}
		});

		JPanel fieldsPanel = new JPanel();
		GridBagConstraints constraints;
		GridBagLayout layout = new GridBagLayout();
		fieldsPanel.setLayout(layout);

		JLabel sumLabel = new JLabel("Amount:");
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(sumLabel, constraints);
		fieldsPanel.add(sumLabel);

		amountField = new JTextField(sum);
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(5, 5, 0, 0);
		constraints.weightx = 1.0D;
		layout.setConstraints(amountField, constraints);
		fieldsPanel.add(amountField);

		JLabel nameLabel = new JLabel("What was it:");
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(nameLabel, constraints);
		fieldsPanel.add(nameLabel);

		trRemarkField = new JTextField(name);
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(trRemarkField, constraints);
		fieldsPanel.add(trRemarkField);

		JLabel cAccountLabel = new JLabel("Charged account:");
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(cAccountLabel, constraints);
		fieldsPanel.add(cAccountLabel);

		String[] caNames = new String[caName.length + 1];
		caNames[0] = "";
		for (int i = 0; i < caName.length; i++) {
			caNames[i + 1] = caName[i];
		}

		caNameBox = new JComboBox<String>(caNames);
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(caNameBox, constraints);
		fieldsPanel.add(caNameBox);

		JLabel clLabel = new JLabel("Category:");
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(clLabel, constraints);
		fieldsPanel.add(clLabel);

		String[] clNames = new String[clName.length + 1];
		clNames[0] = "";
		for (int i = 0; i < clName.length; i++) {
			clNames[i + 1] = clName[i];
		}

		clNameBox = new JComboBox<String>(clNames);
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(clNameBox, constraints);
		fieldsPanel.add(clNameBox);

		JLabel mkLabel = new JLabel("Where was it:");
		constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(mkLabel, constraints);
		fieldsPanel.add(mkLabel);

		String[] mkNames = new String[mkID.length + 1];
		mkNames[0] = "";
		for (int i = 0; i < mkID.length; i++) {
			mkNames[i + 1] = mkID[i];
		}
		mkIDBox = new JComboBox<String>(mkNames);
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets = new Insets(5, 5, 0, 0);
		layout.setConstraints(mkIDBox, constraints);
		fieldsPanel.add(mkIDBox);

		initializeDateTimePanel(actyear, actmonth, actday);
		initializeButtonsPanel();

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(fieldsPanel, BorderLayout.NORTH);
		getContentPane().add(dateTimePanel, BorderLayout.CENTER);
		getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

		setLocationRelativeTo(parent);
	}

	private void initializeDateTimePanel(int actyear, int actmonth, int actday) {
		dateTimePanel = new JPanel();
		GridBagConstraints dtconstraints;
		GridBagLayout dtlayout = new GridBagLayout();
		dateTimePanel.setLayout(dtlayout);

		JLabel timeLabel = new JLabel("Time:");
		dtconstraints = new GridBagConstraints();
		dtconstraints.anchor = GridBagConstraints.EAST;
		dtconstraints.insets = new Insets(5, 5, 0, 0);
		dtlayout.setConstraints(timeLabel, dtconstraints);
		dateTimePanel.add(timeLabel);

		JLabel hourLabel = new JLabel("Hour:");
		dtconstraints.fill = GridBagConstraints.HORIZONTAL;
		dtconstraints.gridwidth = GridBagConstraints.RELATIVE;
		dtconstraints.insets = new Insets(5, 5, 0, 0);
		dtlayout.setConstraints(hourLabel, dtconstraints);
		dateTimePanel.add(hourLabel);

		hourBox = new JComboBox<Integer>();
		Integer[] hours = new Integer[24];
		for (int i = 0; i < 24; i++) {
			hours[i] = (i);
		}
		hourBox = new JComboBox<Integer>(hours);
		hourBox.setSelectedItem(12);

		dtconstraints = new GridBagConstraints();
		dtconstraints.fill = GridBagConstraints.HORIZONTAL;
		dtconstraints.gridwidth = GridBagConstraints.RELATIVE;
		dtconstraints.insets = new Insets(5, 5, 0, 0);
		dtlayout.setConstraints(hourBox, dtconstraints);
		dateTimePanel.add(hourBox);

		JLabel minuteLabel = new JLabel("Minute:");
		dtconstraints.fill = GridBagConstraints.HORIZONTAL;
		dtconstraints.gridwidth = GridBagConstraints.RELATIVE;
		dtconstraints.insets = new Insets(5, 5, 0, 0);
		dtlayout.setConstraints(minuteLabel, dtconstraints);
		dateTimePanel.add(minuteLabel);

		minuteBox = new JComboBox<Integer>();
		Integer[] minutes = new Integer[60];
		for (int i = 0; i < 60; i++) {
			minutes[i] = (i);
		}
		minuteBox = new JComboBox<Integer>(minutes);
		minuteBox.setSelectedItem(30);

		dtconstraints = new GridBagConstraints();
		dtconstraints.fill = GridBagConstraints.HORIZONTAL;
		dtconstraints.gridwidth = GridBagConstraints.REMAINDER;
		dtconstraints.insets = new Insets(5, 5, 0, 0);
		dtlayout.setConstraints(minuteBox, dtconstraints);
		dateTimePanel.add(minuteBox);

		JLabel dateLabel = new JLabel("Date:");
		dtconstraints = new GridBagConstraints();
		dtconstraints.anchor = GridBagConstraints.EAST;
		dtconstraints.insets = new Insets(5, 5, 0, 0);
		dtlayout.setConstraints(dateLabel, dtconstraints);
		dateTimePanel.add(dateLabel);

		JLabel yearLabel = new JLabel("Year:");
		dtconstraints.fill = GridBagConstraints.HORIZONTAL;
		dtconstraints.gridwidth = GridBagConstraints.RELATIVE;
		dtconstraints.insets = new Insets(5, 5, 0, 0);
		dtlayout.setConstraints(yearLabel, dtconstraints);
		dateTimePanel.add(yearLabel);

		Integer[] years = new Integer[110];
		for (int i = 0; i < 110; i++) {
			years[i] = (actyear - 10 + i);
		}
		yearBox = new JComboBox<Integer>(years);
		yearBox.setSelectedItem(actyear);

		dtconstraints = new GridBagConstraints();
		dtconstraints.fill = GridBagConstraints.HORIZONTAL;
		dtconstraints.gridwidth = GridBagConstraints.RELATIVE;
		dtconstraints.insets = new Insets(5, 5, 0, 0);
		dtlayout.setConstraints(yearBox, dtconstraints);
		dateTimePanel.add(yearBox);

		JLabel monthLabel = new JLabel("Month:");
		dtconstraints.fill = GridBagConstraints.HORIZONTAL;
		dtconstraints.gridwidth = GridBagConstraints.RELATIVE;
		dtconstraints.insets = new Insets(5, 5, 0, 0);
		dtlayout.setConstraints(monthLabel, dtconstraints);
		dateTimePanel.add(monthLabel);

		monthBox = new JComboBox<String>(months);
		monthBox.setSelectedItem(actmonth);

		dtconstraints = new GridBagConstraints();
		dtconstraints.fill = GridBagConstraints.HORIZONTAL;
		dtconstraints.gridwidth = GridBagConstraints.RELATIVE;
		dtconstraints.insets = new Insets(5, 5, 0, 0);
		dtlayout.setConstraints(monthBox, dtconstraints);
		dateTimePanel.add(monthBox);

		JLabel dayLabel = new JLabel("Day:");
		dtconstraints.fill = GridBagConstraints.HORIZONTAL;
		dtconstraints.gridwidth = GridBagConstraints.RELATIVE;
		dtconstraints.insets = new Insets(5, 5, 0, 0);
		dtlayout.setConstraints(dayLabel, dtconstraints);
		dateTimePanel.add(dayLabel);

		Integer[] days = new Integer[new GregorianCalendar(
				(int) yearBox.getSelectedItem(), monthBox.getSelectedIndex(), 1)
				.getActualMaximum(GregorianCalendar.DAY_OF_MONTH)];
		for (int i = 1; i <= days.length; i++) {
			days[i - 1] = i;
		}
		dayBox = new JComboBox<Integer>(days);
		dayBox.setSelectedItem(actday);

		dtconstraints = new GridBagConstraints();
		dtconstraints.fill = GridBagConstraints.HORIZONTAL;
		dtconstraints.gridwidth = GridBagConstraints.RELATIVE;
		dtconstraints.insets = new Insets(5, 5, 0, 0);
		dtlayout.setConstraints(dayBox, dtconstraints);
		dateTimePanel.add(dayBox);
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
		if (trRemarkField.getText().trim().length() < 1
				|| amountField.getText().trim().length() < 1) // ||
																// where.getText().trim().length()
																// < 1)
		{
			JOptionPane.showMessageDialog(this,
					"One or more fields are missing.", "Missing Field(s)",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (amountField.getText().matches(".*[^0-9].*")) {
			JOptionPane
					.showMessageDialog(
							this,
							"Amount field contains invalid character(s). Allowed characters are: [0-9].",
							"Invalid character(s)...",
							JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (caNameBox.getSelectedIndex() == 0) {
			JOptionPane
					.showMessageDialog(
							this,
							"Charged Account is not selected! Please select an account!",
							"Charged Account is not selected...",
							JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (clNameBox.getSelectedIndex() == 0) {
			JOptionPane.showMessageDialog(this,
					"Cluster name is not selected! Please select a cluster!",
					"Cluster is not selected...", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (mkIDBox.getSelectedIndex() == 0) {
			JOptionPane.showMessageDialog(this,
					"Market is not selected! Please select a market!",
					"Market is not selected...", JOptionPane.ERROR_MESSAGE);
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

	public String getName() {
		return trRemarkField.getText();
	}

	public String getAmount() {
		return amountField.getText();
	}

	public String getMkID() {
		return mkIDBox.getSelectedItem().toString();
	}

	public String getDate() {
		return yearBox.getSelectedItem().toString() + "-"
				+ monthBox.getSelectedIndex() + "-"
				+ dayBox.getSelectedItem().toString() + " "
				+ hourBox.getSelectedItem().toString() + ":"
				+ minuteBox.getSelectedItem().toString();
	}

	public String getCAID() {
		return caNameBox.getSelectedItem().toString();
	}

	public String getCLName() {
		return clNameBox.getSelectedItem().toString();
	}
}
