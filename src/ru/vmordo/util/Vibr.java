package ru.vmordo.util;

import android.content.Context;
import android.os.Vibrator;

public class Vibr {
	
	public static void vibrate(Context context, long[] pattern) {
		// Get instance of Vibrator from current Context
		Vibrator v = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		// Only perform this pattern one time (-1 means "do not repeat")
		v.vibrate(pattern, -1);
	}

	public static void ring(Context context) {
		// Get instance of Vibrator from current Context
		Vibrator v = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);

		// This example will cause the phone to vibrate "SOS" in Morse Code
		// In Morse Code, "s" = "dot-dot-dot", "o" = "dash-dash-dash"
		// There are pauses to separate dots/dashes, letters, and words
		// The following numbers represent millisecond lengths
		int dot = 200; // Length of a Morse Code "dot" in milliseconds
		int dash = 500; // Length of a Morse Code "dash" in milliseconds
		int short_gap = 200; // Length of Gap Between dots/dashes
		int medium_gap = 500; // Length of Gap Between Letters
		int long_gap = 1000; // Length of Gap Between Words
		long[] pattern = { 0, // Start immediately
				dot, short_gap, dot, short_gap, dot, // s
				medium_gap, dash, short_gap, dash, short_gap, dash, // o
				medium_gap, dot, short_gap, dot, short_gap, dot, // s
				long_gap };

		// Only perform this pattern one time (-1 means "do not repeat")
		v.vibrate(pattern, -1);
	}
}
