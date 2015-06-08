package com.tankwar;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

final public class GameRes {
	private static Resources	 mResource 	= null;
	private static AssetManager  mAssets 	= null;
	private static boolean 		 isLoaded	= false;

	private static Bitmap Redflag			= null;
	private static Bitmap Gameover			= null;
	private static Bitmap EnemyIco			= null;
	private static Bitmap[] Player1Icos		= new Bitmap[2];
	private static Bitmap[] Player2Icos		= new Bitmap[2];
	private static Bitmap[] Tiles			= new Bitmap[7];
	private static Bitmap[] Bullets			= new Bitmap[4];
	private static Bitmap[] Bore			= new Bitmap[4];
	private static Bitmap[] Number			= new Bitmap[10];
	private static Bitmap[] Bonus			= new Bitmap[6];
	private static Bitmap[] Shield			= new Bitmap[2];
	private static Bitmap[] Explode			= new Bitmap[2];
	private static Bitmap[][] EnemyTanks	= new Bitmap[4][16];
	private static Bitmap[][][] PlayerTanks	= new Bitmap[2][4][8];

	public static final String PNG_TILE		= "tile.png";
	public static final String PNG_BORE		= "bore.png";
	public static final String PNG_BONUS	= "bonus.png";
	public static final String PNG_BULLET	= "bullet.png";
	public static final String PNG_ENEMY	= "enemy.png";
	public static final String PNG_EXPLODE1 = "explode1.png";
	public static final String PNG_EXPLODE2 = "explode2.png";
	public static final String PNG_FLAG		= "flag.png";
	public static final String PNG_GAMEOVER = "gameover.png";
	public static final String PNG_GAMEICO	= "gameico.png";
	public static final String PNG_NUMBER	= "number.png";
	public static final String PNG_PLAYER1	= "player1.png";
	public static final String PNG_PLAYER2  = "player2.png";
	public static final String PNG_SHIELD	= "shield.png";


	public final static void initialize(Resources resource) {
		if (isLoaded == false) {
			try{
				mAssets = resource.getAssets();
				mResource = resource;
				loadBitmaps();
				loadTankBitmap();
				isLoaded = true;
			}catch(Throwable e) {
				GameLog.e(e);
			}
		}
	}


	public final static Bitmap getGameover() {
		return Gameover;
	}


	public final static Bitmap getRedflag() {
		return Redflag;
	}


	public final static Bitmap getEnemyIco() {
		return EnemyIco;
	}


	public final static Bitmap[] getTiles() {
		return Tiles;
	}


	public final static Bitmap[] getBonus() {
		return Bonus;
	}


	public final static Bitmap[] getBore() {
		return Bore;
	}


	public final static Bitmap[] getExplode() {
		return Explode;
	}


	public final static Bitmap[] getBullets() {
		return Bullets;
	}


	public final static Bitmap[] getShield() {
		return Shield;
	}


	public final static Bitmap[][] getEnemyTankBitmaps() {
		return EnemyTanks;
	}


	public final static Bitmap[][][] getPlayerTankBitmaps() {
		return PlayerTanks;
	}


	public final static Bitmap[] getPlayer1Icos() {
		return Player1Icos;
	}


	public final static Bitmap[] getPlayer2Icos() {
		return Player2Icos;
	}


	private final static Bitmap getAssetBmp(String s) throws IOException {
		InputStream ins = mAssets.open(s);
		Bitmap b = BitmapFactory.decodeStream(ins);
		ins.close();
		return b;
	}


	public final static void recycle() {
		try{
			Bitmap[][] objs = {
				Player1Icos, Player2Icos, Tiles, Bore, Bonus, Number, Shield, Explode
			};
			Bitmap[] objs2 = {
				Redflag, Gameover, EnemyIco
			};

			for (int i = 0; i < objs2.length; i++) {
				if (objs2[i] != null && !objs2[i].isRecycled()) {
					objs2[i].recycle();
				}
			}

			for (int i = 0; i < objs.length; i++) {
				for (int j = 0; j < objs[i].length; j++) {
					if (objs[i][j] != null && !objs[i][j].isRecycled()) {
						objs[i][j].recycle();
					}
				}
			}

			for (int i = 0; i < PlayerTanks.length; i++) {
				for (int j = 0; j < PlayerTanks[i].length; j++) {
					for (int k = 0; k < PlayerTanks[i][j].length; k++) {
						if (PlayerTanks[i][j][k] != null && !PlayerTanks[i][j][k].isRecycled()) {
							PlayerTanks[i][j][k].recycle();
						}
					}
				}
			}

			for (int j = 0; j < EnemyTanks.length; j++) {
				for (int k = 0; k < EnemyTanks[j].length; k++) {
					if (EnemyTanks[j][k] != null && !EnemyTanks[j][k].isRecycled()) {
						EnemyTanks[j][k].recycle();
					}
				}
			}
		}catch(Throwable e) {
			GameLog.e(e);
		}finally{
			isLoaded = false;
		}
	}


