package polcz.budget.session;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.primefaces.component.datatable.DataTable;

import polcz.budget.global.R;
import polcz.budget.model.TTransaction;

@ManagedBean(name = "tableBean")
@RequestScoped
public class PaginatorBean
{
    private static final int rows = 25;
    private static final String rowsTemplate = "25,50,100";
    private static final String paginator = "{CurrentPageReport} {FirstPageLink} {PreviousPageLink} "
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

    public String rowStyleClass(TTransaction item)
    {
        return item.isPivot() ? "pivot" // R.CLNAME_PIVOT.toLowerCase()
            : (item.getCluster().getName().equals(R.CLNAME_TRANSFER) ? R.CLNAME_TRANSFER.toLowerCase()
                : (item.isProductInfo() ? "pinfo" : null));
    }

    public int firstOnLast()
    {
        return (t.getPageCount() - 1) * rows;
    }

    public int getRowCountOnPage()
    {
        return rows;
    }

    public String getPaginator()
    {
        return paginator;
    }

    public int getRows()
    {
        return rows;
    }

    public String getRowsTemplate()
    {
        return rowsTemplate;
    }
}