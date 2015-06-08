package com.tankwar;

import java.util.ArrayList;
import java.util.Arrays;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import com.tankwar.compoent.Explosion;
import com.tankwar.structs.Tile;
import com.tankwar.structs.Tile.IronTile;
import com.tankwar.structs.Tile.WallTile;


final public class GameMap
{
	public final static int TILE_HEIGHT 	= 32;
	public final static int TILE_WIDTH	 	= 32;
	public final static int X_TILE_NUM		= 13;
	public final static int Y_TILE_NUM		= 13;
	public final static int MAP_LEFT		= 0;
	public final static int MAP_TOP			= 0;
	public final static int MAP_WIDTH 	 	= X_TILE_NUM * TILE_WIDTH;
	public final static int MAP_HEIGHT 		= Y_TILE_NUM * TILE_HEIGHT;
	public final static int LEFT_MARGIN		= TILE_WIDTH;
	public final static int RIGHT_MARGIN	= TILE_WIDTH << 1;
	public final static int TOP_MARGIN		= TILE_HEIGHT >> 1;
	public final static int BOTTOM_MARGIN	= TILE_HEIGHT;
	public final static int TILE_WALL		= 0x00;
	public final static int	TILE_IRON	 	= 0x01;
	public final static int TILE_TREE	 	= 0x02;
	public final static int TILE_WATER	 	= 0x03;
	public final static int TILE_SYMBOL	 	= 0x05;
	public final static int TILE_DESTROY 	= 0x06;
	public final static int TILE_LAND		= 0x07;
	public final static int ORIGINAL_WIDTH	= LEFT_MARGIN + MAP_WIDTH + RIGHT_MARGIN;
	public final static int ORIGINAL_HEIGHT	= TOP_MARGIN + MAP_HEIGHT + BOTTOM_MARGIN;
	public final static String MAP_FILE_EXT	= ".map";

	private Rect		mapRect 		 = null;
	private boolean		isUpdate		 = false;
	private boolean		mapIsDrawed		 = false;
	public  boolean[][]	wallOptions		 = {
		{false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true},
		{false, false, false, false, false, false, false, false, false, false, true, true, false, false, true, true},
		{false, false, true, true, false, false, true, true, false, false, true, true, false, false, true, true},
		{false, false, true, true, false, false, true, true, false, false, false, false, false, false, false, false},
		{true, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false},
	};
	public  boolean[][]	ironOptions		= {
		{false, false, true, true},
		{false, false, true, true},
		{false, true, false, true},
		{true, true, false, false},
		{true, true, false, false},
	};

	private Bitmap[]	mapTiles;
	private Bitmap		MAP_BITMAP;
	private Bitmap		MAP_BUFFER;
	private Bitmap		tileWall;
	private Bitmap		tileIron;
	private GameView	mGameView;

	public final Point[]enemyBornPos	 = {new Point(0, 0), new Point(6,0), new Point(12, 0)};
	public final Point[]playerBornPos	 = {new Point(3, 12), new Point(9, 12)};
	public final Point	playerSymbolPos	 = new Point(6, 12);
	private Tile[][]	mapData			 = new Tile[X_TILE_NUM][Y_TILE_NUM];
	private Paint 		mPaint			 = new Paint();
	public  Tile[]		playerSymbolWall = {
		new WallTile(playerSymbolPos.x-1 << 5, playerSymbolPos.y << 5, TILE_WIDTH, TILE_HEIGHT, wallOptions[0]),
		new WallTile(playerSymbolPos.x-1 << 5, playerSymbolPos.y-1 << 5, TILE_WIDTH, TILE_HEIGHT, wallOptions[1]),
		new WallTile(playerSymbolPos.x << 5, playerSymbolPos.y-1 << 5, TILE_WIDTH, TILE_HEIGHT, wallOptions[2]),
		new WallTile(playerSymbolPos.x+1 << 5, playerSymbolPos.y-1 << 5, TILE_WIDTH, TILE_HEIGHT, wallOptions[3]),
		new WallTile(playerSymbolPos.x+1 << 5, playerSymbolPos.y << 5, TILE_WIDTH, TILE_HEIGHT, wallOptions[4]),
	};

	public  Tile[]		playerSymbolIron = {
		new IronTile(playerSymbolPos.x-1 << 5, playerSymbolPos.y << 5, TILE_WIDTH, TILE_HEIGHT, ironOptions[0]),
		new IronTile(playerSymbolPos.x-1 << 5, playerSymbolPos.y-1 << 5, TILE_WIDTH, TILE_HEIGHT, ironOptions[1]),
		new IronTile(playerSymbolPos.x << 5, playerSymbolPos.y-1 << 5, TILE_WIDTH, TILE_HEIGHT, ironOptions[2]),
		new IronTile(playerSymbolPos.x+1 << 5, playerSymbolPos.y-1 << 5, TILE_WIDTH, TILE_HEIGHT, ironOptions[3]),
		new IronTile(playerSymbolPos.x+1 << 5, playerSymbolPos.y << 5, TILE_WIDTH, TILE_HEIGHT, ironOptions[4]),
	};

	private static GameMap mInstance = null;



	public GameMap(Bitmap[] tiles, GameView gv) {
		mapTiles  = tiles;
		mapRect	  = new Rect(LEFT_MARGIN, TOP_MARGIN, MAP_WIDTH + LEFT_MARGIN, MAP_HEIGHT + TOP_MARGIN);
		mInstance = this;
		mGameView = gv;
		initPositions();
	}


	public synchronized final void draw(Canvas c, Paint p) {
		c.drawBitmap(MAP_BITMAP, mapRect.left, mapRect.top, null);
	}


