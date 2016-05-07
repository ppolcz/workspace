package polcz.peter.hf4.task1;

import polcz.peter.hf4.R;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class Task1Fragment extends Fragment {

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_task_1, container, false);
        
        final EditText text = (EditText) view.findViewById(R.id.main_edit_text);
        Button open = (Button) view.findViewById(R.id.main_button_open);
        Button share = (Button) view.findViewById(R.id.main_button_share);

        // Open URL button
        // --
        open.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick (View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + text.getText().toString()));
                startActivity(Intent.createChooser(intent, "Open URL"));
            }
        });

        // Share link button
        // --
        share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick (View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                intent.putExtra(Intent.EXTRA_TEXT, text.getText().toString());
                startActivity(Intent.createChooser(intent, "Share URL"));
            }
        });

        return view;
    }
}
