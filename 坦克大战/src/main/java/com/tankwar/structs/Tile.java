package com.tankwar.structs;

import android.graphics.Rect;

import com.tankwar.GameLog;
import com.tankwar.GameMap;

public class Tile {
	protected int absX;
	protected int absY;
	protected int width;
	protected int height;
	protected int tileType;
	protected int destructionTimes;

	protected Rect	 tileRect;
	protected Rect[] tileRects;

	public final static class WallTile extends Tile implements Destructible {
		public WallTile(int x, int y, int w, int h, boolean[] rOpts) {
			super(x, y, w, h, GameMap.TILE_WALL);
			int ww = w >> 2;
			int wh = h >> 2;
			Rect[] tempRect = new Rect[16];
			for (int i = 0, c = 0; i < w; i += ww) {
				for (int j = 0; j < h; j += wh) {
					if (rOpts != null && !rOpts[c]) {
						c++;
						continue;
					}
					tempRect[c++] = new Rect(x + i, y + j, x + i + ww, y + j + wh);
				}
			}
			tileRects = tempRect;
		}

		@Override
		public final Rect destroyRegion(int index) {
			int length = tileRects.length;
			Rect tempR = null;

			if (length > index && index >= 0) {
				tempR = new Rect(tileRects[index]);
				tileRects[index] = null;
				destructionTimes++;
			}
			if (destructionTimes >= length) {
				tileType = GameMap.TILE_LAND;
			}
			return tempR;
		}
	}

	public final static class IronTile extends Tile implements Destructible {
		public IronTile(int x, int y, int w, int h, boolean[] rOpts) {
			super(x, y, w, h, GameMap.TILE_IRON);
			tileRects = new Rect[4];
			int ww = w >> 1;
			int wh = h >> 1;
			Rect[] tempRect = new Rect[4];
			for (int i = 0, c = 0; i < w; i += ww) {
				for (int j = 0; j < h; j += wh) {
					if (rOpts != null && !rOpts[c]) {
						c++;
						continue;
					}
					tempRect[c++] = new Rect(x + i, y + j, x + i + ww, y + j + wh);
				}
			}
			tileRects = tempRect;
		}

		@Override
		public final Rect destroyRegion(int index) {
			int length = tileRects.length;
			Rect tempR = null;
			if (length > index && index >= 0) {
				tempR = new Rect(tileRects[index]);
				tileRects[index] = null;
				destructionTimes++;
			}
			if (destructionTimes >= length) {
				tileType = GameMap.TILE_LAND;
			}
			return tempR;
		}
	}


	protected static interface Destructible {
		public Rect destroyRegion(int index);
	}



	public Tile(int x, int y, int w, int h, int type) {
		absX = x;
		absY = y;
		width = w;
		height = h;
		tileType = type;
		tileRect = new Rect(x, y, x + w, y + h);
		tileRects = null;
		destructionTimes = 0;
	}

	public final int getX() {
		return absX;
	}

	public final int getY() {
		return absY;
	}

	public final int getW() {
		return width;
	}

	public final int getH() {
		return height;
	}

	public final int getType() {
		return tileType;
	}


	public final Rect getRect() {
		return tileRect;
	}

	public final Rect[] getRects() {
		return tileRects;
	}
}