import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**A server and client used to communicate with the server and send commands to it via a terminal when it is running,this is the main communication interface with the server*/
public class CommandServerAndClient {
	public static int DEFAULT_COMMAND_PORT=21312;
	
	/**Setup a server listening to DEFAULT_COMMAND_PORT port for incoming commands*/
	public static void setupServer(final String[] args){
		(new Thread(){
			public void run() {
				ServerSocket server=null;
				
				int serverPort=DEFAULT_COMMAND_PORT;
				String customPort=Utils.argVal(args, "--cport","-o");
				if(customPort!=null)serverPort=Integer.parseInt(customPort);
				
				do{
					try {
						server = new ServerSocket(serverPort);
						if(serverPort!=DEFAULT_COMMAND_PORT){
							Logger.log("command port(default was in use or custom selected): "+serverPort);
						}
						break;
					} catch (IOException e1) {
						serverPort++;
					}
				}while(true);

				/*while the server is running ,continue to receive incoming commands */
				while(MiniJServer.RUNNING){
					try {
						Socket socket = server.accept();
						BufferedReader  br =new BufferedReader(new InputStreamReader(socket.getInputStream()));
						BufferedWriter  bw =new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
						if(execCommands(br.readLine(),bw,socket)){
							bw.close();
							socket.close();
						}
					} catch (IOException e) {}
				}
			}
		}).start();
	}
	/**Setup a client connected  to DEFAULT_COMMAND_PORT port for outgoing commands*/
	public static void setupClient(String[] args) throws UnknownHostException, IOException{
		/*the client can access a remote server running on another machine by specific its address with --ip [address] argument */
		String serverIP=InetAddress.getLocalHost().getHostName();
		String customIP=Utils.argVal(args, "--ip","-i");
		if(customIP!=null)serverIP=customIP;
		
		/*you can set a custom port for the command server with the --cport [port] parameter*/
		int serverPort=DEFAULT_COMMAND_PORT;
		String customPort=Utils.argVal(args, "--cport","-o");
		if(customPort!=null)serverPort=Integer.parseInt(customPort);
		
		/**create a connection with the machine at serverIP address,send all the commands and print the responses*/
		try{
			Socket client = new Socket();
			client.connect(new InetSocketAddress(serverIP, serverPort), 5000);
			BufferedWriter  bw =new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			BufferedReader  br =new BufferedReader(new InputStreamReader(client.getInputStream()));
			String argsString="";
			for(String argument:args)argsString+=argument+ " ";
			bw.write(argsString);
			bw.newLine();
			bw.flush();
			try{
				for(String line=br.readLine();line!=null;line=br.readLine()){
					System.out.println(line);
				}
			}catch(Exception ex){}
			bw.close();
			br.close();
			client.close();
		}catch(Exception ex){
			System.out.println(ex);
			System.out.println("no server found at "+serverIP+":"+DEFAULT_COMMAND_PORT);
		}
	}
	/**Execute a command such as --quit on the connected HTTP Server*/
	public static boolean execCommands(String command,final BufferedWriter bw,final Socket socket) throws IOException{
		if(command==null)return true;
		boolean closeConnectionAfter=true;
		String[] commands=command.split(" ");
		if(Utils.contains(commands, "--status","-t")){
			bw.write("[status]");bw.newLine();
			bw.write("status: server is running");	bw.newLine();
			bw.write("address: "+InetAddress.getLocalHost().getHostAddress());bw.newLine();
			bw.write("port: "+MiniJServer.server.getLocalPort());	bw.newLine();
			bw.flush();
		}
		if(Utils.contains(commands, "--connections","-n")){
			bw.write("ID\t\tCLIENT ADRESS");bw.newLine();
			for(int c=0;c<MiniJServer.activeConnections.size();c++){
				Connection con=MiniJServer.activeConnections.get(c);
				bw.write(c+"\t\t"+con.getSocket().getRemoteSocketAddress().toString().substring(1));
				bw.newLine();
			}
			bw.flush();
		}
		if(Utils.contains(commands, "--kill","-k")){
			if(Utils.argVal(commands,  "--kill","-k").equals("all")){
				for(Connection con:MiniJServer.activeConnections){
					con.close();
				}
				bw.write("all the connections have been closed");
			}else{
				int id=Integer.parseInt(Utils.argVal(commands,  "--kill","-k"));
				if(id>=0 && id<MiniJServer.activeConnections.size()){
					MiniJServer.activeConnections.get(id).close();
					bw.write("connection has been closed");
				}else{
					bw.write("no connection found with id "+id);
				}
			}
			bw.flush();
		}
		for(String file=Utils.argVal(commands, "--savelog","-l");file!=null;){/*if the command is --savelog then save all the log(calls to Logger.println) into the selected file*/
			FileOutputStream writer = new FileOutputStream(new File(MiniJServer.USING_ROOT+file));
			writer.write(Logger.getLogString().getBytes());
			writer.close();
		}
		if(Utils.contains(commands, "--log","-l")){/**if the command is --log then add a listener to the Logger.println and on every println ,send the data to the client*/
			bw.write("[log]"       );bw.newLine();
			Logger.addLogListener(new Logger.OnLogListener() {
				public void onLogCall(String line) {
					try {
						bw.write(line);
						bw.newLine();
						bw.flush();
					} catch (IOException e) {/**if the client has been disconnected ,then remove the println listener from Logger.println and close the connection*/
						try {
							bw.close();
							socket.close();
						} catch (IOException e1) {}
						System.out.println("log listener["+socket.getRemoteSocketAddress()+"]"+" removed");
						Logger.removeLogListener(this);
					}	
				}
			});
			Logger.log("log listener["+socket.getRemoteSocketAddress()+"]"+" added");
			closeConnectionAfter=false;
		}
		if(Utils.contains(commands, "--quit","-q")){
			System.out.println("quiting server");
			System.exit(0);
		}
		return closeConnectionAfter;
	}
}
