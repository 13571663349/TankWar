package com.tankwar.compoent;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

import com.tankwar.GameLog;
import com.tankwar.GameMap;
import com.tankwar.GameRes;
import com.tankwar.GameSound;
import com.tankwar.GameView;
import com.tankwar.interfaces.Workable;
import com.tankwar.structs.Tile;
import com.tankwar.structs.Tile.IronTile;
import com.tankwar.structs.Tile.WallTile;

final public class Bullet implements Workable {
	private int x;
	private int y;
	private int cx;
	private int cy;
	private int speed;
	private int direction;
	private int hittingType;

	private boolean isPlayer;
	private boolean isExplosion;
	private boolean canDestroyIron;

	private Bullet		hittingBullet;
	private GameMap		gameMap;
	private GameView	gameView;
	private Tank		myTank;
	private Tank		hitTank;
	private Bitmap[]	bulletBitmap = GameRes.getBullets();
	private Point[]		hitPoint;

	private ArrayList<Point> 			destroyPoint;
	private static ArrayList<Bullet>	allBullets = new ArrayList<Bullet>(20);

	protected final int WIDTH = 8;
	protected final int HEIGHT= 8;
	protected final int CCW   = WIDTH << 2;
	protected final int CCH   = HEIGHT << 2;
	protected final int X_MIN = GameMap.MAP_LEFT;
	protected final int Y_MIN = GameMap.MAP_TOP;
	protected final int X_MAX = GameMap.MAP_WIDTH - WIDTH;
	protected final int Y_MAX = GameMap.MAP_HEIGHT - HEIGHT;
	protected final int SD_ID = GameSound.GSD_HITWALL;

	public final int HIT_TANK  = 1;
	public final int HIT_WALL  = 2;
	public final int HIT_IRON  = 3;
	public final int HIT_SIDE  = 4;
	public final int HIT_BULLET= 5;

	public final int[] playerBulletSpeeds = {
		4, 6, 6, 6
	};

	public final int[] enemyBulletSpeeds = {
		5, 5, 6, 6, 5, 5, 5, 5
	};

	public int[] bullets = {
		1, 1, 2, 2
	};

	public boolean[] killIron = {
		false, false, false, true
	};


	public Bullet(Tank whose, GameMap map, GameView gv, int dir) {
		myTank = whose;
		gameMap = map;
		gameView = gv;
		direction = dir;
		initPos(dir);
		setProfile();
		allBullets.add(this);
	}


	public Bullet() {
		
	}


	private final void initPos(int dir) {
		switch(dir) {
			case Tank.DIR_UP:
				x = myTank.getX() + ((myTank.getW() - WIDTH) >> 1);
				y = myTank.getY();
				break;
			case Tank.DIR_DOWN:
				x = myTank.getX() + ((myTank.getW() - WIDTH) >> 1);
				y = myTank.getY() + myTank.getH();
				break;
			case Tank.DIR_LEFT:
				x = myTank.getX();
				y = myTank.getY() + ((myTank.getH() - HEIGHT) >> 1);
				break;
			case Tank.DIR_RIGHT:
				x = myTank.getX() + myTank.getW();
				y = myTank.getY() + ((myTank.getH() - HEIGHT) >> 1);
				break;
		}
		cx = myTank.getX();
		cy = myTank.getY();
	}


	public final synchronized static void drawAll(Canvas c) {
		ArrayList<Bullet> bullets = allBullets;
		int size = bullets.size();

		for (int i = 0; i < size; i++) {
			Bullet b = bullets.get(i);
			if (b != null) {
				b.draw(c);
			}
		}
	}


	public final static ArrayList<Bullet> getAllBullets() {
		return allBullets;
	}


	public final synchronized static void moveAll() {
		ArrayList<Bullet> bullets = allBullets;

		for (int i = 0; i < bullets.size(); i++) {
			Bullet b = bullets.get(i);
			if (b != null) {
				b.move();
			}
		}
	}


	public final void move() {
		if (isExplosion) {
			return;
		}
		switch(direction) {
			case Tank.DIR_UP:
				y -= speed;
				cy -= speed;
				hitPoint = new Point[] {
					new Point(cx, cy), new Point(cx + CCW, cy)
				};
				break;
			case Tank.DIR_DOWN:
				y += speed;
				cy += speed;
				hitPoint = new Point[] {
					new Point(cx, cy + CCH), new Point(cx + CCW, cy + CCH)
				};
				break;
			case Tank.DIR_LEFT:	
				x -= speed;
				cx -= speed;
				hitPoint = new Point[] {
					new Point(cx, cy), new Point(cx, cy + CCH)
				};
				break;
			case Tank.DIR_RIGHT:
				x += speed;
				cx += speed;
				hitPoint = new Point[] {
					new Point(cx + CCW, cy), new Point(cx + CCW, cy + CCH)
				};
				break;
		}

		if (isHitting()) {
			explosion();
		}
	}


