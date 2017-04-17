package com.alexmochalov.dialogs;
import java.util.*;

import com.example.draw.R;

import android.view.*;
import android.content.*;
import android.widget.*;
import android.graphics.*;
import android.graphics.drawable.*;

public class AdapterCanvas  extends BaseAdapter {
	Context context;
	LayoutInflater lInflater;
	ArrayList<Integer> objects;
	  
	AdapterCanvas(Context context, ArrayList<Integer> obj) {
		this.context = context;
	    objects = obj;
	    lInflater = (LayoutInflater)context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 }
	
	
	@Override
	public int getCount() {
		return objects.size();
	}

	@Override
	public Object getItem(int position) {
		return objects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
	    if (view == null) {
	      view = lInflater.inflate(R.layout.canvas_item, parent, false);
	    }
		
	    BitmapFactory.Options o = new BitmapFactory.Options();
	    o.inDither=false;                     
	    
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),(int)(getItem(position)), o);
		//BitmapDrawable d = new BitmapDrawable(context.getResources(), bmp);
		//LinearLayout l = (LinearLayout) view.findViewById(R.id.layoutItemColor);
	   // l.setBackground(d);
		ImageView l = (ImageView) view.findViewById(R.id.layoutItemColor);
		l.setImageBitmap(Bitmap.createBitmap(bmp, 0,0,100, 100));
	    return view;	
	  }

}
