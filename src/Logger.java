import java.util.ArrayList;
import java.util.List;


public class Logger {
	/** listener for interesecting incoming call to println */
	public static interface OnLogListener {
		public void onPrintlnCall(String line);
	}

	private static ArrayList<OnLogListener> onPrintListeners = new ArrayList<OnLogListener>();
	private static ArrayList<OnLogListener> readyToRemoveListeners = new ArrayList<OnLogListener>();

	/** add a listener that gets called every time there is a new println call */
	public static void addLogListener(OnLogListener lis) {
		onPrintListeners.add(lis);
	}

	/** set to remove the listener on the next println call */
	public static void removeLogListener(OnLogListener lis) {
		readyToRemoveListeners.add(lis);
	}
	
	/**
	 * This is used instead of the System,out.println to resent the input to all
	 * the clients that have requested to listen to the logs via --log command
	 */
	public static void log(String text) {
		LogEntry logEntry=new LogEntry(text,Utils.getTime());
		
		for (OnLogListener toRemove : readyToRemoveListeners)
			onPrintListeners.remove(toRemove);
		readyToRemoveListeners.clear();
		
		for (OnLogListener listener : onPrintListeners)
			listener.onPrintlnCall(logEntry.toString());
		
		System.out.println(logEntry);
		log.add(logEntry);
	}
	
	public static class LogEntry{
		private String text,date;
		public LogEntry(String text, String date) {
			this.text = text;
			this.date = date;
		}
		public String getText() {
			return text;
		}
		public String getDate() {
			return date;
		}
		public String toString(){
			return text;
		}
	}
	private static List<LogEntry> log=new ArrayList<LogEntry>();
	public static String getLogString(){
		String text="";
		for(LogEntry l:log)text+=l;
		return text;
	}
}
