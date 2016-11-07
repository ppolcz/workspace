package polcz.budget.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.primefaces.component.datatable.DataTable;

import polcz.budget.global.R;
import polcz.budget.model.Ugylet;

@ManagedBean(name = "tableBean")
@RequestScoped
public class PaginatorBean
{
    private static final int NR_ROWS = 25;
    private static final String ROWS_TEMPLATE = "25,50,100";
    private static final String PAGINATOR_DEFS = "{CurrentPageReport} {FirstPageLink} {PreviousPageLink} "
        + "{PageLinks} {NextPageLink} {LastPageLink} {RowsPerPageDropdown}";

    private DataTable t;

    public DataTable getDataTable()
    {
        return t;
    }

    public void setDataTable(DataTable dataTable)
    {
        t = dataTable;
    }

    public boolean setupTrListPaginator()
    {
        return true;
    }

    public String rowStyleClass(Ugylet item)
    {
        return item.isPivot() ? "pivot" // R.CLNAME_PIVOT.toLowerCase()
            : (item.getCluster().getName().equals(R.CLNAME_TRANSFER) ? R.CLNAME_TRANSFER.toLowerCase()
                : (item.isProductInfo() ? "pinfo" : null));
    }

    public int firstOnLast()
    {
        return (t.getPageCount() - 1) * NR_ROWS;
    }

    public int getRowCountOnPage()
    {
        return NR_ROWS;
    }

    public String getPaginator()
    {
        return PAGINATOR_DEFS;
    }

    public int getRows()
    {
        return NR_ROWS;
    }

    public String getRowsTemplate()
    {
        return ROWS_TEMPLATE;
    }
}