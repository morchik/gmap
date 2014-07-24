package ru.vmordo.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.commons.net.ftp.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class upLoader {
	// Now, declare a public FTP client object.

	private static final String TAG = "MyFTPClient";
	public FTPClient mFTPClient = null;

	public static String u_host = "earflap.chat.ru";
	public static String u_user = "earflap";
	public static String u_pswr = "1accb9degh";
	public static String u_dir = "/a";

	String u_srcFilePath;
	Context u_context;
	List<String> u_list;

	// Method to connect to FTP server:
	public boolean ftpConnect(String host, String username, String password,
			int port) {
		try {
			mFTPClient = new FTPClient();
			// connecting to the host
			mFTPClient.connect(host, port);

			// now check the reply code, if positive mean connection success
			if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
				// login using username & password
				boolean status = mFTPClient.login(username, password);

				/*
				 * Set File Transfer Mode
				 * 
				 * To avoid corruption issue you must specified a correct
				 * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
				 * EBCDIC_FILE_TYPE .etc. Here, I use BINARY_FILE_TYPE for
				 * transferring text, image, and compressed files.
				 */
				mFTPClient.setFileType(FTP.ASCII_FILE_TYPE);
				mFTPClient.enterLocalPassiveMode();

				return status;
			}
		} catch (Exception e) {
			Log.d(TAG, "Error: could not connect to host " + host);
		}

		return false;
	}

	// Method to disconnect from FTP server:

	public boolean ftpDisconnect() {
		try {
			mFTPClient.logout();
			mFTPClient.disconnect();
			return true;
		} catch (Exception e) {
			Log.d(TAG, "Error occurred while disconnecting from ftp server.");
		}

		return false;
	}

	// Method to get current working directory:

	public String ftpGetCurrentWorkingDirectory() {
		try {
			String workingDir = mFTPClient.printWorkingDirectory();
			return workingDir;
		} catch (Exception e) {
			Log.d(TAG, "Error: could not get current working directory.");
		}

		return null;
	}

	// Method to change working directory:
	public boolean ftpChangeDirectory(String directory_path) {
		try {
			return mFTPClient.changeWorkingDirectory(directory_path);
		} catch (Exception e) {
			Log.d(TAG, "Error: could not change directory to " + directory_path);
		}
		return false;
	}

	// Method to list all files in a directory:

	public void ftpPrintFilesList(String dir_path) {
		try {
			FTPFile[] ftpFiles = mFTPClient.listFiles(dir_path);
			int length = ftpFiles.length;

			for (int i = 0; i < length; i++) {
				String name = ftpFiles[i].getName();
				boolean isFile = ftpFiles[i].isFile();

				if (isFile) {
					Log.d(TAG, "File : " + name);
				} else {
					Log.d(TAG, "Directory : " + name);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Method to create new directory:

	public boolean ftpMakeDirectory(String new_dir_path) {
		try {
			boolean status = mFTPClient.makeDirectory(new_dir_path);
			return status;
		} catch (Exception e) {
			Log.d(TAG, "Error: could not create new directory named "
					+ new_dir_path);
		}

		return false;
	}

	// Method to delete/remove a directory:

	public boolean ftpRemoveDirectory(String dir_path) {
		try {
			boolean status = mFTPClient.removeDirectory(dir_path);
			return status;
		} catch (Exception e) {
			Log.d(TAG, "Error: could not remove directory named " + dir_path);
		}

		return false;
	}

	// Method to delete a file:

	public boolean ftpRemoveFile(String filePath) {
		try {
			boolean status = mFTPClient.deleteFile(filePath);
			return status;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	// Method to rename a file:

	public boolean ftpRenameFile(String from, String to) {
		try {
			boolean status = mFTPClient.rename(from, to);
			return status;
		} catch (Exception e) {
			Log.d(TAG, "Could not rename file: " + from + " to: " + to);
		}

		return false;
	}

	// Method to download a file from FTP server:

	/**
	 * mFTPClient: FTP client connection object (see FTP connection example)
	 * srcFilePath: path to the source file in FTP server desFilePath: path to
	 * the destination file to be saved in sdcard
	 */
	public boolean ftpDownload(String srcFilePath, String desFilePath) {
		boolean status = false;
		try {
			FileOutputStream desFileStream = new FileOutputStream(desFilePath);
			;
			status = mFTPClient.retrieveFile(srcFilePath, desFileStream);
			desFileStream.close();

			return status;
		} catch (Exception e) {
			Log.d(TAG, "download failed");
		}

		return status;
	}

	// Method to upload a file to FTP server:

	/**
	 * mFTPClient: FTP client connection object (see FTP connection example)
	 * srcFilePath: source file path in sdcard desFileName: file name to be
	 * stored in FTP server desDirectory: directory path where the file should
	 * be upload to
	 */
	public boolean ftpUpload(String srcFilePath, String desFileName,
			String desDirectory, Context context) {
		boolean status = false;
		Log.v(TAG, desFileName + " " + desDirectory);
		try {
			FileInputStream srcFileStream = new FileInputStream(srcFilePath);
			// change working directory to the destination directory
			if (ftpChangeDirectory(desDirectory)) {
				status = mFTPClient.storeFile(desFileName, srcFileStream);
			} else {
				Log.v(TAG, "try to ftpMakeDirectory " + desDirectory);
				if (ftpMakeDirs(desDirectory+"/"))
					Log.v(TAG, "ftpMakeDirectory OK");
				else
					Log.v(TAG, "ftpMakeDirectory NO");
				ftpChangeDirectory(desDirectory);
				status = mFTPClient.storeFile(desFileName, srcFileStream);
			}
			srcFileStream.close();
			return status;
		} catch (Exception e) {
			Log.d(TAG, "upload failed: " + e);
		}
		return status;
	}

	// Объект Runnable, который запускает метод для выполнения задач
	// в фоновом режиме.
	private Runnable doBackgroundThreadProcessing = new Runnable() {
		public void run() {
			backgroundUpload(u_srcFilePath, u_context); // !!!not open for each
														// file???
		}
	};

	// Объект Runnable, который запускает метод для выполнения задач
	private Runnable doListThreadProcessing = new Runnable() {
		public void run() {
			listUpload(u_list, u_context);
		}
	};

	public boolean ftpMakeDirs(String new_dir_path) {
		boolean status = true;
		ftpChangeDirectory("/");
		int pos = new_dir_path.indexOf('/', 2);
		//Log.v(TAG, "pos "+pos);
		while (pos > 0){
			String cur_d = new_dir_path.substring(0, pos);
			Log.v(TAG, pos+" "+cur_d);
			if (!ftpChangeDirectory(cur_d)){
				if (ftpMakeDirectory(cur_d))
					Log.v(TAG, pos+" ftpMakeDirectory ok "+cur_d);
				else
					Log.v(TAG, pos+" ftpMakeDirectory no "+cur_d);
			}
			if (!ftpChangeDirectory(cur_d)){
				Log.d(TAG, pos+" No sucs for "+cur_d);
				return false;
			}
			pos = new_dir_path.indexOf('/', pos + 1);
			//Log.v(TAG, "pos "+pos);
		}
		return status;
	}

	public void oneTaskUpLoad(String srcFilePath, Context context) {
		Log.d(TAG, " someTask " + srcFilePath);
		u_srcFilePath = srcFilePath;
		u_context = context;
		// Здесь трудоемкие задачи переносятся в дочерний поток.
		Thread thread = new Thread(null, doBackgroundThreadProcessing,
				"Background");
		thread.start();
	}

	public void listTaskUpLoad(List<String> list, Context context) {
		Log.d(TAG, " listTaskUpLoad size: " + list.size());
		u_list = list;
		u_context = context;
		// Здесь трудоемкие задачи переносятся в дочерний поток.
		Thread thread = new Thread(null, doListThreadProcessing,
				"BackgroundLST");
		thread.start();
	}

	public void listUpload(List<String> list, Context context) {
		mFTPClient = new FTPClient();
		try {
			if (context != null) {
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(context);
				u_host = prefs.getString("ftp_address", "earflap.chat.ru");
			}
			mFTPClient.connect(u_host, 21);
			mFTPClient.login(u_user, u_pswr);
		} catch (Exception e) {
			Log.e(TAG, "upload ftp connect failed: " + e);
			return;
		}
		// now check the reply code, if positive mean connection success
		if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
			for (int i = 0; i < list.size(); i++)
				try {
					String srcFilePath = list.get(i);
					Log.v(TAG, i + " Upload for    " + srcFilePath);
					Log.v(TAG, " isPositiveCompletion   ");
					mFTPClient.setFileType(FTP.ASCII_FILE_TYPE);
					mFTPClient.enterLocalPassiveMode();
					boolean isFile = ftpUpload(srcFilePath, (new java.io.File(
							srcFilePath)).getName(), u_dir
							+ (new java.io.File(srcFilePath)).getParent(),
							context);
					if (isFile) {
						Log.v(TAG, " File uploaded ... ");
						//(new Db_Helper(context)).update_status(srcFilePath);
					} else {
						Log.v(TAG, " File not uploaded!!! ");
					}
					Log.v(TAG, " ftpUpload   ");

				} catch (Exception e) {
					Log.e(TAG, "upload ftp failed: " + e);
				}
			try {
				mFTPClient.logout();
				mFTPClient.disconnect();
			} catch (Exception e) {
				Log.e(TAG, "upload ftp disconnect failed: " + e);
			}
		}
	}

	public boolean backgroundUpload(String srcFilePath, Context context) {
		mFTPClient = new FTPClient();
		// connecting to the host
		boolean status = false;
		try {
			Log.v(TAG, " backgroundUpload   " + srcFilePath);
			mFTPClient.connect(u_host, 21);
			status = mFTPClient.login(u_user, u_pswr);
			// now check the reply code, if positive mean connection success
			if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
				/*
				 * Set File Transfer Mode
				 * 
				 * To avoid corruption issue you must specified a correct
				 * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
				 * EBCDIC_FILE_TYPE .etc. Here, I use BINARY_FILE_TYPE for
				 * transferring text, image, and compressed files.
				 */
				Log.v(TAG, " isPositiveCompletion   ");
				mFTPClient.setFileType(FTP.ASCII_FILE_TYPE);
				mFTPClient.enterLocalPassiveMode();
				boolean isFile = ftpUpload(srcFilePath, (new java.io.File(
						srcFilePath)).getName(), u_dir
						+ (new java.io.File(srcFilePath)).getParent(), context);
				if (isFile) {
					Log.v(TAG, " File uploaded ... ");
					//(new Db_Helper(context)).update_status(srcFilePath);
				} else {
					Log.v(TAG, " File not uploaded!!! ");
				}
				Log.v(TAG, " ftpUpload   ");
				mFTPClient.logout();
				mFTPClient.disconnect();
			}
		} catch (Exception e) {
			Log.e(TAG, "upload ftp failed: " + e);
		}
		return status;
	}
}
