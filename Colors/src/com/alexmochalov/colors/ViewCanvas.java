package com.alexmochalov.colors;

import android.content.SharedPreferences.Editor;
import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import android.view.MotionEvent.PointerCoords;
import android.widget.Toast;
import android.preference.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ViewCanvas extends View
{
	private int mBrushSize = 0; // 1 - 10
	private int mColor = 0; 
	private int mAlpha = 0;
	private Pixel mPixel = null;

	private Pixel sunny = new Pixel(0, 0, 0, 100);
	private Pixel shadow = new Pixel(25, 25, 50, 0);
	
	int NNN = 0;
	
	private int restOfColor; // 
	
	int cellsCountHor;
	int cellsCountVert;

	
	private Brush brush;

	int N = 0;
	
	private Bitmap bgBitmap;
	private Bitmap mBitmap;
	private Rect rectBG;
	
	int width;
	int height;
	
	int CELLSIZE = 5;
	
	double maxDistance;
	
	private int offsetX = 0;
	private int offsetY = 0;
	private double k = 1;
	
	
	private Canvas mCanvas;
	private Context mContext;
	
	private int idBG;
	
	private Rect mRect = new Rect();
	private Rect mRectBitmap = new Rect();
	private Paint mPaint = null;
	
	private int mCurX;
	private int mCurY;
	private float mCurPressure;
	private float mCurSize;
	private int mCurWidth;
	

	public ViewCanvas(Context c) {
		super(c);
		mContext = c;
		init();
	}

	public ViewCanvas(Context c, AttributeSet attrs) {
		super(c, attrs);
		mContext = c;
		init();
	}
	
	private void init(){
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setARGB(255, 255, 255, 255);
		brush = new Brush(mContext);
		
		mBrushSize = 11;
		maxDistance = Math.hypot(mBrushSize, mBrushSize);
		
		Utils.brushRadius = 30; // ????????????????????????????????????????
		brush.setRadius(Utils.brushRadius, true);
	}
	
	public void setBG(int idBG, Context context)
	{
		mContext = context;
		this.idBG = idBG;
		bgBitmap = BitmapFactory.decodeResource(mContext.getResources(), idBG);
		rectBG = new Rect(0, 0,  bgBitmap.getWidth(), bgBitmap.getHeight());
	}
	
	
	@Override 
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		if (bgBitmap != null)
			canvas.drawBitmap(bgBitmap, rectBG, mRect, null);
		if (mBitmap != null) {
			canvas.drawBitmap(mBitmap, mRectBitmap, mRect, null);
		}
	}
	
	class Cell{
		int color;
		int alpha;
		Pixel pixel = null;
		int height;
	}
	Cell cells[][] = null;
	
	@Override protected void onSizeChanged(int wd, int h, int oldw,
										   int oldh) {
		super.onSizeChanged(wd, h, oldw,oldh);

		width = Math.max(wd, h);
		height = Math.max(wd, h);

Log.d("dr","size "+wd+" "+h);

		cellsCountHor = wd/CELLSIZE;
		cellsCountVert = h/CELLSIZE;
		
		if (cells == null){
			cells = new Cell[cellsCountHor][cellsCountVert];
			for (int i = 0; i < cellsCountHor; i++)
				for (int j = 0; j < cellsCountVert; j++)
					cells[i][j] = new Cell();
			;
			
		}
	
		//load(Var.APP_FOLDER+"/screen.png");
		load("/screen.png");
		
		if (offsetX > width) offsetX = 0;
		if (offsetY > height) offsetY = 0;
		
		mRect.set(0, 0, (int)(width * k), (int)(height * k));
		mRect.offset(offsetX, offsetY);
		
		//int i = prefs.getInt(PREFS_BRUSH_TRANSP, 100);
		//int j = prefs.getInt(PREFS_BRUSH_SIZE, 1003);
		
		//brush.setTransparency(i);
		//brush.setSize(j, width);
	}


	private int x0 = -1; 
	private int y0 = -1; 
	private double distance = 0; 
	 
	private Point center0 = new Point();
	private Point center1 = new Point();
	private boolean resize = false;
	private boolean pointerUp = false;
	
	public void setColor(Pixel pixel){
		mPixel = pixel;
		mColor = Utils.ryb2rgb(mPixel);
		mAlpha = 255;
		
		restOfColor = 100;
		
	}
	
	public void setBrushSize(int size) {
		mBrushSize = size;
		maxDistance = Math.hypot(mBrushSize, mBrushSize);
	}
	
	private void getColorFromTheCanvas(int x, int y){
		int nX = x/CELLSIZE;
		int nY = y/CELLSIZE;
		mPixel = cells[nX][nY].pixel;
		if (mPixel != null && mColor != -1 && restOfColor == 0){
			mColor = Utils.ryb2rgb(mPixel);
			mAlpha = cells[nX][nY].alpha;
			restOfColor = cells[nX][nY].alpha/5;
			
			brush.setColor(mPixel);
		}
	}
	
	
	public void clearColor(){
		mBrushSize = 0;
	}
	
	@Override public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int x = (int)event.getX();
		int y = (int)event.getY();
		
		if (x0 == -1){
			x0 = x;
			y0 = y;

			return true;
		}
		
		switch (action){
			case MotionEvent.ACTION_DOWN:
				
				if (restOfColor > 0){
					setCells(x, y);
					//mPaint.setColor(mColor);
					//mCanvas.drawRect(x-mBrushSize, y-mBrushSize,x+mBrushSize, y+mBrushSize, mPaint);
					N++;
				} else; 
					getColorFromTheCanvas(x, y);
				
				break;
			case MotionEvent.ACTION_MOVE:
				if (restOfColor > 0){
					
					setCells(x, y);
					
					float dX;
					float dY;
					
					float n;
					if (Math.abs(x - x0) >= Math.abs(y - y0)){
						n = Math.abs(x - x0)/(mBrushSize*2);
						
						dX = (x - x0)/n;
						dY = (y - y0)/n;
					} else {
						n = Math.abs(y - y0)/(mBrushSize*2);

						dX = (x - x0)/n;
						dY = (y - y0)/n;
					}
					
					float tX = x0 + dX;
					float tY =  y0 + dY;
					
					for (int i = 0; i < n; i++)
					{
						setCells((int)tX, (int)tY);
						tX += dX;
						tY += dY;
					}
					
					restOfColor--;
					N++;
					
				} else 
					getColorFromTheCanvas(x, y);
				
				break;
			case MotionEvent.ACTION_UP:
				for (int i = 0; i < cellsCountHor; i++)
					for (int j = 0; j < cellsCountVert; j++)
					{
						if (cells[i][j].height == 1){
							
							Pixel pixel = new Pixel(cells[i][j].pixel, 1);
							
							int color = Utils.ryb2rgb(pixel);
							mPaint.setColor(color);
							mPaint.setAlpha(cells[i][j].alpha);
							mCanvas.drawRect(i * CELLSIZE, j * CELLSIZE, 
											 i * CELLSIZE + CELLSIZE, 
											 j * CELLSIZE + CELLSIZE,
											 mPaint);
							
							for (int i1 = i-1; i1 <= i+1; i1++)
								for (int j1 = j-1; j1 <= j+1; j1++)
									if (cells[i1][j1].height == 0){
									
										pixel = new Pixel(cells[i][j].pixel, -1);
										
										color = Utils.ryb2rgb(pixel);
										mPaint.setColor(color);
										mPaint.setAlpha(cells[i1][j1].alpha);
										mCanvas.drawRect(i1 * CELLSIZE, j1 * CELLSIZE, 
														 i1 * CELLSIZE + CELLSIZE, 
														 j1 * CELLSIZE + CELLSIZE,
														 mPaint);
										
									}
							
						}
						
					}
				
				x0 = -1;
				y0 = -1;
				invalidate();
				return true;
		}

		x0 = x;
		y0 = y;
		invalidate();
		return true;
	}

	private void setCells(int x, int y)
	{
		if (x0 == -1)
			return;
			
		//if (NNN > 0)
			//return;
		
		int nX = x/CELLSIZE;
		int nY = y/CELLSIZE;
		
		int tempmBrushSize = mBrushSize;
		
		for (int i = nX - mBrushSize; i <= nX + mBrushSize; i++)
			for (int j = nY - mBrushSize; j <= nY + mBrushSize; j++){
				if ( i < 0 || j < 0 || i >= cellsCountHor || j >= cellsCountVert) 
					continue;

				double distance = Math.hypot(nX-i, nY-j);
				
				if (distance >= maxDistance-3)
					continue;
				
				cells[i][j].pixel = new Pixel(mPixel);
				cells[i][j].color = mColor;

				
				if (cells[i][j].pixel == null){
					cells[i][j].pixel = new Pixel(mPixel);
					cells[i][j].color = mColor;
				}
				else if (cells[i][j].color == mColor){
					
				}
				else {
					cells[i][j].pixel.add(mPixel, true); 
					cells[i][j].color = Utils.ryb2rgb(cells[i][j].pixel);
				}
				


				cells[i][j].alpha = (int) (255 - 255 * distance/maxDistance);
				if (restOfColor < 20){
					cells[i][j].alpha = cells[i][j].alpha/(20-restOfColor);
				}
				
				
				int x01 = x0 + (x-x0);
				int x11 = x + (x-x0);
				
				int y01 = y0 + (y-y0);
				int y11 = y + (y-y0);
				
				float dx = x11-x01;
				float dy = y11-y01;
				
				float di = i-nX;
				float dj = j-nY;
				
				if (distance >= maxDistance-5)
				{
					float scal = dx * di + dy * dj;  
					double modA = Math.hypot(dx, dy);
					double modB = Math.hypot(di, dj);
					
					double cos = scal/(modA*modB);
					//double degree = Math.acos(cos);
					//Log.d("xxx", ""+NNN+" dx "+dx+" dy "+dy+" di "+di+" dj "+dj+"  "+cos+" "+Math.toDegrees(degree));
//					Log.d("", ""+NNN+" alpha "+Math.toDegrees(Math.acos(cos)));
					
					
					//if (degree < -1 || degree > 1)
					//	cells[i][j].height = 1;
						
					/*if ( dx < 0 && dy == 0 && cos >= 0.6
						
						)
						cells[i][j].height = 1;*/
						/*
if (cos >= 0){
					mPaint.setColor(Color.RED);
					mCanvas.drawLine(x01,y01,x11,y11,mPaint);

					mPaint.setColor(Color.GREEN);
					mCanvas.drawLine(x,y,i * CELLSIZE,j* CELLSIZE,mPaint);
						}*/
				if ( cos >= 0){
						cells[i][j].height = 1;
					
						}
					
			 } else 
					cells[i][j].height = 0;
					
				mPaint.setColor(cells[i][j].color);
				mPaint.setAlpha(cells[i][j].alpha);
				
				mCanvas.drawRect(i * CELLSIZE, j * CELLSIZE, 
								 i * CELLSIZE + CELLSIZE, 
								 j * CELLSIZE + CELLSIZE,
								 mPaint);
								 
			}
		
		NNN++;
	
	}

