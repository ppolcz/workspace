package com.napol.koltsegvetes.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.napol.koltsegvetes.db.AbstractQuery;
import com.napol.koltsegvetes.db.IColumn;
import com.napol.koltsegvetes.dboperator.DatabaseOperator;
import com.napol.koltsegvetes.dialogs.AccountsDialog;
import com.napol.koltsegvetes.dialogs.ChargeAccountsDialog;
import com.napol.koltsegvetes.dialogs.ClustersDialog;
import com.napol.koltsegvetes.dialogs.MarketsDialog;
import com.napol.koltsegvetes.dialogs.ProductsDialog;
import com.napol.koltsegvetes.dialogs.TransactionDialog;

public class MainWindow extends JFrame implements ActionListener {
	private DatabaseOperator database;
	private String actTable;

	private boolean saved = true;
	private JSplitPane mainSplitPane, secondarySplitPane;
	private JMenuBar menu;
	private JButton select, newRow, dayDetails;
	private JMenu file, view, edit;
	private JMenuItem save, saveas, newTransaction, sqltb, calendartb, exit;
	private JTable vtable;
	private DynamicDBTree tree;
	private JScrollPane treeScroll;
	private JToolBar sqltoolbar;
	private JDesktopPane tableDesktop, calendarDesktop;
	private CalendarFrame cali;

	private static final long serialVersionUID = 1L;

