package com.alexmochalov.colors;

import com.example.draw.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

public class MainActivity extends Activity implements View.OnClickListener {
	ViewCanvas viewCanvas;
	
	Context mContext;

	// The size of tbe list of the last brushes
	private int MIXES_COUNT = 10;
	// And the list
	private Brush mixes[] = new Brush[MIXES_COUNT];

	private RelativeLayout root = null;
	private LinearLayout menu = null;
	private LinearLayout submenu = null;
	private LinearLayout submenu1 = null;
	private LinearLayout submenu2 = null;
	private LinearLayout submenu3 = null;
	private LinearLayout submenuSizes = null;
	
	private Tool optionsSubmenu;
	
	// Temporary Utilsiables to create interface
	private Tube tube;
	private ToolSize toolSize = null;
	private Tool toolOpaque = null;
	
	private boolean rootVisible = true;
	private boolean submenuVisible = false;
	private boolean submenu3Visible = false;
	private boolean submenu4Visible = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        
        final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        		| View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        decorView
        .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
        {

            @Override
            public void onSystemUiVisibilityChange(int visibility)
            {
                if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                {
                    decorView.setSystemUiVisibility(uiOptions);
                }
            }
        });
        
        mContext = this;
        
	    ActionBar getActionBar = getActionBar();
	    getActionBar.hide();
		setSize();
		
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Utils.initBG(this);
		
		viewCanvas = (ViewCanvas)this.findViewById(R.id.viewCanvas);
		
		createMenu();
		
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		int idBG = prefs.getInt("PREFS_BG", R.drawable.canvas0);
		viewCanvas.setBG(idBG, this);
	}	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
	    super.onWindowFocusChanged(hasFocus);
	    if(hasFocus)
	    {
	        getWindow().getDecorView().setSystemUiVisibility(
	        		View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
		            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
		            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
		            | View.SYSTEM_UI_FLAG_FULLSCREEN
	            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
	            );
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@SuppressLint("NewApi")
	private void createMenu(){
		
		root = (RelativeLayout) findViewById(R.id.root);
		menu = (LinearLayout) findViewById(R.id.menu);
	    submenu = (LinearLayout) findViewById(R.id.submenu);
		submenu.setVisibility(View.INVISIBLE);

		submenu1 = (LinearLayout) findViewById(R.id.submenu1);
		submenu2 = (LinearLayout) findViewById(R.id.submenu2);
		//read();

		final Brush brush = viewCanvas.getBrush();
		
		menu.addView(brush);
		brush.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Log.d("", brush.pixelToString());
				if (!submenuVisible)
					submenu.setVisibility(View.VISIBLE);
				else submenu.setVisibility(View.INVISIBLE);
				submenuVisible = !submenuVisible;
			}});
		
		final Tool tool = new Tool(this);
		tool.setMode(Utils.Mode.none, BitmapFactory.decodeResource(getResources(), R.drawable.void_tube));
		tool.setOnClickListener(this);
		menu.addView(tool);
		
		tube = new Tube(this);
		tube.setColor(hexToRYB(ContextCompat.getColor(this, R.color.ryb_red)));
		tube.setBrush(viewCanvas);
		
		try{
			menu.addView(tube);
		} catch(Exception e){
			Log.d("dr","error "+e.toString());
		}

		tube = new Tube(this);
		tube.setColor(hexToRYB(ContextCompat.getColor(this, R.color.ryb_yellow)));
		tube.setBrush(viewCanvas);
		menu.addView(tube);
		
		tube = new Tube(this);
		tube.setColor(hexToRYB(ContextCompat.getColor(this, R.color.ryb_blue)));
		tube.setBrush(viewCanvas);
		menu.addView(tube);
		
		tube = new Tube(this);
		tube.setColor(hexToRYB(ContextCompat.getColor(this, R.color.ryb_umbra)));
		tube.setBrush(viewCanvas);
		menu.addView(tube);

		tube = new Tube(this);
		tube.setColor(hexToRYB(ContextCompat.getColor(this, R.color.ryb_white)));
		tube.setBrush(viewCanvas);
		menu.addView(tube);

		//-------------- SUBMENU size -----------------------
		toolSize = new ToolSize(this);
		toolSize.setViewCanvas(viewCanvas);
		toolSize.callback = new ToolSize.Listener(){
			@Override
			public void callbackVALUE_CHANGED(int value)
			{
				viewCanvas.setBrushSize(value);
			}
		};
		menu.addView(toolSize);
		
		//-------------- \SUBMENU size -----------------------

		
		if (!ViewConfiguration.get(this).hasPermanentMenuKey()){
			optionsSubmenu = new Tool(this);
			optionsSubmenu.setMode(2, BitmapFactory.decodeResource(getResources(), R.drawable.menu));
			optionsSubmenu.setOnClickListener(this);
			menu.addView(optionsSubmenu);
		}
		
		
		for (int i = 0; i<MIXES_COUNT; i++){
			mixes[i] = new Brush(this);
			mixes[i].setRadius(Utils.brushRadius, false);
			submenu1.addView(mixes[i]);
			mixes[i].setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						submenu.setVisibility(View.INVISIBLE);
						submenuVisible = false;
						brush.copy((Brush)v);
					}});
		}

		
		
		final Tool toolSpread = new Tool(this);
		toolSpread.setMode(Utils.Mode.spread, BitmapFactory.decodeResource(getResources(), R.drawable.spread));
		toolSpread.setOnClickListener(this);
		submenu2.addView(toolSpread);

		final Tool toolErase = new Tool(this);
		toolErase.setMode(Utils.Mode.erase, BitmapFactory.decodeResource(getResources(), R.drawable.erase));
		toolErase.setOnClickListener(this);
		submenu2.addView(toolErase);

		//-------------- SUBMENU opaque -----------------------
		submenu3 = new LinearLayout(this);
		submenu3.setOrientation(LinearLayout.HORIZONTAL);
		submenu2.addView(submenu3);

		toolOpaque = new Tool(this);
		toolOpaque.setMode(100, null);
		submenu3.addView(toolOpaque);

		final Tool toolOpaque1 = new Tool(this);
		toolOpaque1.setMode(100, null);
		toolOpaque1.setOnClickListener(this);
		submenu3.addView(toolOpaque1);

		final Tool toolOpaque4 = new Tool(this);
		toolOpaque4.setMode(80, null);
		toolOpaque4.setOnClickListener(this);
		submenu3.addView(toolOpaque4);

		final Tool toolOpaque2 = new Tool(this);
		toolOpaque2.setMode(50, null);
		toolOpaque2.setOnClickListener(this);
		submenu3.addView(toolOpaque2);

		final Tool toolOpaque3 = new Tool(this);
		toolOpaque3.setMode(10, null);
		toolOpaque3.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					brush.setTransparency(toolOpaque3.getCode());
					submenu.setVisibility(View.INVISIBLE);
					submenuVisible = false;
				}});
		submenu3.addView(toolOpaque3);

		toolOpaque1.setVisibility(View.INVISIBLE);
		toolOpaque2.setVisibility(View.INVISIBLE);
		toolOpaque3.setVisibility(View.INVISIBLE);
		toolOpaque4.setVisibility(View.INVISIBLE);

		toolOpaque.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					if (submenu3Visible){
						toolOpaque1.setVisibility(View.INVISIBLE);
						toolOpaque2.setVisibility(View.INVISIBLE);
						toolOpaque3.setVisibility(View.INVISIBLE);
						toolOpaque4.setVisibility(View.INVISIBLE);
					} else {
						toolOpaque1.setVisibility(View.VISIBLE);
						toolOpaque2.setVisibility(View.VISIBLE);
						toolOpaque3.setVisibility(View.VISIBLE);
						toolOpaque4.setVisibility(View.VISIBLE);
					}
					submenu3Visible = !submenu3Visible;
				}

			});
			
