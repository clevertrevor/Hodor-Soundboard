package com.blogspot.foreapps.hodorsoundboard;

import java.io.IOException;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {

	private static Context context;
	private ArrayList<Song> songList = new ArrayList<Song>();
	private static MediaPlayer mediaPlayer = new MediaPlayer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Initialize variables
		createList();

		// Create temp, which holds the names of songs for now
		String[] temp = new String[songList.size()];
		for (int i = 0; i < temp.length; i++) {
			temp[i] = songList.get(i).getName();
		}

		// Binding resources Array to ListAdapter
		this.setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item,
				R.id.label, temp));

		ListView lv = getListView();
		// listening to single list item on click
		lv.setOnItemClickListener(listOnClickListener());

		MainActivity.context = getApplicationContext();

	} // end of onCreate()

	/**
	 * Listener to play each item in the ListView
	 * 
	 * @return OnItemClickListener for each item in the list
	 */
	private OnItemClickListener listOnClickListener() {

		return new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				// get name of song from assets folder
				AssetFileDescriptor afd = null;
				try {
					afd = getAssets().openFd(songList.get(position).getName());
				} catch (IOException e) {
					e.printStackTrace();
				}
				// turn off MediaPlayer if playing
				if (mediaPlayer != null) {
					mediaPlayer.reset();
				}
				// try to play media
				try {
					mediaPlayer.setDataSource(afd.getFileDescriptor(),
							afd.getStartOffset(), afd.getLength());
					mediaPlayer.prepare();
					mediaPlayer.start();
				} catch (Exception e) {
					Log.e("ERROR", "Unable to play song");
				}
			}
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Creates the songList by getting the information from the Assets folder
	 */
	private void createList() {
		AssetManager assetManager = getAssets();

		// Get files from Assets folder
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Initialize songList from files
		for (int i = 0; i < files.length - 3; i++) {
			songList.add(new Song(files[i], "/assets"));
		}

	}

	public static Context getContext() {
		return MainActivity.context;
	}

}
