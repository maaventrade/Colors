package com.alexmochalov.dialogs;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;

import java.util.*;

import com.example.draw.R;

public class DialogCanvas extends Dialog
  {
	private Context context;
	private Dialog dialog;
	MyCallback callback = null;
	interface MyCallback {
		void callbackACTION_DOWN(int bgID); 
	} 

	public DialogCanvas(Context context) {
		super(context);
		this.context = context;
		dialog = this;
		setTitle(context.getResources().getString(R.string.select_canvas));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
        //        WindowManager.LayoutParams.MATCH_PARENT);

		setContentView(R.layout.canvas_dialog);

        final ArrayList<Integer> canvas = new ArrayList<Integer>();
        AdapterCanvas boxAdapter; 
        boxAdapter = new AdapterCanvas(context, canvas);

		canvas.add(new Integer(R.drawable.canvas0));
		canvas.add(new Integer(R.drawable.canvas1));
		canvas.add(new Integer(R.drawable.canvas2));
		
		ListView listView = (ListView) findViewById(R.id.listViewCanvas);
        listView.setAdapter(boxAdapter);
		listView.setOnItemClickListener( new ListView.OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int index, long p4)
				{
					callback.callbackACTION_DOWN(canvas.get(index));
					dialog.dismiss();
				}}
		);
	}	
}
