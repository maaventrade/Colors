package com.alexmochalov.dialogs;

import java.util.ArrayList;
import java.util.List;

import com.example.draw.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.util.*;
import android.webkit.*;

public class DialogInfo extends Dialog  {
	private Context context;
	private Dialog dialog;
	
	public DialogInfo(Context context) {
		super(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		this.context = context;
		dialog = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
        //        WindowManager.LayoutParams.MATCH_PARENT);

	
        
		setContentView(R.layout.page_about);
		WebView webView = (WebView)findViewById(R.id.textViewInfo);
        webView.loadUrl("file:///android_asset/info.html");
		
        Button ButtonMail = (Button)findViewById(R.id.imageButtonMail);
        ButtonMail.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
			            "mailto","maaventrade@gmail.com", null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "RybLite Application");
				context.startActivity(Intent.createChooser(emailIntent, "Send Email..."));				
			}
        });
        
        Button ButtonOk = (Button)findViewById(R.id.ButtonOk);
        ButtonOk.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
        });
        
       
	}	
}
