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
	class Cell{
		int color;
		int alpha;
		Pixel pixel = null;
		int height;
		boolean painted = false;
	}
	Cell cells[][] = null;

	private int mBrushSize = 0; // 1 - 10
	
	private int[][] mBrushPoints = null;
	private int[][] mBrushPoints1 = null;
	
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
		
		int mBrushSize2 = mBrushSize << 1;
		
		mBrushPoints = new int[mBrushSize2][mBrushSize2];
		for (int i = 0; i<mBrushSize2; i++)
			for (int j = 0; j<mBrushSize2; j++){
				// Test is our point is inside circle
				double distance = Math.hypot(i - mBrushSize, 
											 j - mBrushSize);
											 
				// If cuttent Cell is far anougth from the center of touching
				// make this Cell more transparent
				if (distance >= maxDistance-3)
					mBrushPoints[i][j] = 0;
				else if (distance >= maxDistance - 6) 
					mBrushPoints[i][j] = (int) (255 - 255 * distance/maxDistance);
	
				else mBrushPoints[i][j] = (int)(Math.random()*255);
			}
		
		mBrushPoints1 = new int[mBrushSize2][mBrushSize2];
		for (int i = 0; i<mBrushSize2; i++)
			for (int j = 0; j<mBrushSize2; j++){
				mBrushPoints1[i][j] = mBrushPoints[i][j] >> 1;
			}
		
		for (int i = 0; i<mBrushSize2; i++)
			for (int j = 0; j<mBrushSize2; j++){
				if (mBrushPoints1[i][j] < 10){
					for (int k = -10; k < 10; k++)
					if (i + k >= 0 && i + k < mBrushSize2 )
						mBrushPoints1[i + k][j] = mBrushPoints1[i+k][j] >> 1;
				}
			}
				

		for (int i = 0; i<mBrushSize2; i++)
			for (int j = 0; j<mBrushSize2/2; j++){
				mBrushPoints1[i][j] = Color.GREEN;
			}
		for (int i = 0; i<mBrushSize2; i++)
			for (int j = mBrushSize2/2; j<mBrushSize2; j++){
				mBrushPoints1[i][j] = Color.RED;
			}
		
		
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
	
	long t0;
	boolean ok = false;
	
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
					
					t0 = event.getEventTime();
					//setCells(x, y, 0, 0, 0);
					
					N++;
				} else; 
					getColorFromTheCanvas(x, y);
				
				break;
			case MotionEvent.ACTION_MOVE:
				// If we have color in the brush
				if (restOfColor > 0){
					
					double d = Math.sqrt((x-x0)*(x-x0)+(y-y0)*(y-y0));
					long t = event.getEventTime();
					
					double v = d/(t-t0);
					//double tg = (y-y0)/(x-x0);
					
					//Log.d("aaa","move  "+x0+" "+x+" "+y0+" "+y);
					
					
					setCells(x, y, v, x-x0, y-y0);
					
					// Draw Cells from the start of movin to the end
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
						//setCells((int)tX, (int)tY, v, x-x0, y-y0);
						tX += dX;
						tY += dY;
					}
					
					//restOfColor--;
					N++;
					
				} else 
				// Brush is empty. Get color from the vanvas
					getColorFromTheCanvas(x, y);
				
				break;
			case MotionEvent.ACTION_UP:
				//Log.d("aaa","UP  "+x0+" "+x+" "+y0+" "+y);
				t0 = event.getEventTime();
				x0 = -1;
				y0 = -1;
				invalidate();
				return true;
		}
		t0 = event.getEventTime();
		x0 = x;
		y0 = y;
		invalidate();
		return true;
	}

	
	/**
	* Set color of the cells, covered by the brush
	x, t are the coordinates of the toaching or moving
	**/
	private void setCells(int x, int y, double v, int dx, int dy)
	{
		if (x0 == -1)
			return;
			
		//if (NNN > 0)
			//return;
		// Translate screen coordinayes to the coordinates of the boofer
		int nX = x/CELLSIZE;
		int nY = y/CELLSIZE;
		
		//int tempmBrushSize = mBrushSize;
				
		int size= mBrushSize;
		
		int mBrushSize2 = mBrushSize << 1;

		
		if (ok){
			return;
		}
		
		
		// Repeet for all point ofthe brush
		for (int i = nX - size; i < nX + size; i++)
			for (int j = nY - size; j < nY + size; j++){
				// Test for the bounds of the buffer
				if ( i < 0 || j < 0 || i >= cellsCountHor || j >= cellsCountVert) 
					continue;

				
				// Create new Pixel in the current Cell
				cells[i][j].pixel = new Pixel(mPixel);
				cells[i][j].color = mColor;
				cells[i][j].painted = false;
				
				// TO DO SOMETHING
				if (cells[i][j].pixel == null){
					cells[i][j].pixel = new Pixel(mPixel);
					cells[i][j].color = mColor;
				}
				else if (cells[i][j].color == mColor){
				}
				else {////
					cells[i][j].pixel.add(mPixel, true); 
					cells[i][j].color = Utils.ryb2rgb(cells[i][j].pixel);
				}
				
				//Log.d("",""+(i-nX+mBrushSize)+"***"+(j-nY+mBrushSize));
				if (v <= -1)
					cells[i][j].alpha = mBrushPoints[i-nX+size][j-nY+size];
				else {
					//if (dy == 0)
					//	cells[i][j].alpha = mBrushPoints1[i-nX+size][j-nY+size];
					//else if (dx == 0)
					//	cells[i][j].alpha = mBrushPoints1[j-nY+size][i-nX+size];
					//else{
						/*
						//dx = 50;
						dy = -50;
						
						double inRads = Math.atan2(dy, dx);
						
						//inRads = inRads - Math.PI/2;
						
						
						int x11 = i-nX+size;
						int y11 = j-nY+size;

						//x11 = 20;
						//y11 = 10;
						
						int x12 = (int) ((x11-size)*Math.cos(inRads) - (y11-size)*Math.sin(inRads)) + size;
						int y12 = (int) ((x11-size)*Math.sin(inRads) + (y11-size)*Math.cos(inRads)) + size;

						if (x12 >= 0 && x12 < mBrushSize2 && y12 >=0 && y12 < mBrushSize)
							cells[i][j].alpha = mBrushPoints1[x12][y12];
						*/
						
						int x11 = i-nX+size;
						int y11 = j-nY+size;

						double inRads = Math.atan2(50, -50);
						
						if (inRads < 0)
					        inRads = Math.abs(inRads);
					    else
					        inRads = 2*Math.PI - inRads;
						

						int x12 = (int) ((x11-size)*Math.cos(inRads) - (y11-size)*Math.sin(inRads)) + size;
						int y12 = (int) ((x11-size)*Math.sin(inRads) + (y11-size)*Math.cos(inRads)) + size;
						
						//x12 = x11;
						//y12 = y11;

						if (x12 >= 0 && x12 < mBrushSize2 && y12 >=0 && y12 < mBrushSize2){
							mPaint.setColor(mBrushPoints1[x12][y12]);
							mCanvas.drawRect(i * CELLSIZE, j * CELLSIZE, i * CELLSIZE
									+ CELLSIZE, j * CELLSIZE + CELLSIZE, mPaint);
						}	
					//};
					
				}
					
				// Draw the cell
				//mPaint.setColor(cells[i][j].color);
				
				//mPaint.setAlpha(cells[i][j].alpha);

				//mCanvas.drawRect(i * CELLSIZE, j * CELLSIZE, i * CELLSIZE
				//		+ CELLSIZE, j * CELLSIZE + CELLSIZE, mPaint);

				ok = true;
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
