package com.alexmochalov.colors;

import com.example.draw.R;

import android.content.*;
import android.graphics.*;
import android.os.*;
import android.preference.*;
import android.util.*;

public final class Utils
{
//	public static PointOfLine firstPoint = null;
//	public static PointOfLine lastPoint = null;
	
	static final float mPercent = 100f;
	static int brushWidth = 0;
	static int brushRadius = 0;
	
	static String PROGRAMM_FOLDER = "xolosoft";
	public static String APP_FOLDER = Environment.getExternalStorageDirectory().getPath()+"/"+PROGRAMM_FOLDER+"/RYB";
	
	public static byte[] buffer;
	public static float mPercentAdd = 1f;

	public static Bitmap bgBitmap;
	public static Bitmap mask;
	public static Rect rectS;
	public static Rect rectD;
	public static Rect rectMask;

	static Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	/*
	public static void setm(double max)
	{
		maxk = max;
	}

	public static double getm()
	{
		// TODO: Implement*/
	public enum Mode{none, paint, spread, erase};
	

	/**
	 * Original Work
	 * http://www.paintassistant.com/rybrgb.html
	 * Modified by Dave Eddy <dave@daveeddy.com>
	 * 
	 * https://github.com/bahamas10/node-ryb2rgb/blob/master/ryb2rgb.js
	 */

	private static float cubicInt(float t, float A, float B){
	    float weight = t*t*(3f-2f*t);
	    return A + weight*(B-A);
	}

	private static int getR(float iR, float iY, float iB, float black) {
		// red
		float x0 = cubicInt(iB, 1.0f, 0.163f);
		float x1 = cubicInt(iB, 1.0f, 0.0f);
		float x2 = cubicInt(iB, 1.0f, 0.5f);
		float x3 = cubicInt(iB, 1.0f, 0.2f);
		float y0 = cubicInt(iY, x0, x1);
		float y1 = cubicInt(iY, x2, x3);
		return (int) Math.ceil (255f * cubicInt(iR, y0, y1) / black);
	}	

	private static int getG(float iR, float iY, float iB, float black) {
		// red
		float x0 = cubicInt(iB, 1.0f, 0.373f);
		float x1 = cubicInt(iB, 1.0f, 0.66f);
		float x2 = cubicInt(iB, 0.0f, 0.0f);
		float x3 = cubicInt(iB, 0.5f, 0.094f);
		float y0 = cubicInt(iY, x0, x1);
		float y1 = cubicInt(iY, x2, x3);
	    return (int) Math.ceil (255f * cubicInt(iR, y0, y1) / black);
	}	

	private static int getB(float iR, float iY, float iB, float black) {
		// red
		float x0 = cubicInt(iB, 1.0f, 0.6f);
		float x1 = cubicInt(iB, 0.0f, 0.2f);
		float x2 = cubicInt(iB, 0.0f, 0.5f);
		float x3 = cubicInt(iB, 0.0f, 0.0f);
		float y0 = cubicInt(iY, x0, x1);
		float y1 = cubicInt(iY, x2, x3);
	    return (int) Math.ceil (255f * cubicInt(iR, y0, y1) / black);
	}
	
	public final static int ryb2rgb(PixelFloat color){
		if (color == null) return 0;

		float c = 255f - 255f / mPercent * color.white;

		int red = 0;
		int yellow = 0;
		int blue = 0;
		float m = 0;

		m = Math.max(Math.max(color.red, color.yellow),color.blue);
		if (m != 0){
			red = (int) (c * color.red / m);
			yellow = (int) (c * color.yellow / m);
			blue = (int) (c * color.blue / m);
		}
		

	    float R = (red  & 0xff)/ 255f;
	    float Y = (yellow  & 0xff)/ 255f;
	    float B = (blue  & 0xff)/ 255f;
	    int R1 = getR(R,Y,B,color.darkness);
	    int G1 = getG(R,Y,B,color.darkness) ;
	    int B1 = getB(R,Y,B,color.darkness);
		//Log.d("","alpha "+color.alpha);
	    return Color.rgb(R1, G1, B1);
	}
	/*
	public static int ryb2rgb(byte r, byte y, byte b, byte w) {
		float c = 255f - 255f / mPercent * w;

		int red = 0;
		int yellow = 0;
		int blue = 0;
		float m = 0;

		m = Math.max(Math.max(r, y),b);
		if (m != 0){
			red = (int) (c * r / m);
			yellow = (int) (c * y / m);
			blue = (int) (c * b / m);
		}

	    float R = (red  & 0xff)/ 255f;
	    float Y = (yellow  & 0xff)/ 255f;
	    float B = (blue  & 0xff)/ 255f;
	    int R1 = getR(R,Y,B) ;
	    int G1 = getG(R,Y,B) ;
	    int B1 = getB(R,Y,B) ;
		//Log.d("","alpha "+color.alpha);
	    return Color.rgb(R1, G1, B1);
	}
*/
/*	
	public static int ryb2rgb0(float red, float yellow, float blue, float white){

		float total = red+yellow+blue+white;
		float c = 255f/total;

	    float R = red * c/ 255f;
	    float Y = yellow * c/ 255f;
	    float B = blue/ 255f;
	    int R1 = getR(R,Y,B) ;
	    int G1 = getG(R,Y,B) ;
	    int B1 = getB(R,Y,B) ;
		
		Log.d("","C "+c+" B "+B+" B1 "+B1);
		//Log.d("","alpha "+color.alpha);
	    return Color.rgb(0, 0, B1);
	}	
*/	
	public static void initBG(Context context) {
		bgBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.canvas0);
        rectS = new Rect(0,0,bgBitmap.getWidth(),bgBitmap.getHeight());
        rectD = new Rect(0,0,brushWidth*2, brushWidth*2);
		mask = BitmapFactory.decodeResource(context.getResources(), R.drawable.mask1);
        rectMask = new Rect(0,0,mask.getWidth(),mask.getHeight());
	}	

	/*
	public static void drawBG(Canvas canvas) {
    	canvas.drawBitmap(Var.bgBitmap, rectD, rectD, null);
	}
	*/
	
//
	public static void drawBG(Canvas canvas) {
		/////////////////paint.setColor(color);
		canvas.drawRect(rectD, paint);
	}

	public static void drawText(Canvas canvas, int text, boolean c) {
        paint.setColor(Color.WHITE);
        if (c)
    		canvas.drawCircle(brushWidth, brushWidth, brushRadius, paint);
        
        String s = text+"%";
        Rect bounds = new Rect(); 
        
        paint.setTextSize(brushRadius/1.5f);
        paint.getTextBounds(s, 0, s.length(), bounds);
        canvas.drawText(""+text+"%", brushWidth-(bounds.right-bounds.left)/2, brushWidth, paint);
        paint.setColor(Color.BLACK);
        canvas.drawText(""+text+"%", brushWidth-(bounds.right-bounds.left)/2+1, brushWidth+1, paint);
	}

};


