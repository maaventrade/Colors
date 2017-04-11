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
	/*
	class Point{
		int mX;
		int mY;
		Point(int x, int y){
			mX = x;
			mY = y;
		}
	}
	*/
	class Cell
	{
		int color;
		int alpha;
		Pixel pixel = null;
		int height;
		boolean painted = false;
	}
	Cell cells[][] = null;

	private int mBrushSize = 0; // 1 - 10

	private int[][] mBrushPoints = null;
	//private int[][] mBrushPoints1 = null;
	private int[][] mBrushPoints2 = null;

	private int mColor = 0; 
	private int mAlpha = 0;
	private Pixel mPixel = null;

	private Pixel sunny = new Pixel(0, 0, 0, 100);
	private Pixel shadow = new Pixel(25, 25, 50, 0);

	int NNN = 0;

	private int restOfColor; // 

	int cellsCountHor;
	int cellsCountVert;


	private Brush mBrush;

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
	//private double k = 1;
	private double kZooming = 1;

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

	MyCallback callback = null;
	
	interface MyCallback {
		void callbackACTION_DOWN(Brush brush); 
	} 

	public ViewCanvas(Context c)
	{
		super(c);
		mContext = c;
		init();
	}

	public ViewCanvas(Context c, AttributeSet attrs)
	{
		super(c, attrs);
		mContext = c;
		init();
	}

	private void init()
	{
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setARGB(255, 255, 255, 255);
		mBrush = new Brush(mContext);

		mBrushSize = 11;

		fillBrush();

		Utils.brushRadius = 30; // ????????????????????????????????????????
		mBrush.setRadius(Utils.brushRadius, true);
	}

	private void fillBrush()
	{
		maxDistance = Math.hypot(mBrushSize, mBrushSize);

		int mBrushSize2 = mBrushSize << 1;

		mBrushPoints = new int[mBrushSize2][mBrushSize2];
		for (int i = 0; i < mBrushSize2; i++)
			for (int j = 0; j < mBrushSize2; j++)
			{
				// Test is our point is inside circle
				double distance = Math.hypot(i - mBrushSize, 
											 j - mBrushSize);

				// If cuttent Cell is far anougth from the center of touching
				// make this Cell more transparent
				if (distance >= maxDistance - 3)
					mBrushPoints[i][j] = 0;
				else if (distance >= maxDistance - 6) 
					mBrushPoints[i][j] = (int) (255 - 255 * distance / maxDistance);

				else mBrushPoints[i][j] = (int)(Math.random() * 255);
			}

		//mBrushPoints1 = new int[mBrushSize2][mBrushSize2];
		mBrushPoints2 = new int[mBrushSize2][mBrushSize2];
		/*
		for (int i = 0; i < mBrushSize2; i++)
			for (int j = 0; j < mBrushSize2; j++)
			{
				mBrushPoints1[i][j] = mBrushPoints[i][j] >> 1;
				mBrushPoints2[i][j] = mBrushPoints[i][j] >> 1;
			}
/*
		for (int i = 0; i < mBrushSize2; i++)
			for (int j = 0; j < mBrushSize2; j++)
			{
				if (mBrushPoints[i][j] < 10)
				{
					for (int k = -3; k < 3; k++)
						if (i + k >= 0 && i + k < mBrushSize2)
							mBrushPoints2[i + k][j] = (int) ((float)mBrushPoints[i + k][j] / 0.9);
				}
			}*/
			
		ArrayList<Point> points = new ArrayList<Point>();
		for (int i = 0; i < mBrushSize2; i++)
			for (int j = 0; j < mBrushSize2; j++){
				mBrushPoints2[i][j] = mBrushPoints[i][j];
				if (mBrushPoints[i][j] < 20)
					points.add(new Point(i,j));
				}
				
		for (Point p: points)
			for (int i = p.x; i < mBrushSize2; i++)
				mBrushPoints2[i][p.y] = mBrushPoints2[i][p.y]  >> 1;
					
	}

	public void setBG(int idBG, Context context)
	{
		mContext = context;
		this.idBG = idBG;
		bgBitmap = BitmapFactory.decodeResource(mContext.getResources(), idBG);
		rectBG = new Rect(0, 0,  bgBitmap.getWidth(), bgBitmap.getHeight());
	}


	@Override 
	protected void onDraw(Canvas canvas)
	{
		canvas.drawColor(Color.BLACK);
		if (bgBitmap != null)
			canvas.drawBitmap(bgBitmap, rectBG, mRect, null);
		if (mBitmap != null)
		{
			canvas.drawBitmap(mBitmap, mRectBitmap, mRect, null);
		}
	}


	@Override protected void onSizeChanged(int wd, int h, int oldw,
										   int oldh)
	{
		super.onSizeChanged(wd, h, oldw, oldh);

		width = Math.max(wd, h);
		height = Math.max(wd, h);

		Log.d("dr", "size " + wd + " " + h);

		cellsCountHor = width / CELLSIZE;
		cellsCountVert = height / CELLSIZE;

		if (cells == null)
		{
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

		mRect.set(0, 0, (int)(width * kZooming), (int)(height * kZooming));
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

	public void setColor(Pixel pixel)
	{
		mPixel = pixel;
		mColor = Utils.ryb2rgb(mPixel);
		mAlpha = 255;

		restOfColor = 100;

	}
	
	public void setBrushSize(int size)
	{
		mBrushSize = size;
		maxDistance = Math.hypot(mBrushSize, mBrushSize);
	}

	private void getColorFromTheCanvas(int x, int y)
	{
		int nX = x / CELLSIZE;
		int nY = y / CELLSIZE;
		mPixel = cells[nX][nY].pixel;
		if (mPixel != null && mColor != -1 && restOfColor == 0)
		{
			mColor = Utils.ryb2rgb(mPixel);
			mAlpha = cells[nX][nY].alpha;
			restOfColor = cells[nX][nY].alpha / 5;

			mBrush.setColor(mPixel);
		}
	}


	public void clearColor()
	{
		mBrushSize = 0;
	}

	long t0;
	
	private double distance(PointerCoords center, PointerCoords coord){
		Float minX = Math.min(center.x, coord.x);
		Float maxX = Math.max(center.x, coord.x);
		Float x2 = (maxX-minX)*(maxX-minX);
		Float minY = Math.min(center.y, coord.y);
		Float maxY = Math.max(center.y, coord.y);
		Float y2 = (maxY-minY)*(maxY-minY);

		return  Math.sqrt(x2 + y2);
	}

	@Override public boolean onTouchEvent(MotionEvent event)
	{
		int action = event.getAction();
		int x = (int)event.getX();
		int y = (int)event.getY();

		switch (action)
		{
			case MotionEvent.ACTION_DOWN:
				if (callback != null)
					callback.callbackACTION_DOWN(mBrush);

				if (restOfColor > 0)
				{

					t0 = event.getEventTime();
					setCells((x-offsetX)/kZooming, (y-offsetY)/kZooming, 0, 0, 0);

					N++;
				}
				//else 
				//getColorFromTheCanvas(x, y);

				break;
			case MotionEvent.ACTION_MOVE:
				if (event.getPointerCount() == 1){
					// If we have color in the brush
					if (restOfColor > 0)
					{

						double d = Math.sqrt((x - x0) * (x - x0) + (y - y0) * (y - y0));
						long t = event.getEventTime();

						double v = d / (t - t0);

						setCells((x-offsetX)/kZooming, (y-offsetY)/kZooming, v, x - x0, y - y0);

						// Draw Cells from the start of movin to the end
						float dX;
						float dY;

						float n;
						if (Math.abs(x - x0) >= Math.abs(y - y0))
						{
							n = Math.abs(x - x0) / (mBrushSize * 2);

							dX = (x - x0) / n;
							dY = (y - y0) / n;
						}
						else
						{
							n = Math.abs(y - y0) / (mBrushSize * 2);

							dX = (x - x0) / n;
							dY = (y - y0) / n;
						}

						float tX = x0 + dX;
						float tY =  y0 + dY;

						for (int i = 0; i < n; i++)
						{
							setCells((tX-offsetX)/kZooming, (tY-offsetY)/kZooming, v, x - x0, y - y0);
							tX += dX;
							tY += dY;
						}

						//restOfColor--;
						N++;

					}
					else 
					// Brush is empty. Get color from the vanvas
						getColorFromTheCanvas(x, y);
					
				} else {
					// Two fingers

					PointerCoords coord0 = new PointerCoords();
					PointerCoords coord1 = new PointerCoords();
					event.getPointerCoords(0, coord0);
					event.getPointerCoords(1, coord1);

					double distance1 = distance(coord0, coord1)/10;

					center1.x = (int) ((coord0.x+coord1.x)/2);
					center1.y = (int) ((coord0.y+coord1.y)/2);

					if (!resize){
						center0.x = (int)((coord0.x+coord1.x)/2);
						center0.y = (int)((coord0.y+coord1.y)/2);
						resize = true;

						distance = distance1/kZooming;

						x0 = center1.x;
						y0 = center1.y;
					}

					double k1 = kZooming;

					if (distance != 0) // && k * distance1/distance > 0.3 && k * distance1/distance < 5
						kZooming = distance1/distance;

					x = center1.x;
					y = center1.y;

					offsetX = offsetX + (x - x0);
					offsetY = offsetY + (y - y0);

					offsetX = offsetX + (int)((-width*kZooming + width*k1)/2);
					offsetY = offsetY + (int)((-height*kZooming + height*k1)/2);

					mRect.set(0, 0, (int)(width * kZooming), (int)(height * kZooming));
					mRect.offset(offsetX, offsetY);

					center0.x = center1.x;
					center0.y = center1.y;

					if (distance == 0)
						distance = distance1;
					x0 = x;
					y0 = y;
					//return true;
					
				}
			
				break;
			case MotionEvent.ACTION_POINTER_UP:
				PointerCoords coord0 = new PointerCoords();
				event.getPointerCoords(0, coord0);
				x0 = 0;//(int) coord0.x;
				y0 = 0;// (int) coord0.y;
				resize = false;
				pointerUp = true;
				distance = 0;
				return true;
			case MotionEvent.ACTION_UP:

				fillBrush();

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
	private void setCells(double d, double e, double v, int dx, int dy)
	{
		if (x0 == -1)
			return;
		Log.d("bbb", "v " + v);
		//if (NNN > 0)
		//return;
		// Translate screen coordinayes to the coordinates of the boofer
		int nX = (int) (d / CELLSIZE);
		int nY = (int) (e / CELLSIZE);

		//int tempmBrushSize = mBrushSize;

		int size= mBrushSize;

		int mBrushSize2 = mBrushSize << 1;




		// Repeet for all point ofthe brush
		for (int i = nX - size; i < nX + size; i++)
			for (int j = nY - size; j < nY + size; j++)
			{
				// Test for the bounds of the buffer
				if (i < 0 || j < 0 || i >= cellsCountHor || j >= cellsCountVert) 
					continue;


				// Create new Pixel in the current Cell
				cells[i][j].pixel = new Pixel(mPixel);
				cells[i][j].color = mColor;
				cells[i][j].painted = false;

				// TO DO SOMETHING
				if (cells[i][j].pixel == null)
				{
					cells[i][j].pixel = new Pixel(mPixel);
					cells[i][j].color = mColor;
				}
				else if (cells[i][j].color == mColor)
				{
				}
				else
				{////
					cells[i][j].pixel.add(mPixel, true); 
					cells[i][j].color = Utils.ryb2rgb(cells[i][j].pixel);
				}

				//Log.d("",""+(i-nX+mBrushSize)+"***"+(j-nY+mBrushSize));
				//if (v <= 2)
				//	cells[i][j].alpha = mBrushPoints[i-nX+size][j-nY+size];
				//else {


				double inRads = Math.atan2(dy, dx);

				if (inRads < 0)
					inRads = Math.abs(inRads);
				else
					inRads = 2 * Math.PI - inRads;

				int x11 = i - nX + size;
				int y11 = j - nY + size;

				int x12 = (int) ((x11 - size) * Math.cos(inRads) - (y11 - size) * Math.sin(inRads)) + size;
				int y12 = (int) ((x11 - size) * Math.sin(inRads) + (y11 - size) * Math.cos(inRads)) + size;

				if (x12 >= 0 && x12 < mBrushSize2 && y12 >= 0 && y12 < mBrushSize2)
					if (v <= 2)
						cells[i][j].alpha = mBrushPoints[x12][y12];
					else if (v <= 5)
						cells[i][j].alpha = mBrushPoints2[x12][y12];
					else
						cells[i][j].alpha = mBrushPoints2[x12][y12];


				//};

				//}

				// Draw the cell
				mPaint.setColor(cells[i][j].color);
				mPaint.setAlpha(cells[i][j].alpha);

				mCanvas.drawRect(i * CELLSIZE, j * CELLSIZE, i * CELLSIZE
								 + CELLSIZE, j * CELLSIZE + CELLSIZE, mPaint);


			}

		NNN++;

	}

	public Bitmap getBitmap()
	{
		return mBitmap;
	}

	public int getN()
	{
		return N;
	}

	protected void onResume(SharedPreferences prefs)
	{
	}

	@Override protected void onDetachedFromWindow()
	{
        super.onDetachedFromWindow();
		mBitmap = null;
    }

	public void onPause(Context context, Editor editor)
	{
	}

	public void load(String fileName)
	{
		Bitmap newBitmap;

		File file = new File(fileName);
		if (file.exists())
		{                          
			Bitmap b = BitmapFactory.decodeFile(fileName);
			try
			{
				newBitmap = b.copy(Bitmap.Config.ARGB_8888, true);
				width = newBitmap.getWidth(); 
				height = newBitmap.getHeight(); 
			}
			catch (Exception e)
			{
				newBitmap = Bitmap.createBitmap(width, height,
												Bitmap.Config.ARGB_8888);
			};
		}
		else
			newBitmap = Bitmap.createBitmap(width, height,
											Bitmap.Config.ARGB_8888);

		Canvas newCanvas = new Canvas();
		newCanvas.setBitmap(newBitmap);
		if (mBitmap != null)
		{
			newCanvas.drawBitmap(mBitmap, 0, 0, null);
		}

		mBitmap = newBitmap;
		mCanvas = newCanvas;
		mRectBitmap = new Rect(0, 0, width, height);

		invalidate();

	}

	public void clear()
	{
		mBitmap.eraseColor(Color.TRANSPARENT);
		for (int i = 0; i < cellsCountHor; i++)
			for (int j = 0; j < cellsCountVert; j++)
				cells[i][j] = new Cell();

	}

	public Brush getBrush()
	{
		return mBrush;
	}

	public void addColor(Pixel pixel)
	{
		mBrush.addColor(pixel);
		setColor(mBrush.getPixel());
	}

	public void setBrush(Brush brush2) {
		
	}

	public void copyBrush(Brush v) {
		mBrush.copy(v);
		setColor(mBrush.getPixel());
	}

	public float getBrushSize() {
		return mBrushSize;
	}


}

/*int x11 = i-nX+size;
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
						}	*/
