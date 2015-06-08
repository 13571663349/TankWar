package com.tankwar.compoent;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

import com.tankwar.GameMap;
import com.tankwar.GameRes;
import com.tankwar.GameView;
import com.tankwar.structs.Tile;

public abstract class Tank
{
	protected int x 			= 0;
	protected int y 			= 0;
	protected int life 			= 0;
	protected int speed 		= 1;
	protected int width			= GameMap.TILE_WIDTH;
	protected int height		= GameMap.TILE_HEIGHT;
	protected int bullets		= 1;
	protected int fireBullets	= 0;
	protected int leftMargin 	= 2;
	protected int rightMargin 	= 2;
	protected int topMargin 	= 2;
	protected int bottomMargin	= 2;
	protected int bornPosition[]= null;
	protected int tankLevel		= 0;
	protected int frameCounter	= 0;
	protected int shieldGetTime = 0;
	protected int shieldDuration= 20;
	protected int splashDistance= 8;

	protected int direction			= DIR_NONE;
	protected int tankType			= TT_ENEMY;
	protected int fireDistance		= 200;
	protected long lastFireTime		= -1;

	protected final int X_MIN		= GameMap.MAP_LEFT;
	protected final int Y_MIN		= GameMap.MAP_TOP;
	protected final int X_MAX		= GameMap.MAP_WIDTH - width;
	protected final int Y_MAX		= GameMap.MAP_HEIGHT - height;

	protected Rect 		tankPos			= new Rect();
	protected boolean	isMoving		= false;
	protected boolean	isDied			= false;
	protected boolean	isPlayer		= false;
	protected boolean	isShield		= false;
	protected boolean	isDrawBorn		= false;
	protected boolean	aiControlled	= false;
	protected GameMap	gameMap			= null;
	protected Bitmap[]	bornBitmaps		= GameRes.getBore();
	protected Bitmap[]	shieldBitmaps	= GameRes.getShield();
	protected Bitmap[][]tankBitmaps 	= null;
	protected GameView  gameView		= null;
	protected Bonus		mBonus			= null;

	// Directions
	public final static int DIR_UP		= 0;
	public final static int DIR_RIGHT	= 1;
	public final static int DIR_DOWN	= 2;
	public final static int DIR_LEFT	= 3;
	public final static int DIR_NONE	= 5;
	// Tank Types.
	public final static int TT_PLAYER	= 1;
	public final static int TT_ENEMY 	= 2;


	public Tank(int type, int dir, Bitmap[][] tankBitmap, GameMap gm, GameView gv) {
		tankType  	= type;
		direction 	= dir;
		gameMap 	= gm;
		tankBitmaps = tankBitmap;
		gameView	= gv;
		mBonus		= gv.getBonus();
	}


	public Tank() {}


	public boolean move(int dir) {
		boolean canMove = false;
		int tempX = x;
		int tempY = y;
		isMoving  = true;
		Point[] cp= null;
		int oldDir = direction;
		int temp = 0;

		if (dir == DIR_NONE) {
			return false;
		}
		if (dir != direction) {
			direction = dir;
		}

		switch(dir) {
			case DIR_UP:
				tempY -= speed;
				cp = new Point[] {
						new Point(tempX + leftMargin, tempY),
						new Point(tempX + width - rightMargin, tempY)
				};
				break; 
			case DIR_RIGHT:
				tempX += speed;
				cp = new Point[]{
						new Point(tempX + width, tempY + topMargin),
						new Point(tempX + width, tempY + height - bottomMargin)
					};
				break;
			case DIR_DOWN:
				tempY += speed;
				cp = new Point[]{
						new Point(tempX + leftMargin, tempY + height),
						new Point(tempX + width - rightMargin, tempY + height)
					};
				break;
			case DIR_LEFT:
				tempX -= speed;
				cp = new Point[]{
						new Point(tempX, tempY + topMargin),
						new Point(tempX, tempY + height - bottomMargin)
					};
				break;
		}

		if (isLand(tempX, tempY, cp) && !isCollisionsTank(tempX, tempY)) {
			x = tempX;
			y = tempY;
			canMove = true;
		}

		if (mBonus.isGetBonus(this))
			mBonus.onGetBonus(this);

		return canMove;
	}


