package com.alexmochalov.colors;

import android.app.ProgressDialog;
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

import com.example.draw.R;

public class ViewCanvas extends View
{

	private String mFileName = "new";

	public void setBrushDarker()
	{
		mBrush.setDarker();
	}
	
	private enum Mode {move, paint};
	private Mode mode = Mode.paint;
	
	class Cell
	{
		int color;
		int alpha;
		PixelFloat PixelFloat = null;
		int height;
		boolean painted = false;
	}
	
	Cell cells[][] = null;
	
	private int CELLSIZE = 5;
	private int cellsCountHor = 300;
	private int cellsCountVert = 300;

	private int mImageWidth = cellsCountHor * CELLSIZE;
	private int mImageHeight = cellsCountVert * CELLSIZE;
	
	private int mBrushSize = 0; // 1 - 10

	private int[][] mBrushPoints = null;
	//private int[][] mBrushPoints1 = null;
	//private int[][] mBrushPoints2 = null;

	private int mColor = 0; 
	private int mAlpha = 0;
	private PixelFloat mPixel = null;

	private PixelFloat sunny = new PixelFloat(0, 0, 0, 100);
	private PixelFloat shadow = new PixelFloat(25, 25, 50, 0);

	int NNN = 0;

	private int restOfColor; // 

	private Brush mBrush;

	int N = 0;

	private Bitmap bgBitmap;
	private Bitmap mBitmap;
	private Rect rectBG;

	int width;
	int height;

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

		Log.d("cc", "init ");
		setBrushSize(11);
		

