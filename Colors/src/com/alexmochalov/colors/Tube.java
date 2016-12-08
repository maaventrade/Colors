package com.alexmochalov.colors;

import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import android.widget.*;

public class Tube extends ImageView{

	private Pixel pixel;
	private Paint paint;
	private ViewCanvas mViewCanvas;
	
	public Tube(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public Tube(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Tube(Context context) {
		super(context);
		init();
	}

	@Override
	public void buildDrawingCache(boolean autoScale)
	{
		// TODO: Implement this method
		super.buildDrawingCache(autoScale);
	}

	@Override
	public void buildDrawingCache()
	{
		// TODO: Implement this method
		super.buildDrawingCache();
	}

	public void setColor(short r, short y, short b, short w)
	{
		pixel = new Pixel(r, y, b, w); 
	}


	public void setBrush(ViewCanvas viewCanvas)
	{
		mViewCanvas = viewCanvas;
	}

	private void init() {
		paint = new Paint();
	}
	
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(Utils.brushWidth*2, Utils.brushWidth*2);
    }

	public void setColor(short[] rgb2ryb) {
    	this.pixel = new Pixel((short)255, rgb2ryb);
    	invalidate();
	}
    
    @Override
    protected void onDraw(Canvas canvas) {
    	Utils.drawBG(canvas);
    
        paint.setColor(Utils.ryb2rgb(pixel));
        canvas.translate(Utils.brushWidth, Utils.brushWidth);
        canvas.drawCircle(0, 0, Utils.brushRadius, paint);

    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() - Utils.brushWidth;
        float y = event.getY() - Utils.brushWidth;

        switch (event.getAction()) {
        	case MotionEvent.ACTION_DOWN:
        		mViewCanvas.addColor(pixel);
        		return true;
        	case MotionEvent.ACTION_MOVE:
        		mViewCanvas.addColor(pixel);
        		return true;
            default:
            	return true;
        }    	
    }
} 
