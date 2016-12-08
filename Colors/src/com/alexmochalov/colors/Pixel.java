package com.alexmochalov.colors;

import java.io.Serializable;

public class Pixel {
	// percent of red
	short red;
	// percent of yellow
	short yellow;
	// percent of blue
	short blue;
	short white;
	// red + yellow + blue + white = 100 
	boolean modified = false;
	
	public Pixel(short thickness){
		this.red = 0;
		this.yellow = 0;
		this.blue = 0;
		this.white = 0;
	}

	public Pixel(Pixel source){
		this.red = source.red;
		this.yellow = source.yellow;
		this.blue = source.blue;
		this.white = source.white;
	}

	public void setModified(boolean p0)
	{
		modified = true;
	}

	public Pixel(short thickness, short[] ryb) {
		this.red = ryb[0];
		this.yellow = ryb[1];
		this.blue = ryb[2];
		this.white = ryb[3];
		//this.white = 0;
	}
	
	public Pixel(int i, int j, int k, int l) {
		this.red = (short)i;
		this.yellow = (short)j;
		this.blue = (short)k;
		this.white = (short)l;
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
//		this.color = Utils.ryb2rgb(this);
	}
	
	public void set(short t, Pixel pixel, boolean modified)
	{
		if (pixel == null) return;

		red = pixel.red;
		yellow = pixel.yellow;
		blue = pixel.blue;
		white = pixel.white;
		
		this.modified = modified;
	}
	
	public void add1(Pixel pixel, boolean modified)
	{
		if (pixel == null) return;
		this.modified = modified;
		
		float rr = 100/ Utils.mPercent * red + Utils.mPercentAdd/Utils.mPercent * pixel.red ;   
		float yy = 100/ Utils.mPercent * yellow + Utils.mPercentAdd/Utils.mPercent * pixel.yellow ;   
		float bb = 100/ Utils.mPercent * blue + Utils.mPercentAdd/Utils.mPercent * pixel.blue ; 
		float ww = 100/ Utils.mPercent * white + Utils.mPercentAdd/Utils.mPercent * pixel.white ; 
		
		red = (short) (rr / (rr + yy + bb + ww) * Utils.mPercent);
		yellow = (short) (yy / (rr + yy + bb + ww) * Utils.mPercent);
		blue = (short) (bb / (rr + yy + bb + ww) * Utils.mPercent);
		white = (short) (ww / (rr + yy + bb + ww) * Utils.mPercent);
	}
	
	public void add(Pixel pixel, boolean modified)
	{
		if (pixel == null) return;
		this.modified = modified;
		
		float rr = 100/ Utils.mPercent * red + 50/Utils.mPercent * pixel.red ;   
		float yy = 100/ Utils.mPercent * yellow + 50/Utils.mPercent * pixel.yellow ;   
		float bb = 100/ Utils.mPercent * blue + 50/Utils.mPercent * pixel.blue ; 
		float ww = 100/ Utils.mPercent * white + 50/Utils.mPercent * pixel.white ; 
		
		red = (short) (rr / (rr + yy + bb + ww) * Utils.mPercent);
		yellow = (short) (yy / (rr + yy + bb + ww) * Utils.mPercent);
		blue = (short) (bb / (rr + yy + bb + ww) * Utils.mPercent);
		white = (short) (ww / (rr + yy + bb + ww) * Utils.mPercent);
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

	public void copy(Pixel pixel) {
		this.red = pixel.red;
		this.yellow = pixel.yellow;
		this.blue = pixel.blue;
		this.white = pixel.white;
		
	}

	public void set(short[] m) {
		this.red = m[0];
		this.yellow = m[1];
		this.blue = m[2];
		this.white = 0;
	}
}