	public MainWindow() {
		Runnable runner = new Runnable() {
			public void run() {
				try {
					initializeGui();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		EventQueue.invokeLater(runner);
	}

	private void initializeGui() throws IOException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e1) {
		}
		// Ha nem mukodik a felso, akkor ird vissza a regire:
		// try
		// {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
		// catch (ClassNotFoundException e) {}
		// catch (InstantiationException e) {}
		// catch (IllegalAccessException e) {}
		// catch (UnsupportedLookAndFeelException e) {}

		actTable = null;

		sqltoolbar = new JToolBar();
		tableDesktop = new JDesktopPane();
		tableDesktop.setBackground(getBackground());
		calendarDesktop = new JDesktopPane();
		calendarDesktop.setBackground(getBackground());
		cali = new CalendarFrame();
		calendarDesktop.add(cali);

		setTitle("Koltsegvetes");
		setSize(900, 700);
		setVisible(true);
		setResizable(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				if (JOptionPane.showConfirmDialog(null,
						"Do you really want to exit?", "Exit?",
						JOptionPane.YES_NO_OPTION) == 0) {
					System.exit(0);
				}
			}
		});

		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				if (secondarySplitPane != null) {
					alignSecondarySplitPane();
					mainSplitPane.setDividerLocation(333);
				}
			}
		});

		setLocationRelativeTo(null);
		setJMenuBar(initializeMenuBar());
		this.add(sqltoolbar, BorderLayout.PAGE_START);
		initializeDataStore();
		initializeToolBar();
		refreshAfterOpen("Database");// , files);
	}

	protected void createFrame(String title, AbstractQuery q) {
		TableFrame frame = new TableFrame(q);
		frame.setTitle(title);
		frame.setVisible(true); // necessary as of 1.3
		tableDesktop.add(frame);
		try {
			frame.setSelected(true);
		} catch (java.beans.PropertyVetoException e) {
		}
	}

	protected JMenuBar initializeMenuBar() {
		menu = new JMenuBar();
		file = new JMenu("File");
		edit = new JMenu("Edit");
		view = new JMenu("View");

		save = new JMenuItem("Save");
		save.setEnabled(false);
		saveas = new JMenuItem("Save as...");
		saveas.setEnabled(false);
		sqltb = new JMenuItem("Hide SQL Tool Bar");
		calendartb = new JMenuItem("Hide Calendar Tool Bar");
		newTransaction = new JMenuItem("Add new transaction...");
		newTransaction.setEnabled(false);
		exit = new JMenuItem("Exit");

		menu.add(file);
		menu.add(edit);
		menu.add(view);
		// file.add(open);
		file.add(save);
		file.add(saveas);
		file.add(exit);
		edit.add(newTransaction);
		view.add(sqltb);
		view.add(calendartb);

		file.addActionListener(this);
		edit.addActionListener(this);
		// open.addActionListener(this);
		save.addActionListener(this);
		saveas.addActionListener(this);
		exit.addActionListener(this);
		newTransaction.addActionListener(this);
		view.addActionListener(this);
		sqltb.addActionListener(this);
		calendartb.addActionListener(this);

		return menu;
	}

	private void initializeToolBar() {
		select = new JButton("Select");
		select.addActionListener(this);
		select.setToolTipText("Show the table choosed on the tree");

		newRow = new JButton("New Row");
		newRow.addActionListener(this);
		newRow.setToolTipText("Add a new rof to this table...");

		dayDetails = new JButton("Days details");
		dayDetails.addActionListener(this);
		dayDetails
				.setToolTipText("Make a query from the transactions with the selected day");

		sqltoolbar.add(select);
		sqltoolbar.add(newRow);
		sqltoolbar.add(dayDetails);
		sqltoolbar.setVisible(true);
		sqltoolbar.setFloatable(false);
	}

	private void initializeDataStore() {
		database = new DatabaseOperator();
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		if (ev.getSource() == exit) {
			exitAction();
		}

		/*
		 * if (ev.getSource() == open) { openAction(); }
		 */
		if (ev.getSource() == select) {
			try {
				selectEvent();
			} catch (NullPointerException e) {
				JOptionPane.showMessageDialog(this,
						"First you should select a table from the tree!",
						"Error on select", JOptionPane.ERROR_MESSAGE);
			}
		}
		if (ev.getSource() == newRow) {
			newRowEvent();
		}

		if (ev.getSource() == dayDetails) {
			makeQuery();
		}

		if (ev.getSource() == save) {
			/*
			 * try { data.writeToFile(); } catch (FileNotFoundException e) {
			 * e.printStackTrace(); }
			 */
			saved = true;
			JOptionPane.showMessageDialog(this, "Saving is done!", "Saving",
					JOptionPane.INFORMATION_MESSAGE);
		}
		if (ev.getSource() == saveas) {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new java.io.File("."));
			chooser.setDialogTitle("Choose a .db file");
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooser.setAcceptAllFileFilterUsed(false);

			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				// String destination = chooser.getSelectedFile().toString();
				/*
				 * try { data.writeToNewFile(destination); } catch
				 * (FileNotFoundException e) { e.printStackTrace(); }
				 */
				saved = true;
				JOptionPane.showMessageDialog(this, "Saving is done!",
						"Saving", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			if (chooser.showOpenDialog(this) == JFileChooser.CANCEL_OPTION) {
				return;
			}
		}
		if (ev.getSource() == newTransaction) {
			newRowEvent();
		}
		if (ev.getSource() == sqltb) {
			sqlToolBarAction();
		}
		if (ev.getSource() == calendartb) {
			calendarToolBarAction();
		}
	}

	/*
	 * private void openAction() { JFileChooser chooser = new JFileChooser();
	 * chooser.setCurrentDirectory(new java.io.File(".."));
	 * chooser.setDialogTitle("Choose a folder which contains .db file(s)");
	 * chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	 * chooser.setAcceptAllFileFilterUsed(false); File[] files = null; File
	 * directory = null; if (chooser.showOpenDialog(this) ==
	 * JFileChooser.APPROVE_OPTION) { if
	 * (chooser.getSelectedFile().toString().endsWith(".db")) { files = new
	 * File[1]; files[0] = chooser.getSelectedFile(); directory =
	 * chooser.getCurrentDirectory(); } else { directory = new
	 * File(chooser.getSelectedFile().toString()); files =
	 * directory.listFiles(new FileFilter() { public boolean accept(File path) {
	 * return path.isFile() && (path.getName().endsWith(".db")); } }); } } else
	 * { JOptionPane.showMessageDialog(this, "Nothing to load!", "Loading",
	 * JOptionPane.INFORMATION_MESSAGE); }
	 * 
	 * }
	 */

	private void refreshAfterOpen(String directory) {
		AbstractQuery[] tables = database.getTables();
		if (tree != null) {
			treeScroll.remove(tree);
			tree = null;
		}
		tree = new DynamicDBTree(directory);
		for (int j = 0; j < tables.length; j++) {
			IColumn[] colls = tables[j].getCols();
			if (colls[0].table() != null) {
				tree.addObject(directory, colls[0].table(),
						tables[j].toString());
			}
		}

		if (treeScroll != null) {
			mainSplitPane.remove(treeScroll);
			treeScroll = null;
		}
		treeScroll = new JScrollPane(tree);

		vtable = new JTable(4, 4);
		vtable.setFillsViewportHeight(true);

		initializeSplitPane();

		save.setEnabled(true);
		saveas.setEnabled(true);
		newTransaction.setEnabled(true);

		this.revalidate();
	}

	private void initializeSplitPane() {
		if (cali.isVisible()) {
			if (mainSplitPane != null) {
				this.getContentPane().remove(mainSplitPane);
				mainSplitPane.removeAll();
				mainSplitPane = null;
			}
			if (secondarySplitPane != null) {
				secondarySplitPane.removeAll();
				secondarySplitPane = null;
			}

			secondarySplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					treeScroll, calendarDesktop);
			secondarySplitPane.setEnabled(false);
			alignSecondarySplitPane();
			mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					secondarySplitPane, tableDesktop);
			mainSplitPane.setDividerLocation(333);
			mainSplitPane.setEnabled(true);
			this.getContentPane().add(mainSplitPane);
			this.revalidate();
		} else {
			if (mainSplitPane != null) {
				this.getContentPane().remove(mainSplitPane);
				mainSplitPane.removeAll();
				mainSplitPane = null;
			}
			if (secondarySplitPane != null) {
				secondarySplitPane.removeAll();
				secondarySplitPane = null;
			}

			mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					treeScroll, tableDesktop);
			this.getContentPane().add(mainSplitPane);
			this.revalidate();
		}
	}

	private void alignSecondarySplitPane() {
		System.out.println(this.getContentPane().getHeight() - cali.getHeight()
				- menu.getHeight() - sqltoolbar.getHeight() - 10);
		if (sqltoolbar.isVisible()) {

			secondarySplitPane.setDividerLocation(this.getContentPane()
					.getHeight()
					- cali.getHeight()
					- sqltoolbar.getHeight()
					- 8);
		} else {
			secondarySplitPane.setDividerLocation(this.getContentPane()
					.getHeight() - cali.getHeight() - 8);
		}
	}

	private void sqlToolBarAction() {
		if (sqltb.getText() == "Show SQL Tool Bar") {
			sqltoolbar.setVisible(true);
			sqltb.setText("Hide SQL Tool Bar");
		} else {
			sqltoolbar.setVisible(false);
			sqltb.setText("Show SQL Tool Bar");
		}
		alignSecondarySplitPane();
	}

	private void selectEvent() {
		actTable = tree.getClicked();
		boolean isOpened = false;
		JInternalFrame[] frames = tableDesktop.getAllFrames();
		for (JInternalFrame f : frames) {
			if (f.getTitle() == actTable) {
				isOpened = true;
				f.toFront();
				f.repaint();
				break;
			}
		}
		if (!isOpened) {
			try
			{
				AbstractQuery q = database.getTable(actTable);
				createFrame(actTable, q);
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(
						this,
						"Error occured when tried to open a table: "
								+ e.getMessage(), "Can't open table" + actTable,
						JOptionPane.ERROR_MESSAGE);
			}
			
		}
	}

	private void newRowEvent() {
		try {
			String[] clNames;
			String[] caNames;
			String[] mkID;

			DateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd' 'HH:mm:ss.SSSXXX");
			Date date = new Date();
			System.out.println(dateFormat.format(date));

			switch (tableDesktop.getSelectedFrame().getTitle()) {
			case "ACCOUNTS":
				com.napol.koltsegvetes.dialogs.AccountsDialog act;

				act = new AccountsDialog(this, cali.getYear(), cali.getMonth(),
						cali.getDays());
				act.setVisible(true);
				if (!act.isCancelled()) {
					String[] newdatas = { act.getUserName(),
							dateFormat.format(date) };
					database.addRow("ACCOUNTS", newdatas);
					saved = false;
					tableDesktop.remove(tableDesktop.getSelectedFrame());
					selectEvent();
				}

				break;

			case "CHARGE_ACCOUNTS":
				ChargeAccountsDialog cact;

				cact = new ChargeAccountsDialog(this, cali.getYear(),
						cali.getMonth(), cali.getDays());
				cact.setVisible(true);
				if (!cact.isCancelled()) {
					String[] newdatas = { cact.getCAID(), cact.getCAName(),
							cact.getDate(), cact.getBalance(),
							dateFormat.format(date), cact.getCAAcid() };
					database.addRow("CHARGE_ACCOUNTS", newdatas);
					saved = false;
					tableDesktop.remove(tableDesktop.getSelectedFrame());
					selectEvent();
				}

				break;

			case "TRANZACTIONS":
				TransactionDialog nt;

				caNames = database.getColumnValues("CA_NAME");
				clNames = database.getColumnValues("CL_NAME");
				mkID = database.getColumnValues("MK_ID");

				nt = new TransactionDialog(this, caNames, clNames, mkID,
						cali.getYear(), cali.getMonth(), cali.getDays());
				nt.setVisible(true);
				if (!nt.isCancelled()) {
					Integer amount;
					Integer multiplier = Integer.parseInt(database
							.getSingleField("CL_DIRECTION", nt.getCLName(),
									"cl_name"));
					Integer balance = Integer.parseInt(database.getSingleField(
							"CA_BALANCE", nt.getCAID(), "ca_name"));

					if (multiplier != 0) {
						amount = (balance + (multiplier * Integer.parseInt(nt
								.getAmount())));
					} else {
						amount = balance + Integer.parseInt(nt.getAmount());

					}
					String[] newdatas = { null, nt.getDate(), nt.getCAID(), nt.getAmount(), amount.toString(),  nt.getCLName(), nt.getName(), nt.getMkID(), "", dateFormat.format(date) };
					database.addRow("TRANZACTIONS", newdatas);
					saved = false;
					tableDesktop.remove(tableDesktop.getSelectedFrame());
					selectEvent();
				}

				break;

			case "CLUSTERS":
				ClustersDialog clustersDialog;
				clustersDialog = new ClustersDialog(this);
				clustersDialog.setVisible(true);
				if (!clustersDialog.isCancelled()) {
					String[] newdatas = { clustersDialog.getClusterName(),
							clustersDialog.getDescription(),
							clustersDialog.getDirection(),
							dateFormat.format(date) };
					database.addRow("CLUSTERS", newdatas);
					saved = false;
					tableDesktop.remove(tableDesktop.getSelectedFrame());
					selectEvent();
				}
				break;

			case "MARKETS":
				MarketsDialog mkDialog;
				mkDialog = new MarketsDialog(this);
				mkDialog.setVisible(true);
				if (!mkDialog.isCancelled()) {
					String[] newdatas = { mkDialog.getMarketID(),
							mkDialog.getMarketName(), dateFormat.format(date) };
					database.addRow("MARKETS", newdatas);
					saved = false;
					tableDesktop.remove(tableDesktop.getSelectedFrame());
					selectEvent();
				}
				break;

			case "PRODUCT_INFO":

				String[] acid = database.getColumnValues("CA_ID");
				clNames = database.getColumnValues("CL_NAME");
				mkID = database.getColumnValues("MK_ID");

				ProductsDialog prodDialog;
				prodDialog = new ProductsDialog(this, mkID, clNames, acid,
						cali.getYear(), cali.getMonth(), cali.getDays());
				prodDialog.setVisible(true);
				if (!prodDialog.isCancelled()) {
					String[] newdatas = { null, prodDialog.getDate(),
							prodDialog.getAmount(), prodDialog.getPIName(),
							prodDialog.getMKID(), prodDialog.getClName(),
							prodDialog.getACID(), dateFormat.format(date) };
					database.addRow("PRODUCT_INFO", newdatas);
					saved = false;
					tableDesktop.remove(tableDesktop.getSelectedFrame());
					selectEvent();
				}

				break;
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(
					this,
					"Error occured when tried to add a new row: "
							+ e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			tableDesktop.repaint();
		}

	}

	private void calendarToolBarAction() {
		if (calendartb.getText() == "Show Calendar Tool Bar") {
			cali.setVisible(true);
			dayDetails.setVisible(true);
			calendartb.setText("Hide Calendar Tool Bar");
			initializeSplitPane();
		} else {
			cali.setVisible(false);
			dayDetails.setVisible(false);
			calendartb.setText("Show Calendar Tool Bar");
			initializeSplitPane();
		}
	}

	private void makeQuery() {
		String date = cali.getSelectedDate();
		boolean isOpened = false;
		JInternalFrame[] frames = tableDesktop.getAllFrames();
		for (JInternalFrame f : frames) {
			if (f.getTitle().equals( "Query by date: " + date)) {
				isOpened = true;
				f.toFront();
				f.repaint();
				break;
			}
		}
		if (!isOpened) {
			try {
				createFrame(
						"Query by date: " + date,
						database.getQuery("TRANZACTIONS", "TR_DATE LIKE " + "'"
								+ date + "%'"));
			} catch (Exception e) {
				JOptionPane.showMessageDialog(
						this,
						"Error occured when tried to make a query: "
								+ e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	private void exitAction() {
		if (saved == true) {
			if (JOptionPane.showConfirmDialog(this,
					"Do you really want to exit?", "Exit?",
					JOptionPane.YES_NO_OPTION) == 0) {
				System.exit(0);
			}
		} else {
			int answer = JOptionPane
					.showConfirmDialog(
							this,
							"Ther are unsaved changes in the database. Do you want to save it?",
							"Unsaved changes", JOptionPane.YES_NO_CANCEL_OPTION);
			if (answer == JOptionPane.CANCEL_OPTION) {
				return;
			} else if (answer == JOptionPane.NO_OPTION) {
				System.exit(0);
			} else if (answer == JOptionPane.YES_OPTION) {
				/*
				 * try { data.writeToFile(); } catch (FileNotFoundException e) {
				 * e.printStackTrace(); }
				 */
				JOptionPane.showMessageDialog(this, "Saving is done!",
						"Saving", JOptionPane.INFORMATION_MESSAGE);
				System.exit(0);
			}
		}

	}

	public static void main(String[] args) {
		new MainWindow();
	}

}
