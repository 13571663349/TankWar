package com.tankwar;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

final public class GameActivity extends Activity {
	private GameView gameView = null;
	private boolean  isExited = false;
	private static GameActivity mInstance = null;

	protected final void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
    						WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mInstance = this;
		gameView = new GameView(this);
		setContentView(gameView);
		gameView.onGameStart();
	}


	public final void onBackPressed() {
		try {
			gameView.onGameStop();
			gameView.getViewThread().join();
			gameView.release();
			finish();
		} catch (Throwable e) {
			GameLog.e(e);
		}
	}


	public final static GameActivity getInstance() {
		return mInstance;
	}


	protected final void onPause() {
		super.onPause();
		gameView.onGamePause();
	}


	protected final void onResume() {
		super.onResume();
		gameView.onGameResume();
	}


	@Override
	public final void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}


