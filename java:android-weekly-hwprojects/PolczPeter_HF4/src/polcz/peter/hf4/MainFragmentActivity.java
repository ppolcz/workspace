package polcz.peter.hf4;

import polcz.peter.hf4.task1.Task1Fragment;
import polcz.peter.hf4.task1.WebViewFragment;
import polcz.peter.hf4.task2.ReceiverFragment;
import polcz.peter.hf4.task2.Task2Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup;

public class MainFragmentActivity extends FragmentActivity {

    private static final String TAG = MainFragmentActivity.class.getSimpleName();

    private ViewGroup mLauncherLayout, mTaskLayout;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: savedInstanceState " + (savedInstanceState == null ? "==" : "!=") + " null"); // OKE

        setContentView(R.layout.fragmentactivity_main);

        // Restore state
        if (savedInstanceState != null) {
            // The fragment manager will handle restoring them if we are being
            // restored from a saved state
        }
        // If this is the first creation of the activity, add fragments to it
        else {

            // If our layout has a container for the image selector fragment, add it
            // --
            mLauncherLayout = (ViewGroup) findViewById(R.id.activity_launcher);
            if (mLauncherLayout != null) {
                Log.i(TAG, "onCreate: adding LauncherFragment to MainActivity"); // OKE

                // Add image selector fragment to the activity's container layout
                LauncherFragment imageSelectorFragment = new LauncherFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(mLauncherLayout.getId(), imageSelectorFragment, LauncherFragment.class.getName());

                // Commit the transaction
                fragmentTransaction.commit();
            }

            // If our layout has a container for the image rotator fragment, add it
            // --
            mTaskLayout = (ViewGroup) findViewById(R.id.activity_task);
            if (mTaskLayout != null) {
                Log.i(TAG, "onCreate: adding Task1 to MainActivity");

                // Add image rotator fragment to the activity's container layout
                Task1Fragment imageRotatorFragment = new Task1Fragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(mTaskLayout.getId(), imageRotatorFragment, Task1Fragment.class.getName());

                // Commit the transaction
                fragmentTransaction.commit();
            }

            // If our activity is proposed to serve an ACTION_VIEW request, and it has an mTaskLayout
            // --
            if (getIntent().getAction() == Intent.ACTION_VIEW && mTaskLayout != null) {
                Log.i(TAG, "onCreate: adding Task1 to MainActivity");

                // Add image rotator fragment to the activity's container layout
                WebViewFragment imageRotatorFragment = new WebViewFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(mTaskLayout.getId(), imageRotatorFragment, WebViewFragment.class.getName());

                // Commit the transaction
                fragmentTransaction.commit();
            }

            // If our activity is proposed to serve an ACTION_VIEW request, but do not possess mTaskLayout
            // --
            if (getIntent().getAction() == Intent.ACTION_VIEW && mTaskLayout == null && mLauncherLayout != null) {
                Log.i(TAG, "onCreate: adding Task1 to MainActivity");

                // Add image rotator fragment to the activity's container layout
                WebViewFragment imageRotatorFragment = new WebViewFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(mLauncherLayout.getId(), imageRotatorFragment, WebViewFragment.class.getName());

                // Commit the transaction
                fragmentTransaction.commit();
            }

            try {
                if (!getIntent().getExtras().getString(Task2Fragment.KEY_INTENT_STARTED).equals(Task2Fragment.INTENT_STARTED))
                    throw new NullPointerException();
                System.out.println("equals");
                
                if (mTaskLayout == null && mLauncherLayout != null) {
                    Log.i(TAG, "onCreate: adding Task1 to MainActivity");

                    // Add image rotator fragment to the activity's container layout
                    ReceiverFragment imageRotatorFragment = new ReceiverFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.add(mLauncherLayout.getId(), imageRotatorFragment, ReceiverFragment.class.getName());

                    // Commit the transaction
                    fragmentTransaction.commit();
                } else if (mTaskLayout != null) {
                    Log.i(TAG, "onCreate: adding Task1 to MainActivity");

                    // Add image rotator fragment to the activity's container layout
                    ReceiverFragment imageRotatorFragment = new ReceiverFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.add(mTaskLayout.getId(), imageRotatorFragment, ReceiverFragment.class.getName());

                    // Commit the transaction
                    fragmentTransaction.commit();
                }
            } catch (NullPointerException e) {}
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onStart () {
        super.onStart();
        Log.v(TAG, "onStart");
    }

    @Override
    public void onResume () {
        super.onResume();
        Log.v(TAG, "onResume");
    }

    @Override
    public void onPause () {
        super.onPause();
        Log.v(TAG, "onPause");
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(TAG, "onSaveInstanceState");
    }

    @Override
    public void onStop () {
        super.onStop();
        Log.v(TAG, "onStop");
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }

    public void switchFragment (Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (mTaskLayout != null) fragmentTransaction.replace(R.id.activity_task, fragment);
        else if (mLauncherLayout != null) fragmentTransaction.replace(R.id.activity_launcher, fragment);
        else return;

        fragmentTransaction.commit();
    }

}