/*
	private void drawPoint(float x, float y, float pressure, float size) {
		mCurX = (int)x;
		mCurY = (int)y;
		if (size == 0 && pressure != 1)
			size = pressure/2;
		
		mCurPressure = pressure;
		mCurSize = size;
		
		mCurWidth = (int)(mCurSize*(brush.getSize()));
		if (mCurWidth < 1) mCurWidth = 1;
		
		int n = mCurWidth*2;
		
		mPaint.setColor(Color.BLUE);
		if (mBitmap != null)
			items.add(new Item(mCurX, mCurY, mCurWidth, brush));
	
	}
	*/

	public Bitmap getBitmap() {
		return mBitmap;
	}

	public int getN() {
		return N;
	}

	
	protected void onResume(SharedPreferences prefs){
	}

	@Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
		mBitmap = null;
    }
	
	public void onPause(Context context, Editor editor) {
	}
	
	public void load(String fileName) {
		Bitmap newBitmap;
		
		File file = new File(fileName);
		if(file.exists()){                          
			Bitmap b = BitmapFactory.decodeFile(fileName);
			try{
				newBitmap = b.copy(Bitmap.Config.ARGB_8888, true);
				width = newBitmap.getWidth(); 
				height = newBitmap.getHeight(); 
			} catch (Exception e) {
				newBitmap = Bitmap.createBitmap(width, height,
						Bitmap.Config.ARGB_8888);
			};
		} else
			newBitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);

		Canvas newCanvas = new Canvas();
		newCanvas.setBitmap(newBitmap);
		if (mBitmap != null) {
			newCanvas.drawBitmap(mBitmap, 0, 0, null);
		}
		
		mBitmap = newBitmap;
		mCanvas = newCanvas;
		mRectBitmap = new Rect(0,0, width, height);
		
		invalidate();
		
	}

	public void clear() {
		mBitmap.eraseColor(Color.TRANSPARENT);
		for (int i = 0; i < cellsCountHor; i++)
			for (int j = 0; j < cellsCountVert; j++)
				cells[i][j] = new Cell();
		
	}

	public Brush getBrush() {
		return brush;
	}

	public void addColor(Pixel pixel) {
		brush.addColor(pixel);
		setColor(brush.getPixel());
	}

	
}
