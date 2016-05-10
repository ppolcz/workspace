package com.polpe.panogl.main.gallery.adapter;

import java.util.ArrayList;

import com.polpe.panogl.R;
import com.polpe.panogl.main.gallery.OnPagerPositionChanged;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImagePagerAdapter extends PagerAdapter {

    Context context;
    ArrayList<String> imgPaths;
    LayoutInflater inflater;
    OnPagerPositionChanged listener;

    public ImagePagerAdapter(Context context, ArrayList<String> imgPaths, OnPagerPositionChanged listener) {
        this.context = context;
        this.imgPaths = imgPaths;
        this.listener = listener;
    }

    @Override
    public int getCount () {
        return this.imgPaths.size();
    }

    @Override
    public boolean isViewFromObject (View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem (ViewGroup container, int position) {
        ImageView iw = new ImageView(context);
        iw.setId(R.id.gallery_pager_iw);
        iw.setOnClickListener((OnClickListener) context);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(imgPaths.get(position), options);
        iw.setImageBitmap(bitmap);

        ((ViewPager) container).addView(iw);

        return iw;
    }

    @Override
    public void setPrimaryItem (ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        listener.positionChanged(position + 1);
    }

    @Override
    public void destroyItem (ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }
}
