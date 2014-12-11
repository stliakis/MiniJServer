import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;


public class Config {
	public static String SUPPORTED_HTTP[]={"HTTP/1.1"};
	public static String SUPPORTED_METHODS[]={"GET","HEAD","PUT","DELETE","OPTIONS","TRACE","POST"};
	public static String SERVER_NAME="MiniJServer/1.0";
	public static String ROOT=System.getProperty("user.home")+"/Server";
	public static String PHP_PATH="C:\\Program Files (x86)\\IIS Express\\PHP\\v5.3\\php-cgi.exe";
	public static long CONNECTION_STAY_ALIVE_TIME=5000;
	public static String FILE_404=System.getProperty("user.home")+"/Server/file_404.html";
	public static String FILE_505=System.getProperty("user.home")+"/Server/file_505.html";
	public static String FILE_400=System.getProperty("user.home")+"/Server/file_400.html";
	public static String FILE_405=System.getProperty("user.home")+"/Server/file_405.html";
	public static String SSL_PASS=null;
	public static String SSL_FILE=null;
	
	public static int PORT=2048;
	
	/**search if there is a server.conf file in the [user folder]/Server folder
	 ** Sample conf file:
	 ** SUPPORTED_METHODS=GET,HEAD,DELETE
	 ** FILE_404=/bob/sponge
	 ** PORT=123
	 */
	public static void load(String filei){
		/*if the filei parrameter is null then load the default config at the default path(userfolder/Server/server.conf*/
		File file=new File((filei==null?System.getProperty("user.home")+"/Server/server.conf":filei));
		if(file.exists()){
			try{
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null) {
						String par=line.split("=")[0];
						String val=line.split("=")[1];
						if(par.equalsIgnoreCase("SUPPORTED_HTTP"))SUPPORTED_HTTP=Utils.toArray(val, ",");
						if(par.equalsIgnoreCase("SUPPORTED_METHODS"))SUPPORTED_METHODS=Utils.toArray(val, ",");
						if(par.equalsIgnoreCase("SERVER_NAME"))SERVER_NAME=val;
						if(par.equalsIgnoreCase("SSL_PASS"))SSL_PASS=val;
						if(par.equalsIgnoreCase("SSL_FILE"))SSL_FILE=val;
						if(par.equalsIgnoreCase("ROOT"))ROOT=val;
						if(par.equalsIgnoreCase("PHP_PATH"))PHP_PATH=val;
						if(par.equalsIgnoreCase("FILE_404"))FILE_404=val;
						if(par.equalsIgnoreCase("FILE_505"))FILE_505=val;
						if(par.equalsIgnoreCase("FILE_400"))FILE_400=val;
						if(par.equalsIgnoreCase("FILE_405"))FILE_405=val;
						if(par.equalsIgnoreCase("PORT"))PORT=Integer.parseInt(val);
						if(par.equalsIgnoreCase("CONNECTION_STAY_ALIVE_TIME"))CONNECTION_STAY_ALIVE_TIME=Long.parseLong(val);
				}
				br.close();
			}catch(Exception ex){}
			
		}else if(filei!=null){
			Logger.log("no config file found at "+filei+" ,loading default settings");
		}
	}
	
}
