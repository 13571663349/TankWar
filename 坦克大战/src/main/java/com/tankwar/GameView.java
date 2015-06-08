package com.tankwar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.tankwar.compoent.Bonus;
import com.tankwar.compoent.Bullet;
import com.tankwar.compoent.EnemyTank;
import com.tankwar.compoent.Explosion;
import com.tankwar.compoent.PlayerTank;
import com.tankwar.compoent.TankAI;
import com.tankwar.compoent.VirtualPad;

final public class GameView extends SurfaceView
		implements SurfaceHolder.Callback, Runnable
{
	public final static int SCREEN_WIDTH  	= GameActivity.getInstance().getResources()
												.getDisplayMetrics().widthPixels;
	public final static int SCREEN_HEIGHT 	= GameActivity.getInstance().getResources()
												.getDisplayMetrics().heightPixels;

	public final static int GM_SINGLE_PLAY	= 1;
	public final static int GM_DOUBLE_PALY	= 2;
	public final static int ORIGINAL_WIDTH	= GameMap.ORIGINAL_WIDTH;
	public final static int ORIGINAL_HEIGHT	= GameMap.ORIGINAL_HEIGHT;
	public final static int MAP_LEFT		= GameMap.LEFT_MARGIN;
	public final static int MAP_TOP			= GameMap.TOP_MARGIN;
	public final static int MAP_RIGHT		= GameMap.LEFT_MARGIN + GameMap.MAP_WIDTH;
	public final static int MAP_BOTTOM		= GameMap.TOP_MARGIN + GameMap.MAP_HEIGHT;

	public final static int DS_GAME_INIT	= 0;
	public final static int DS_GAME_START	= 1;
	public final static int DS_GAME_PAUSE	= 2;
	public final static int DS_GAME_RESUME	= 3;
	public final static int DS_GAME_STOP	= 4;
	public final static int DS_GAME_FAILED	= 5;
	public final static int DS_GAME_SUCCESS	= 6;

	public final static float WIDTH_SCALE	= (float)SCREEN_WIDTH / (float)GameMap.ORIGINAL_WIDTH;
	public final static float HEIGHT_SCALE	= (float)SCREEN_HEIGHT / (float)GameMap.ORIGINAL_HEIGHT;

	private int  mGameFps		  = 0;
	private int	 mFrameCounter	  = 40;
	private int  mDrawState		  = -1;
	private int  mFpsCountDistance= 1000;
	private long mSpendTime		  = 0;
	private	long mPerSecCounter	  = 0;
	private boolean isSoundOn	  = true;
	private boolean isCustomMap	  = false;
	private boolean isGamePause	  = false;
	private boolean isGameExit	  = false;
	private boolean isGameFailed  = false;
	private boolean isDrawOpen	  = false;

	private Paint 	mPaint;
	private Canvas 	mCanvas;
	private GameMap mGameMap;
	private Thread 	mViewThread;
	private Bonus	mBonus;

	private TankAI			mTankAI;
	private GameWorker 		mGameWorker;
	private SurfaceHolder 	mHolder;
	private GameActivity 	mActivity;
	private Matrix			mMatrix;
	private VirtualPad		mVirtualPad;

	private static int		gameMode  = GM_SINGLE_PLAY;
	private static GameView mInstance = null;


	public GameView(Context context) {
		super(context);
		mActivity = (GameActivity) context;
		initGame();
	}


	private final void initGame() {
		Bundle b 	= mActivity.getIntent().getExtras();
		gameMode 	= b.getInt("GameMode");
		isSoundOn 	= b.getBoolean("GameSound");
		isCustomMap = b.getBoolean("CustomMap");
		loadResourece();

		mPaint 		= new Paint();
		mGameWorker = new GameWorker(this);
		mViewThread	= new Thread(this);
		mGameMap 	= new GameMap(GameRes.getTiles(), this);
		mMatrix 	= new Matrix();
		mVirtualPad = new VirtualPad();
		mHolder		= getHolder();
		mTankAI		= new TankAI(mGameMap, this, null, EnemyTank.getTanks());
		mBonus		= new Bonus(mGameMap, this);

		PlayerTank.init(mGameMap, this);
		EnemyTank.init(mGameMap, this);

		mMatrix.setScale(WIDTH_SCALE, HEIGHT_SCALE);
		mVirtualPad.setPlayer((PlayerTank) PlayerTank.getPlayerOne());
		mHolder.addCallback(this);
		mGameWorker.addWork(new Bullet())
				   .addWork(mTankAI);
	}


	private final void loadResourece() {
		GameRes.initialize(mActivity.getResources());
		GameSound.load(mActivity);
	}


	private final void drawStartAnimation(boolean isOpen) {
		int start = SCREEN_HEIGHT >> 1, end = SCREEN_HEIGHT, right = SCREEN_WIDTH;
		for (int y = start; y <= end && !isGameExit; y+=10) {
			if (isOpen == true) {
				Canvas c = mHolder.lockCanvas(new Rect(0, end-y, right, y));
				if (c != null) {
					mPaint.setColor(Color.BLACK);
					c.drawRect(new Rect(0, end-y, right, y), mPaint);
					mHolder.unlockCanvasAndPost(c);
				}
			}else{
				mPaint.setColor(Color.GRAY);
				Canvas c = mHolder.lockCanvas(null);
				if (c != null) {
					c.drawRect(new Rect(0, 0, right, y-start), mPaint);
					c.drawRect(new Rect(0, end-(y-start), right, end), mPaint);
					mHolder.unlockCanvasAndPost(c);
				}
			}
		}
	}


	private final void beforeGameStart() {
		GameSound.play(GameSound.GSD_START);
		drawStartAnimation(false);
		mGameMap.drawOnBuffer();
		drawStartAnimation(true);
		//GameSound.play(GameSound.GSD_ENEMYMOVE, -1, 1.3f);
	}


	private final void doDraw() {
		if (!isDrawOpen) {
			beforeGameStart();
			isDrawOpen = true;
			return;
		}

		mCanvas = mHolder.lockCanvas();
		if (mCanvas != null && !isGameExit && !isGamePause) {
			mCanvas.setMatrix(mMatrix);
			drawGame(mCanvas);
			mHolder.unlockCanvasAndPost(mCanvas);
			countFps();
			mGameWorker.doWorkList();
		}
	}


	private final void drawGame(Canvas c) {
		drawBackgroud(c);
		mGameMap.draw(c, mPaint);
		drawFpsText(c);
		Bullet.drawAll(c);
		Explosion.drawAll(c);
		PlayerTank.drawAll(c);
		EnemyTank.drawAll(c);
		mBonus.draw(c);
		mVirtualPad.draw(c, mPaint);
	}


	private final void drawBackgroud(Canvas c) {
		mPaint.setColor(Color.GRAY);
		c.drawRect(0, 0, ORIGINAL_WIDTH, ORIGINAL_HEIGHT, mPaint);
		mPaint.setColor(Color.BLACK);
		c.drawRect(MAP_LEFT, MAP_TOP, MAP_RIGHT, MAP_BOTTOM, mPaint);
	}


	private final void drawFpsText(Canvas c) {
		Rect rect = new Rect();
		String fps = "fps:"+Integer.toString(mGameFps);
		mPaint.setTextSize(14);
		mPaint.getTextBounds(fps, 0, fps.length(), rect);
		mPaint.setColor(Color.GRAY);
		c.drawRect(rect, mPaint);
		mPaint.setColor(Color.GREEN);
		c.drawText(fps, 0, rect.bottom - rect.top, mPaint);
	}


	private final void countFps() {
		if (System.currentTimeMillis() - mSpendTime >= mFpsCountDistance) {
			mGameFps = mFrameCounter >> (mFpsCountDistance / 2000);
			mSpendTime = System.currentTimeMillis();
			mFrameCounter = 0;
			++mPerSecCounter;
		}else{
			mFrameCounter = -~mFrameCounter;
		}
	}


	private final void pauseGame() throws InterruptedException {
		if (mViewThread.getState().equals(Thread.State.RUNNABLE)) {
			isGamePause = true;
			GameSound.pauseAll();
			synchronized(mViewThread) {
				mViewThread.wait();
			}
		}

	}


	private synchronized final void resumeGame() {
		isGamePause = false;
		mDrawState = DS_GAME_START;
		GameSound.resumeAll();
	}


	private final void stopGame() throws InterruptedException {
		isGameExit = true;
	}


	public synchronized final void onGameStart() {
		mDrawState = DS_GAME_START;
	}


	public synchronized final void onGamePause() {
		if (!isGameExit && mDrawState != DS_GAME_STOP)
			mDrawState = DS_GAME_PAUSE;

	}


	public final void onGameResume() {
		if (mDrawState == DS_GAME_PAUSE) {
			synchronized(mViewThread) {
				mViewThread.notify();
			}
			mDrawState = DS_GAME_RESUME;
		}

	}


	public synchronized final void onGameStop() {
		mDrawState = DS_GAME_STOP;
	}


	public synchronized final void onGameFailed() {
		mDrawState = DS_GAME_FAILED;
	}


	public synchronized final void onGameSuccess() {
		mDrawState = DS_GAME_SUCCESS;
	}


	public final static int getGameMode() {
		return gameMode;
	}


	public synchronized final boolean isPause() {
		return isGamePause;
	}


	public synchronized final boolean isExit() {
		return isGameExit;
	}


	public synchronized final boolean isFailed() {
		return isGameFailed;
	}


	public final long getSecondCounter() {
		return mPerSecCounter;
	}


	public final int getFPS() {
		return mGameFps;
	}


	public final TankAI getTankAI() {
		return mTankAI;
	}


	public final Bonus getBonus() {
		return mBonus;
	}


	public final Thread getViewThread() {
		return mViewThread;
	}


	public final Matrix getScaledMatrix() {
		return mMatrix;
	}


	public final boolean onTouchEvent(MotionEvent e) {
		super.onTouchEvent(e);
		int x = 0, y = 0, index = -1;

		if (mVirtualPad != null) {
			switch(e.getAction()) {
				case MotionEvent.ACTION_DOWN:
					x = (int) e.getX();
					y = (int) e.getY();
					mVirtualPad.onPressed(x, y);
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					index = e.getActionIndex();
					x = (int) e.getX(index);
					y = (int) e.getY(index);
					mVirtualPad.onPressed(x, y);
					break;
				case MotionEvent.ACTION_UP:
					x = (int) e.getX();
					y = (int) e.getY();
					mVirtualPad.onLoosen(x, y);
					break;
				case MotionEvent.ACTION_POINTER_UP:
					index = e.getActionIndex();
					x = (int) e.getX(index);
					y = (int) e.getY(index);
					mVirtualPad.onPressed(x, y);
					break;
				case MotionEvent.ACTION_MOVE:
					int pc = e.getPointerCount();
					for (int i = 0; i < pc; i++) {
						x = (int) e.getX(i);
						y = (int) e.getY(i);
						mVirtualPad.onLongPress(x, y);
					}
					break;
			}
		}
		return true;
	}


	@Override
	public final void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	
	}


	@Override
	public final void surfaceCreated(SurfaceHolder arg0) {
		if (mViewThread.getState() == Thread.State.NEW) {
			mViewThread.setPriority(Thread.MAX_PRIORITY);
			mViewThread.start();
		}
	}


	@Override
	public final void surfaceDestroyed(SurfaceHolder arg0) {
	}


	@Override
	public final void run() {
		try{
			while ( !isGameExit ) {
				switch(mDrawState) {
					case DS_GAME_START:
						doDraw();
						break;
					case DS_GAME_PAUSE:
						pauseGame();
						break;
					case DS_GAME_RESUME:
						resumeGame();
						break;
					case DS_GAME_STOP:
						stopGame();
						break;
					case DS_GAME_FAILED:
						break;
					case DS_GAME_SUCCESS:
						break;
				}
			}
		}catch(Throwable e) {
			GameLog.e(e);
		}
	}


	public final void release() {
		Bullet.reset();
		Explosion.reset();
		mGameMap.recycle();
		EnemyTank.reset();
		GameSound.releaseResource();
		GameRes.recycle();
	}


	public final static GameView getInstance() {
		return mInstance;
	}
}
