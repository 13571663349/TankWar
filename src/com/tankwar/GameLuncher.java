package com.tankwar;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.*;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RadioButton;

final public class GameLuncher extends Activity implements OnClickListener
{
	private View gameView 			= null;
	private RadioButton rdbSingle 	= null;
	private RadioButton rdbDouble 	= null;
	private CheckBox ckbCustom 		= null;
	private CheckBox ckbMusicCase	= null;
	public final String START_GAME	= "START_GAME";
	public final String START_TEST	= "TEST";

	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	if (getResources().getConfiguration().orientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
    							WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	setContentView(R.layout.game_launcher);

    	findViewById(R.id.btnStart).setOnClickListener(this);
		findViewById(R.id.btnExit).setOnClickListener(this);
		(rdbSingle = (RadioButton) findViewById(R.id.rdbSingle)).setOnClickListener(this);
		(rdbDouble = (RadioButton) findViewById(R.id.rdbDouble)).setOnClickListener(this);
		(ckbCustom = (CheckBox) findViewById(R.id.ckbCustomMap)).setOnClickListener(this);
		(ckbMusicCase = (CheckBox) findViewById(R.id.ckbMusicCase)).setOnClickListener(this);
    }


	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.btnStart:
				startGame();
				break;
			case R.id.btnExit:
				finish();
				break;
		}
	}


	private void startGame() {
		Bundle b = new Bundle();
		Intent i = new Intent();
		b.putInt("GameMode", rdbSingle.isChecked() ? 1 : 2);
		b.putBoolean("CustomMap", ckbCustom.isChecked() ? true : false);
		b.putBoolean("GameSound", ckbMusicCase.isChecked()? true : false);
		i.putExtras(b).setAction(START_GAME);
		startActivity(i);
		//startService(new Intent(START_TEST));
	}


     public void onConfigurationChanged(Configuration newConfig) {
    	 super.onConfigurationChanged(newConfig);
 	}

}



