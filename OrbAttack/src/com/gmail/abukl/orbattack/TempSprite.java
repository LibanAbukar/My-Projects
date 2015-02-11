package com.gmail.abukl.orbattack;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class TempSprite {
	private float x;
	private float y;
	private int width;
	private int height;
	private Bitmap bmp;
	private int life = 25;
	private List<TempSprite> temps;
	private int row = -1;
	private int col = -1;
	private int numCols;
	private int numRows;
	
	public TempSprite(List<TempSprite> temps, GameView gameView, float x,
			float y, Bitmap bmp, int cols, int rows) {
		this.width = bmp.getWidth() / cols;
		this.height = bmp.getHeight() / rows;
		this.x = Math.min(Math.max(x - width / 2, 0), gameView.getWidth()
				- width);
		this.y = Math.min(Math.max(y - height / 2, 0), gameView.getHeight()
				- height);
		this.bmp = bmp;
		this.temps = temps;
		this.numCols = cols;
		this.numRows = rows;
	}

	public void onDraw(Canvas canvas) {
		update();
		int srcX = col * width;
		int srcY = row * height;
		Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);
		Rect dst = new Rect((int)Math.round(x), (int)Math.round(y), (int)Math.round(x) + width, (int)Math.round(y) + height);
		canvas.drawBitmap(bmp, src, dst, null);
	}

	private void update() {
		if (--life < 1) {
			temps.remove(this);
		} else {
			col = ++col % numCols;
			if (col == 0) {
				row++;
			}
		}
	}
}