		Utils.brushRadius = 30; // ????????????????????????????????????????
		mBrush.setRadius(Utils.brushRadius, true);
	}

	private void fillBrush()
	{
		//double maxDistance = Math.hypot(mBrushSize, mBrushSize);

		int mBrushSize2 = mBrushSize << 1;

		ArrayList<Point> points = new ArrayList<Point>();
		
		mBrushPoints = new int[mBrushSize2][mBrushSize2];
		for (int i = 0; i < mBrushSize2; i++)
			for (int j = 0; j < mBrushSize2; j++)
			{
				// Test is our point is inside circle
				double distance = Math.hypot(i - mBrushSize, 
											 j - mBrushSize);

				// If cuttent Cell is far anougth from the center of touching
				// make this Cell more transparent
				int n1 = 0;
				if (mBrushSize > 3)
					n1 = 3;
				
				if (distance > mBrushSize)
					mBrushPoints[i][j] = 0;
				else if (distance >= mBrushSize - (mBrushSize >> 2)) 
					mBrushPoints[i][j] = (int) (255 - 255 * distance / (mBrushSize << 1));
				else {
					mBrushPoints[i][j] = (int) (128 + (int)(Math.random() * 128));
					if (mBrushPoints[i][j] < 140) //130
						points.add(new Point(i,j));
				}
			}

				
		for (Point p: points)
			for (int i = p.x; i < mBrushSize2; i++)
				mBrushPoints[i][p.y] = (byte) (mBrushPoints[i][p.y] >> 1);
					
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
			canvas.drawBitmap(mBitmap, mRectBitmap, mRect, null);
	}


	@Override protected void onSizeChanged(int wd, int h, int oldw,
										   int oldh)
	{
		super.onSizeChanged(wd, h, oldw, oldh);

		width = Math.max(wd, h);
		height = Math.max(wd, h);

		if (offsetX > width) offsetX = 0;
		if (offsetY > height) offsetY = 0;

		
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

	public void setColor(PixelFloat PixelFloat)
	{
		mPixel = PixelFloat;
		mColor = Utils.ryb2rgb(mPixel);
		mAlpha = 255;

		restOfColor = 100;

	}
	
	public void setBrushSize(int size)
	{
		mBrushSize = size;
		fillBrush();
		
	}

	private void getColorFromTheCanvas(int x, int y)
	{
		int nX = x / CELLSIZE;
		int nY = y / CELLSIZE;
		mPixel = cells[nX][nY].PixelFloat;
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
		
		if (x0 == -1){
			x0 = x;
			y0 = y;
		}
		

		switch (action)
		{
			case MotionEvent.ACTION_DOWN:
				if (mode == Mode.paint){
					if (callback != null)
						callback.callbackACTION_DOWN(mBrush);

					//if (restOfColor > 0)
					//{

						t0 = event.getEventTime();
						setCells((x-offsetX)/kZooming, (y-offsetY)/kZooming, 0, 0, 0);

						N++;
					//}
					//else 
					//getColorFromTheCanvas(x, y);

				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (mode == Mode.paint){
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
					PointerCoords coord0 = new PointerCoords();
					PointerCoords coord1 = new PointerCoords();
					
					event.getPointerCoords(0, coord0);
					if (event.getPointerCount() > 1)
						event.getPointerCoords(1, coord1);
					else coord1.copyFrom(coord0);

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

					offsetX = offsetX + (int)((-mImageWidth*kZooming + mImageWidth*k1)/2);
					offsetY = offsetY + (int)((-mImageHeight*kZooming + mImageHeight*k1)/2);

					mRect.set(0, 0, (int)(mImageWidth * kZooming), (int)(mImageHeight * kZooming));
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
				if (mode == Mode.paint){
					
				} else {
				PointerCoords coord0 = new PointerCoords();
				event.getPointerCoords(0, coord0);
				x0 = -1;//(int) coord0.x;
				y0 = -1;// (int) coord0.y;
				resize = false;
				pointerUp = true;
				distance = 0;
				}
				return true;
			case MotionEvent.ACTION_UP:
				if (mode == Mode.paint){
					fillBrush();
					t0 = event.getEventTime();
					x0 = -1;
					y0 = -1;
					invalidate();
				}
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
		//if (x0 == -1)
			//return;
		//Log.d("bbb", "v " + v);
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

				double inRads = Math.atan2(dy, dx);

				if (inRads < 0)
					inRads = Math.abs(inRads);
				else
					inRads = 2 * Math.PI - inRads;

				int x11 = i - nX + size;
				int y11 = j - nY + size;

				int x12 = (int) ((x11 - size) * Math.cos(inRads) - (y11 - size) * Math.sin(inRads)) + size;
				int y12 = (int) ((x11 - size) * Math.sin(inRads) + (y11 - size) * Math.cos(inRads)) + size;

				//try{
					cells[i][j].PixelFloat.add(mPixel, true); 
					cells[i][j].color = Utils.ryb2rgb(cells[i][j].PixelFloat);
					
				if (x12 >= 0 && x12 < mBrushSize2 && y12 >= 0 && y12 < mBrushSize2)
						cells[i][j].alpha = mBrushPoints[x12][y12];
					
				/*} catch (Exception ex) {
					Toast.makeText(mContext, "i "+i+" j "+j, Toast.LENGTH_LONG).show();
					if (x12 >= 0 && x12 < mBrushSize2 && y12 >= 0 && y12 < mBrushSize2)
						cells[i][j].alpha = mBrushPoints[x12][y12];
					
				} 
				*/

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

		if (cells == null)
		{
			cells = new Cell[cellsCountHor][cellsCountVert];
			for (int i = 0; i < cellsCountHor; i++)
				for (int j = 0; j < cellsCountVert; j++){
					cells[i][j] = new Cell();
					cells[i][j].PixelFloat = new PixelFloat();
					cells[i][j].color = 0;
				}	
		}

		load1(Utils.APP_FOLDER+"/screen.png");
		
		mRect.set(0, 0, (int)(mImageWidth * kZooming), (int)(mImageHeight * kZooming));
		mRect.offset(offsetX, offsetY);
		
		load(Utils.APP_FOLDER+"/screen");
	}

	@Override protected void onDetachedFromWindow()
	{
        super.onDetachedFromWindow();
		mBitmap = null;
    }

	public void onPause(Context context, Editor editor)
	{
//		editor.putInt(PREFS_OFFSETX, offsetX);
//		editor.putInt(PREFS_OFFSETY, offsetY);
//		editor.putFloat(PREFS_K, (float)kZooming);

//		editor.putInt(PREFS_BRUSH_TRANSP, brush.getTransparency());
//		editor.putInt(PREFS_BRUSH_SIZE, brush.getSize0());
		
		mBrush = null;
		save(Utils.APP_FOLDER+"/screen.png");
	}

	public void save() {
		if (save(Utils.APP_FOLDER+"/"+mFileName+".png"))
			Toast.makeText(mContext, "Fiale saved "+Utils.APP_FOLDER+"/"+mFileName+".png", Toast.LENGTH_LONG).show();
	}

	private boolean save(String fileName) {
		FileOutputStream out = null;

		Log.d("","SAVE ...");
		
		try {
			if (mBitmap == null)
				return true;
			
		    out = new FileOutputStream(fileName);
		    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
			
		    out.flush();
	        out.close();
	         
		    String fileName1 = fileName.replaceFirst("[.][^.]+$", "")+".plt";
		    
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName1));
			
			for (int i = 0; i < cellsCountHor; i++)
				for (int j = 0; j < cellsCountVert; j++){
				//	bos.write(cells[i][j].alpha);
				//	bos.write(cells[i][j].pixel.red);
				//	bos.write(cells[i][j].PixelFloat.yellow);
				//	bos.write(cells[i][j].PixelFloat.blue);
				//	bos.write(cells[i][j].PixelFloat.white);
				}		
			
			bos.flush();
			bos.close();
			
			Log.d("","SAVED.");
		} catch (Exception e) {
			Toast.makeText(mContext, "Error "+e.toString(), Toast.LENGTH_LONG).show();
		    e.printStackTrace();
			return false;
		} finally {
		    try {
		        if (out != null) {
		            out.close();
		        }
		    } catch (IOException e) {
				Toast.makeText(mContext, "Error "+e.toString(), Toast.LENGTH_LONG).show();
		        e.printStackTrace();
				return false;
		    }
		}	
		return true;
	}
	
	
	private String result;

	boolean load(final String fileName) {
		
		result = "";
		final ProgressDialog mDialog = new ProgressDialog(mContext);

		mDialog.setMessage("Loading..."); //getResources().getString(R.string.executing_task)
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setProgress(0);
        mDialog.setMax(cellsCountHor);
        mDialog.show();		
		
        new Thread(new Runnable() {
            @Override
            public void run(){
        		FileInputStream in = null;
                
        		try {
        		    in = new FileInputStream(fileName);

        		    String fileName1 = fileName.replaceFirst("[.][^.]+$", "")+".plt";
        		    
        			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName1));
        			int n = 0;
        			for (int i = 0; i < cellsCountHor; i++){
        				mDialog.setProgress(i);
        				for (int j = 0; j < cellsCountVert; j++){

        					if (bis.available() <= 0) break;
        		            byte b1 = (byte)bis.read();
        		            
        					if (bis.available() <= 0) break;
        		            byte b2 = (byte)bis.read();
        		            
        					if (bis.available() <= 0) break;
        		            byte b3 = (byte)bis.read();
        		            
        					if (bis.available() <= 0) break;
        		            byte b4 = (byte)bis.read();
        		            
        					if (bis.available() <= 0) break;
        		            byte b5 = (byte)bis.read();
        		            
        					cells[i][j].alpha = b1;
        					cells[i][j].PixelFloat = new PixelFloat(b2, b3, b4, b5);
        					cells[i][j].color = Utils.ryb2rgb(cells[i][j].PixelFloat); 
        					
        				}	
        				if (bis.available() <= 0) break;
        			}
        			
        			bis.close();
        			Log.d("","LOADED.");
        		} catch (Exception e) {
        			result = e.toString();
        		} finally {
        		    try {
        		        if (in != null) {
        		            in.close();
        		        }
        		    } catch (IOException e) {
            			result = e.toString();
        		    }
        		}	
                mDialog.dismiss();
            }
        }).start();
        
        if (result.equals(""))
        	return true;
        else {
            Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
    		return false;
        }
	}
	
	public void load1(String fileName)
	{
		Bitmap newBitmap;
		
//		mFileName = fileName;

		File file = new File(fileName);
		
		if (file.exists())
		{                          
			Bitmap b = BitmapFactory.decodeFile(fileName);
			try
			{
				newBitmap = b.copy(Bitmap.Config.ARGB_8888, true);
				//width = newBitmap.getWidth(); 
				//height = newBitmap.getHeight(); 
			}
			catch (Exception e)
			{
				newBitmap = Bitmap.createBitmap(mImageWidth, mImageHeight,
												Bitmap.Config.ARGB_8888);
			};
		}
		else
			
		newBitmap = Bitmap.createBitmap(mImageWidth, mImageHeight,
										Bitmap.Config.ARGB_8888);

		Canvas newCanvas = new Canvas();
		newCanvas.setBitmap(newBitmap);
		if (mBitmap != null)
		{
			newCanvas.drawBitmap(mBitmap, 0, 0, null);
		}

		mBitmap = newBitmap;
		mCanvas = newCanvas;
		mRectBitmap = new Rect(0, 0, mImageWidth, mImageHeight);

		invalidate();

	}

	public void clear()
	{
		//mBitmap.eraseColor(Color.TRANSPARENT);
		mBitmap = Bitmap.createBitmap(mImageWidth, mImageHeight,
				Bitmap.Config.ARGB_8888);
		mCanvas.setBitmap(mBitmap);
		
		for (int i = 0; i < cellsCountHor; i++)
			for (int j = 0; j < cellsCountVert; j++){
				cells[i][j] = new Cell();
				cells[i][j].PixelFloat = new PixelFloat();
				cells[i][j].color = 0;
			}
		
		invalidate();

	}

	public Brush getBrush()
	{
		return mBrush;
	}

	public void addColor(PixelFloat PixelFloat)
	{
		mBrush.addColor(PixelFloat);
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
	
	public void setMode(boolean isPaint){
		if (isPaint)
			mode = Mode.paint;
		else
			mode = Mode.move;
		
	}
	
	public void changeMode(){
		if (mode == Mode.paint)
			mode = Mode.move;
		else
			mode = Mode.paint;
		
	}

	public Bitmap getIcon() {
		if (mode == Mode.paint)
			return BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
		else
			return BitmapFactory.decodeResource(getResources(), R.drawable.arrow1);
	}

	public void setFileName(String fileName) {
	    mFileName = fileName.replaceFirst("[.][^.]+$", "");
	}

	public String getFilename() {
		return mFileName;
	}

	public int getImageWidth() {
		return mImageWidth;
	}

	public int getImageHeight() {
		return mImageHeight;
	}

	public void setPixel(PixelFloat PixelFloat) {
		mBrush.setPixel(PixelFloat);
		
	}
	
}