	public final boolean move() {
		return move(direction);
	}


	public final void setDirection(int dir) {
		direction = dir;
	}


	protected final int getDirection() {
		return direction;
	}


	protected final boolean isLand(int x, int y, Point[] pts) {
		if (x > X_MAX || y > Y_MAX || x < X_MIN || y < Y_MIN) {
			return false;
		}

		for (Point p : pts) {
			Point np = getMapTPos(p.x, p.y);
			Tile tile =  gameMap.getMapData()[np.x][np.y];
			int  type = tile.getType();

			switch(type) {
				case GameMap.TILE_WALL:
				case GameMap.TILE_IRON:
					Rect[] rs = tile.getRects();
					for (int i = 0; i < rs.length; i++) {
						if (rs[i] != null) {
							if (isCollisions(x, y, width, height, rs[i].left, rs[i].top,
								rs[i].right - rs[i].left, rs[i].bottom - rs[i].top)) {
								return false;
							}
						}
					}
					break;
				case GameMap.TILE_LAND:
				case GameMap.TILE_TREE:
					break;
				default:
					return false;
			}
		}
		return true;
	}


	protected final Point getMapTPos(int tx, int ty) {
		return new Point(
			Math.max(0, Math.min(GameMap.X_TILE_NUM - 1, tx >> 5)),
			Math.max(0, Math.min(GameMap.Y_TILE_NUM - 1, ty >> 5))
		);	
	}


	public final boolean isCollisions(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
		if ( x1 >= x2 && x1 >= x2 + w2 ) {
			return false;
		}else if ( x1 <= x2 && x1 + w1 <= x2 ) {
			return false;
		}else if ( y1 >= y2 && y1 >= y2 + h2 ) {
			return false;
		}else if ( y1 <= y2 && y1 + h1 <= y2 ) {
			return false;
		}
		return true;
	}


	public void stop() {
		isMoving = false;
	}


	public boolean fire() {
		if (System.currentTimeMillis() - lastFireTime > fireDistance) {
			lastFireTime = System.currentTimeMillis();
			if (bullets - fireBullets > 0) {
				new Bullet(this, gameMap, gameView, direction);
				fireBullets++;
				return true;
			}
		}
		return false;
	}


	public void addBullet() {
		if (fireBullets > 0)
			--fireBullets;
	}


	public void onHittingBy(Tank t) {
		if (isPlayer != t.isPlayer()) {
			if (--life <= 0)
				onDied();
		}
	}


	public void onDied() {
		isDied = true;
		new Explosion(x, y, gameView);
	}


	public final void onHittingTank(Tank t) {
		t.onHittingBy(this);
	}


	protected final int getRealX() {
		return gameMap.getAbsoluteX(x) + leftMargin;
	}


	protected final int getRealY() {
		return gameMap.getAbsoluteY(y) + topMargin;
	}


	public final boolean isMoving() {
		return isMoving;
	}


	public final boolean isPlayer() {
		return isPlayer;
	}


	public final boolean isDied() {
		return isDied;
	}


	public final int getType() {
		return tankType;
	}


	public final int getBulletTotal() {
		return bullets;
	}


	public final void setBulletTotal(int bn) {
		bullets = bn;
	}


	public final int getLevel() {
		return tankLevel;
	}


	public final int getSpeed() {
		return speed;
	}


	public final int getX() {
		return x;
	}


	public final int getY() {
		return y;
	}


	public final int getW() {
		return width;
	}


	public final int getH() {
		return height;
	}



	//All abstract methods.
	abstract boolean isCollisionsTank(int tempX, int tempY);

	abstract public Bitmap getBitmap(int i);

	abstract public void draw(Canvas c);

	abstract public void onGetLife();

	abstract public void onGetAttack();

	abstract public void onGetShield();

	abstract public void onGetBomb();

	abstract public void onGetSpader();

	abstract public void onGetTimer();
}

