import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;


public class MiniJServer {
	public static boolean RUNNING=true;
	public  static ServerSocket server;
	public static CopyOnWriteArrayList<Connection> activeConnections=new CopyOnWriteArrayList<Connection>(); /*contains all the active connections to the http clients*/
	public static boolean RUNNING_AS_SERVER=false;/*if the program runs as an HTTP server ,or as a client sending commands to a running HTTP Server*/
	
 	public static void main(String[] args) throws Exception{
 		
 		proccessParameters(args);
 		
		/**if the commands are one of the following  then start the server */
		if(RUNNING_AS_SERVER   || args.length==0){
			/*check if the port is already binded*/
			if(!Utils.isPortInUse(Config.PORT)){
				Logger.log("starting the server");
				server = new ServerSocket(Config.PORT);
				Logger.log("server address(local):  "+InetAddress.getLocalHost().getHostAddress()+":"+Config.PORT);
				Logger.log("server root:  "+Config.ROOT);;
				Logger.log("server name: "+Config.SERVER_NAME);
				Logger.log("supported methods: "+Arrays.toString(Config.SUPPORTED_METHODS));;
				Logger.log("supported http versions: "+Arrays.toString(Config.SUPPORTED_HTTP));;
				if(Config.PHP_PATH!=null && (new File(Config.PHP_PATH)).exists())Logger.log("php: "+Config.PHP_PATH);
				else if(Config.PHP_PATH!=null) Logger.log("php support:disabled");
				else  Logger.log("php:the file path was incorrect ("+Config.PHP_PATH+")");
				
				/*tcp server for getting incoming commands to interact with the server*/
				CommandServerAndClient.setupServer(args);
				
				/*while the server is running get any incoming connection ,put it in another thread and generate a response*/
				while(RUNNING){
					final Socket socket = server.accept();
					(new Thread(){
						public void run() {
							try {
								new Connection(socket);
							} catch (Exception e) {
								Logger.log("error creating connection");
								e.printStackTrace();	
							}
						}
					}).start();
				}
			}else{
				Logger.log("Server is already running on "+Config.PORT);
			}
		}
		else if(Utils.contains(args, "--help","--about","-a","-h")){/**if ts not running as a server and there is a help parrameter then print the manual*/
			Logger.log(HelpManual.getManual());
		}
		else{/*since this is a client and the command,then send the parameters  to a running server(if one exists)*/
			CommandServerAndClient.setupClient(args);
		}
	}
	
	public static void proccessParameters(String[] args){
		/**if there is an config specified then load it ,else load the default one(if exists)*/
		String argconf=Utils.argVal(args, "--config","-c");
		if(argconf!=null){
			Config.load(argconf);	
			RUNNING_AS_SERVER=true;
		}else Config.load(null);	
		
		/*check if the user has specified a port in the arguments*/
		String argport=Utils.argVal(args, "--port","-p");
		if(argport!=null){
			RUNNING_AS_SERVER=true;
			Config.PORT=Integer.parseInt(argport);
		}
		
		/*check if the user has specified a path for the php executable in the arguments*/
		String argphp=Utils.argVal(args, "--php","-g");
		if(argphp!=null){
			RUNNING_AS_SERVER=true;
			Config.PHP_PATH=argphp;
		}
		
		/*check if the user has specified a root path for the files,the default root is in the root of the C drive*/
		String argroot=Utils.argVal(args, "--root","-r");
		if(argroot!=null){
			RUNNING_AS_SERVER=true;
			Config.ROOT=argroot;
		}
		
		/*check if the user has specified a custom timeout time(time before a connection ends*/
		String connectionTimeout=Utils.argVal(args, "--timeout","-t");
		if(connectionTimeout!=null){
			RUNNING_AS_SERVER=true;
			Config.CONNECTION_STAY_ALIVE_TIME=Long.parseLong(connectionTimeout);
		}
		
		/*useless command :)*/
		if(Utils.contains(args, "--start","-s")){
			RUNNING_AS_SERVER=true;
		}
	}
}
