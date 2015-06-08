package com.tankwar.compoent;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

import com.tankwar.GameLog;
import com.tankwar.GameMap;
import com.tankwar.GameRes;
import com.tankwar.GameView;
import com.tankwar.interfaces.Workable;

final public class EnemyTank extends Tank {
	private final static int W_LOW_TANK = 0;
	private final static int R_LOW_TANK = 1;
	private final static int W_MID_TANK = 2;
	private final static int R_MID_TANK = 3;
	private final static int G_BIG_TANK = 4;
	private final static int Y_BIG_TANK = 5;
	private final static int W_BIG_TANK = 6;
	private final static int R_BIG_TANK = 7;

	public final static int MAX_TANK_NUM 	 = 20;
	public final static int MAX_ACTIVED_TANK = 3;
	private final int[] tankSpeeds = {	1, 1, 2, 2, 1, 1, 1, 1	};
	private final int[] tankLifes  = {	1, 2, 1, 2, 4, 3, 1, 2	};

	private int 		id = 0;
	private Point[] 	bornPosition;
	private static int	currentTanks;
	private static Tank[] allTanks   = new Tank[MAX_ACTIVED_TANK];


	public EnemyTank(int id, GameMap gm, GameView gv) {
		super(TT_ENEMY, DIR_DOWN, GameRes.getEnemyTankBitmaps(), gm, gv);
		tankLevel = (int)Math.abs(Math.random() * 64 % 8);
		bornPosition = gm.enemyBornPos;
		this.id   = id;
		Point pos = bornPosition[id % 3];
		x 		  = gameMap.getMapXByBlockX(pos.x);
		y 		  = gameMap.getMapYByBlockY(pos.y);
		life      = tankLifes[tankLevel];
		speed     = tankSpeeds[tankLevel];
		splashDistance = speed << 2;
	}


	public EnemyTank() {}



	public final Bitmap getBitmap(int i) {
		return tankBitmaps[direction][(tankLevel<<1)+i];
	}


	@Override
	public void draw(Canvas c) {
		if (isDied)
			return;

		int _s = splashDistance >> 1;
		int  i = ++frameCounter % splashDistance < _s ? 0 : 1,
			_x = gameMap.getAbsoluteX(x),
			_y = gameMap.getAbsoluteY(y);

		if (!isMoving) i = 0;

		c.drawBitmap(getBitmap(i), _x + leftMargin, _y + topMargin, null);
	}


	@Override
	public final boolean isCollisionsTank(int tempX, int tempY) {
		Tank[] pl = PlayerTank.getAllPlayers();
		int length= pl.length;
		
		for (int i = 0; i < length; i++) {
			if (pl[i] != null && !pl[i].isDied()) {
				if (isCollisions(tempX, tempY, width, height, pl[i].getX(), pl[i].getY(), width, height)) {
					return true;
				}
			}
		}

		length = allTanks.length;
		for (int i = 0; i < length; i++) {
			Tank e = allTanks[i];
			if (e != null && e != this && !e.isDied()) {
				if (isCollisions(tempX, tempY, width, height, e.getX(), e.getY(), width, height)) {
					return true;
				}
			}
		}
		return false;
	}


	public final static void init(GameMap gm, GameView gv) {
		for (int i = 0; i < MAX_ACTIVED_TANK; i++) {
			allTanks[i] = new EnemyTank(i, gm, gv);
			++currentTanks;
		}
	}


	public final void onDied() {
		super.onDied();
		allTanks[id] = new EnemyTank(id, gameMap, gameView);
		if (++currentTanks > MAX_TANK_NUM) {
			gameView.onGameSuccess();
		}
	}


	public final void onHittingBy(Tank t) {
		super.onHittingBy(t);
		switch(tankLevel) {
			case R_LOW_TANK:
			case R_MID_TANK:
			case R_BIG_TANK:
				mBonus.create();
				if (tankLevel == R_BIG_TANK)
					tankLevel = G_BIG_TANK;
				else
					--tankLevel;
				break;
			case G_BIG_TANK:
			case Y_BIG_TANK:
			case W_BIG_TANK:
				++tankLevel;
				break;
			default:
				--tankLevel;
		}
	}


	public final static Tank[] getTanks() {
		return allTanks;
	}


	public final static void drawAll(Canvas c) {
		int length = allTanks.length;
		for (int i = 0; i < length; i++) {
			Tank t = allTanks[i];
			if (t != null) {
				t.draw(c);
			}
		}
	}


	public final static void reset() {
		for (Tank t : allTanks) {
			if (t != null) {
				t = null;
			}
		}
		currentTanks = 0;
	}


	@Override
	public final void onGetLife() {
	}


	@Override
	public final void onGetAttack() {
	}


	@Override
	public final void onGetShield() {
	}


	@Override
	public final void onGetBomb() {
	}


	@Override
	public final void onGetSpader() {
	}


	@Override
	public final void onGetTimer() {
	}
}
