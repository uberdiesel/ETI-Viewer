package com.android.luelinksviewer;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity{
	LuelinksViewer LueApp;	//Global Application Class
    EditText username;
    EditText password;
    CheckBox cbSave;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        LueApp = (LuelinksViewer)getApplicationContext();
        LueApp.clearCookies();	//If this activity is launched, something happened with the session. Clear the cookies
        
        username = (EditText) findViewById(R.id.login_username);
        username.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            	//handles editor button clicks
                if (actionId == EditorInfo.IME_ACTION_NEXT || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    password.requestFocus();
                    return true;
                }
                return false;
            }
        });
        password = (EditText) findViewById(R.id.login_password);
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            	//handles editor button clicks
                if (actionId == EditorInfo.IME_ACTION_GO || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    login(findViewById(R.id.login_btn));
                    return true;
                }
                return false;
            }
        });
        
        //cbSave = (CheckBox) findViewById(R.id.save_info);
        
        username.setText(LueApp.getSavedUsername());
        password.setText(LueApp.getSavedPassword());
        
        /*if (!username.getText().toString().contains("")){
        	cbSave.setChecked(false);
        }*/
        
        final Button loginbtn = (Button) findViewById(R.id.login_btn);
        loginbtn.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			try {
    				login(findViewById(R.id.login_btn));
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
        });
        
        
    }
    
    
    
    public void login(View v) {
    	//Onclick method for button
    	new LoginTask().execute("");
    }
    
    
	//AsyncTask <input, progress, results>
	private class LoginTask extends AsyncTask <String, Integer, Boolean > {
		ProgressDialog pd;
		@Override
		protected void onPreExecute(){
			//UI Thread, run before executing
			pd = ProgressDialog.show(Login.this, "Loading", "Logging in...");	//opens progress dialog
		}
		
		@Override
		protected Boolean doInBackground(String... url) {
			//what to do in the background
			try {
				return Helper.Login(username.getText().toString(), password.getText().toString());
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			
		}
		
		protected void onPostExecute(Boolean result){
			//UI Thread, what to do after
			pd.cancel();
			if (result){
				LueApp.saveCookies();
				/*if (cbSave.isChecked()){
					ETIapp.saveLogin(username.getText().toString(), password.getText().toString());
				}*/
				finish();
				
			}else {
				Toast.makeText(Login.this, "Please try again", Toast.LENGTH_LONG).show();
			}
			
		}
		
	}
	
}