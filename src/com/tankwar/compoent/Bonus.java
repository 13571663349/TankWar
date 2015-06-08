package com.tankwar.compoent;

import java.util.Timer;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.tankwar.GameMap;
import com.tankwar.GameRes;
import com.tankwar.GameSound;
import com.tankwar.GameView;

final public class Bonus {
	public final static int BT_LIFEUP = 0;
	public final static int BT_TIMER  = 1;
	public final static int BT_SPADER = 2;
	public final static int BT_BOMB   = 3;
	public final static int BT_ATKUP  = 4;
	public final static int BT_SHIELD = 5;

	private int x = 0;
	private int y = 0;
	private int width = 28;
	private int height = 28;
	private int bonusType = -1;
	private int createTime = 0;
	private int frameCounter = 0;

	private final int SOUND_ID = GameSound.GSD_BONUS;
	private final int DURATION = 10;

	private boolean		isBonus;
	private Bitmap[]	bonusBitmap;
	private GameMap		mGameMap;
	private GameView	mGameView;
	private Timer		mShowTimer;


	public Bonus(GameMap GameMap, GameView GameView) {
		mGameMap = GameMap;
		mGameView = GameView;
		bonusBitmap = GameRes.getBonus();
	}


	public final void draw(Canvas c) {
		if (isBonus) {
			int curFps = mGameView.getFPS() >> 2;
			int showDistance = curFps >> 1;
			if (++frameCounter < showDistance)
				c.drawBitmap(bonusBitmap[bonusType], GameMap.LEFT_MARGIN + x, GameMap.TOP_MARGIN + y, null);
			else if (frameCounter >= curFps)
				frameCounter = 0;

			if (mGameView.getSecondCounter() - createTime > DURATION)
				isBonus = false;
		}
	}


	public final int getX() {
		return x;
	}


	public final int getY() {
		return y;
	}


	public final int getHeight() {
		return height;
	}


	public final int getType() {
		return bonusType;
	}


	public final int getWidth() {
		return width;
	}


	public final boolean isGetBonus(Tank t) {
		if (isBonus) {
			createTime = (int) mGameView.getSecondCounter();
			if (isCollisions(x, y, width, height, t.getX(), t.getY(),
				t.getW(), t.getH())) {
				return true;
			}
		}
		return false;
	}


	public final void deleteBonus() {
		isBonus = false;
	}


	public final int getBonus() {
		return bonusType;
	}


	public final void create() {
		x = (int) Math.abs(Math.random() * 10240 % GameMap.MAP_WIDTH);
		y = (int) Math.abs(Math.random() * 10240 % GameMap.MAP_HEIGHT);
		bonusType = (int) Math.abs(Math.random() * 64 % 6);
		isBonus = true;
		GameSound.play(SOUND_ID);
	}


	public final boolean isCollisions(int x1, int y1, int w1, int h1, int x2, int y2,
		int w2, int h2) {
		if (x1 >= x2 && x1 >= x2 + w2)
			return false;
		else if (x1 <= x2 && x1 + w1 <= x2)
			return false;
		else if (y1 >= y2 && y1 >= y2 + h2)
			return false;
		else if (y1 <= y2 && y1 + h1 <= y2)
			return false;

		return true;
	}


	public final void onGetBonus(Tank t) {
		switch(bonusType) {
			case Bonus.BT_ATKUP:
				t.onGetAttack();
				break;
			case Bonus.BT_BOMB:
				t.onGetBomb();
				break;
			case Bonus.BT_LIFEUP:
				t.onGetLife();
				break;
			case Bonus.BT_SPADER:
				t.onGetSpader();
				break;
			case Bonus.BT_SHIELD:
				t.onGetShield();
				break;
			case Bonus.BT_TIMER:
				t.onGetTimer();
				break;
		}
	}
}
