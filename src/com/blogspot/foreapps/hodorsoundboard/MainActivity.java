package com.blogspot.foreapps.hodorsoundboard;

import java.io.IOException;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity {

	// TODO change .mp3 files to be more descriptive
	// TODO add latest hodor sounds + amplify them

	// AdMob
	private AdView adView;
	private static final String AD_UNIT_ID = "ca-app-pub-0843597389735051/9793657960";

	// list of song names with .mp3
	private ArrayList<Song> songList = new ArrayList<Song>();
	// list of song names that are shown eg. 'user-friendly'
	private String[] songNameList = { "Hodor?", "Hodor agreeing",
			"Hodor is worried", "Hodor!!!", "Hodor!", "Hodor is angry",
			"Hodor, Hodor, Hodor!", "Hodorrr", "Hodorrrrrrr", "Don't Hodor...",
			"Hodor", "Hodor..", "Hodor agrees for the first time",
			"Hodor agrees for the second time",
			"Hodor agrees for the third time",
			"Hodor agrees for the fourth time", "Hodor is confused", "Hodor" };
	// for playing the sounds
	private static MediaPlayer mediaPlayer = new MediaPlayer();
	// for other classes to reference the context
	private static Context context;
	// For setting/saving sounds
	public static String lastPlayed = "";
	Helper helper = new Helper();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		runAdMob();

		// Initialize list of song names from assets folder
		createList();

		// Create temp, which holds the names of songs for now
		String[] temp = new String[songList.size()];
		for (int i = 0; i < temp.length; i++) {
			temp[i] = songList.get(i).getName();
		}

		// Setup ListView
		ListView listview = (ListView) findViewById(R.id.list);
		// Binding resources Array to ListAdapter
		listview.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item,
				R.id.label, songNameList));
		// set Listener for list clicks to play sounds
		listview.setOnItemClickListener(listOnClickListener());

		MainActivity.context = getApplicationContext();
		
		org.codechimp.apprater.AppRater.app_launched(this); // AppRater

	} // end of onCreate()

	
	/**
	 * Creates and runs the AdMob banner at the bottom of the screen
	 */
	private void runAdMob() {
		// Create an ad.
		adView = new AdView(this);
		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId(AD_UNIT_ID);

		// Add the AdView to the view hierarchy. The view will have no size
		// until the ad is loaded.
		LinearLayout layout = (LinearLayout) findViewById(R.id.linear_layout);
		layout.addView(adView);

		// Create an ad request. Check logcat output for the hashed device ID to
		// get test ads on a physical device.
		AdRequest adRequest = new AdRequest.Builder().build();

		// Start loading the ad in the background.
		adView.loadAd(adRequest);
	}

	
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
					lastPlayed = songList.get(position).getName();
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
			songList.add(new Song(files[i]));
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.save:
			// Open dialog only if a song has been played
			if (lastPlayed != "") {
				openSaveDialog();
			} else { // error message
				Toast.makeText(this, "Please play a sound first",
						Toast.LENGTH_LONG).show();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Opens an AlertDialog for the user to select to save/set as
	 * ringtone/notification or save to SD card
	 */
	private void openSaveDialog() {

		// Build AlertDialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Save " + lastPlayed + " to: ");
		AlertDialog dialog;
		builder.setSingleChoiceItems(
				getResources().getStringArray(R.array.save_options), -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {

						switch (item) {
						case 0: // Ringtone
							helper.saveRingtone();
							break;
						case 1: // Notification
							helper.saveNotification();
							break;
						case 2: // SD Card
							helper.saveSdCard();
							break;

						}
						dialog.dismiss();
					}
				});

		// build, then show dialog
		dialog = builder.create();
		dialog.show();

	}

	/**
	 * Returns the context of the app
	 * 
	 * @return context the context of the app
	 */
	public static Context getContext() {
		return MainActivity.context;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (adView != null) {
			adView.resume();
		}
	}

	@Override
	public void onPause() {
		if (adView != null) {
			adView.pause();
		}
		super.onPause();
	}

	@Override
	public void onDestroy() {
		// Destroy the AdView.
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}

}
