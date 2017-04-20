package com.alexmochalov.colors;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ViewSubmenu extends View{
	// Maximal size of tbe list of the last brushes
	private int MIXES_MAX_COUNT = 50;
	
	ArrayList <Brush> brushes = null;
	Context mContext;
	
	int shiftY = 0;
	float yStart = 0;
	float y0 = 0;
	
	MyCallback callback = null;

	interface MyCallback {
		void callbackSELECTED(Brush brush); 
	} 
	
	public ViewSubmenu(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ViewSubmenu(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ViewSubmenu(Context context) {
		super(context);
	}

	@Override 
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.GREEN);
		if (isInEditMode()) return;

		canvas.translate(0, shiftY);
		for (Brush b: brushes){
			b.draw(canvas);
			canvas.translate(0, Utils.brushWidth*2);
		}
	}
	
	@Override 
	public boolean onTouchEvent(MotionEvent event) {
		float y = event.getY(); 
		
		switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            y0 = y;
            yStart = y;
            return true;
        case MotionEvent.ACTION_MOVE:
        	shiftY = shiftY + (int)(y - y0);
        	invalidate();
            y0 = y;
            return true;
        case MotionEvent.ACTION_UP:
        	Log.d("", "Math.abs(yStart - y) "+Math.abs(yStart - y));
        	if (Math.abs(yStart - y) < 5){
        		int t = shiftY;
        		for (Brush b: brushes){
        			if (y >= t && y <= t+Utils.brushWidth*2){
        				callback.callbackSELECTED(b);
        	            return true;
        			} else 
						t = t + Utils.brushWidth*2;
        		}
        	}
    		
            return true;
		}
		return false;
	}
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	   DisplayMetrics metrics = new DisplayMetrics();
	   WindowManager windowManager = (WindowManager) mContext
               .getSystemService(Context.WINDOW_SERVICE);
       windowManager.getDefaultDisplay().getMetrics(metrics);
	   
       setMeasuredDimension(Utils.brushWidth*2, metrics.heightPixels-Utils.brushWidth*2);
    }
	
	public void insertBrush(Brush brush) {
		if (brush.isEmpty()) return;
		
		for (int i = 0; i < brushes.size()-1; i++)
			if (brushes.get(i).equal(brush)){
				brushes.remove(i);
				break;
			}
		
    	Brush b = new Brush(mContext);
    	b.setRadius(Utils.brushRadius, false);
		
		brushes.add(0, b);
		brushes.get(0).copy(brush);

		if (brushes.size() == MIXES_MAX_COUNT)
			brushes.remove(MIXES_MAX_COUNT-1);
		invalidate();
	}

	public void saveBrushes() {
		// Save the list of the used brushes
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(Utils.APP_FOLDER+"/brushes1.txt"));
			for (Brush b: brushes){
		        out.write(b.pixelToString());
			} 
			out.close();			
	    } catch (FileNotFoundException e) {
	    	Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
	        e.printStackTrace();
	    } catch (IOException e) {
	    	Toast.makeText(mContext, e.toString(), Toast.LENGTH_LONG).show();
	        e.printStackTrace();
	    }	
	}

	public void loadBrushes(ViewCanvas viewCanvas) {
		File file = new File(Utils.APP_FOLDER+"/brushes1.txt");
		if(file.exists()){                          
		    try {
		    	FileInputStream input = new FileInputStream(Utils.APP_FOLDER+"/brushes1.txt");
						
				if ( input != null ) {
		            InputStreamReader in = new InputStreamReader(input);
		            BufferedReader br = new BufferedReader(in);
	            	String str1 = "";
	            	String str2 = "";
	            	String str3 = "";
	            	String str4 = "";
	            	String str5 = "";
	            	int i = 0;
					// Parce the input string
		            while ( true ) {
		            	str1 = br.readLine(); if (str1 == null) break;
		            	str2 = br.readLine(); if (str2 == null) break;
		            	str3 = br.readLine(); if (str3 == null) break;
		            	str4 = br.readLine(); if (str4 == null) break;
		            	str5 = br.readLine(); if (str4 == null) break;
		            	Brush b = new Brush(mContext);
		            	b.setRadius(Utils.brushRadius, false);
				        b.setPixel(str1, str2, str3, str4, str5);
						b.setMode(Utils.Mode.paint);
						brushes.add(b);
				        ///mixes[i++].invalidate();
		            }
		            in.close();
		        }				
				
		        input.close();
		        if (brushes.size() > 0)  
		        	viewCanvas.setBrush(brushes.get(0));
		    } catch (StreamCorruptedException e) {
		        e.printStackTrace();
		    } catch (FileNotFoundException e) {
		        e.printStackTrace();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }	
		}
	}

	public void clearBrushes() {
		for (Brush b: brushes)
			b = null;
		brushes = null;
		callback = null;
	}

	public void initValues(Context context) {
		mContext = context;
		brushes = new ArrayList <Brush>();
	}

	public void selectBrush(Brush brush) {
		brushes.get(0).copy(brush);
	}	
	
}
