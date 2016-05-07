package com.napol.koltsegvetes.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseMotionListener;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class CalendarFrame extends JInternalFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static int openFrameCount = 0;
    static final int xOffset = 30, yOffset = 30;
    
    static String[] months =  {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
	static JLabel lblMonth, lblYear;
	static JButton btnPrev, btnNext, actButton;
	static JTable tblCalendar;
	static JComboBox<String> cmbYear;
	static DefaultTableModel mtblCalendar; //Table model
	static JScrollPane stblCalendar; //The scrollpane
	static JPanel pnlCalendar;
	static int realYear, realMonth, realDay, currentYear, currentMonth, currentDay;
    
    @SuppressWarnings({ "serial" })
	public CalendarFrame() {
        super("Calendar", false, false, false, false);
        try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
		catch (ClassNotFoundException e) {}
		catch (InstantiationException e) {}
		catch (IllegalAccessException e) {}
		catch (UnsupportedLookAndFeelException e) {}

		this.setSize(330,360); 
		removeMouseListener();
		
		//Create controls
		lblMonth = new JLabel ("January");
		lblYear = new JLabel ("Change year:");
		cmbYear = new JComboBox<String>();
		btnPrev = new JButton ("<<");
		btnNext = new JButton (">>");
		actButton = new JButton("akt"); 
		mtblCalendar = new DefaultTableModel(){public boolean isCellEditable(int rowIndex, int mColIndex){return false;}};
		tblCalendar = new JTable(mtblCalendar);
		stblCalendar = new JScrollPane(tblCalendar);
		pnlCalendar = new JPanel(null);

		//Set border
		pnlCalendar.setBorder(BorderFactory.createTitledBorder("Calendar"));
		
		//Register action listeners
		btnPrev.addActionListener(new btnPrev_Action());
		btnNext.addActionListener(new btnNext_Action());
		cmbYear.addActionListener(new cmbYear_Action());
		
		//Add controls to pane
		this.add(pnlCalendar);
		pnlCalendar.add(lblMonth);
		pnlCalendar.add(lblYear);
		pnlCalendar.add(cmbYear);
		pnlCalendar.add(btnPrev);
		pnlCalendar.add(btnNext);
		pnlCalendar.add(stblCalendar);
		
		//Set bounds
		pnlCalendar.setBounds(0, 0, 320, 335);
		lblMonth.setBounds(85, 25, 100, 25);
		lblYear.setBounds(10, 305, 80, 20);
		cmbYear.setBounds(85, 305, 80, 20);
		btnPrev.setBounds(10, 25, 50, 25);
		btnNext.setBounds(140, 25, 50, 25);
		stblCalendar.setBounds(10, 50, 300, 250);
		
		//Make frame visible
		//frmMain.setResizable(false);
		this.setVisible(true);

		
		//Get real month/year
		GregorianCalendar cal = new GregorianCalendar(); //Create calendar
		realDay = cal.get(GregorianCalendar.DAY_OF_MONTH); //Get day
		realMonth = cal.get(GregorianCalendar.MONTH); //Get month
		realYear = cal.get(GregorianCalendar.YEAR); //Get year
		currentDay = realDay;
		currentMonth = realMonth; //Match month and year
		currentYear = realYear;
		
		//Add headers
		String[] headers = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"}; //All headers
		for (int i=0; i<7; i++){
			mtblCalendar.addColumn(headers[i]);
		}
		
		tblCalendar.getParent().setBackground(tblCalendar.getBackground()); //Set background

		//No resize/reorder
		tblCalendar.getTableHeader().setResizingAllowed(false);
		tblCalendar.getTableHeader().setReorderingAllowed(false);

		//Single cell selection
	////	tblCalendar.setColumnSelectionAllowed(true);
	//	tblCalendar.setRowSelectionAllowed(true);
		//tblCalendar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		//Set row/column count
		tblCalendar.setRowHeight(38);
		mtblCalendar.setColumnCount(7);
		mtblCalendar.setRowCount(6);

		
		//Populate table
		for (int i=realYear-100; i<=realYear+100; i++){
			cmbYear.addItem(String.valueOf(i));
		}
		
		//Refresh calendar
		refreshCalendar (realMonth, realYear); //Refresh calendar
        setLocation(0, 0);
    }
    
    private void removeMouseListener() {
    	BasicInternalFrameUI ui = (BasicInternalFrameUI) this.getUI();
    	   Component northPane = ui.getNorthPane();
    	   MouseMotionListener[] motionListeners = (MouseMotionListener[]) northPane.getListeners(MouseMotionListener.class);

    	   for (MouseMotionListener listener: motionListeners)
    	      northPane.removeMouseMotionListener(listener);
    	   }

	public static void refreshCalendar(int month, int year){
		int nod, som; //Number Of Days, Start Of Month
		//Allow/disallow buttons
		btnPrev.setEnabled(true);
		btnNext.setEnabled(true);
		if (month == 0 && year <= realYear-10){btnPrev.setEnabled(false);} //Too early
		if (month == 11 && year >= realYear+100){btnNext.setEnabled(false);} //Too late
		lblMonth.setText(months[month]); //Refresh the month label (at the top)
		lblMonth.setBounds(80, 25, 180, 25); //Re-align label with calendar
		cmbYear.setSelectedItem(String.valueOf(year)); //Select the correct year in the combo box
		
		//Clear table
		for (int i=0; i<6; i++){
			for (int j=0; j<7; j++){
				mtblCalendar.setValueAt(null, i, j);
			}
		}
		
		//Get first day of month and number of days
		GregorianCalendar cal = new GregorianCalendar(year, month, 1);
		nod = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		som = cal.get(GregorianCalendar.DAY_OF_WEEK)+6;
		
		//Draw calendar
		for (int i=1; i<=nod; i++){
			int row = new Integer((i+som-2)/7);
			int column  =  ((i+som-2)%7);
			mtblCalendar.setValueAt(i, row, column);
		}

		//Apply renderers
		tblCalendar.setDefaultRenderer(tblCalendar.getColumnClass(0), new tblCalendarRenderer());
	}

	static class tblCalendarRenderer extends DefaultTableCellRenderer{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent (JTable table, Object value, boolean selected, boolean focused, int row, int column){
			super.getTableCellRendererComponent(table, value, selected, focused, row, column);
			if (column == 5 || column == 6){ //Week-end
				setBackground(new Color(255, 220, 220));
			}
			else{ //Week
				setBackground(new Color(255, 255, 255));
			}
			if (value != null){
				if (Integer.parseInt(value.toString()) == realDay && currentMonth == realMonth && currentYear == realYear){ //Today
					setBackground(new Color(220, 220, 255));
				}
			}
			setForeground(Color.black);
			return this;  
		}
	}
	
	static class btnPrev_Action implements ActionListener{
		public void actionPerformed (ActionEvent e){
			if (currentMonth == 0){ //Back one year
				currentMonth = 11;
				currentYear -= 1;
			}
			else{ //Back one month
				currentMonth -= 1;
			}
			refreshCalendar(currentMonth, currentYear);
		}
	}
	
	static class btnNext_Action implements ActionListener{
		public void actionPerformed (ActionEvent e){
			if (currentMonth == 11){ //Foward one year
				currentMonth = 0;
				currentYear += 1;
			}
			else{ //Foward one month
				currentMonth += 1;
			}
			refreshCalendar(currentMonth, currentYear);
		}
	}
	static class cmbYear_Action implements ActionListener{
		public void actionPerformed (ActionEvent e){
			if (cmbYear.getSelectedItem() != null){
				String b = cmbYear.getSelectedItem().toString();
				currentYear = Integer.parseInt(b);
				refreshCalendar(currentMonth, currentYear);
			}
		}
	}
	
	public int getYear()
	{
		return currentYear;
	}
    
	
	public int getMonth()
	{
		return currentMonth;
	}
	
	public int getDays()
	{
		return realDay;
	}
	
	public String getSelectedDate()
	{
		try
		{
			currentDay = Integer.parseInt(tblCalendar.getValueAt(tblCalendar.getSelectedRow(), tblCalendar.getSelectedColumn()).toString());
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(
					pnlCalendar,
					"Select a valid field!", "Invalid field selected!",
					JOptionPane.ERROR_MESSAGE);
		}
		String date;
		if ((currentMonth+1)<10)
			date = currentYear + "-0" + (currentMonth+1) + "-" + currentDay;
		else
			date = currentYear + "-" + (currentMonth+1) + "-" + currentDay;
		
		return date;
	}
    
}
