package com.tankwar.compoent;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.tankwar.GameMap;
import com.tankwar.GameRes;
import com.tankwar.GameSound;
import com.tankwar.GameView;

final public class Explosion {
	private int x;
	private int y;
	private int allFrames;
	private int curFrame = 0;
	private int hittingType;
	private int times;
	private int explosionSound = GameSound.GSD_BLAST;
	private int bombSound = GameSound.GSD_BOMB;
	private int firstFrameDuration;
	private int secondFrameDuration;
	private int frameCounter;

	private boolean	isBomb = false;
	private boolean	isFinished = false;

	private final int MAX_FRAME = 2;

	private Bullet	 bullet;
	private GameView mGameView;
	private Bitmap[] expBitmap = GameRes.getExplode();

	private static ArrayList<Explosion> allExplosion = new ArrayList<Explosion>(4096);

	public Explosion(Bullet b, GameView gv) {
		x = b.getX() - ((expBitmap[curFrame].getWidth() - b.getW()) >> 1);
		y = b.getY() - ((expBitmap[curFrame].getWidth() - b.getH()) >> 1);
		bullet = b;
		mGameView = gv;
		allFrames = 1;
		allExplosion.add(this);
	}


	public Explosion(int x, int y, GameView gv) {
		this.x = x;
		this.y = y;
		mGameView = gv;
		allFrames = MAX_FRAME;
		allExplosion.add(this);
		GameSound.play(explosionSound);
	}


	public final static void drawAll(Canvas c) {
		ArrayList<Explosion> exps = allExplosion;
		int size = exps.size();

		for (int i = 0; i < size; i++) {
			Explosion e = exps.get(i);
			if (e != null) {
				e.draw(c);
				if (e.isFinished()) {
					exps.remove(e);
					--size;
				}
			}
		}
	}


	public final void draw(Canvas c) {
		if (isFinished)
			return;

		int fps = mGameView.getFPS();
		firstFrameDuration = allFrames == MAX_FRAME ? fps >> 2 : fps >> 4;
		secondFrameDuration = firstFrameDuration;

		c.drawBitmap(expBitmap[curFrame], GameMap.LEFT_MARGIN + x, GameMap.TOP_MARGIN + y, null);
		switch(curFrame) {
			case 0:
				if (++frameCounter >= firstFrameDuration) {
					++curFrame;
					frameCounter &= 0;
					x = x - ((expBitmap[curFrame].getWidth() - expBitmap[curFrame-1].getWidth()) >> 1);
					y = y - ((expBitmap[curFrame].getHeight() - expBitmap[curFrame-1].getHeight()) >> 1);
				}
				break;
			case 1:
				if (frameCounter++ >= secondFrameDuration)
					++curFrame;
				break;
		}

		if (curFrame >= allFrames)
			isFinished = true;
	}


	public final static ArrayList<Explosion> getAllExplosion() {
		return allExplosion;
	}


	public final boolean isFinished() {
		return isFinished;
	}


	public final static void reset() {
		allExplosion.clear();
	}
}
