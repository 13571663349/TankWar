package com.tankwar.compoent;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

import com.tankwar.GameMap;
import com.tankwar.GameRes;
import com.tankwar.GameSound;
import com.tankwar.GameView;

final public class PlayerTank extends Tank {
	public final static int PLAYER_ONE  = 0;
	public final static int PLAYER_TWO  = 1;
	public final static int PLAYER_LIFE = 2;
	public final static int MAX_PLAYER  = 2;
	public final static int MAX_LEVEL	= 3;

	public final int[] playerSpeeds = {
		2, 2, 2, 2
	};

	private int 	mPlayer 	 = -1;
	private int		gameMode	 = GameView.getGameMode();
	private int 	fireSound	 = GameSound.GSD_FIRE;
	private int 	bonusSound	 = GameSound.GSD_GETBONUS;
	private Point[] bornPosition = null;
	private static Tank[] allPlayers = null;
	

	public PlayerTank(int i, GameMap gm, GameView gv) {
		super(TT_PLAYER, DIR_UP, GameRes.getPlayerTankBitmaps()[i], gm, gv);
		bornPosition= gm.playerBornPos;
		Point pos	= bornPosition[i];
		x 			= gameMap.getMapXByBlockX(pos.x);
		y 			= gameMap.getMapYByBlockY(pos.y);
		life		= PLAYER_LIFE;
		mPlayer		= i;
		isPlayer 	= true;
		speed 		= playerSpeeds[tankLevel];
		allPlayers  = new Tank[gv.getGameMode()];
		allPlayers[i] = this;
		splashDistance = speed * 3;
	}


	public final Bitmap getBitmap(int i) {
		return tankBitmaps[direction][(tankLevel<<1)+i];
	}


	public final void draw(Canvas c) {
		if (isDied) {
			return;
		}

		int _s = splashDistance >> 1;
		int  i = ++frameCounter % splashDistance < _s ? 0 : 1,
			_t = i,
			_x = gameMap.getAbsoluteX(x),
			_y = gameMap.getAbsoluteY(y);

		if (!isMoving)
			_t = 0;
		c.drawBitmap(getBitmap(_t), _x + leftMargin, _y + topMargin, null);

		if (isShield) {
			c.drawBitmap(shieldBitmaps[i], _x, _y, null);
			if (gameView.getSecondCounter() - shieldGetTime > shieldDuration)
				isShield = false;
		}
	}


	public final boolean fire() {
		if (super.fire()) {
			GameSound.play(fireSound);
			return true;
		}
		return false;
	}


	@Override
	protected final boolean isCollisionsTank(int x, int y) {
		Tank[] gl = EnemyTank.getTanks();

		if (gameMode == GameView.GM_DOUBLE_PALY) {
			Tank p = isPlayerOne() ? getPlayerTwo() : getPlayerOne();
			if (p != null && isCollisions(x, y, width, height, p.getX(), p.getY(), width, height)) {
				return true;
			}
		}

		for (int i = 0; i < gl.length; i++) {
			Tank g = gl[i];
			if (g != null) {
				if (isCollisions(x, y, width, height, g.getX(), g.getY(), width, height)) {
					return true;
				}
			}
		}
		return false;
	}


	public final void onDied() {
		super.onDied();
		allPlayers[mPlayer] = null;
	}


	public final boolean isPlayerOne() {
		return mPlayer == PLAYER_ONE;
	}


	public final boolean isPlayerTwo() {
		return mPlayer == PLAYER_TWO;
	}


	public final static Tank getPlayerOne() {
		return allPlayers[PLAYER_ONE];
	}


	public final static Tank getPlayerTwo() {
		return allPlayers[PLAYER_TWO];
	}


	public final static void drawAll(Canvas c) {
		for (Tank player : allPlayers) {
			if (player != null)
				player.draw(c);
		}
	}


	public final static void init(GameMap gm, GameView gv) {
		if (GameView.getGameMode() == GameView.GM_DOUBLE_PALY) {
			new PlayerTank(PLAYER_TWO, gm, gv);
		}
		new PlayerTank(PLAYER_ONE, gm, gv);
	}


	public final static Tank[] getAllPlayers() {
		return allPlayers;
	}


	@Override
	public final void onGetLife() {
		mBonus.deleteBonus();
		GameSound.play(GameSound.GSD_GETLIFE);
		life++;
	}


	@Override
	public final void onGetAttack() {
		mBonus.deleteBonus();
		GameSound.play(bonusSound);
		tankLevel = Math.min(MAX_LEVEL, tankLevel + 1);
		speed = playerSpeeds[tankLevel];
	}


	@Override
	public final void onGetShield() {
		mBonus.deleteBonus();
		GameSound.play(bonusSound);
		isShield = true;
		shieldGetTime = (int) gameView.getSecondCounter();
	}


	@Override
	public final void onGetBomb() {
		mBonus.deleteBonus();
		GameSound.play(GameSound.GSD_BOMB);
		Tank[] ts = EnemyTank.getTanks();
		for (int i = 0; i < ts.length; i++) {
			if (ts[i] != null)
				((EnemyTank)ts[i]).onDied();
		}
	}


	@Override
	public final void onGetSpader() {
		mBonus.deleteBonus();
		GameSound.play(bonusSound);
	}


	@Override
	public void onGetTimer() {
		mBonus.deleteBonus();
		GameSound.play(bonusSound);
		gameView.getTankAI().pauseAI();
	}
}
