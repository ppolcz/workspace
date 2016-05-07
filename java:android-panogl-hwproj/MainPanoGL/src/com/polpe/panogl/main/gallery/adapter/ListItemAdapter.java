package com.polpe.panogl.main.gallery.adapter;

import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.polpe.panogl.db.PanoItem;

public class ListItemAdapter extends ArrayAdapter<PanoItem> {

    int resourceId;
    int nameId;
    int dateId;
    LayoutInflater inflater;

    List<PanoItem> items;

    public ListItemAdapter(Context context, int resourceId, int nameId, int dateId, LayoutInflater inflater, List<PanoItem> items) {
        super(context, resourceId, items);

        this.resourceId = resourceId;
        this.nameId = nameId;
        this.dateId = dateId;
        this.inflater = inflater;
        this.items = items;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        ViewHolder vh = new ViewHolder();
        if (convertView == null) {
            convertView = inflater.inflate(resourceId, null);

            vh.twName = (TextView) convertView.findViewById(nameId);
            vh.twDate = (TextView) convertView.findViewById(dateId);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.twName.setText(items.get(position).getName());
        vh.twDate.setText(items.get(position).getDate());

        return convertView;
    }
}

class ViewHolder {
    TextView twName;
    TextView twDate;
}
