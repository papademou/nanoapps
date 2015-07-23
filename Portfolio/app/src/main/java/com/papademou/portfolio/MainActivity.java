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

        //bind click action to all buttons
        LinearLayout btn_wrapper = (LinearLayout) findViewById(R.id.layout_btn_wrapper);
        for (int i = 0; i <= btn_wrapper.getChildCount(); i++) {
            View v = btn_wrapper.getChildAt(i);
            if (v instanceof Button) {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        displayButtonPurpose(view);
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
