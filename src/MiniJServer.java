import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;


public class MiniJServer {
	public static boolean RUNNING=true;
	public static String USING_ROOT=Config.DEFAULT_ROOT;
	public static int USING_PORT=Config.DEFAULT_PORT;
	public  static ServerSocket server;
	public static CopyOnWriteArrayList<Connection> activeConnections=new CopyOnWriteArrayList<Connection>();
	public static void main(String[] args) throws IOException{
		
		/**if there is an config specified then load it ,else load the default one(if exists)*/
		String argconf=Utils.argVal(args, "--config","-c");
		if(argconf!=null){
			Config.load(argconf);	
		}else Config.load(null);	
		
		USING_PORT=Config.DEFAULT_PORT;
		USING_ROOT=Config.DEFAULT_ROOT;  
		
		/*check if the user has specified a port in the arguments*/
		String argport=Utils.argVal(args, "--port","-p");
		if(argport!=null)USING_PORT=Integer.parseInt(argport);
		
		/*check if the user has specified a root path for the files,the default root is in the root of the C drive*/
		String argroot=Utils.argVal(args, "--root","-r");
		if(argroot!=null)USING_ROOT=argroot;
		
		/**if the commands are one of the following  then start the server */
		if(Utils.contains(args, "--start","-s") || Utils.contains(args, "--port","-p") || Utils.contains(args, "--root","-r") || Utils.contains(args, "--config","-c")    || args.length==0){
			/*check if the port is already binded*/
			if(!Utils.isPortInUse(USING_PORT)){
				Logger.log("starting the server");
				server = new ServerSocket(USING_PORT);
				Logger.log("server address(local):  "+InetAddress.getLocalHost().getHostAddress()+":"+USING_PORT);
				Logger.log("server root:  "+USING_ROOT);;
				Logger.log("server name: "+Config.SERVER_NAME);
				Logger.log("supported methods: "+Arrays.toString(Config.SUPPORTED_METHODS));;
				Logger.log("supported http versions: "+Arrays.toString(Config.SUPPORTED_HTTP));;
				
				/*tcp server for getting incoming commands to interact with the server*/
				CommandServerAndClient.setupServer(args);
				
				/*while the server is running get any incoming connection ,put it in another thread and generate a response*/
				while(RUNNING){
					final Socket socket = server.accept();
					(new Thread(){
						public void run() {
							try {
								new Connection(socket);
							} catch (IOException e) {
								Logger.log("error creating connection");
								e.printStackTrace();	
							}
						}
					}).start();
				}
			}else{
				Logger.log("Server is already running on "+USING_PORT);
			}
		}
		else if(Utils.contains(args, "--help","--about","-a","-h")){/**if one of the requested commands are help,then return the help manual*/
			Logger.log(HelpManual.getManual());
		}
		else{/*since the commands are not --start ,--port , --help , --config  or --root,then send them to a running server(if one exists)*/
			CommandServerAndClient.setupClient(args);
		}
	}
}
