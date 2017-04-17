package com.alexmochalov.dialogs;

import android.app.*;
import android.content.*;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.*;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

import com.alexmochalov.colors.Utils;
import com.alexmochalov.dialogs.DialogNew.MyCallback;
import com.example.draw.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DialogSelectImage extends Dialog
  {
	private Context context;
	private Dialog dialog;
	private LinearLayout myGallery;
	private String fileName = "";
	private int width;
	private int height;
	
	public MyCallback callback = null;

	private int THUMBSIZE = 200;
	
	public DialogSelectImage(Context context, int width, int height) {
		super(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen );
		this.context = context;
		dialog = this;
		this.width = width;
		this.height = height;
	}

	public interface MyCallback {
		void callbackACTION_OPEN(String name); 
		void callbackACTION_NEW(int w, int h, String name); 
	} 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
        //         WindowManager.LayoutParams.MATCH_PARENT);

		setContentView(R.layout.dialig_select_image);

		File dir = new File(Utils.APP_FOLDER);
		if(!dir.exists()){                          
			dir.mkdirs();                  
		}
		
		final ArrayList<File> listFiles = new ArrayList<File>();
        File[] files = dir.listFiles();
        if (files != null )
            for (int i=0; i<files.length; i++){
            	boolean addFile = false;
            	if (!files[i].isDirectory() &&
					files[i].getName().endsWith(".png")
					&& !files[i].getName().equals("screen.png"))
            			listFiles.add(files[i]);
        	}	
		
        AdapterImagesList boxAdapter; 
        boxAdapter = new AdapterImagesList(context, listFiles);
        ListView listView = (ListView) findViewById(R.id.dialigselectimageListView);
        listView.setAdapter(boxAdapter);
		listView.setOnItemClickListener( new ListView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> p1, View p2, int index, long p4)
			{
				fileName = listFiles.get(index).getAbsolutePath();
				loadImage(listFiles.get(index));
			}}
		);
		
		Button ButtonOk = (Button)findViewById(R.id.dialogselectimageOpen);
        ButtonOk.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				if (!fileName.equals("")){
					callback.callbackACTION_OPEN(fileName);
					dialog.dismiss();
				}	
			}
        });
		
		Button ButtonNew = (Button)findViewById(R.id.dialogselectimageNew);
		ButtonNew.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				 DialogNew dialogNew = new DialogNew(context, 
						 	width, 
							height);
				 dialogNew.callback = new DialogNew.MyCallback(){
					 @Override
					 public void callbackACTION_OK(int w, int h, String fileName)
					 {
						 if (!fileName.endsWith(".png"))
							 fileName = fileName + ".png";
						 callback.callbackACTION_NEW(w, h, fileName);
						 dialog.dismiss();
					 }};
					 dialogNew.show();
			}
        });
		
		
	}
	
	private void loadImage(File file){
	    BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    
	    BitmapFactory.decodeFile(file.getAbsolutePath(), options);
	    
	    options.inSampleSize = calculateInSampleSize(options, THUMBSIZE, THUMBSIZE);
	     
	     // Decode bitmap with inSampleSize set
	     options.inJustDecodeBounds = false;
	     Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath(), options); 		    
	     ((ImageView)findViewById(R.id.imageViewFileName)).setImageBitmap(bmp);
	}
	
	private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	     // Raw height and width of image
	     final int height = options.outHeight;
	     final int width = options.outWidth;
	     int inSampleSize = 1;
	        
	     if (height > reqHeight || width > reqWidth) {
	    	 if (width > height) {
	    		 inSampleSize = Math.round((float)height / (float)reqHeight);   
	    	 } else {
	    		 inSampleSize = Math.round((float)width / (float)reqWidth);   
	    	 }   
	     }
	     
	     return inSampleSize;   
	}		
	
	public class AdapterImagesList extends BaseAdapter
	{
		LayoutInflater lInflater;
		ArrayList<File> objects;
		
		public AdapterImagesList(Context context,ArrayList<File> obj){
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
				view = lInflater.inflate(R.layout.item_image, parent, false);
			}

			File file = (File) getItem(position);
			
		    BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inJustDecodeBounds = true;
		    BitmapFactory.decodeFile(file.getAbsolutePath(), options);
			
			((TextView) view.findViewById(R.id.textViewFileName)).setText(file.getName());
			((TextView) view.findViewById(R.id.textViewFileSize)).setText("("+options.outWidth+"x"+options.outHeight+")");
			((TextView) view.findViewById(R.id.textViewFileLastModified)).setText(
					new SimpleDateFormat("MM/dd/yyyy (hh:mm:ss)").format(new Date(file.lastModified())));
			
			return view;	
		}
	}
}
