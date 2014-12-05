import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;


public class Connection {
	private Socket socket;
	private boolean alive=true;
	/**This class is used to manage a connection between a client and the server ,
	 * it gets as parameter a socket and creates the requered input and output stream for the response 
	 * to be generated based on the client' s request
	 * @throws IOException */
	
	public Connection(Socket socket) throws IOException  {
		this.socket=socket;
		
		MiniJServer.activeConnections.add(this);
		
		Logger.log("---------------------Connection to "+socket.getRemoteSocketAddress()+" started---------------------");
		
		/**make a timer that will end the connection after the timeouttime has passed
		 * the timer will reset everytime there is activity (client send a new request)
		 */
		final Utils.ReschedulableTimer  timeoutTimer=new Utils.ReschedulableTimer () ;
		timeoutTimer.schedule(new Runnable(){
			public void run() {
				alive=false;
			}
		}, Config.CONNECTION_STAY_ALIVE_TIME);
		
		/*create a reader and a writter using the socket's stream,this objects will be used to receive the request and 
		 * send the response to the client */
		
		DataInputStream dis=new DataInputStream(socket.getInputStream());
		DataOutputStream dos =new DataOutputStream(socket.getOutputStream());
		
		while(socket.isConnected() && !socket.isClosed()){
			try{
				/**wait until there are new data in to the stream,if the connection is no more alive then close it*/
				while(dis.available()==0){
					if(alive==false){
						socket.close();
						break;
					}
				}
				
				/*at this point the stream has new data ,or the alive attribute has been set to false */
				if(!socket.isClosed()){
					/*parse the request text */
					Request request=new Request(dis,this);
					
					/*generate a response based on the request*/
					Response response=new Response(request,this);
					
					/*send the response back to the client*/
					response.send(dos);
					
					/*log the details of the communication*/
					Logger.log(toString(request,response,socket));
	
					/*if the request is bad formatted or it has its Connection header set to close , close the connection after sending the response
					 * else reset the timeout time*/
					if(request.isBadRequest() || !"keep-alive".equalsIgnoreCase(request.getHeader("Connection"))){
						close();
					}else timeoutTimer.reschedule(Config.CONNECTION_STAY_ALIVE_TIME);
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
		MiniJServer.activeConnections.remove(this);
		Logger.log("--------------------Connection to "+socket.getRemoteSocketAddress()+" ended--------------------");
	}
	
	public String toString(Request request,Response response,Socket socket){
		return   "-----------------------[Request from "+socket.getRemoteSocketAddress()+"]--------------------------"+Utils.newLine()+
					   request+Utils.newLine()+
					   "--------------------------------------------------------------------------------"+Utils.newLine()+
					   "-----------------------[Response to "+socket.getRemoteSocketAddress()+"]---------------------------"+Utils.newLine()+
					   response+Utils.newLine()+
					   "--------------------------------------------------------------------------------";
	}
	public void close(){
		alive=false;
	}
	public Socket getSocket(){
		return this.socket;
	}
}
