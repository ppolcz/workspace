package polcz.peter.hf4.task1;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class WebViewFragment extends Fragment {

    private String mUrl;
    private WebView mView;
    
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = new WebView(getActivity());
        
        try {
            Uri uri = getActivity().getIntent().getData();
            mUrl = uri.getAuthority();
            mView.loadUrl("http://" + mUrl);
        } catch (Exception e) {
            mView.loadData("<html><body><h1>url is null</h1></body></html>", "text/html", "UTF-8");
        }
        
        return mView;
    }
 
    public void setUrl(String url) {
        try {
            mUrl = url;
            mView.loadUrl("http://" + mUrl);
        } catch (Exception e) {
            mView.loadData("<html><body><h1>url is null</h1></body></html>", "text/html", "UTF-8");
        }
        
    }

}
