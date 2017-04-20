package com.alexmochalov.colors;

import java.io.Serializable;

public class PixelFloat {
	// percent of red
	public float red;
	// percent of yellow
	public float yellow;
	// percent of blue
	public float blue;
	// percent of white
	public float white;
	// red + yellow + blue + white = 100
	
	// level of BLACK
	public float darkness; // 1...10;  G,R,B divided to "black"  
	
	boolean modified = false;
	
	public PixelFloat(){
		this.red = 0;
		this.yellow = 0;
		this.blue = 0;
		this.white = 0;
		this.darkness = 1;
	}

	public PixelFloat(PixelFloat source)
	{
		this.red = source.red;
		this.yellow = source.yellow;
		this.blue = source.blue;
		this.white = source.white;
		this.darkness = source.darkness;
	}

	public void setModified(boolean p0)
	{
		modified = true;
	}

	public PixelFloat(short thickness, short[] ryb) {
		this.red = ryb[0];
		this.yellow = ryb[1];
		this.blue = ryb[2];
		this.white = ryb[3];
		this.darkness = 1;
	}
	
	public PixelFloat(float r, float y, float b, float w) {
		this.red = r;
		this.yellow = y;
		this.blue = b;
		this.white = w;
		this.darkness = 1;
	}
	
	public PixelFloat(float r, float y, float b) {
		float k = 100/(r+y+b);
		this.red = r*k;
		this.yellow = y*k;
		this.blue = b*k;
		this.white = 0;
		this.darkness = 1;
	}
	
	
	public PixelFloat(float r, float y, float b, float w, boolean percent) {
		float k = 100/(r+y+b);
		this.red = r*k;
		this.yellow = y*k;
		this.blue = b*k;
		this.white = w;
		this.darkness = 1;
	}
	
	public PixelFloat(short r, short y, short b, short w) {
		this.red = r;
		this.yellow = y;
		this.blue = b;
		this.white = w;
		this.darkness = 1;
	}

	public boolean isZero()
	{
		return red + yellow + blue + white == 0;
	}

	public void clear()
	{
		this.red = 0;
		this.yellow = 0;
		this.blue = 0;
		this.white = 0;
		this.darkness = 1;
//		this.color = Utils.ryb2rgb(this);
	}
	
	public void set(short t, PixelFloat PixelFloat, boolean modified)
	{
		if (PixelFloat == null) return;

		red = PixelFloat.red;
		yellow = PixelFloat.yellow;
		blue = PixelFloat.blue;
		white = PixelFloat.white;
		darkness = PixelFloat.darkness;
		
		this.modified = modified;
	}
	
	public void add1(PixelFloat PixelFloat, boolean modified)
	{
		if (PixelFloat == null) return;
		this.modified = modified;
		
		float rr = 100/ Utils.mPercent * red + Utils.mPercentAdd/Utils.mPercent * PixelFloat.red ;   
		float yy = 100/ Utils.mPercent * yellow + Utils.mPercentAdd/Utils.mPercent * PixelFloat.yellow ;   
		float bb = 100/ Utils.mPercent * blue + Utils.mPercentAdd/Utils.mPercent * PixelFloat.blue ; 
		float ww = 100/ Utils.mPercent * white + Utils.mPercentAdd/Utils.mPercent * PixelFloat.white ; 
		
		red = (rr / (rr + yy + bb + ww) * Utils.mPercent);
		yellow = (yy / (rr + yy + bb + ww) * Utils.mPercent);
		blue = (bb / (rr + yy + bb + ww) * Utils.mPercent);
		white = (ww / (rr + yy + bb + ww) * Utils.mPercent);
	}
	
	public void add(PixelFloat PixelFloat, boolean modified)
	{
		if (PixelFloat == null) return;
		this.modified = modified;
		
		float rr = 100/ Utils.mPercent * red + 50/Utils.mPercent * PixelFloat.red ;   
		float yy = 100/ Utils.mPercent * yellow + 50/Utils.mPercent * PixelFloat.yellow ;   
		float bb = 100/ Utils.mPercent * blue + 50/Utils.mPercent * PixelFloat.blue ; 
		float ww = 100/ Utils.mPercent * white + 50/Utils.mPercent * PixelFloat.white ; 
		
		red = (rr / (rr + yy + bb + ww) * Utils.mPercent);
		yellow = (yy / (rr + yy + bb + ww) * Utils.mPercent);
		blue = (bb / (rr + yy + bb + ww) * Utils.mPercent);
		white = (ww / (rr + yy + bb + ww) * Utils.mPercent);
		
		darkness = (darkness + PixelFloat.darkness)/2;
	}
	
	public boolean isModified() {
		return modified;
	}
	
	public void clearModified() {
		modified = false;
	}
	public String toStr() {
		// TODO Auto-generated method stub
		return ""+" red "+red+" yellow "+yellow+" blue "+blue+" white "+white;
	}

	public void copy(PixelFloat PixelFloat) {
		this.red = PixelFloat.red;
		this.yellow = PixelFloat.yellow;
		this.blue = PixelFloat.blue;
		this.white = PixelFloat.white;
		this.darkness = PixelFloat.darkness;
		
	}
}
