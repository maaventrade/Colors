package com.alexmochalov.tools;

import com.alexmochalov.colors.PixelFloat;
import com.alexmochalov.colors.Utils;
import com.alexmochalov.colors.ViewCanvas;

import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import android.widget.*;

public class Dark extends ImageView{

	private Float mValue = 0f;
	private Paint mPaint;
	private ViewCanvas mViewCanvas;
	
	private int delta = 0;

	private int y0;
	
	public Dark(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public Dark(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Dark(Context context) {
		super(context);
		init();
	}
/*
	public void setColor(short r, short y, short b, short w)
	{
		PixelFloat = new PixelFloat(r, y, b, w); 
	}

*/
	public void setCanvas(ViewCanvas viewCanvas)
	{
		mViewCanvas = viewCanvas;
	}

	private void init() {
		mPaint = new Paint();
	}
	
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(Utils.getBrushWidth()*2, Utils.getBrushWidth()*2);
    }
/*
	public void setColor(short[] rgb2ryb) {
    	this.PixelFloat = new PixelFloat((short)255, rgb2ryb);
    	invalidate();
	}
    */
    @Override
    protected void onDraw(Canvas canvas) {
    	Utils.drawBG(canvas);
    
        mPaint.setColor(Color.WHITE);
        
        Log.d("", "delta "+delta);
        
        int y = delta;
        int w = 0;
        int dy = getHeight()/7;
        
        for (int i = 0; i < 7; i++){
        	mPaint.setStrokeWidth(w);
            canvas.drawLine(0, y, getWidth(), y, mPaint);
            
            if (i <= 3)
            	w++;
            else
            	w--;
            
            y = y + dy;
            
        }
        
        //canvas.translate(Utils.getBrushWidth(), Utils.getBrushWidth());
        //canvas.drawCircle(0, 0, Utils.getBrushRadius(), paint);

    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY() - Utils.getBrushWidth();

        switch (event.getAction()) {
        	case MotionEvent.ACTION_DOWN:
        		y0 = (int)y;
        		return true;
        	case MotionEvent.ACTION_MOVE:
        		
        		if (mValue + (y - y0)/10 >= 0){
        			delta = delta + (int)(y - y0);
        			mValue = mValue + (y - y0)/10;
        			mViewCanvas.setDrawValue(mValue);
        		}
        		
        		y0 = (int)y;
        		invalidate();
        		return true;
            default:
            	return true;
        }    	
    }

} 
