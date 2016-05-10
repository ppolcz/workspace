package polcz.peter.hf4.task2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReceiverFragment extends Fragment {
    private static final int NAN = -12313123;
    TextView view;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView view = new TextView(getActivity());

        Bundle data = getActivity().getIntent().getExtras();

        view.append("String message = ");
        try {
            String string = data.getString(Task2Fragment.KEY_STRING);

            view.append(string);
        } catch (NullPointerException e) {}
        view.append(System.getProperty("line.separator"));

        view.append("Number message = ");
        try {
            int number = data.getInt(Task2Fragment.KEY_NUMBER, NAN);
            if (number == NAN) throw new NullPointerException();

            view.append(String.valueOf(number));
        } catch (NullPointerException e) {}
        view.append(System.getProperty("line.separator"));

        view.append("Array message = ");
        try {
            ArrayListParcelable numbers = (ArrayListParcelable) data.getParcelable(Task2Fragment.KEY_ARRAY);

            for (int i = 0; i < numbers.size(); ++i)
                view.append(String.valueOf(numbers.get(i)) + " ");
        } catch (NullPointerException e) {}
        view.append(System.getProperty("line.separator"));

        return view;
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
