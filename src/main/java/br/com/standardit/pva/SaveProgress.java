package br.com.standardit.pva;

import java.io.File;

import br.com.standardit.util;
//import java.util.Scanner;

public class SaveProgress {
	public static boolean isFileReady(String filePath) {
		File file = new File(filePath);

		boolean isCopying = true;
		while (true) {
			try {
				// Scanner scanner = new Scanner(file);
				file.setReadOnly();
				isCopying = false;
			} catch (Exception e) {
				util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
				sleepThread();
			}
			if (isCopying == false) {
				break;
			}
		}
		util.log.info("[" + util.getMetod() + "] - copy completed ::");
		return isCopying;
	}

	/**
	 * sleep for 10 seconds
	 */
	private static void sleepThread() {
		util.log.info("[" + util.getMetod() + "] - sleeping for 10 seconds");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		}
	}

}