	public final void onHittingByBullet() {
		hittingType = HIT_BULLET;
		allBullets.remove(this);
		myTank.addBullet();
	}


	public final synchronized void explosion() {
		isExplosion = true;
		allBullets.remove(this);
		new Explosion(this, gameView);
		myTank.addBullet();
	}


	public final boolean isHitting() {
		Tank[] ts = EnemyTank.getTanks();
		int  size = 0;
		boolean play = false;
		boolean cols = false;

		for (Point p : hitPoint) {
			Point np  = myTank.getMapTPos(p.x, p.y);
			Tile tile =  gameMap.getMapData()[np.x][np.y];
			int type = tile.getType();
			if (type == GameMap.TILE_WALL || type == GameMap.TILE_IRON) {
				Rect[] rs = tile.getRects();
				for (int i = 0; i < rs.length; i++) {
					if (rs[i] != null) {
						if (myTank.isCollisions(cx, cy, CCW, CCH, rs[i].left, rs[i].top, rs[i].right -
								rs[i].left, rs[i].bottom - rs[i].top)) {
							Rect destroyRegion = null;
							if (type == GameMap.TILE_IRON) {
								if (canDestroyIron)
									destroyRegion = ((IronTile)tile).destroyRegion(i);
								else
									play = true;
							}else{
								destroyRegion = ((WallTile)tile).destroyRegion(i);
							}
							if (destroyRegion != null)
								gameMap.onDestroyTile(destroyRegion);

							cols = true;
						}
					}
				}
			}else if (type == GameMap.TILE_SYMBOL) {
				gameMap.onDestroySymbol();
				cols = true;
			}
		}

		if (x > X_MAX || y > Y_MAX || x < X_MIN || y < Y_MIN) {
			hittingType = HIT_SIDE;
			cols = true;
			play = true;
		}
		if (play && myTank.isPlayer()) GameSound.play(SD_ID);
		if (cols) return cols;

		for (Tank t : PlayerTank.getAllPlayers()) {
			if (t != null && t != myTank) {
				if (myTank.isCollisions(x, y, WIDTH, HEIGHT, t.getX(), t.getY(), t.getW(), t.getH())) {
					hittingType = HIT_TANK;
					myTank.onHittingTank(t);
					return true;
				}
			}
		}

		if (myTank.isPlayer()) {
			for (int i = 0; i < ts.length; i++) {
				Tank t = ts[i];
				if (t != null && myTank.isCollisions(x, y, WIDTH, HEIGHT, t.getX(), t.getY(), t.getW(),
					t.getH())) {
					hittingType = HIT_TANK;
					myTank.onHittingTank(t);
					return true;
				}
			}
		}

		size = allBullets.size();
		for (int i = 0; i < size; i++) {
			Bullet b = allBullets.get(i);
			if (b != null && b != this) {
				if (myTank.isCollisions(x, y, WIDTH, HEIGHT, b.getX(), b.getY(), WIDTH, HEIGHT)) {
					hittingType = HIT_BULLET;
					onHittingByBullet();
					b.onHittingByBullet();
					return false;
				}
			}
		}

		return false;
	}


	private final void setProfile() {
		if (myTank.isPlayer()) {
			int lv = myTank.getLevel();
			speed = playerBulletSpeeds[lv];
			canDestroyIron = killIron[lv];
			myTank.setBulletTotal(bullets[lv]);
		}else{
			speed = enemyBulletSpeeds[myTank.getLevel()];
		}
	}


	public final void draw(Canvas c) {
		if (!isExplosion) {
			c.drawBitmap(bulletBitmap[direction], gameMap.getAbsoluteX(x), gameMap.getAbsoluteY(y), null);
		}
	}


	public final Tank getHittingTank() {
		return hitTank;
	}


	public final int getX() {
		return x;
	}


	public final int getY() {
		return y;
	}

	
	public final int getW() {
		return WIDTH;
	}
	

	public final int getH() {
		return HEIGHT;
	}


	@Override
	public final void work() {
		moveAll();
	}


	public final static void reset() {
		allBullets.clear();
	}
}
