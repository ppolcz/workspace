package polcz.peter.hf5.masodik;

import java.util.List;

import polcz.peter.hf5.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import fr.marvinlabs.widget.CheckableRelativeLayout;

public class DownloadListAdapter extends ArrayAdapter<MediaURL> {

    private LayoutInflater mInflater;

    public DownloadListAdapter(Context context, int resourceId, List<MediaURL> objects) {
        super(context, resourceId, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {

        // The item we want to get the view for
        // --
        final MediaURL item = getItem(position);

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
        holder.tw_name.setText(item.getName());
        holder.tw_address.setText(item.getAddress());

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
            tw_name = (TextView) root.findViewById(R.id.tw_name);
            tw_address = (TextView) root.findViewById(R.id.tw_address);
            layout = (CheckableRelativeLayout) root.findViewById(R.id.item_layout_as_marvinlabs);
        }

        private TextView tw_name;
        private TextView tw_address;
        public CheckableRelativeLayout layout;
    }
}