	private final static void loadTankBitmap() throws IOException {
		Bitmap temp[] = {
				getAssetBmp(PNG_PLAYER1),
				getAssetBmp(PNG_PLAYER2)
		};
		Bitmap temp2  = getAssetBmp(PNG_ENEMY);
		int bmpWidth  = temp[0].getWidth() / PlayerTanks[0][0].length,
			bmpHeight = temp[0].getHeight() / PlayerTanks[0].length;
		int bmpWidth2 = temp2.getWidth() / (EnemyTanks[0].length / 2),
			bmpHeight2= temp2.getHeight() / (EnemyTanks[0].length / 2),
			perLineNum= EnemyTanks[0].length / 2;

		for (int i = 0; i < PlayerTanks.length; i++) {
			for (int j = 0; j < PlayerTanks[i].length; j++) {
				for (int k = 0; k < PlayerTanks[i][j].length; k++) {
					PlayerTanks[i][j][k] = Bitmap.createBitmap
											(temp[i], k * bmpWidth, j * bmpHeight, bmpWidth, bmpHeight);
				}
			}
			temp[i].recycle();
		}
		for (int j = 0; j < EnemyTanks.length; j++) {
			for (int k = 0; k < EnemyTanks[j].length; k++) {
				EnemyTanks[j][k] = Bitmap.createBitmap(
									temp2, k % perLineNum * bmpWidth2,
									(j + k / perLineNum * (perLineNum / 2)) *
									bmpHeight2, bmpWidth2, bmpHeight2);
			}
		}
		temp2.recycle();
	}


	private final static void loadBitmaps() throws IOException {
		Bitmap[] temp = {
			getAssetBmp(PNG_TILE),		getAssetBmp(PNG_BORE),
			getAssetBmp(PNG_BONUS),		getAssetBmp(PNG_NUMBER),
			getAssetBmp(PNG_BULLET),	getAssetBmp(PNG_SHIELD),
		};
		Bitmap[][] objs = {
			Tiles, Bore, Bonus, Number, Bullets, Shield
		};
		Bitmap[] other = {
			getAssetBmp(PNG_GAMEICO),	getAssetBmp(PNG_FLAG),
			getAssetBmp(PNG_EXPLODE1),	getAssetBmp(PNG_EXPLODE2),
			getAssetBmp(PNG_GAMEOVER)
		};

		for (int i = 0; i < temp.length; i++) {
			int rawWidth = temp[i].getWidth(), rawHeight = temp[i].getHeight(),
				bmpWidth = 0, bmpHeight = 0;
			if (rawWidth > rawHeight) {
				bmpWidth  = rawWidth / objs[i].length;
				rawWidth  = bmpWidth;
				bmpHeight = 0;
			}else{
				bmpHeight = rawHeight / objs[i].length;
				rawHeight = bmpHeight;
				bmpWidth  = 0;
			}

			for (int l = 0; l < objs[i].length; l++) {
				objs[i][l] = Bitmap.createBitmap(temp[i], l * bmpWidth, l * bmpHeight, rawWidth, rawHeight);
			}
			temp[i].recycle();
		}

		Redflag = other[1];
		Explode[0] = other[2];
		Explode[1] = other[3];
		Gameover = other[4];
		EnemyIco = Bitmap.createBitmap(other[0], 0, 0, 14, 14);
		Player1Icos[0] = Player2Icos[0] = Bitmap.createBitmap(other[0], 14, 0, 14, 14);
		Player1Icos[1] = Bitmap.createBitmap(other[0], 28, 0, 28, 14);
		Player2Icos[1] = Bitmap.createBitmap(other[0], 56, 0, 28, 14);
	}
}
