package com.alexmochalov.colors;

import android.content.*;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.util.*;
import android.view.*;
import android.widget.*;

public class ToolSize extends ImageView{
	
	private boolean mVisible = true;
	private boolean mExpanded = true;
	private boolean mMoved = false;
	
	private Paint paint;
	private Rect rectS;
	private Rect rectD;
	
	private int MIN = 1;
	private int MAX = 11;
	
	private int mValue = 1;
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

	public ToolSize(Context context, int min, int max, int value) {
		super(context);
		
		MIN = min;
		MAX = max;
		mValue = value;
		
		init();
	}

	private void init() {
		paint = new Paint();
	}

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(Utils.getBrushWidth()*10, Utils.getBrushWidth()*2);
        rectD = new Rect(0,0,Utils.getBrushWidth()*10,Utils.getBrushWidth()*2);
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

    	canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    	
    	paint.setTextSize(32);
    	canvas.drawText(""+mValue, 4, 30, paint);
    	
    	//if (mExpanded){
        	paint.setStrokeWidth(6);
			paint.setColor(Color.GRAY);
			
			int h8 = getHeight()+8;
			int h2 = getHeight()/2;
			int w = getWidth();
			
        	canvas.drawLine(h2, h2 , w - h2, h2, paint);
        	
        	int h = w - getHeight();
        	int pos = (int) ((h/(MAX-MIN) * (mValue-1)))+h2;
        	
			paint.setStrokeWidth(8);
			paint.setColor(Color.rgb(200,200,255));
			canvas.drawLine(h2, h2, pos, h2, paint);
			
			paint.setStrokeWidth(1);
			paint.setColor(Color.BLUE);
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
        	canvas.drawCircle(pos, h2 , h2 >> 1, paint);
        	
			paint.setColor(Color.rgb(200,200,255));
        	canvas.drawCircle(pos, h2 , (h2 >> 1) - 2, paint);
        	
    	//}
        	paint.setStrokeWidth(1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() - Utils.getBrushWidth();
        float y = event.getY() - Utils.getBrushWidth();

        switch (event.getAction()) {
        	case MotionEvent.ACTION_UP:
        		if (mExpanded && mMoved){
        			mExpanded = false;
        		//} else if (mExpanded && y < getWidth()){
				//	mExpanded = false;
				} else if (!mExpanded && y < getWidth()){
        			mMoved = false;
        			mExpanded = true;
        		}
				
        		
                invalidate();
        		return true;
        	case MotionEvent.ACTION_DOWN:
				if (mExpanded){
					mMoved = true;
					int h2 = getHeight()/2;
		        	int h = getWidth() - getHeight();
					float value = (x-h2)/h*(MAX-MIN)+1; 
					mValue = (int) Math.min(Math.max(value, MIN), MAX); 
					if (callback != null)
						callback.callbackVALUE_CHANGED(mValue);
					//if (callback != null)
						//callback.callbackVALUE_CHANGED((int)value);
					invalidate();
				}
        			
                invalidate();
        		return true;
        	case MotionEvent.ACTION_MOVE:
				if (mExpanded){
					mMoved = true;
					int h2 = getHeight()/2;
		        	int h = getWidth() - getHeight();
					float value = (x-h2)/h*(MAX-MIN)+1; 
					mValue = (int) Math.min(Math.max(value, MIN), MAX); 
					if (callback != null)
						callback.callbackVALUE_CHANGED(mValue);
					invalidate();
				}
        		
        		return true;
            default:
            	return true;
        }    	
    }
    
} 
