package polcz.peter.hf4;

import java.util.ArrayList;
import java.util.List;

import polcz.peter.hf4.task1.Task1Fragment;
import polcz.peter.hf4.task2.Task2Fragment;
import polcz.peter.hf4.task3.Task3Fragment;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LauncherFragment extends ListFragment {
    private static final String TAG = LauncherFragment.class.getSimpleName();

    LayoutInflater mLayoutInflater;

    @Override
    public void onAttach (Activity activity) {
        super.onAttach(activity);
        Log.v(TAG, "onAttach");

        Fragment parentFragment = getParentFragment();
        if (parentFragment != null) {}
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new SampleAdapter(querySampleActivities()));
    }

    @Override
    public void onListItemClick (ListView lv, View v, int pos, long id) {
        ((MainFragmentActivity) getActivity()).switchFragment(((SampleInfo) getListAdapter().getItem(pos)).fragment);
    }

    protected List<SampleInfo> querySampleActivities () {
        ArrayList<SampleInfo> samples = new ArrayList<SampleInfo>();
        samples.add(new SampleInfo("Task 1", new Task1Fragment()));
        samples.add(new SampleInfo("Task 2", new Task2Fragment()));
        samples.add(new SampleInfo("Task 3", new Task3Fragment()));
        
        return samples;
    }

    static class SampleInfo {
        Fragment fragment;
        String name;

        SampleInfo(String name, Fragment fragment) {
            this.name = name;
            this.fragment = fragment;
        }
    }

    class SampleAdapter extends BaseAdapter {
        private List<SampleInfo> mItems;

        public SampleAdapter(List<SampleInfo> items) {
            mItems = items;
        }

        @Override
        public int getCount () {
            return mItems.size();
        }

        @Override
        public Object getItem (int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId (int position) {
            return position;
        }

        @Override
        public View getView (int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
                convertView.setTag(convertView.findViewById(android.R.id.text1));
            }
            TextView tv = (TextView) convertView.getTag();
            tv.setText(mItems.get(position).name);
            return convertView;
        }

    }
}
