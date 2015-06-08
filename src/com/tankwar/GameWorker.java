package com.tankwar;

import java.util.ArrayList;

import com.tankwar.interfaces.Workable;

final public class GameWorker {
	private GameView 			gameView	= null;
	private ArrayList<Workable>	workList	= null;
	private boolean 			isPause		= false;
	private boolean 			isExit		= false;


	public GameWorker(GameView gv) {
		super();
		gameView = gv;
		workList = new ArrayList<Workable>();
	}


	public final synchronized GameWorker addWork(Workable work) {
		workList.add(work);
		return this;
	}


	public final synchronized void removeWork(Workable work) {
		workList.remove(work);
	}


	public final synchronized void doWorkList() {
		int size = workList.size();
		for (int i = 0; i < size; i++) {
			Workable w = workList.get(i);
			if (w != null) {
				w.work();
			}
		}
	}
}
