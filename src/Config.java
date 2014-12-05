import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;


public class Config {
	public static String SUPPORTED_HTTP[]={"HTTP/1.1"};
	public static String SUPPORTED_METHODS[]={"GET","HEAD","PUT","DELETE","OPTIONS","TRACE","POST"};
	public static String SERVER_NAME="MiniJServer/1.0";
	public static String DEFAULT_ROOT=System.getProperty("user.home")+"/Server";
	public static long CONNECTION_STAY_ALIVE_TIME=5000;
	
	
	public static String FILE_404=System.getProperty("user.home")+"/Server/file_404.html";
	public static String FILE_505=System.getProperty("user.home")+"/Server/file_505.html";
	public static String FILE_400=System.getProperty("user.home")+"/Server/file_400.html";
	public static String FILE_405=System.getProperty("user.home")+"/Server/file_405.html";
	public static int DEFAULT_PORT=2048;
	
	/**search if there is a server.conf file in the [user folder]/Server folder
	 ** Sample conf file:
	 ** SUPPORTED_METHODS=GET,HEAD,DELETE
	 ** FILE_404=/bob/sponge
	 ** DEFAULT_PORT=123
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
						if(par.equalsIgnoreCase("DEFAULT_ROOT"))DEFAULT_ROOT=val;
						if(par.equalsIgnoreCase("FILE_404"))FILE_404=val;
						if(par.equalsIgnoreCase("FILE_505"))FILE_505=val;
						if(par.equalsIgnoreCase("FILE_400"))FILE_400=val;
						if(par.equalsIgnoreCase("FILE_405"))FILE_405=val;
						if(par.equalsIgnoreCase("DEFAULT_PORT"))DEFAULT_PORT=Integer.parseInt(val);
						if(par.equalsIgnoreCase("CONNECTION_STAY_ALIVE_TIME"))CONNECTION_STAY_ALIVE_TIME=Long.parseLong(val);
				}
				br.close();
			}catch(Exception ex){}
			
		}else if(filei!=null){
			Logger.log("no config file found at "+filei+" ,loading default settings");
		}
		
	}
}
