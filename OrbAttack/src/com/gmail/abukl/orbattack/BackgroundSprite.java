package com.gmail.abukl.orbattack;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class BackgroundSprite {
	private int x = 0;
	private int y = 0;
	private Bitmap bmp;
	private int width;
	private int height;

	public BackgroundSprite(GameView gameView, Bitmap bmp, int y) {
		this.bmp = bmp;
		this.y = y;
		this.width = gameView.getWidth();
		this.height = gameView.getHeight();
	}

	public void onDraw(Canvas canvas) {
		update();
		canvas.drawBitmap(bmp, x, y, null);
	}

	private void update() {
		y += 10;
		if (y >= bmp.getHeight()) {
			y = -bmp.getHeight();
		}
	}
}
