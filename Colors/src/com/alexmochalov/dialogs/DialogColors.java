package com.alexmochalov.dialogs;

import java.util.ArrayList;

import com.alexmochalov.colors.Brush;
import com.alexmochalov.colors.PixelFloat;
import com.alexmochalov.colors.Utils;
import com.example.draw.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.util.*;
import android.widget.*;

public class DialogColors extends Dialog  implements OnSeekBarChangeListener {
	private Context context;
	private Dialog dialog;
	private ImageView image;
	private SeekBar sbRed;
	private SeekBar sbYellow;
	private SeekBar sbBlue;
	private SeekBar sbWhite;
	private SeekBar sbBlack;

	private TextView textViewRed;
	private TextView textViewYellow;
	private TextView textViewBlue;
	private TextView textViewWhite;
	private TextView textViewBlack;
	
	//private CheckBox checkBoxAdd;
	private boolean fromList = false;
	
	private PixelFloat pixel = new PixelFloat(0,0,0,0);
	
	private Brush mBrush;
	
	public MyCallback callback = null;
	
	public interface MyCallback {
		void callbackColorSelected(); 
	} 
	
	public DialogColors(Context context, Brush brush) {
		super(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);//
		this.context = context;
		mBrush = brush;
		dialog = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
        //        WindowManager.LayoutParams.MATCH_PARENT);

		setContentView(R.layout.colors);
		
		image = (ImageView)findViewById(R.id.ImageViewColor);
		
		sbRed = (SeekBar)findViewById(R.id.seekBarRed);
		sbRed.setMax(100);
		sbRed.setOnSeekBarChangeListener(this);
		sbYellow = (SeekBar)findViewById(R.id.seekBarYellow); 
		sbYellow.setMax(100);
		sbYellow.setOnSeekBarChangeListener(this);
		sbBlue = (SeekBar)findViewById(R.id.seekBarBlue); 
		sbBlue.setMax(100);
		sbBlue.setOnSeekBarChangeListener(this);
		
		sbWhite = (SeekBar)findViewById(R.id.seekBarWhite); 
		sbWhite.setMax(100);
		sbWhite.setOnSeekBarChangeListener(this);
		
		sbBlack = (SeekBar)findViewById(R.id.seekBarBlack); 
		sbBlack.setMax(100);
		sbBlack.setOnSeekBarChangeListener(this);
	
		textViewRed = (TextView)findViewById(R.id.textViewRed); 
		textViewYellow = (TextView)findViewById(R.id.textViewYellow); 
		textViewBlue = (TextView)findViewById(R.id.textViewBlue); 
		textViewWhite = (TextView)findViewById(R.id.textViewWhite); 
		textViewBlack = (TextView)findViewById(R.id.textViewBlack); 
        
       //checkBoxAdd = (CheckBox)findViewById(R.id.colorsCheckBoxAdd);
		
		Button ButtonOk = (Button)findViewById(R.id.buttonToColors);
        ButtonOk.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				mBrush.setPixel(pixel);
				if (callback != null)
					callback.callbackColorSelected();
					
			//	brush.setMode(Utils.Mode.paint);
				dialog.dismiss();
			}
        });
        
        final ArrayList<ColorName> colors = new ArrayList<ColorName>();
        AdapterColorsList boxAdapter; 
        boxAdapter = new AdapterColorsList(context, colors);


		colors.add(new ColorName(context.getResources().getString(R.string.english_red),new PixelFloat(39.68254f,28.571428f, 31.746033f, 0)));
		colors.add(new ColorName(context.getResources().getString(R.string.cadmium_red),new PixelFloat(51.04895f,20.27972f,28.67133f,0)));
		colors.add(new ColorName(context.getResources().getString(R.string.cinnobar),new PixelFloat(59.285717f,10.0f ,30.714287f)));
        colors.add(new ColorName(context.getResources().getString(R.string.madder_red),new PixelFloat(55, 2, 34)));

        colors.add(new ColorName(context.getResources().getString(R.string.naples_body), new PixelFloat(25,19,0,70, true)));
		colors.add(new ColorName(context.getResources().getString(R.string.naples_yellow), new PixelFloat(41.37931f,58.62069f,0,70f)));
		
        colors.add(new ColorName(context.getResources().getString(R.string.cadmium_orange), new PixelFloat(53,70,0)));
        colors.add(new ColorName(context.getResources().getString(R.string.ocher), new PixelFloat(16,23,7)));
        
        colors.add(new ColorName(context.getResources().getString(R.string.cadmium_yellow), new PixelFloat(30,70,0)));
        colors.add(new ColorName(context.getResources().getString(R.string.cadmium_lemon), new PixelFloat(24,79,12)));
        
        colors.add(new ColorName(context.getResources().getString(R.string.olive),new PixelFloat(41, 55, 43)));
        colors.add(new ColorName(context.getResources().getString(R.string.grass_grin),new PixelFloat(27, 47, 32)));
        colors.add(new ColorName(context.getResources().getString(R.string.english_green),new PixelFloat(29, 73, 50)));
		colors.add(new ColorName(context.getResources().getString(R.string.olympic_green),new PixelFloat( 16.860464f, 33.72093f,49.418602f)));

		colors.add(new ColorName(context.getResources().getString(R.string.aquamarine), new PixelFloat(9.746552f,27.700722f,62.55272f,0)));
		colors.add(new ColorName(context.getResources().getString(R.string.cobalt_blue),new PixelFloat(16.058393f,10.948905f, 72.9927f )));
		colors.add(new ColorName(context.getResources().getString(R.string.ultramarine), new PixelFloat(21.428572f, 7.1428576f, 71.42857f, 0)));
		colors.add(new ColorName(context.getResources().getString(R.string.cerulium), new PixelFloat(21.428572f, 7.1428576f, 71.42857f, 0)));
				
        colors.add(new ColorName(context.getResources().getString(R.string.cobalt_violet),new PixelFloat(20, 0, 33)));
		
		colors.add(new ColorName(context.getResources().getString(R.string.raw_sienna),new PixelFloat(38, 55, 26)));
		
        ListView listView = (ListView) findViewById(R.id.listViewColors);
        listView.setAdapter(boxAdapter);
		listView.setOnItemClickListener( new ListView.OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int index, long p4)
				{
					PixelFloat p = colors.get(index).pixelFloat;
					pixel.copy(p);
					fromList = true;	
					
					sbRed.setProgress((int)p.red);
					sbYellow.setProgress((int)p.yellow);
					sbBlue.setProgress((int)p.blue);
					sbWhite.setProgress((int)p.white);
					sbBlack.setProgress((int)(p.darkness*10));
					
				}}
			);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (!fromList){
			pixel.red = sbRed.getProgress();
			pixel.yellow = sbYellow.getProgress();
			pixel.blue = sbBlue.getProgress();
			
			pixel.white = sbWhite.getProgress();
			
			pixel.darkness = (float)sbBlack.getProgress()/10+1;                                                                                                                                                                                                                                                                                             
		}
		
		int color = Utils.ryb2rgb(pixel);
		image.setBackgroundColor(color);
		
		textViewRed.setText(""+(int)pixel.red);
		textViewYellow.setText(""+(int)pixel.yellow);
		textViewBlue.setText(""+(int)pixel.blue);
		textViewWhite.setText(""+(int)pixel.white);
		textViewBlack.setText(""+(int)(pixel.darkness*10));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		fromList = false;
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}	
}
