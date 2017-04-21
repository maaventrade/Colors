package com.alexmochalov.colors;

import java.io.File;

import com.alexmochalov.dialogs.*;
import com.alexmochalov.tools.Dark;
import com.alexmochalov.tools.Tube;
import com.example.draw.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

	ViewCanvas viewCanvas;

	Context mContext;

	// The size of tbe list of the last brushes
	private int MIXES_COUNT = 10;
	// And the list
	private Brush mixes[] = new Brush[MIXES_COUNT];

	private RelativeLayout root = null;
	//private LinearLayout menu = null;
	private LinearLayout submenu = null;

	private ViewSubmenu viewSubmenu;

	// private LinearLayout submenu1 = null;
	private LinearLayout submenu2 = null;
	private LinearLayout submenu3 = null;
	private LinearLayout submenuSizes = null;

	private Tool optionsSubmenu;

	// Temporary Utilsiables to create interface
	private Tube tube;
	private Tool toolOpaque = null;

	private boolean rootVisible = true;
	private boolean submenuVisible = false;
	private boolean submenu3Visible = false;
	private boolean submenu4Visible = false;

	SharedPreferences prefs;

	private final int action_mode = 1101;
	private final int action_black = 1102;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_main);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		final View decorView = getWindow().getDecorView();
		
		// Hide both the navigation bar and the status bar.
		// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and
		// higher, but as
		// a general rule, you should design your app to hide the status bar
		// whenever you
		// hide the navigation bar.

		final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN							
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		
		decorView.setSystemUiVisibility(uiOptions);
		decorView
				.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

					@Override
					public void onSystemUiVisibilityChange(int visibility) {
						if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
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

		File file = new File(Utils.APP_FOLDER);
		if (!file.exists()) {
			file.mkdirs();
		}

		
		Utils.initBG(this);

		viewCanvas = (ViewCanvas) this.findViewById(R.id.viewCanvas);

		createMenu();

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		int idBG = prefs.getInt("PREFS_BG", R.drawable.canvas0);
		viewCanvas.setBG(idBG, this);

		viewCanvas.callback = new ViewCanvas.MyCallback() {
			// When some brush is used lets add it to the list (submenu)
			@Override
			public void callbackACTION_DOWN(Brush brush) {
				if (viewSubmenu != null)
					viewSubmenu.insertBrush(brush);
				else
					Log.d("", "viewSubmenu = NULL !!!??? ");
			}
		};

		// Load the list of the used brushes
		viewSubmenu.loadBrushes(viewCanvas);
		viewCanvas
				.onResume(PreferenceManager.getDefaultSharedPreferences(this));
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@SuppressLint("NewApi")
	private void createMenu() {
		root = (RelativeLayout) findViewById(R.id.root);
		
		LinearLayout menu = (LinearLayout) findViewById(R.id.menu);
		menu.removeAllViews();
		
		submenu = (LinearLayout) findViewById(R.id.submenu);
		submenu.setVisibility(View.INVISIBLE);

		viewSubmenu = (ViewSubmenu) findViewById(R.id.submenu1);
		viewSubmenu.initValues(this);

		// submenu1 = (LinearLayout) findViewById(R.id.submenu1);
		submenu2 = (LinearLayout) findViewById(R.id.submenu2);
		// read();

		Brush brush = viewCanvas.getBrush();

		menu.addView(brush);
		
		brush.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!submenuVisible)
					submenu.setVisibility(View.VISIBLE);
				else
					submenu.setVisibility(View.INVISIBLE);
				submenuVisible = !submenuVisible;
			}
		});

		final Tool tool = new Tool(this);
		tool.setMode(Utils.Mode.none, BitmapFactory.decodeResource(
				getResources(), R.drawable.void_tube));
		tool.setOnClickListener(this);
		menu.addView(tool);

		tube = new Tube(this);
		tube.setColor(hexToRYB(ContextCompat.getColor(this, R.color.ryb_red)));
		tube.setBrush(viewCanvas);

		try {
			menu.addView(tube);
		} catch (Exception e) {
			Log.d("dr", "error " + e.toString());
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
		
		/*
		colorBlack = new Tool(this);
		colorBlack.setMode(action_black,
					  BitmapFactory.decodeResource(getResources(), R.drawable.erase));
		colorBlack.setOnClickListener(this);
		menu.addView(colorBlack);
		*/
		
		Tool arrow;
		arrow = new Tool(this);
		arrow.setMode(action_mode,
				BitmapFactory.decodeResource(getResources(), R.drawable.arrow));
		arrow.setOnClickListener(this);
		menu.addView(arrow);
		
		Dark dark;
		dark = new Dark(this);
		dark.setCanvas(viewCanvas);
		dark.setOnClickListener(this);
		menu.addView(dark);

		if (!ViewConfiguration.get(this).hasPermanentMenuKey()) {
			optionsSubmenu = new Tool(this);
			optionsSubmenu.setMode(2, BitmapFactory.decodeResource(
					getResources(), R.drawable.menu));
			optionsSubmenu.setOnClickListener(this);
			menu.addView(optionsSubmenu);
		}

		viewSubmenu.callback = new ViewSubmenu.MyCallback() {
			@Override
			public void callbackSELECTED(Brush v) {
				submenu.setVisibility(View.INVISIBLE);
				submenuVisible = false;
				viewCanvas.copyBrush(v);
			}
		};

		// -------------- SUBMENU size -----------------------
		ToolSize toolSize = null;
		
		toolSize = new ToolSize(this, 1, 11, (int)viewCanvas.getBrushSize());
		toolSize.callback = new ToolSize.Listener() {
			@Override
			public void callbackVALUE_CHANGED(int value) {
				viewCanvas.setBrushSize(value);
			}
		};
		submenu2.addView(toolSize);
		// -------------- SUBMENU size -----------------------
		ToolSize tooldarkness = null;
		
		tooldarkness = new ToolSize(this,1, 100, (int)viewCanvas.getDarkness());
		tooldarkness.callback = new ToolSize.Listener() {
			@Override
			public void callbackVALUE_CHANGED(int value) {
				viewCanvas.setDarkness(1+(float)value/10);
			}
		};
		submenu2.addView(tooldarkness); 
		// -------------- \SUBMENU size -----------------------

		final Tool toolSpread = new Tool(this);
		toolSpread
				.setMode(Utils.Mode.spread, BitmapFactory.decodeResource(
						getResources(), R.drawable.spread));
		toolSpread.setOnClickListener(this);
		submenu2.addView(toolSpread);

		final Tool toolErase = new Tool(this);
		toolErase.setMode(Utils.Mode.erase,
				BitmapFactory.decodeResource(getResources(), R.drawable.erase));
		toolErase.setOnClickListener(this);
		submenu2.addView(toolErase);

		// -------------- SUBMENU opaque -----------------------
		submenu3 = new LinearLayout(this);
		submenu3.setOrientation(LinearLayout.VERTICAL);
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

		/*
		final Tool toolOpaque3 = new Tool(this);
		toolOpaque3.setMode(10, null);
		toolOpaque3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				brush.setTransparency(toolOpaque3.getCode());
				submenu.setVisibility(View.INVISIBLE);
				submenuVisible = false;
			}
		});
		submenu3.addView(toolOpaque3);
		*/

		toolOpaque1.setVisibility(View.INVISIBLE);
		toolOpaque2.setVisibility(View.INVISIBLE);
		//toolOpaque3.setVisibility(View.INVISIBLE);
		toolOpaque4.setVisibility(View.INVISIBLE);
/*
		toolOpaque.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (submenu3Visible) {
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
*/
		/*
		 * //-------------- SUBMENU size ----------------------- submenuSizes =
		 * new LinearLayout(this);
		 * submenuSizes.setOrientation(LinearLayout.VERTICAL); //LayoutParams
		 * linLayoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT,
		 * LayoutParams.WRAP_CONTENT); submenu2.addView(submenuSizes);
		 * 
		 * /* toolSize = new Tool(this); toolSize.setMode(Utils.Mode.erase,
		 * BitmapFactory.decodeResource(getResources(), R.drawable.size2));
		 * submenuSizes.addView(toolSize);
		 * //submenuSizes.setVisibility(View.INVISIBLE);
		 * 
		 * 
		 * final Tool toolSize0 = new Tool(this); toolSize0.setMode(1000,
		 * BitmapFactory.decodeResource(getResources(), R.drawable.size1));
		 * toolSize0.setOnClickListener(this); submenuSizes.addView(toolSize0);
		 * 
		 * final Tool toolSize1 = new Tool(this); toolSize1.setMode(1001,
		 * BitmapFactory.decodeResource(getResources(), R.drawable.size1));
		 * toolSize1.setOnClickListener(this); submenuSizes.addView(toolSize1);
		 * 
		 * final Tool toolSize2 = new Tool(this); toolSize2.setMode(1002,
		 * BitmapFactory.decodeResource(getResources(), R.drawable.size2));
		 * toolSize2.setOnClickListener(this); submenuSizes.addView(toolSize2);
		 * 
		 * final Tool toolSize3 = new Tool(this); toolSize3.setMode(1003,
		 * BitmapFactory.decodeResource(getResources(), R.drawable.size3));
		 * toolSize3.setOnClickListener(this); submenuSizes.addView(toolSize3);
		 * 
		 * final Tool toolSize4 = new Tool(this); toolSize4.setMode(1004,
		 * BitmapFactory.decodeResource(getResources(), R.drawable.size4));
		 * toolSize4.setOnClickListener(this); submenuSizes.addView(toolSize4);
		 * 
		 * toolSize1.setVisibility(View.INVISIBLE);
		 * toolSize2.setVisibility(View.INVISIBLE);
		 * toolSize3.setVisibility(View.INVISIBLE);
		 * toolSize4.setVisibility(View.INVISIBLE);
		 * toolSize0.setVisibility(View.INVISIBLE);
		 * 
		 * toolSize.setOnClickListener(new OnClickListener(){
		 * 
		 * @Override public void onClick(View v) { if (submenu4Visible){
		 * toolSize1.setVisibility(View.INVISIBLE);
		 * toolSize2.setVisibility(View.INVISIBLE);
		 * toolSize3.setVisibility(View.INVISIBLE);
		 * toolSize4.setVisibility(View.INVISIBLE);
		 * toolSize0.setVisibility(View.INVISIBLE); } else {
		 * toolSize1.setVisibility(View.VISIBLE);
		 * toolSize2.setVisibility(View.VISIBLE);
		 * toolSize3.setVisibility(View.VISIBLE);
		 * toolSize4.setVisibility(View.VISIBLE);
		 * toolSize0.setVisibility(View.VISIBLE); } submenu4Visible =
		 * !submenu4Visible; }
		 * 
		 * });
		 */
	}

	public void openMenu() {
		PopupMenu dropDownMenu = new PopupMenu(getApplicationContext(),
				optionsSubmenu);
		dropDownMenu.getMenuInflater().inflate(R.menu.main,
				dropDownMenu.getMenu());
		dropDownMenu
				.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem menuItem) {
						int id = menuItem.getItemId();
						switch (id) {
						case R.id.action_colors:
							DialogColors dialogColors = new DialogColors(mContext, viewCanvas.getBrush());
							dialogColors.callback = new DialogColors.MyCallback() {

								@Override
								public void callbackColorSelected() {
										//viewSubmenu.selectBrush(brush);
								}
								
							};

							dialogColors.show();
							break;
						case R.id.action_clear:
							AlertDialog.Builder dlgAlert = new AlertDialog.Builder(
									mContext);
							dlgAlert.setMessage("" + viewCanvas.getN());
							dlgAlert.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											// dismiss the dialog
										}
									});
							dlgAlert.show();
							viewCanvas.clear();
							break;
						case R.id.action_save:
							viewCanvas.save();
							return true;
						case R.id.action_load:
							DialogSelectImage dialog = new DialogSelectImage(
									mContext, viewCanvas.getWidth(), viewCanvas
											.getHeight());

							dialog.callback = new DialogSelectImage.MyCallback() {

								@Override
								public void callbackACTION_OPEN(String name) {
									viewCanvas.clear(); // ?????
									int i = name.lastIndexOf("/");
									if (i > 0)
										viewCanvas.setFileName(name.substring(i + 1));
									else
										viewCanvas.setFileName(name);

									viewCanvas.load1(name);
									viewCanvas.load(name);

								}

								@Override
								public void callbackACTION_NEW(int w, int h,
										String name) {
									viewCanvas.clear(); // ?????
									viewCanvas.setFileName(name);
	//								viewCanvas.newImage(w, h, name);
								}

							};

							dialog.show();

							return true;
						 case R.id.action_save_with:
							 /*
							 	final SelectFileDialog selectFileDialog = new SelectFileDialog(this, initPath, FILE_EXT, "", true, "" );
							 	selectFileDialog.callback = new SelectFileDialog.MyCallback() {
									@Override
									public void callbackACTION_SELECTED(String fileName) {
										if (fileName.equals("send picture by email")){
											viewCanvas.saveWithCanvas(fileName, true);
											return;
									    } else if (!fileName.endsWith(".png"))
											fileName = fileName + ".png";
										if (viewCanvas.saveWithCanvas(fileName, false));
											Toast.makeText(MainActivity.this, "File saved ", Toast.LENGTH_LONG).show();
									}
								};
							 	
							 	selectFileDialog.show();
							 	*/
								return true;
						 case R.id.action_file_information:
							 AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
							 
							 builder.setMessage(viewCanvas.getFilename()+"\n("+viewCanvas.getImageWidth()+"x"+viewCanvas.getImageHeight()+")")
							        .setTitle(mContext.getResources().getString(R.string.action_file_information))
							        .setCancelable(false)
							        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
							            public void onClick(DialogInterface dialog, int id) {
							                 //do things
							            }
							        });
							 AlertDialog alert = builder.create();
							 alert.show();
							 return true;
						case R.id.action_exit:
							finish();
							break;
						default:
						}
						return true;
					}
				});
		dropDownMenu.show();
	}

	@Override
	public void onClick(View v) {
		Tool tool = (Tool) v;

		Brush brush = viewCanvas.getBrush();
		if (tool.getCode() <= 0)
			brush.setMode(tool.getMode(), tool.getIcon());
		else if (tool.getCode() == 2)
			openMenu();
		else if (tool.getCode() < 1000) {
			toolOpaque.setCode(tool.getCode());
			brush.setTransparency(tool.getCode());
		} else if (tool.getCode() == action_mode) {
			viewCanvas.changeMode();
			tool.setIcon(viewCanvas.getIcon());
		}

		submenu.setVisibility(View.INVISIBLE);
		submenuVisible = false;
	}

	private short[] hexToRYB(int color) {
		short[] ret = { (short) (color >> 24), (short) (color >> 16 & 0x00ff),
				(short) (color >> 8 & 0x00ff), (short) (color & 0x00ff) };
		return ret;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (rootVisible) {
				root.setVisibility(View.INVISIBLE);
			} else {
				root.setVisibility(View.VISIBLE);
			}
			rootVisible = !rootVisible;
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void setSize() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		float yInches = metrics.heightPixels / metrics.ydpi;
		float xInches = metrics.widthPixels / metrics.xdpi;
		double diagonalInches = Math
				.sqrt(xInches * xInches + yInches * yInches);
		// Toast.makeText(this, "suze "+diagonalInches,
		// Toast.LENGTH_LONG).show();
		// double k = Math.sqrt(4.795f/diagonalInches);
		// Var.brushWidth = (int)(50f*k);
		// Var.brushRadius = (int)(32f*k);
		Utils.setBrushWidth (Math.min(50, (int) (metrics.widthPixels / 16f)));

		// Toast.makeText(this, "Var.brushWidth "+Var.brushWidth,
		// Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Editor editor = prefs.edit();
		// The ViewCanvas doesnt have its own method onPause
		viewCanvas.onPause(this, editor);
		// Save the list of the used brushes
		viewSubmenu.saveBrushes();
		viewSubmenu.clearBrushes();
		viewSubmenu = null;

		/*
		 * //editor.putInt(PREFS_CANVAS_COLOR, palette.getCanvasColor());
		 * editor.putInt(PREFS_BRUSH_RADIUS, Var.brushRadius);
		 * editor.putString(PREFS_FILENAME, fileName);
		 * editor.putString(PREFS_INIT_PATH, initPath);
		 * editor.putBoolean(PREFS_FIRST_START, false);
		 * editor.putInt("PREFS_BG", viewCanvas.getIdBG());
		 * 
		 * editor.apply(); // To avoid memory leaking if (mHandler != null)
		 * mHandler.removeCallbacksAndMessages(null); viewSubmenu = null;
		 * viewCanvas = null;
		 * 
		 * brush = null; viewCanvas = null;
		 * 
		 * root = null; menu = null; submenu = null; viewSubmenu = null;
		 * submenu2 = null; submenu3 = null; submenu4 = null;
		 * 
		 * ///// Tube tube; tube = null; toolSize = null; toolOpaque = null;
		 */
	}

}
