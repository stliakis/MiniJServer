import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class Utils {

	/** returns the system String representation of a new line */
	private static String newLineChar = System.getProperty("line.separator");;

	public static String newLine() {
		return newLineChar;
	}
	
	/**returns a hashman containing all the queries in the given query string*/
	public static HashMap<String,String> getQueries(String text){
		HashMap<String,String> map=new HashMap<String,String>();
		if(text.contains("&")){
			String quers[]=text.split("&");
			for(String q:quers){
				if(q.contains("="))map.put(q.split("=")[0], q.split("=")[1].replace("%20", " "));
			}
		}else{
			if(text.contains("="))map.put(text.split("=")[0], text.split("=")[1].replace("%20", " "));
		}
		return map;
	}

	/** returns the date in format */
	public static String getDate() {

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		dateFormat.setTimeZone(TimeZone.getDefault().getTimeZone("GMT"));
		return dateFormat.format(calendar.getTime());
	}

	/** returns the time */
	public static String getTime() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
		dateFormat.setTimeZone(TimeZone.getDefault().getTimeZone("GMT"));
		return dateFormat.format(calendar.getTime());
	}
	
	/** converts the given milliseconds in to a date */
	public static String longToDate(long millis) {
		Date date=new Date(millis);
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		dateFormat.setTimeZone(TimeZone.getDefault().getTimeZone("GMT"));
		return dateFormat.format(date);
	}
	
	/** check if a parrameter exists */
	public static boolean contains(String[] args, String... arg) {
		for (int c = 0; c < args.length; c++) {
			if(!args[c].contains("--") && args[c].trim().charAt(0)=='-' ){
				String argstring=args[c].replace("-", "");
				for(int c2=0;c2<argstring.length();c2++){
					for (String s2 : arg) {
						if(s2.contains("-") && !s2.contains("--")){
							String cha=s2.replace("-", "");
							if (argstring.charAt(c2)==cha.charAt(0)) {
									return true;
							}
						}
					}
				}
			}
			else{
				for (String s2 : arg) {
					if (args[c].equals(s2)) {
							return true;
					}
				}
			}
		}
		return false;
	}

	/** get correct argument value
	 * args=--port 2321 ,arg=--port , returns 2321,
	 * args=-p 213 arg=-p  return 213,
	 * args= -pr 232 /home/server ,arg=-r returns /home/server,
	 *  */
	public static String argVal(String[] args, String... arg) {
		for (int c = 0; c < args.length; c++) {
			if(!args[c].contains("--") && args[c].trim().charAt(0)=='-' ){
				String argstring=args[c].replace("-", "");
				for(int c2=0;c2<argstring.length();c2++){
					for (String s2 : arg) {
						if(s2.contains("-") && !s2.contains("--")){
							String cha=s2.replace("-", "");
							if (argstring.charAt(c2)==cha.charAt(0)) {
								if (args.length > c + (c2+1)){
									return args[c + (c2+1)];
								}
								else
									return null;
							}
						}
					}
				}
			}
			else{
				for (String s2 : arg) {
					if (args[c].equals(s2)) {
						if (args.length > c + 1)
							return args[c + 1];
						else
							return null;
					}
				}
			}
		}
		return null;
	}

	/**
		returns the content of a file as byte array ,if the file doesnt exits then returns null
	 */
	public static byte[] readFile(String filepath) {
		return readFile(new File(filepath));
	}
	
	/**
	returns the content of a file as byte array ,if the file doesnt exits then returns null
 */
	public static byte[] readFile(File file) {
		if (file.exists()) {
			byte[] bytes = new byte[(int) file.length()];
			FileInputStream fin;
			try {
				fin = new FileInputStream(file);
				fin.read(bytes);
				return bytes;
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		}
		return null;
	}
	
	public static String[] toArray(String text, String splitter) {
		if (text.contains(splitter)) {
			return text.split(splitter);
		}
		String[] ret = new String[1];
		ret[0] = text;
		return ret;
	}
	/** get the extension of a file , /bob/sponge/ho.html -> html*/
	public static String getFileExtension(File file){
		String extension = "";
		int i = file.getAbsoluteFile().toString().lastIndexOf('.');
		if (i > 0) {
		    extension = file.getAbsoluteFile().toString().substring(i+1);
		}
		return extension;
	}
	
	/** check if a port is currently in use */
	public static boolean isPortInUse(int port) {
		boolean result = true;
		try {
			(new ServerSocket(port)).close();
			result = false;
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return result;
	}
	
	public static class ReschedulableTimer  extends java.util.Timer {
		  private Runnable task;
		  private TimerTask timerTask;
		  public void schedule(Runnable runnable, long delay) {
		    task = runnable;
		    timerTask = new TimerTask() { public void run() { task.run(); }};
		    new java.util.Timer().schedule(timerTask, delay);        
		  }

		  public void reschedule(long delay) {
		    timerTask.cancel();
		    timerTask = new TimerTask() {
		    	public void run() {
		    		task.run(); 
		    	}
		    };
		    new java.util.Timer().schedule(timerTask, delay);        
		  }
		}
}
