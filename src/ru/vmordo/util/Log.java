package ru.vmordo.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import android.content.Context;
import android.widget.EditText;

public class Log {
	static public Context context = null;
	static public EditText lView = null;

	static String fstr(int inum) {
		String cRes = inum + "";
		if (inum <= 9) {
			cRes = "0" + inum;
		}
		return cRes;
	}

	public static String getDay() {
		final Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		String mMonth = fstr(c.get(Calendar.MONTH) + 1);
		String mDay = fstr(c.get(Calendar.DAY_OF_MONTH));
		return mYear + "_" + mMonth + "_" + mDay;
	}

	public static String getMSec() {
		final Calendar c = Calendar.getInstance();
		String mHour = fstr(c.get(Calendar.HOUR_OF_DAY));
		String mMinute = fstr(c.get(Calendar.MINUTE));
		String mSec = fstr(c.get(Calendar.SECOND));
		String mMis = fstr(c.get(Calendar.MILLISECOND));
		return mHour + ":" + mMinute + ":" + mSec + "." + mMis + " ";
	}

	public static String getDir() {
		File sdDir;
		sdDir = new File("/storage/external_SD/DCIM");
		if (!sdDir.isDirectory()) {
			String sdState = android.os.Environment.getExternalStorageState();
			if (sdState.equals(android.os.Environment.MEDIA_MOUNTED))
				sdDir = android.os.Environment.getExternalStorageDirectory();
			else
				sdDir = android.os.Environment.getDataDirectory();
		}
		return sdDir.getPath();
	}

	static public void wrLogView(String str) {
		try {
			if (lView != null) {
				lView.append("\n" + getMSec() + str);
			}
		} catch (Exception e) {
			e.printStackTrace();
			android.util.Log.e("LLG", e.getMessage());
		}
	}

	static public void wrLogFile(String str) {
		String dir = getDir();
		try {
			if (context != null) {
				FileOutputStream os = new FileOutputStream(dir + "/log_"
						+ getDay() + ".txt", true);
				OutputStreamWriter oos = new OutputStreamWriter(os);
				oos.append(getMSec()+str);
				oos.flush();
				oos.close();
				os.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			android.util.Log.e("LLG", e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			android.util.Log.e("LLG", e.getMessage());
		}
	}

	public static int v(String tag, String msg) {
		wrLogFile("V " + tag + " " + msg);
		wrLogView("V " + tag + " " + msg);
		return android.util.Log.v(tag, msg);
	}

	public static int d(String tag, String msg) {
		wrLogFile("D " + tag + " " + msg);
		wrLogView("D " + tag + " " + msg);
		return android.util.Log.d(tag, msg);
	}

	public static int i(String tag, String msg) {
		wrLogFile("I " + tag + " " + msg);
		wrLogView("I " + tag + " " + msg);
		return android.util.Log.i(tag, msg);
	}

	public static int w(String tag, String msg) {
		wrLogFile("W " + tag + " " + msg);
		wrLogView("W " + tag + " " + msg);
		return android.util.Log.w(tag, msg);
	}

	public static int e(String tag, String msg) {
		wrLogFile("E " + tag + " " + msg);
		wrLogView("E " + tag + " " + msg);
		return android.util.Log.e(tag, msg);
	}

}
