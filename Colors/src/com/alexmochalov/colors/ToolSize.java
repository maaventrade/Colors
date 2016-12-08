package com.alexmochalov.colors;

import android.content.*;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.util.*;
import android.view.*;
import android.widget.*;

public class ToolSize extends ImageView{
	
	private ViewCanvas mViewCanvas;
	
	private boolean mVisible = true;
	private boolean mExpanded = false;
	private boolean mMoved = false;
	
	private Paint paint;
	private Rect rectS;
	private Rect rectD;
	
	private int MIN = 1;
	private int MAX = 11;
	private float value = 1;
	/*
	 public OnTestedListener mCallback;

	 public String getTextToTTS()
	 {
	 return null;
	 }

	 public interface OnTestedListener {
	 public void onTested();
	 public void onFinished(Fragment thisFragment);
	 }
	 
	*/
	
	
	public Listener callback = null;
	interface Listener {
		void callbackVALUE_CHANGED(int value); 
	} 
	

	public ToolSize(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public ToolSize(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ToolSize(Context context) {
		super(context);
		init();
	}

	private void init() {
		paint = new Paint();
	}

	void setViewCanvas(ViewCanvas viewCanvas) {
		mViewCanvas = viewCanvas;
	}
	
	
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(Utils.brushWidth*2, Utils.brushWidth*10);
        rectD = new Rect(0,0,Utils.brushWidth*2,Utils.brushWidth*10);
    }
	
    @Override
    protected void onDraw(Canvas canvas) {
    	//Utils.drawBG(canvas); //, Color.GRAY
    	paint = new Paint();
    	paint.setColor(Color.WHITE);
    	paint.setAlpha(150);
    	
    	if (mExpanded){
        	canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    	} else {
        	canvas.drawRect(0, 0, getWidth(), getWidth(), paint);
    	}

    	paint.setColor(Color.BLACK);
    	paint.setAlpha(255);
    	paint.setStyle(Paint.Style.STROKE);

    	canvas.drawRect(0, 0, getWidth(), getWidth(), paint);
    	
    	paint.setTextSize(32);
    	canvas.drawText(""+(int)value, 4, 30, paint);
    	
    	if (mExpanded){
        	canvas.drawRect(0, getWidth(), getWidth(), getHeight(), paint);
        	paint.setStrokeWidth(3);
			paint.setColor(Color.GRAY);
        	canvas.drawLine(getWidth()/2, getWidth()+8, getWidth()/2, getHeight()-8, paint);
        	//canvas.drawLine(8, getHeight()-8, getWidth()- 8, getWidth()+8, paint);
        	//canvas.drawLine(8, getWidth()+8, getWidth()- 8, getWidth()+8, paint);
        	
        	int h = getHeight()-8 - (getWidth()+8);
        	int pos = (int) ((h/(MAX-MIN) * (value-1)))+getWidth()+8;
        	
			paint.setStrokeWidth(8);
			paint.setColor(Color.rgb(200,200,255));
			canvas.drawLine(getWidth()/2, getWidth()+8, getWidth()/2, pos, paint);
			
			paint.setStrokeWidth(1);
			paint.setColor(Color.BLUE);
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
        	canvas.drawRect(new Rect(4, pos, getWidth()-4, pos+4), paint);
    	}
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() - Utils.brushWidth;
        float y = event.getY() - Utils.brushWidth;

        switch (event.getAction()) {
        	case MotionEvent.ACTION_UP:
        		if (mExpanded && mMoved){
        			mExpanded = false;
        		} else if (mExpanded && y < getWidth()){
					mExpanded = false;
				} else if (!mExpanded && y < getWidth()){
        			mMoved = false;
        			mExpanded = true;
        		}
				
        		
                invalidate();
        		return true;
        	case MotionEvent.ACTION_DOWN:
        		
        			
                invalidate();
        		return true;
        	case MotionEvent.ACTION_MOVE:
				if (mExpanded){
					mMoved = true;

					int h = getHeight()-8 - (getWidth()+8);
					value = (y)/h*(MAX-MIN)+1;

					value = Math.min(Math.max(value, MIN), MAX); 

					if (callback != null)
						callback.callbackVALUE_CHANGED((int)value);
        			
					
					invalidate();
				}
        		
        		return true;
            default:
            	return true;
        }    	
    }
    
} 
