package com.alexmochalov.colors;

import java.io.Serializable;

import com.alexmochalov.colors.Utils.Mode;
import com.example.draw.R;

import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.util.*;


public class Brush extends ImageView{
	private int radius = 0;
	private Mode mode;
	
	private PixelFloat mPixel;
	private int rgb;
	private int brushThickness;
	
	private Bitmap icon;
	private Rect rectS;
	private Rect rectD;
	
	private Paint paint;
	private Bitmap mask;

	private Context context;
	
	private int transparency = 100;
	private boolean main = false;
	
	private int size = 1003; // 1000...1003;
	private int code;
	 
	public Brush(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public Brush(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public Brush(Context context) {
		super(context);
		init(context);
	}

	public void setDarkerness(float d)
	{
		mPixel.darkness = d;
		rgb = Utils.ryb2rgb(mPixel);
		invalidate();
	}

	public void setMode(Mode mode, Bitmap icon)
	{
		setMode(mode);
		this.icon = icon;
        rectS = new Rect(0,0,icon.getWidth(),icon.getHeight());
		invalidate();
	}

	public Mode getMode(){
		return mode;
	}
	
	public void setMode(Mode m)
	{
		this.mode = m;
		if (this.mode != Mode.paint)
			mPixel.clear();
		invalidate();
	} 

	public PixelFloat getPixel()
	{
		//PixelFloat p = new PixelFloat((short)mPixel.red, (short)mPixel.yellow, (short)mPixel.blue, (short)mPixel.white);
		//return p;
		return mPixel;
	}

	public int getColor()
	{
		return rgb;
	}

	private void init(Context context) {
		paint = new Paint();
		this.context = context;
		brushThickness = 255;
		icon = BitmapFactory.decodeResource(getResources(), R.drawable.void_tube);
        rectS = new Rect(0,0,icon.getWidth(),icon.getHeight());
	}
	
	public void setRadius(int radius, boolean main){
		this.radius = radius;
		this.rgb = Color.WHITE;
		
		mPixel = new  PixelFloat();
		
		mask = Bitmap.createBitmap(radius * 2, radius * 2, 
				Bitmap.Config.ARGB_8888); 
		mask.eraseColor(android.graphics.Color.TRANSPARENT);;
		Paint paint = new Paint();
		paint.setColor(Color.BLUE);
		Canvas canvas = new Canvas(mask);
		canvas.drawCircle(radius, radius, radius, paint);
		
		this.main = main;
	}

    @Override
    protected void onDraw(Canvas canvas) {
//    	canvas.drawColor(Color.WHITE);
    	Utils.drawBG(canvas);
		
    	if (radius != 0){
    		if (mode != Mode.paint){
        		Path path = new Path();
        		path.addCircle(Utils.getBrushWidth(), Utils.getBrushWidth(), radius, Path.Direction.CW);
    	    	if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2){
    	    		canvas.clipPath(path);
    				canvas.drawBitmap(icon, rectS, rectD, null);
    	    	} else {
    				canvas.drawBitmap(icon, rectS, rectD, null);
    	    		canvas.drawBitmap(Utils.mask, Utils.rectMask, rectD, null);
    	    	}	
    		} else {
    			paint.setColor(rgb);
				//Log.d("","RGB === "+rgb+" radius "+radius+" paint "+paint);
    			canvas.drawCircle(Utils.getBrushWidth(), Utils.getBrushWidth(), radius, paint);
    			
    			if (main)
    				Utils.drawText(canvas, transparency, false);    			
    		}
		} else {
			paint.setColor(Color.BLUE);
			canvas.drawCircle(Utils.getBrushWidth(), Utils.getBrushWidth(), 22, paint);			
		}

    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(Utils.getBrushWidth()*2, Utils.getBrushWidth()*2);
        rectD = new Rect(0,0,Utils.getBrushWidth()*2,Utils.getBrushWidth()*2);
    }
	
	public void addColor(PixelFloat pixelFloat){
		mode = Mode.paint;
		mPixel.add1(pixelFloat, false);
		rgb = Utils.ryb2rgb(mPixel);
		invalidate();
	}

	public void clear() {
		rgb = Color.WHITE;
		mPixel.clear();
		//Log.d("","CLEAR "+rgb);
		invalidate();
	}

	public Bitmap getMask() {
		return mask;
	}

	public int getThickness() {
		if (brushThickness > 0)
			return brushThickness--;
		else return 0;
	}

	public void restoreThickness() {
		brushThickness = 255;
	}

	public void copy(Brush brush) {
		radius = brush.radius;
		mPixel.copy(brush.mPixel);
		rgb = brush.rgb;
		brushThickness = brush.brushThickness;
		setMode(Mode.paint);
		
		//Log.d("", "Copy"+mPixel.red+" "+mPixel.yellow+"  "+mPixel.blue+"  "+mPixel.white+"  rgb "+rgb);
		
		invalidate();
	}

	public boolean equal(Brush brush) {
		return rgb == brush.rgb;
	}

	public void setPixel(PixelFloat pixelFloat) {
		mPixel.copy(pixelFloat);
		rgb = Utils.ryb2rgb(mPixel);
		invalidate();
	}

	public String pixelToString() {
		return ""+mPixel.red+"\n"+mPixel.yellow+"\n"+mPixel.blue+"\n"+mPixel.white+"\n"+mPixel.darkness+"\n";
	}

	public void setPixel(String str1, String str2, String str3, String str4, String str5) {
		mPixel.red = Float.parseFloat(str1);
		mPixel.yellow = Float.parseFloat(str2);
		mPixel.blue = Float.parseFloat(str3);
		mPixel.white = Float.parseFloat(str4);
		mPixel.darkness = Float.parseFloat(str5);
		
		rgb = Utils.ryb2rgb(mPixel);
		
		invalidate();
	}

	public boolean isEmpty() {
		return mPixel.red == 0 && mPixel.yellow == 0 && mPixel.blue == 0 && mPixel.white == 0;
	}

	public void setTransparency(int t) {
		transparency = t;
		invalidate();
	}

	public int getTransparency() {
		return transparency;
	}

	public void setSize(int code, ViewCanvas viewCanvas) {
		this.code = code;
		switch (code){
		case 1000:
			size = 1;
			break;
		case 1001:
			size = 3;
			break;
		case 1002:
			size = 5;
			break;
		case 1003:
			size = 7;
			break;
		default:	
			size = 11;
		};
		viewCanvas.setBrushSize(size);
		
		invalidate();
	}

	public int getSize() {
		return size;
	}

	public int getSize0() {
		return code;
	}

	public void setColor(float r, float y, float b, float w) {
		mPixel = new PixelFloat(r, y, b, w);
		rgb = Utils.ryb2rgb(mPixel);
	}

	public void setColor(PixelFloat pixelFloat) {
		mPixel = new PixelFloat();
		rgb = Utils.ryb2rgb(mPixel);
	}

	public float getDarkness() {
		return mPixel.darkness;
	}
}
