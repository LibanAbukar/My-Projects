package com.gmail.abukl.orbattack;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

public class SpaceShipSprite {
	private int x;
	private int y;
	private int width;
	private int height;
	private Bitmap bmp;
	private int life = 2;
	private boolean isHit = false;
	private int row = 1;
	private int col = -1;
	private int numCols;
	private int numRows;
	private GameView gameView;

	public SpaceShipSprite(GameView gameview, Bitmap bmp, int cols, int rows) {
		this.width = bmp.getWidth() / cols;
		this.height = bmp.getHeight() / rows;
		this.x = gameview.getWidth() / 2 - width / 2;
		this.y = gameview.getHeight() - height;
		this.bmp = bmp;
		this.numCols = cols;
		this.numRows = rows;
		this.gameView = gameview;  
	}

	public void onDraw(Canvas canvas) {
		update();
		int srcX = col * width;
		int srcY = row * height;
		Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);
		Rect dst = new Rect(x, y, x + width, y + height);
		canvas.drawBitmap(bmp, src, dst, null);
	}

	public void update() {
		col = ++col % numCols;
		if (isHit) {
			if (life > 0) {
				life--;
				isHit = false;
			}
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getLife() {
		return life;
	}

	public void gotHit() {
		isHit = true;
	}

	public void incX(int x) {
		int inc = this.x + x;
		if (inc <= gameView.getWidth() - width && inc >= 0) {
			this.x = inc;
		}
	}

	public void incY(int y) {
		int inc = this.y + y;
		if (inc <= gameView.getHeight() && inc >= height) {
			this.y = inc;
		}
	}

	public boolean isCollision(float x2, float y2) {
		return x2 > x - width / 3 && x2 < x + width && y2 > y - height / 4
				&& y2 < y + height - height / 4;
	}

	public boolean touchCollision(float x2, float y2) {
		return x2 > x && x2 < x + width && y2 > y && y2 < y + height;
	}
}
