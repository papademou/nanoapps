package com.papademou.portfolio;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Displays Toast message stating what application will be launched
     * @param v view
     */
    public void displayButtonPurpose(View v) {
        Context context = getApplicationContext();
        String appName = (String) v.getTag(); //retrieve application name from tag field - TODO: Can we pass data attribute instead?
        CharSequence text = "This button will launch my " + appName + " app!";
        int duration = Toast.LENGTH_SHORT;
        int[] location = new int[2];
        v.getLocationOnScreen(location);

        Toast t = Toast.makeText(context, text, duration);
        t.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, location[1]);
        t.show();
    }
}
