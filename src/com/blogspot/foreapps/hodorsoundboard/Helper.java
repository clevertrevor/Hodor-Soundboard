package com.blogspot.foreapps.hodorsoundboard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.res.AssetManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class Helper {

	private static String LOG = "com.blogspot.foreapps.hodorsoundboard.helper";
	private File k = null; // global var to use content values from copyFile and
							// saving
	private String[] files = null;
	private int selectedSoundIndex = -1;

	/**
	 * Creates a list of sound file names from the assets to display in the
	 * ListView
	 * 
	 * @return a String array from the asset names
	 */
	public static String[] createSoundNameList() {

		AssetManager assetManager = MainActivity.getContext().getResources()
				.getAssets();
		String assetNames[] = null;

		// get list of asset names
		try {
			assetNames = assetManager.list("");
		} catch (Exception e) {
			Log.e(LOG, "Error listing files: " + e.toString());
		}

		// Extra two variables b/c arrays are different sizes
		// remove extra data from assetNames
		String[] temp = removeExtraData(assetNames);
		// Convert sound names to look nice
		String[] result = makeSoundNamesNice(temp);
		

		return result;
	}

	/**
	 * Removes the extras included from AssetManager aka 'images', 'sounds'
	 * 'webkit'
	 * 
	 * @param assetNames
	 *            input string array
	 * @return result aka assetNames without extras
	 */
	private static String[] removeExtraData(String[] assetNames) {
		String[] result = new String[assetNames.length - 3];
		int count = 0;

		for (int i = 0; i < assetNames.length; i++) {
			String temp = assetNames[i];
			if (!temp.equals("images")) {
				if (!temp.equals("sounds")) {
					if (!temp.equals("webkit")) {
						result[count] = temp; // TODO not setting the last index
						count++;
					}
				}
			}
		}

		return result;
	}

	/**
	 * Converts the names to look better by removing .mp3, uppercase Hodor,
	 * replace '_' by ' '
	 * 
	 * @param names
	 * @return
	 */
	private static String[] makeSoundNamesNice(String[] names) {

		for (int i = 0; i < names.length; i++) {
			String current = names[i];
			current = current.replace('_', ' '); // replace '_' with ' '
			current = current.replaceAll("hodor", "Hodor"); // uppercase Hodor
			current = current.substring(0, current.length() - 4); // remove .mp3
			names[i] = current;
		}

		return names;
	}

	// Saves the sound to a ringtone
	public void saveRingtone() {

		// if file was successfully copied, then enter it into ringtone database
		if (copyFile()) {
			ContentValues values = new ContentValues();
			values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());
			values.put(MediaStore.MediaColumns.TITLE, files[selectedSoundIndex]);
			values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
			values.put(MediaStore.Audio.Media.ARTIST, "Hodor Soundboard");
			values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
			values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
			values.put(MediaStore.Audio.Media.IS_ALARM, false);
			values.put(MediaStore.Audio.Media.IS_MUSIC, false);

			// Insert it into the database
			Uri uri = MediaStore.Audio.Media.getContentUriForPath(k
					.getAbsolutePath());
			Uri newUri = MainActivity.getContext().getContentResolver()
					.insert(uri, values);

			RingtoneManager.setActualDefaultRingtoneUri(
					MainActivity.getContext(), RingtoneManager.TYPE_RINGTONE,
					newUri);
		}

	}

	// Saves the sound to a Notification
	public void saveNotification() {

		// if file was successfully copied, then enter it into notification
		// database
		if (copyFile()) {
			ContentValues values = new ContentValues();
			values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());
			values.put(MediaStore.MediaColumns.TITLE, files[selectedSoundIndex]);
			values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
			values.put(MediaStore.Audio.Media.ARTIST, "Hodor Soundboard");
			values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
			values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
			values.put(MediaStore.Audio.Media.IS_ALARM, false);
			values.put(MediaStore.Audio.Media.IS_MUSIC, false);

			// Insert it into the database
			Uri uri = MediaStore.Audio.Media.getContentUriForPath(k
					.getAbsolutePath());
			Uri newUri = MainActivity.getContext().getContentResolver()
					.insert(uri, values);

			RingtoneManager.setActualDefaultRingtoneUri(
					MainActivity.getContext(),
					RingtoneManager.TYPE_NOTIFICATION, newUri);
		}

	}

	// Save sound to SD Card
	public void saveSdCard() {

		// if file was successfully copied, then post path to file
		if (copyFile()) {
			Toast.makeText(
					MainActivity.getContext(),
					"Saved to : "
							+ Environment.getExternalStorageDirectory()
									.getAbsolutePath() + "/Hodor Soundboard/",
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Copies the sound file to the SD card
	 * 
	 * @return true if successfully copied
	 */
	private boolean copyFile() {
		AssetManager assetManager = MainActivity.getContext().getResources()
				.getAssets();

		// get list of sounds
		try {
			files = assetManager.list("");
		} catch (Exception e) {
			Log.e(LOG, "Error listing files: " + e.toString());
		}

		// try to copy selected sound to sd card
		InputStream in = null;
		OutputStream out = null;
		try {
			selectedSoundIndex = getSoundIndex();
			in = assetManager.open(files[selectedSoundIndex]);
			// create directory for sounds
			File file = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/Hodor Soundboard/");
			// create directory if it does not exist
			file.mkdirs();
			// open place to write
			out = new FileOutputStream(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/Hodor Soundboard/" + files[selectedSoundIndex]);
			// set global var k for adding sound to RingtoneManager
			k = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/Hodor Soundboard/"
					+ files[selectedSoundIndex]);

			byte[] buffer = new byte[65536 * 2];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
			Log.d(LOG, "Sound File Copied in SD Card");
			return true;
		} catch (Exception e) {
			Log.e(LOG, "ERROR: " + e.toString());
			return false;
		}
	}

	/**
	 * Searches for the index of the sound from the assets directory
	 * 
	 * @return index the index of the sound in assets
	 */
	private int getSoundIndex() {
		int index = -1;

		String[] files = null;
		AssetManager assetManager = MainActivity.getContext().getResources()
				.getAssets();

		// Get list of files
		try {
			files = assetManager.list(""); // ringtone is folder name
		} catch (Exception e) {
			Log.e(LOG, "Error listing files: " + e.toString());
		}

		// Search for index
		for (int i = 0; i < files.length; i++) {
			if (files[i].equals(MainActivity.lastPlayed)) {
				return i;
			}
		}

		// Error - code should never hit here
		Log.d(LOG, "Error: did not find selected sound in list.");
		return index;
	}
}
