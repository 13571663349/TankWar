package com.tankwar;

import android.os.Environment;
import java.io.*;
import java.util.Date;


final public class GameLog
{
	public static String LOG_FILE_NAME = "game.log";
	public static String LOG_FILE_PATH = Environment.getExternalStorageDirectory().getPath()
											+ App.DS + LOG_FILE_NAME;

	public final static void e(Throwable e) {
		try {
			FileWriter fw = new FileWriter(LOG_FILE_PATH, true);
			File f = new File(LOG_FILE_PATH);
			String date = new Date().toString();
			String eMsg = null;

			if (f.length() > 1024 * 100) {
				f.delete();
				f.createNewFile();
			}

			eMsg = "Messages: " + e.getMessage() + "\nException: " + e.getClass() + "\n";
			for (StackTraceElement st : e.getStackTrace()) {
				eMsg += "\tFile: " + st.getFileName() + "\n\t\t" + "Class\t: " + st.getClassName() + "\n\t\t" +
						"Line\t: " + st.getLineNumber() + "\n\t\t" + "Method\t: " + st.getMethodName() + "\n";
			}

			eMsg = date + "\n" + eMsg + "\n";
			fw.append(eMsg);
			fw.flush();
			fw.close();
		} catch (IOException err) {
			err.printStackTrace();
		}
	}


	public final static void s(String s) {
		try {
			FileWriter fw = new FileWriter(LOG_FILE_PATH, true);
			File f = new File(LOG_FILE_PATH);
			String date = new Date().toLocaleString();

			if (f.length() > 1024 * 100) {
				f.delete();
				f.createNewFile();
			}

			fw.append(date + " Msg: " + s + "\n");
			fw.flush();
			fw.close();
		} catch (IOException err) {
			err.printStackTrace();
		}
	}
}


