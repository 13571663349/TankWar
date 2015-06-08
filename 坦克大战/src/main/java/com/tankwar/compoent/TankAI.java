package com.tankwar.compoent;

import com.tankwar.GameMap;
import com.tankwar.GameSound;
import com.tankwar.GameView;
import com.tankwar.interfaces.Workable;

public final class TankAI implements Workable {
	public final static int AE_STATE_OFF = 0;
	public final static int AE_STATE_ON	 = 1;
	public final static int MAX_CONTROLLALBE_ENEMY = EnemyTank.MAX_ACTIVED_TANK;
	public final static int MAX_CONTROLLABLE_PLAYER= GameView.getGameMode();

	private long 	lastTime;
	private boolean isOn;
	private boolean isPause;

	private GameMap		mGameMap;
	private GameView	mGameView;
	private Tank[]		mAIEnemy;
	private Tank[]		mAIPlayer;


	public TankAI(GameMap map, GameView view, Tank[] players, Tank[] enemies) {
		this(map, view);
		mAIEnemy = enemies;
		mAIPlayer = players;
	}


	public TankAI(GameMap map, GameView view) {
		isOn = true;
		isPause = false;
		mGameMap = map;
		mGameView = view;
	}


	private final void findPlayerSymbol() {
		int length = mAIEnemy.length;
		for (int i = 0; i < length; i++) {
			int x, y;

			x = mAIEnemy[i].getX();
			y = mAIEnemy[i].getY();
			
		}
	}


	@Override
	public final void work() {
		if (isPause)
			return;

		final int[] dirs = {
			0, 1, 2, 3, 3, 2, 1, 0
		};
		final boolean[] fire = {
			true, false, true, false, true, false, true, false
		};
		final int rand = (int) (Math.random() * 50 % dirs.length);
		int length = mAIEnemy.length;

		for (int i = 0; i < length; i++) {
			Tank mTank = mAIEnemy[i];
			if (!mTank.move())
				mTank.move(dirs[rand]);
			if (fire[rand])
				mTank.fire();
		}
	}


	public final void setPlayer(Tank[] t) {
		for (int i = 0; i < t.length; i++) {
			mAIPlayer[i] = t[i];
		}
	}


	public final void setEnemy(Tank[] t) {
		for (int i = 0; i < t.length; i++) {
			mAIEnemy[i] = t[i];
		}
	}


	public final boolean isTurnOn() {
		return isOn;
	}


	public final void pauseAI() {
		isPause = true;
	}


	public final void resumeAI() {
		isPause = false;
	}
}
