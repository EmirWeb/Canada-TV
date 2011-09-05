package canada.tv.utilities;

import android.util.Log;

public class Logger {

	private boolean isDebuggable = true;
	private final String name = Statics.s().APPLICATION_NAME;

	private static Logger instance;

	private Logger() {
	}

	private static Logger getInstance() {
		if (instance == null)
			instance = new Logger();
		return instance;
	}

	/**
	 * prints to debug
	 * 
	 * @param className
	 * @param message
	 */
	public static void debug(Class className, String message) {
		if (isDebuggable())
			Log.d(getInstance().name, className.toString() + " : " + message);
	}

	/**
	 * Prints To error
	 * 
	 * @param className
	 * @param message
	 */
	public static void error(Class className, String message) {
		Log.e(getInstance().name, className.toString() + " : " + message);
	}

	/**
	 * @param isDebuggable
	 *            the isDebuggable to set
	 */
	public static void setDebuggable(boolean isDebuggable) {
		getInstance().isDebuggable = isDebuggable;
	}

	/**
	 * @return the isDebuggable
	 */
	public static boolean isDebuggable() {
		return getInstance().isDebuggable;
	}
}
