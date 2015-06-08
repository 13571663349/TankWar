package com.tankwar.compoent;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.tankwar.GameLog;
import com.tankwar.GameMap;
import com.tankwar.GameSound;
import com.tankwar.GameView;

final public class VirtualPad {
	public int D_Pad_W = 50;
	public int D_Pad_H = 50;
	public int A_Pad_W = 50;
	public int A_Pad_H = 50;
	public int keyDistance 	= 50;
	public int bottomMargin = 20;
	public int topMargin	= GameMap.ORIGINAL_HEIGHT - (bottomMargin + (D_Pad_H << 1) + keyDistance);
	public int leftMargin 	= 20;
	public int rightMargin 	= 20;

	public Rect upKeyRect 	= null;
	public Rect downKeyRect = null;
	public Rect leftKeyRect = null;
	public Rect rightKeyRect= null;
	public Rect atkKeyRect	= null;
	public RectF ukRealRect = new RectF();
	public RectF dkRealRect = new RectF();
	public RectF lkRealRect = new RectF();
	public RectF rkRealRect = new RectF();
	public RectF akRealRect = new RectF();

	private PlayerTank myPlayer = null;

	public final int KEY_UP		= 1;
	public final int KEY_RIGHT	= 2;
	public final int KEY_LEFT	= 3;
	public final int KEY_DOWN	= 4;
	public final int KEY_ATTACK = 5;
	public final int KEY_NONE	= 6;
	public final int SOUND_ID	=  GameSound.GSD_PLAYERMOVE;

	public VirtualPad() {
		Matrix mtx = new Matrix();
		mtx.setScale(GameView.WIDTH_SCALE, GameView.HEIGHT_SCALE);

		upKeyRect  = new Rect(leftMargin + D_Pad_W + ((keyDistance - D_Pad_W) / 2),
			topMargin, rightMargin + D_Pad_W + ((keyDistance - D_Pad_W) / 2) + D_Pad_W,
			topMargin + D_Pad_H);
		downKeyRect = new Rect(upKeyRect.left, upKeyRect.top + keyDistance + D_Pad_H,
			upKeyRect.right, upKeyRect.top + keyDistance + D_Pad_H + D_Pad_H);
		leftKeyRect = new Rect(leftMargin, topMargin + D_Pad_H +((keyDistance -
			D_Pad_W) / 2), leftMargin + D_Pad_W, topMargin + D_Pad_H + ((keyDistance - D_Pad_W) / 2)
			+ D_Pad_W);
		rightKeyRect = new Rect(leftKeyRect.left + keyDistance + D_Pad_W, leftKeyRect.top,
			leftKeyRect.left + keyDistance + D_Pad_W*2, leftKeyRect.bottom);
		atkKeyRect	 = new Rect(GameMap.ORIGINAL_WIDTH - rightMargin - A_Pad_W, GameMap.ORIGINAL_HEIGHT -
			bottomMargin - A_Pad_H, GameMap.ORIGINAL_WIDTH - rightMargin, GameMap.ORIGINAL_HEIGHT -
			bottomMargin);

		mtx.mapRect(ukRealRect, new RectF(upKeyRect));
		mtx.mapRect(dkRealRect, new RectF(downKeyRect));
		mtx.mapRect(lkRealRect, new RectF(leftKeyRect));
		mtx.mapRect(rkRealRect, new RectF(rightKeyRect));
		mtx.mapRect(akRealRect, new RectF(atkKeyRect));
	}


	public VirtualPad(PlayerTank t) {
		this();
		myPlayer = t;
	}


	public final void draw(Canvas c, Paint p) {
		p.setARGB(100, 255, 255, 255);
		c.drawRect(upKeyRect, p);
		c.drawRect(downKeyRect, p);
		c.drawRect(leftKeyRect, p);
		c.drawRect(rightKeyRect, p);
		c.drawRect(atkKeyRect, p);
	}


	public void onLoosen(int x, int y) {
		if (myPlayer.isDied()) {
			return;
		}
		switch(getKey(x, y)) {
			case KEY_UP:
				onKeyUp(true);
				break;
			case KEY_DOWN:
				onKeyDown(true);
				break;
			case KEY_RIGHT:
				onKeyRight(true);
				break;
			case KEY_LEFT:
				onKeyLeft(true);
				break;
			case KEY_ATTACK:
				onKeyAttack(true);
				break;
		}
	}


	private void onKeyAttack(boolean isLoosen) {
		if (!isLoosen) {
			myPlayer.fire();
		}
	}


	private void onKeyLeft(boolean isLoosen) {
		if (!isLoosen) {
			myPlayer.move(Tank.DIR_LEFT);
		}else{
			myPlayer.stop();
		}
	}


	private void onKeyRight(boolean isLoosen) {
		if (!isLoosen) {
			myPlayer.move(Tank.DIR_RIGHT);
		}else{
			myPlayer.stop();
		}
	}


	private void onKeyDown(boolean isLoosen) {
		if (!isLoosen) {
			myPlayer.move(Tank.DIR_DOWN);
		}else{
			myPlayer.stop();
		}
	}


	private void onKeyUp(boolean isLoosen) {
		if (!isLoosen) {
			myPlayer.move(Tank.DIR_UP);
		}else{
			myPlayer.stop();
		}
	}


	private int getKey(int x, int y) {
		if (ukRealRect.contains(x, y)) {
			return KEY_UP;
		}else if (dkRealRect.contains(x, y)) {
			return KEY_DOWN;
		}else if (rkRealRect.contains(x, y)) {
			return KEY_RIGHT;
		}else if (lkRealRect.contains(x, y)) {
			return KEY_LEFT;
		}else if (akRealRect.contains(x, y)){
			return KEY_ATTACK;
		}else{
			return KEY_NONE;
		}
	}


	public void onPressed(int x, int y) {
		boolean play = true;
		if (myPlayer.isDied()) {
			return;
		}
		//GameSound.play(GameSound.GSD_TANKMOVE);
		switch(getKey(x, y)) {
			case KEY_UP:
				onKeyUp(false);
				break;
			case KEY_DOWN:
				onKeyDown(false);
				break;
			case KEY_RIGHT:
				onKeyRight(false);
				break;
			case KEY_LEFT:
				onKeyLeft(false);
				break;
			case KEY_ATTACK:
				onKeyAttack(false);
				break;
		}
	}


	public void onLongPress(int x, int y) {
		if (myPlayer.isDied()) {
			return;
		}
		//GameSound.play(id);
		switch(getKey(x, y)) {
			case KEY_UP:
				onKeyUp(false);
				break;
			case KEY_DOWN:
				onKeyDown(false);
				break;
			case KEY_RIGHT:
				onKeyRight(false);
				break;
			case KEY_LEFT:
				onKeyLeft(false);
				break;
			case KEY_ATTACK:
				onKeyAttack(false);
				break;
		}
	//	GameSound.stop((Integer)id);
	}


	public void setPlayer(PlayerTank playerOne) {
		myPlayer = playerOne;
	}
}
