package com.alexmochalov.dialogs;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;

import java.io.File;
import java.util.*;

import com.alexmochalov.colors.Utils;
import com.example.draw.R;

import android.view.View.*;
 
public class DialogNew extends Dialog implements OnClickListener
{

	private Context context;
	private Dialog dialog;
	MyCallback callback = null;
	private int width;
	private int height;
	private EditText tWidth;
	private EditText tHeight;
	private EditText tName;
	private Button buttonAdd;
	private Button buttonCancel;
	
	@Override
	public void onClick(View p1)
	{
		if (p1 == buttonAdd){
			int w = Integer.parseInt(tWidth.getText().toString());
			int h = Integer.parseInt(tHeight.getText().toString());
			String s = tName.getText().toString();
			
			if (w < 100 || h < 100){
				Toast.makeText(context, context.getResources().getString(R.string.size_warning1)+" 100", Toast.LENGTH_LONG).show();
				return;
			}
			int m = Math.max(width,  height);
			if (w > m || h > m){
				Toast.makeText(context, context.getResources().getString(R.string.size_warning2)+" "+m, Toast.LENGTH_LONG).show();
				return;
			}
			
			callback.callbackACTION_OK(w, h, s);
		};
		dialog.dismiss();
	}
	
	public DialogNew(Context context, int width, int height) {
		super(context);
		this.context = context;
		dialog = this;
		setTitle(context.getResources().getString(R.string.new_file));
		this.width = width;
		this.height = height;
	}

	interface MyCallback {
		void callbackACTION_OK(int w, int h, String name); 
	} 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
        //        WindowManager.LayoutParams.MATCH_PARENT);

		setContentView(R.layout.dialog_new);
		
		tWidth = (EditText) findViewById(R.id.editTextWidth);
    	tHeight = (EditText) findViewById(R.id.editTextHeight);
		tName = (EditText) findViewById(R.id.editTextName);
		tWidth.setText(""+width);
		tHeight.setText(""+height);
		
		int i;
		for (i = 0; i<10000; i++){
			File file = new File(Utils.APP_FOLDER+"/"+"NewImage"+i+".png");
			if (!file.exists()) break;                          
		}
		
		tName.setText("NewImage"+i+".png");
		
		buttonAdd = (Button) findViewById(R.id.buttonAdd);
		buttonAdd.setOnClickListener(this);
		buttonCancel = (Button) findViewById(R.id.buttonCancel);
		buttonCancel.setOnClickListener(this);
	}	
}
