package com.napol.koltsegvetes.gui;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.napol.koltsegvetes.db.AbstractQuery;

/* Used by InternalFrameDemo.java. */
public class TableFrame extends JInternalFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static int openFrameCount = 0;
    static final int xOffset = 30, yOffset = 30;
    
    public TableFrame(AbstractQuery table) {
        super("Document #" + (++openFrameCount), 
              true, //resizable
              true, //closable
              true, //maximizable
              true);//iconifiable
         
        TableForGui visibleTable = new TableForGui(table);
        JTable vtable = new JTable(visibleTable);

        JScrollPane tableScroll = new JScrollPane(vtable);
		vtable.setFillsViewportHeight(true);
        
		this.add(tableScroll);
		
        setSize(300,300);
        setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
    }
}
