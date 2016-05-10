package com.napol.koltsegvetes.gui;

import javax.swing.table.AbstractTableModel;

import com.napol.koltsegvetes.db.AbstractQuery;
import com.napol.koltsegvetes.db.IColumn;

public class TableForGui extends AbstractTableModel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AbstractQuery table;
	String[] columnNames;
	
	public TableForGui(AbstractQuery _table)
	{
		super();
		table = _table;
		IColumn[] cols = table.getCols();
		columnNames = new String[cols.length];
		for (int i=0; i<cols.length; i++)
		{
			columnNames[i] = cols[i].name();
		}
		
	}

	public int getRowCount() {
		return table.size();
	}

	public int getColumnCount() {
		return columnNames.length;
	}
	
	public String getColumnName(int col)
	{
		return columnNames[col];
	}
/*
	public Object getValueAt(EColumnNames colName, int rowIndex) {
		return table.getValue(colName, rowIndex);
	}
	*/
	
	public boolean isCellEditable(int row, int col) {
       /* if (col < 2) {
            return false;
        } else {
            return true;
        }*/
		return false;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		try{
			return table.getValue(table.getCols()[columnIndex], rowIndex);
		}
		catch (IndexOutOfBoundsException e)
		{
			return null;
		}
	}
}