/*
		//-------------- SUBMENU size -----------------------
		submenuSizes = new LinearLayout(this);
		submenuSizes.setOrientation(LinearLayout.HORIZONTAL);
        //LayoutParams linLayoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 		
		submenu2.addView(submenuSizes);
		
/*		
		toolSize = new Tool(this);
		toolSize.setMode(Utils.Mode.erase, 
			BitmapFactory.decodeResource(getResources(), R.drawable.size2));
		submenuSizes.addView(toolSize);
		//submenuSizes.setVisibility(View.INVISIBLE);
		
	
		final Tool toolSize0 = new Tool(this);
		toolSize0.setMode(1000, BitmapFactory.decodeResource(getResources(), R.drawable.size1));
		toolSize0.setOnClickListener(this);
		submenuSizes.addView(toolSize0);
		
		final Tool toolSize1 = new Tool(this);
		toolSize1.setMode(1001, BitmapFactory.decodeResource(getResources(), R.drawable.size1));
		toolSize1.setOnClickListener(this);
		submenuSizes.addView(toolSize1);
		
		final Tool toolSize2 = new Tool(this);
		toolSize2.setMode(1002, BitmapFactory.decodeResource(getResources(), R.drawable.size2));
		toolSize2.setOnClickListener(this);
		submenuSizes.addView(toolSize2);
		
		final Tool toolSize3 = new Tool(this);
		toolSize3.setMode(1003, BitmapFactory.decodeResource(getResources(), R.drawable.size3));
		toolSize3.setOnClickListener(this);
		submenuSizes.addView(toolSize3);
		
		final Tool toolSize4 = new Tool(this);
		toolSize4.setMode(1004, BitmapFactory.decodeResource(getResources(), R.drawable.size4));
		toolSize4.setOnClickListener(this);
		submenuSizes.addView(toolSize4);

		toolSize1.setVisibility(View.INVISIBLE);
		toolSize2.setVisibility(View.INVISIBLE);
		toolSize3.setVisibility(View.INVISIBLE);
		toolSize4.setVisibility(View.INVISIBLE);
		toolSize0.setVisibility(View.INVISIBLE);
		
		toolSize.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					if (submenu4Visible){
						toolSize1.setVisibility(View.INVISIBLE);
						toolSize2.setVisibility(View.INVISIBLE);
						toolSize3.setVisibility(View.INVISIBLE);
						toolSize4.setVisibility(View.INVISIBLE);
						toolSize0.setVisibility(View.INVISIBLE);
					} else {
						toolSize1.setVisibility(View.VISIBLE);
						toolSize2.setVisibility(View.VISIBLE);
						toolSize3.setVisibility(View.VISIBLE);
						toolSize4.setVisibility(View.VISIBLE);
						toolSize0.setVisibility(View.VISIBLE);
					}
					submenu4Visible = !submenu4Visible;
				}

			});
		*/	
	}

	
	public void openMenu(){
		PopupMenu dropDownMenu = new PopupMenu(getApplicationContext(), optionsSubmenu);
        dropDownMenu.getMenuInflater().inflate(R.menu.main, dropDownMenu.getMenu());
        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
        		int id = menuItem.getItemId();
        	    switch (id)
        	     {
        			case R.id.action_clear:
        				AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(mContext);
        				dlgAlert.setMessage(""+ viewCanvas.getN());
        				dlgAlert.setPositiveButton("Ok",
        					    new DialogInterface.OnClickListener() {
        					        public void onClick(DialogInterface dialog, int which) {
        					          //dismiss the dialog  
        					        }
        					    });
        				dlgAlert.show();
        				viewCanvas.clear();
        				break;
        			case R.id.action_exit:
        				finish();
        		 default:
        	     } 
                return true;
            }
        });
        dropDownMenu.show();		
	}
	
	@Override
	public void onClick(View v) {
		Tool tool = (Tool)v;
		
		Brush brush = viewCanvas.getBrush();
		if (tool.getCode() <= 0)
			brush.setMode(tool.getMode(), tool.getIcon());
		else if (tool.getCode() == 2)
			openMenu();
		else if (tool.getCode() < 1000){
			toolOpaque.setCode(tool.getCode());
			brush.setTransparency(tool.getCode());
//		} else {
//			toolSize.setIcon(tool.getIcon());
//			brush.setSize(tool.getCode(), viewCanvas);
		}
		
		submenu.setVisibility(View.INVISIBLE);
		submenuVisible = false;
	}
	
	private short[] hexToRYB(int color)
	{
		short[] ret = { (short)(color >> 24), 
			(short)(color >> 16 & 0x00ff), 
			(short)(color >> 8 & 0x00ff), 
			(short)(color & 0x00ff) };
		return ret;
	}
	
	@Override public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (rootVisible){
				root.setVisibility(View.INVISIBLE);
			}else{
				root.setVisibility(View.VISIBLE);
			}
			rootVisible = !rootVisible;
			return true; 
		} return super.onKeyDown(keyCode, event);
	}
	
	private void setSize(){
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		float yInches= metrics.heightPixels/metrics.ydpi; 
		float xInches= metrics.widthPixels/metrics.xdpi;
		double diagonalInches = Math.sqrt(xInches*xInches + yInches*yInches); 
		//Toast.makeText(this, "suze "+diagonalInches, Toast.LENGTH_LONG).show();
		//double k = Math.sqrt(4.795f/diagonalInches);
		//Var.brushWidth = (int)(50f*k);
		//Var.brushRadius = (int)(32f*k);
		Utils.brushWidth = Math.min(50, (int)(metrics.widthPixels/16f));
		Utils.brushRadius = (int)(Utils.brushWidth*0.64f);

		//Toast.makeText(this, "Var.brushWidth "+Var.brushWidth, Toast.LENGTH_LONG).show();
	}
	
}