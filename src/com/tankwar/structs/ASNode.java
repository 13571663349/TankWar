package com.tankwar.structs;

import android.graphics.Point;

public class ASNode extends Point {
	private int mG = 0;
	private int mH = 0;
	private int mF = 0;

	private ASNode mParent;
	private ASNode mTarget;

	public ASNode(int x, int y, ASNode parent, ASNode target) {
		super(x, y);
		mParent = parent;
		mTarget = target;
		setG();
		setH();
		setF();
	}


	public ASNode(ASNode p) {
		this(p.x, p.y, p.getParent(), p.getTarget());
	}


	private final void setG() {
		if (mParent == null)
			return;

		if (mParent.x != x && mParent.y != y)
			mG = mParent.getG() + 14;
		else
			mG = mParent.getG() + 10;
	}


	private final void setH() {
		if (mTarget == null)
			mH = 0;
		else
			mH = (Math.abs(mTarget.x - x) + Math.abs(mTarget.y - y)) * 10; 
	}


	private final void setF() {
		mF = mG + mH;
	}


	public final void setParent(ASNode parent) {
		mParent = parent;
		setG();
		setH();
		setF();
	}


	public final ASNode getParent() {
		return mParent;
	}


	private ASNode getTarget() {
		return mTarget;
	}


	public final int getG() {
		return mG;
	}


	public final int getG(ASNode p) {
		int g = -1;
		if (p != null)
			g = p.getG() + ((p.x != x && p.y != y) ? 14 : 10);

		return g;
	}


	public final int getH() {
		return mH;
	}

	
	public final int getF() {
		return mF;
	}
}
