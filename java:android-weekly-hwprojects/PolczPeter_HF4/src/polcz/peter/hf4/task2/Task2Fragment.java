package polcz.peter.hf4.task2;

import polcz.peter.hf4.MainFragmentActivity;
import polcz.peter.hf4.R;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Polcz Peter - KUE5RC
 * 
 * Sajat object parcelable : {@link ArrayListParcelable}
 * Ha ez nem eleg, akkor a masodik hazi feladatban (listview-s) ott csinaltam egy
 * Contact osztaly ami a Parcelable-bol szarmazik. Ott van egy csomo getter-setter. 
 */
public class Task2Fragment extends Fragment {
    public static final String INTENT_STARTED = "IntentStarted";
    public static final String KEY_INTENT_STARTED = "_intent_started_";
    public static final String KEY_STRING = "_key_string_";
    public static final String KEY_NUMBER = "_key_number_";
    public static final String KEY_ARRAY = "_key_array_";

    EditText mSText, mNText, mAText;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_2, container, false);
        
        mSText = (EditText) view.findViewById(R.id.text1);
        mNText = (EditText) view.findViewById(R.id.text2);
        mAText = (EditText) view.findViewById(R.id.text3);

        Button sendall = (Button) view.findViewById(R.id.button4);

        sendall.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick (View v) {
                Intent intent = new Intent(Task2Fragment.this.getActivity(), MainFragmentActivity.class);
                
                // putting a specific key that will draw MainFragmentActivity's attention to call RecieverFragment
                // --
                intent.putExtra(KEY_INTENT_STARTED, INTENT_STARTED);
                
                // putting string
                // --
                intent.putExtra(KEY_STRING, mSText.getText().toString());
                
                // putting number
                // --
                try {
                    intent.putExtra(KEY_NUMBER, Integer.parseInt(mNText.getText().toString()));
                } catch (NumberFormatException e) {}

                // putting array
                // --
                ArrayListParcelable array = new ArrayListParcelable(mAText.getText().toString());
                intent.putExtra(KEY_ARRAY, (Parcelable) array);

                startActivity(intent);
            }

        });
        
        return view;
    }
}
