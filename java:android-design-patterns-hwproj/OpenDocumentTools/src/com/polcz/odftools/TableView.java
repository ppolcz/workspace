//package com.polcz.odftools;
//
//import static java.lang.Math.max;
//import static java.lang.Math.min;
//
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.graphics.Color;
//import org.eclipse.swt.graphics.RGB;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Shell;
//import org.eclipse.swt.widgets.Table;
//import org.eclipse.swt.widgets.TableColumn;
//import org.eclipse.swt.widgets.TableItem;
//
//import com.napol.koltsegvetes.db.AbstractQuery;
//import com.napol.koltsegvetes.db.EColumnNames;
//import com.napol.koltsegvetes.util.Debug;
//import com.polcz.odftools.ParseODF.ECaids;
//
///**
// * This class is responsive to visualize a PQuery (i.e. AbstractQuery or TableModel)
// * 
// * TODO - this is only an experimental version (or like a feasibility study)
// * TODO - color items using: colorItem(RGB rgb, ??? whereStatement)
// * 
// * @author Polcz Péter <ppolcz@gmail.com>
// * Created on Nov 15, 2014 9:35:01 AM
// */
//public class TableView
//{
//    private static final String TITLE_TRANZACTION_ONE_BY_ONE = "Tranzactions listed one by one";
//    
//    private static final String NULL_STRING = "null";
//
//    private static final RGB rgbLightPurple = new RGB(0.1f, 0.1f, 0.9f);
//    private static final RGB rgbLightBlue = new RGB(198.0f, .36f, 1.0f);
//    private static final RGB rgbLightGreen = new RGB(117.0f, .53f, 0.99f);
//    private static final RGB rgbLightGreen2 = new RGB(117.0f, .44f, 1.0f);
//    private static final RGB rgbWhite = new RGB(0.0f, 0.0f, 1.0f);
//
//    private static int minmax(int a)
//    {
//        return min(max(a, 0), 255);
//    }
//
//    private static RGB appendColor(RGB orig, RGB b)
//    {
//        return new RGB(minmax(orig.red + b.red - 255), minmax(orig.green + b.green - 255), minmax(orig.blue + b.blue - 255));
//    }
//
//    @SuppressWarnings("unused")
//    private static Color appendColor(Color a, Color b)
//    {
//        return new Color(b.getDevice(), appendColor(a.getRGB(), b.getRGB()));
//    }
//
//    private static Color appendColor(Color a, RGB b)
//    {
//        return new Color(a.getDevice(), appendColor(a.getRGB(), b));
//    }
//
//    AbstractQuery query;
//
//    public TableView(AbstractQuery q)
//    {
//        query = q;
//
//        final Display display = new Display();
//        // final Device device = display.getCurrent();
//        final Shell shell = new Shell(display);
//        shell.setLayout(new GridLayout());
//        shell.setText(TITLE_TRANZACTION_ONE_BY_ONE);
//
//        // set dimensions of the window
//        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
//        gridData.heightHint = 300;
//        gridData.widthHint = 1024;
//
//        // configure table
//        final Table table = new Table(shell, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
//        table.setLinesVisible(true);
//        table.setHeaderVisible(true);
//        table.setLayoutData(gridData);
//
//        // create columns
//        final int cols = query.getTypes().length;
//        Debug.debug("types.length = %d", cols);
//        final String[] titles = new String[cols];
//        for (int i = 0; i < cols; ++i)
//        {
//            new TableColumn(table, SWT.NONE).setText(query.getTypes()[i].displayName());
//        }
//
//        final int[] balanceIndex = { query.getPosition(EColumnNames.TR_NEWBALANCE) };
//        Debug.debug("balanceIndex = %d", balanceIndex[0]);
//        
//        new Thread()
//        {
//            @Override
//            public void run()
//            {
//                final boolean[] initialize = { true };
//
//                Debug.debug("query.size() = %d", query.size());
//                for (int i = 0; i < query.size(); ++i)
//                {
//                    if (table.isDisposed()) return;
//
//                    // row index pointer = rowip
//                    final int[] rowip = { i };
//                    
//                    display.syncExec(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            if (table.isDisposed()) return;
//                            TableItem item = new TableItem(table, SWT.NONE);
//
//                            if (query.getValue(EColumnNames.TR_CAID, rowip[0]).equals(ECaids.potp.str))
//                            {
//                                item.setBackground(appendColor(item.getBackground(), rgbLightGreen));
//                            }
//
//                            item.setBackground(balanceIndex[0], appendColor(item.getBackground(), rgbLightBlue));
//
//                            for (int j = 0; j < cols; ++j)
//                            {
//                                String itemContent = query.getTypes()[j].toDisplayString(query.get(rowip[0])[j]);
//                                item.setText(j, itemContent);
//
//                                if (itemContent.equals(NULL_STRING))
//                                {
//                                    // System.out.println(itemContent + " == " + NULL_STRING + "  row: " + index[0] + "  col: " + j);
//                                    item.setBackground(j, appendColor(item.getBackground(), rgbLightPurple));
//                                }
//                                else
//                                {
//                                    // item.setBackground(j, colorWhite);
//                                }
//                            }
//
//                            /* pack columns and the whole widget, then open it */
//                            if (initialize[0])
//                            {
//                                for (int i = 0; i < titles.length; i++)
//                                    table.getColumn(i).pack();
//
//                                shell.pack();
//                                shell.open();
//                                shell.setMaximized(true);
//
//                                initialize[0] = false;
//                            }
//                        }
//                    });
//                }
//            }
//        }.start();
//
//        // main GUI loop
//        while (!shell.isDisposed())
//        {
//            if (!display.readAndDispatch()) display.sleep();
//        }
//        display.dispose();
//    }
//}
