package com.gmail.abukl.orbattack;

import java.util.List;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Sprite {
	private static final int BMP_ROWS = 4;
	private static final int BMP_COLUMNS = 4;
	private int x = 0;
	private int y = 0;
	private int xSpeed = 5;
	private int ySpeed = 5;
	private int MAX_SPEED = 15;
	private GameView gameView;
	private Bitmap bmp;
	private Bitmap explosion;
	private Bitmap miniExplosion;
	private int currentFrame = 0;
	private int width;
	private int height;
	private int src_mult = 2;
	private List<SpaceShipSprite> ships;
	private List<TempSprite> temps;
	private List<Sprite> sprites;
	private boolean hitShip = false;

	public Sprite(GameView gameView, Bitmap bmp, Bitmap explosion,
			Bitmap miniExplosion, List<SpaceShipSprite> ships,
			List<TempSprite> temps, List<Sprite> sprites) {
		this.gameView = gameView;
		this.bmp = bmp;
		this.width = bmp.getWidth() / BMP_COLUMNS;
		this.height = bmp.getHeight() / BMP_ROWS;
		this.ships = ships;
		this.temps = temps;
		this.explosion = explosion;
		this.sprites = sprites;
		this.miniExplosion = miniExplosion;
		Random rnd = new Random();
		this.x = rnd.nextInt(gameView.getWidth() - width);
		this.y = -10;
		xSpeed = rnd.nextInt(MAX_SPEED) + 7;
		ySpeed = rnd.nextInt(MAX_SPEED) + 7;
	}

	private void update() {
		if (y > gameView.getHeight()) {
			y = -height;
		}
		for (int i = 0; i < ships.size(); i++) {
			SpaceShipSprite ship = ships.get(i);
			int shipCenterX = ship.getX() + ship.getWidth() / 2;
			int shipCenterY = ship.getY() + ship.getHeight() / 2;
			if (ship.isCollision(x, y) && !hitShip) {
				synchronized (gameView.getHolder()) {
					hitShip = true;
					ship.gotHit();
					sprites.remove(this);
					sprites.add(new Sprite(gameView, bmp, explosion, miniExplosion, ships, temps, sprites));
					temps.add(new TempSprite(temps, gameView, x + width / 2, y
							+ height / 2, miniExplosion, 5, 5));
					if (ship.getLife() == 0) {
						ships.remove(ship);
						temps.add(new TempSprite(temps, gameView, shipCenterX,
								shipCenterY, explosion, 3, 4));
						break;
					}
				}
			}
		}
		if (x >= gameView.getWidth() - width - xSpeed) {
			xSpeed = -xSpeed;
			src_mult = 1;
		}
		if (x + xSpeed <= 0) {
			xSpeed = -xSpeed;
			src_mult = 2;
		}
		x = x + xSpeed;
		y = y + ySpeed;
		currentFrame = ++currentFrame % BMP_COLUMNS;
	}

	public void onDraw(Canvas canvas) {
		update();
		int srcX = currentFrame * width;
		int srcY = src_mult * height;
		Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);
		Rect dst = new Rect(x, y, x + width, y + height);
		canvas.drawBitmap(bmp, src, dst, null);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isCollision(float x2, float y2) {
		return x2 > x && x2 < x + width && y2 > y && y2 < y + height;
	}

}
