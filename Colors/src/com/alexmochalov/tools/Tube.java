package com.alexmochalov.tools;

import com.alexmochalov.colors.PixelFloat;
import com.alexmochalov.colors.Utils;
import com.alexmochalov.colors.ViewCanvas;

import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import android.widget.*;

public class Tube extends ImageView{

	private PixelFloat PixelFloat;
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

	public void setColor(short r, short y, short b, short w)
	{
		PixelFloat = new PixelFloat(r, y, b, w); 
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
        setMeasuredDimension(Utils.getBrushWidth()*2, Utils.getBrushWidth()*2);
    }

	public void setColor(short[] rgb2ryb) {
    	this.PixelFloat = new PixelFloat((short)255, rgb2ryb);
    	invalidate();
	}
    
    @Override
    protected void onDraw(Canvas canvas) {
    	Utils.drawBG(canvas);
    
        paint.setColor(Utils.ryb2rgb(PixelFloat));
        canvas.translate(Utils.getBrushWidth(), Utils.getBrushWidth());
        canvas.drawCircle(0, 0, Utils.getBrushRadius(), paint);

    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() - Utils.getBrushWidth();
        float y = event.getY() - Utils.getBrushWidth();

        switch (event.getAction()) {
        	case MotionEvent.ACTION_DOWN:
        		mViewCanvas.addColor(PixelFloat);
        		return true;
        	case MotionEvent.ACTION_MOVE:
        		mViewCanvas.addColor(PixelFloat);
        		return true;
            default:
            	return true;
        }    	
    }
} 
