package com.tankwar;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class GameSound extends SoundPool{
	public static final int[] soundIds   = {
		R.raw.game_start,
		R.raw.game_over,
		R.raw.blast,
		R.raw.bomb, 
		R.raw.bonus,
		R.raw.get_bonus,
		R.raw.fire,
		R.raw.get_life,
		R.raw.hit_wall,
		R.raw.player_move,
		R.raw.enemy_move,
	};

	public final static int GSD_START		= 0;
	public final static int GSD_GAMEOVER	= 1;
	public final static int GSD_BLAST		= 2;
	public final static int GSD_BOMB		= 3;
	public final static int GSD_BONUS		= 4;
	public final static int GSD_GETBONUS	= 5;
	public final static int GSD_FIRE		= 6;
	public final static int GSD_GETLIFE		= 7;
	public final static int GSD_HITWALL		= 8;
	public final static int GSD_PLAYERMOVE	= 9;
	public final static int GSD_ENEMYMOVE	= 10;
	private static int[] 	allSounds 	    = new int[soundIds.length];
	private static GameSound mGameSound		= null;


	private GameSound(Context context) {
		super(50, AudioManager.STREAM_MUSIC, 10);
		for (int i = 0; i < soundIds.length; i++) {
			allSounds[i] = load(context, soundIds[i], 1);
		}
	}


	public final static void load(Context context) {
		mGameSound = null;
		mGameSound = new GameSound(context);
	}


	public final static void play(int soundId) {
		mGameSound.play(allSounds[soundId], 1f, 1f, 1, 0, 1);
	}


	public final static void play(int soundId, float rate) {
		mGameSound.play(allSounds[soundId], 1, 1, 1, 0, rate);
	}


	public final static void play(int soundId, int loop) {
		mGameSound.play(allSounds[soundId], 1f, 1f, 1, loop, 1);
	}


	public final static void play(int soundId, int loop, float rate) {
		mGameSound.play(allSounds[soundId], 1, 1, 1, loop, rate);
	}


	public final static void stop(Integer id) {
		mGameSound.stop(allSounds[id]);
	}


	public final static void releaseResource() {
		mGameSound.release();
	}


	public final static void pauseAll() {
		for (int i = 0; i < allSounds.length; i++) {
			mGameSound.pause(allSounds[i]);
		}
	}


	public final static void resumeAll() {
		for (int i = 0; i < allSounds.length; i++) {
			mGameSound.resume(allSounds[i]);
		}
	}


	public final static void stopAll() {
		for (int i = 0; i < allSounds.length; i++) {
			mGameSound.stop(allSounds[i]);
		}
	}
}
