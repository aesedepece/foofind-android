package foofind.kadede.android;

import foofind.kadede.android.R;
import foofind.kadede.android.typeandsource;
import foofind.kadede.android.search;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class foofind extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Set up click listeners for all the buttons
		View typebutton = findViewById(R.id.typeandsourcebutton);
		typebutton.setOnClickListener(this);
		View searchbutton = findViewById(R.id.searchbutton);
		searchbutton.setOnClickListener(this);
	}
	
    @Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.typeandsourcebutton:
			Intent i = new Intent(this, typeandsource.class);
			startActivity(i);
			break;
		case R.id.searchbutton:
			TextView searchbox = (TextView) findViewById(R.id.searchbox);
			String query = searchbox.getText().toString().trim();
			if(query.compareTo("") > 0)search(query);
			break;
		// More buttons go here (if any) ...
		}
	}
    
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case R.id.settings_button:
    			Intent i = new Intent(this, settings.class);
    			startActivity(i);
            break;
            case R.id.credits_button:
            	AlertDialog credits = new AlertDialog.Builder(this).create();
                credits.setTitle(getString(R.string.credits));
                credits.setMessage(getString(R.string.credits_text));
                credits.show();
            break;
            case R.id.quit_button:
            	finish();
            break;
        }
		return false;
    }

	private void search(String query) {
		final Intent k = new Intent(this, search.class);
		k.putExtra("query", query);
		final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.searching));
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.show();
        new Thread() {
            public void run() {
                 try{
             		startActivity(k);
                 } catch (Exception e) {  }
                 pd.dismiss();
            }
       }.start(); 
	}
}