	protected final void initPositions() {
		ArrayList<Point> list = new ArrayList<Point>();
		list.addAll(Arrays.asList(enemyBornPos));
		list.addAll(Arrays.asList(playerBornPos));

		for (int x = 0; x < X_TILE_NUM; x ++) {
			for (int y = 0; y < Y_TILE_NUM; y++) {
				int i = y < 10 && y > 3  ? 0 : 7;//*/(byte) Math.abs(Math.random() * 32 % 5);
				int rx = x << 5, ry = y << 5;

				switch ( i ) {
					case TILE_WALL:
						mapData[x][y] = new WallTile(rx, ry, TILE_WIDTH, TILE_HEIGHT, null);
						break;
					case TILE_IRON:
						mapData[x][y] = new IronTile(rx, ry, TILE_WIDTH, TILE_HEIGHT, null);
						break;
					default:
						mapData[x][y] = new Tile(rx, ry, TILE_WIDTH, TILE_HEIGHT, i);
				}
			}
		}

		mapData[playerSymbolPos.x][playerSymbolPos.y] = new Tile(playerSymbolPos.x << 5,
				playerSymbolPos.y << 5, TILE_WIDTH, TILE_HEIGHT, TILE_SYMBOL);
		for (Point p : list) {
			mapData[p.x][p.y] = new Tile(p.x << 5, p.y << 5, TILE_WIDTH, TILE_HEIGHT, TILE_LAND);
		}
		for (Tile t : playerSymbolWall) {
			WallTile wt = (WallTile) t;
			int row = t.getY() >> 5, col = t.getX() >> 5;
			mapData[col][row] = t;
		}
	}


	public final void drawOnBuffer() {
		if (mapIsDrawed != false) {
			return;
		}

		mapIsDrawed = true;
		MAP_BITMAP	= Bitmap.createBitmap(MAP_WIDTH, MAP_HEIGHT, Bitmap.Config.RGB_565);
		MAP_BUFFER  = Bitmap.createBitmap(MAP_BITMAP);
		Canvas c 	= new Canvas(MAP_BITMAP);

		c.setDensity(MAP_BITMAP.getDensity());
		for (int x = 0; x < X_TILE_NUM; x ++) {
			for (int y = 0; y < Y_TILE_NUM; y ++) {
				Tile tile = mapData[x][y];
				if (tile != null) {
					int type  = tile.getType();
					switch(type) {
						case TILE_WALL:
						case TILE_IRON:
							Rect[] rs = tile.getRects();
							for (int i = 0; i < rs.length; i++) {
								if (rs[i] != null) {
									c.save();
									c.clipRect(rs[i]);
									c.drawBitmap(mapTiles[type], x * TILE_WIDTH, y * TILE_HEIGHT, null);
									c.restore();
								}
							}
							break;
						case TILE_WATER:
						case TILE_TREE:
						case TILE_SYMBOL:
							c.drawBitmap(mapTiles[type], x * TILE_WIDTH, y * TILE_HEIGHT, null);
							break;
					}
				}
			}
		}
	}


	public final int getTileType(int x, int y) {
		x = Math.min(x, X_TILE_NUM - 1);
		y = Math.min(y, X_TILE_NUM - 1);
		return mapData[x][y].getType();
	}


	public final int getTileType(Point p) {
		p.x = Math.min(p.x, X_TILE_NUM - 1);
		p.y = Math.min(p.y, X_TILE_NUM - 1);
		return mapData[p.x][p.y].getType();
	}


	public final Tile[][] getMapData() {
		return mapData;
	}


	public final int getMapXByBlockX(int x) {
		return x << 5;
	}


	public final int getMapYByBlockY(int y) {
		return y << 5;
	}


	public final int getAbsoluteY(int y) {
		return TOP_MARGIN + y;
	}


	public final int getAbsoluteX(int x) {
		return LEFT_MARGIN + x;
	}


	public final void recycle() {
		if (MAP_BITMAP != null && MAP_BITMAP.isRecycled()) {
			MAP_BITMAP.recycle();
		}
	}

	public final synchronized static GameMap getInstance() {
		return mInstance;
	}


	public final void onBuildTile(int row, int col, Tile t) {
		if (t != null)
			mapData[col][row] = t;
	}


	public final synchronized void onDestroyTile(Rect r) {
		Canvas c = new Canvas(MAP_BITMAP);
		c.setDensity(MAP_BITMAP.getDensity());
		c.clipRect(r);
		mPaint.setColor(Color.BLACK);
		c.drawRect(r, mPaint);
	}


	public final synchronized void onChangeTile(int col, int row, Tile t) {
		if (t != null)
			mapData[col][row] = t;

		int type = t.getType();
		Rect[] rs= t.getRects();
		Canvas c = new Canvas(MAP_BITMAP);
		c.setDensity(MAP_BITMAP.getDensity());
		if (rs != null && (t instanceof WallTile || t instanceof IronTile)) {
			for (int i = 0; i < rs.length; i++) {
				if (rs[i] != null) {
					c.clipRect(rs[i]);
					c.drawRect(rs[i], mPaint);
					c.drawBitmap(mapTiles[type], t.getRect(), rs[i], null);
				}
			}
		}else{
			c.clipRect(t.getRect());
			c.drawRect(t.getRect(), mPaint);
			c.drawBitmap(mapTiles[type], t.getX(), t.getY(), null);
		}
	}


	public final synchronized void onDestroySymbol() {
		new Explosion(playerSymbolPos.x << 5, playerSymbolPos.y << 5, mGameView);
		onChangeTile(playerSymbolPos.x, playerSymbolPos.y, new Tile(playerSymbolPos.x << 5,
				playerSymbolPos.y << 5, TILE_WIDTH, TILE_HEIGHT, TILE_DESTROY));
	}
}

