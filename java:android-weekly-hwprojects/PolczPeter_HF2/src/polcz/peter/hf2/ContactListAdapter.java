package polcz.peter.hf2;

import java.util.List;

import polcz.peter.hf2.R;
import polcz.peter.hf2.db.Contact;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import fr.marvinlabs.widget.CheckableRelativeLayout;

public class ContactListAdapter extends ArrayAdapter<Contact> {

    private LayoutInflater mInflater;

    public ContactListAdapter(Context context, int resourceId, List<Contact> objects) {
        super(context, resourceId, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {

        // The item we want to get the view for
        // --
        final Contact item = getItem(position);

        // Re-use the view if possible
        // Initializing, creating view holder, retrieving existing view holder
        // --
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_as_marvinlabs, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.id.holder, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.holder);
        }

        // Setting real data to the actual item
        // --
        try {
            holder.tw_name.setText(item.getName());
            holder.tw_telnr.setText(item.getTelNr());
            holder.iw.setImageDrawable(item.getImg());
        } catch (NullPointerException e) {
            // TODO Setting out of memory picture
            try {
                holder.iw.setImageDrawable(getContext().getResources().getDrawable(R.drawable.noface));
            } catch (OutOfMemoryError ome) {}
        }

        // Restore the checked state properly
        final ListView lv = (ListView) parent;
        holder.layout.setChecked(lv.isItemChecked(position));

        return convertView;
    }

    @Override
    public long getItemId (int position) {
        return getItem(position).getId();
    }

    @Override
    public boolean hasStableIds () {
        return true;
    }

    private static class ViewHolder {
        public ViewHolder(View root) {
            iw = (ImageView) root.findViewById(R.id.iw);
            tw_name = (TextView) root.findViewById(R.id.tw_name);
            tw_telnr = (TextView) root.findViewById(R.id.tw_telnr);
            layout = (CheckableRelativeLayout) root.findViewById(R.id.item_layout_as_marvinlabs);
        }

        private ImageView iw;
        private TextView tw_name;
        private TextView tw_telnr;
        public CheckableRelativeLayout layout;
    }
}
