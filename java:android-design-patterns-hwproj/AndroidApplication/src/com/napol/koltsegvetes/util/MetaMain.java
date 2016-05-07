package com.napol.koltsegvetes.util;

import com.napol.koltsegvetes.MainActivity;
import com.napol.koltsegvetes.TrFormActivity;
import com.napol.koltsegvetes.TransactionListAdapter;
import com.napol.koltsegvetes.db.DataStore;
import com.napol.koltsegvetes.db.ParcelableQuery;
import com.napol.koltsegvetes.dbdriver.MySQLiteHelper;
import com.napol.koltsegvetes.dbdriver.MySQLiteOpenHelper;
import com.napol.koltsegvetes.util.MetaUtil;

public class MetaMain
{
    public void main (String[] args)
    {
        MetaUtil.GeneratePlantUML(DataStore.class);
        MetaUtil.GeneratePlantUML(MainActivity.class);
        MetaUtil.GeneratePlantUML(TrFormActivity.class);
        MetaUtil.GeneratePlantUML(TransactionListAdapter.class);
        MetaUtil.GeneratePlantUML(ParcelableQuery.class);
        MetaUtil.GeneratePlantUML(MySQLiteHelper.class);
        MetaUtil.GeneratePlantUML(MySQLiteOpenHelper.class);
    }

}
