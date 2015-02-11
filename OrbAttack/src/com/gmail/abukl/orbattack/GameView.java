package com.gmail.abukl.orbattack;

import java.util.ArrayList;
import java.util.List;

import com.gmail.abukl.orbatack.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.v4.view.MotionEventCompat;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView {
	private Bitmap bmp;
	private Bitmap explosion;
	private Bitmap explosion2;
	private Bitmap background;
	private Bitmap background2;
	private SurfaceHolder holder;
	private GameLoopThread gameLoopThread;
	private Sprite sprite;
	private int y = 0;
	private int ySpeed = 5;
	private List<Sprite> sprites = new ArrayList<Sprite>();
	private List<TempSprite> temps = new ArrayList<TempSprite>();
	private List<SpaceShipSprite> ships = new ArrayList<SpaceShipSprite>();
	private List<BackgroundSprite> backgroundSprites = new ArrayList<BackgroundSprite>();
	private long lastClick;
	private boolean touched;
	private Paint paint = new Paint(); 
	
	public GameView(Context context) {
		super(context);
		paint.setColor(Color.WHITE); 
		paint.setStyle(Style.FILL);
		paint.setTextSize(50);
		holder = getHolder();
		gameLoopThread = new GameLoopThread(this);
		holder.addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				boolean retry = true;
				gameLoopThread.setRunning(false);
				while (retry) {
					try {
						gameLoopThread.join();
						retry = false;
					} catch (InterruptedException e) {
					}
				}
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				createSprites();
				gameLoopThread.setRunning(true);
				gameLoopThread.start();
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
			}
		});
		bmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.spaceship2);
		explosion = BitmapFactory.decodeResource(getResources(),
				R.drawable.explosion);
		explosion2 = BitmapFactory.decodeResource(getResources(),
				R.drawable.explosion2);
		background = BitmapFactory.decodeResource(getResources(),
				R.drawable.spacebackground);
		background2 = BitmapFactory.decodeResource(getResources(),
				R.drawable.spacebackground2);
	}

	private void createSprites() {
		for (int i = 0; i < 3; i++) {
			sprites.add(createSprite(R.drawable.waterball));
		}
		ships.add(new SpaceShipSprite(this, bmp, 4, 2));
		backgroundSprites.add(new BackgroundSprite(this, background, 0));
		backgroundSprites.add(new BackgroundSprite(this, background2,
				-background2.getHeight()));

	}

	private Sprite createSprite(int resouce) {
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), resouce);
		return new Sprite(this, bmp, explosion2, explosion, ships, temps,
				sprites);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);
		for (int i = 0; i < backgroundSprites.size(); i++) {
			backgroundSprites.get(i).onDraw(canvas);
		}
		for (int i = temps.size() - 1; i >= 0; i--) {
			temps.get(i).onDraw(canvas);
		}
		for (int i = 0; i < sprites.size(); i++) {
			sprites.get(i).onDraw(canvas);
		}
		for (int i = 0; i < ships.size(); i++) {
			ships.get(i).onDraw(canvas);
			canvas.drawText("Life: " + Integer.toString(ships.get(i).getLife() + 1), 0, 50, paint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int action = MotionEventCompat.getActionMasked(event);
		float x = event.getX();
		float y = event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			if (ships.size() > 0 && ships.get(0).touchCollision(x, y)) {
				touched = true;
			} else {
				lastClick = System.currentTimeMillis();
				synchronized (getHolder()) {
					for (int i = sprites.size() - 1; i >= 0; i--) {
						Sprite sprite = sprites.get(i);
						if (sprite.isCollision(x, y)) {
							sprites.remove(sprite);
							sprites.add(createSprite(R.drawable.waterball));
							temps.add(new TempSprite(temps, this, x, y,
									explosion, 5, 5));
							break;
						}
					}
				}
			}
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			if (ships.size() > 0 && touched) {
				float dx = x - ships.get(0).getX() - ships.get(0).getWidth()
						/ 2;
				ships.get(0).incX((int) Math.round(dx));
			}
			break;
		}
		case MotionEvent.ACTION_UP: {
			touched = false;
		}
		}
		return true;
	}

}
