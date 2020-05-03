package br.com.standardit;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

public class UtilAutomation {

	Screen s = new Screen();

	public UtilAutomation(Screen s) {
		this.s = s;
	}

	public WaitResult WaitFor(int timeout, String[] images) {
		if (timeout == 0)
			timeout = 3600;
		Date inicio = Calendar.getInstance().getTime();
		Date fim = Calendar.getInstance().getTime();
		WaitResult retorno = new WaitResult();
		do {

			fim = Calendar.getInstance().getTime();
			long seconds = (fim.getTime() - inicio.getTime()) / 1000;
			if (seconds > timeout) {
				util.log.warn("[" + util.getMetod() + "] - Time out.");
				break;
			}
			for (String image : images) {
				util.log.info("[" + util.getMetod() + "] - Search Image: " + image);
				try {
					retorno.region = s.wait(image);
				} catch (FindFailed e) {
					util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
					retorno.region = null;
				}
				if (retorno.region != null) {
					retorno.imageFound = image;
					break;
				}
			}

		} while (retorno.region == null);
		return retorno;
	}

	/*
	 * public boolean Click(String location) { WaitResult result = WaitFor(3, new
	 * String[] { location }); if (result.region != null) { result.region.click();
	 * return true; } else return false; }
	 */
	public boolean Click(String image, int timeout) {
		WaitResult waitResult = WaitFor(timeout, new String[] { image });
		if (waitResult.region == null)
			return false;
		util.log.info("[" + util.getMetod() + "] - Click in Image: " + image);
		waitResult.region.click();
		return true;
	}

	public boolean Click(String image) {
		return Click(image, 3);
	}

	public void WaitFinish(String image) {
		try {
			Region aguarde = null;
			do {
				aguarde = s.wait(image);
				util.sleep(2);
			} while (aguarde != null);
		} catch (FindFailed e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
		}
	}

	public boolean Existe(String image) {
		return Existe(image, 10);
	}

	public boolean Existe(String image, int timeout) {
		WaitResult waitResult = WaitFor(timeout, new String[] { image });
		if (waitResult.region == null)
			return false;
		return true;
	}

	public boolean DoubleClick(String image, int timeout) {
		WaitResult waitResult = WaitFor(timeout, new String[] { image });
		if (waitResult.region == null)
			return false;

		util.log.info("[" + util.getMetod() + "] - DoubleClick in Image: " + image);
		waitResult.region.doubleClick();
		return true;
	}

	public boolean DoubleClick(String image) {
		return DoubleClick(image, 3);
	}
	public static void sleep(int seconds) {
		try {
			TimeUnit.SECONDS.sleep(seconds);
		} catch (InterruptedException e) {
		}
	}
}
