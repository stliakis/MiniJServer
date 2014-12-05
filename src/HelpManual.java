
public class HelpManual {
	/**returns a String that contains the help manual*/
	public static String getManual(){
		String ret="\r\n"+
				"[help]\r\n"+
				" --start :\r\n" + 
				"	starts the server using the default port(81)\r\n" + 
				" --port 500  :\r\n" + 
				"	starts the server on port 500\r\n" + 
				" --quit :\r\n" + 
				"	quits an already running server\r\n" + 
				" --timeout 10000 :\r\n" + 
				"	set the timeout of the http connections to 10 seconds\r\n" + 
				" --config settings.conf :\r\n" + 
				"	start the server with the specified configuration\r\n" + 
				" --ip 192.168.1.16 --status]  :\r\n" + 
				"	the status of the server running at 192.168.1.16\r\n" + 
				" --status :\r\n" + 
				"	returns the status of the running server on the local machine\r\n" + 
				" --log :\r\n" + 
				"	starts listening to the server log messeges and prints them\r\n" + 
				" --help :\r\n" + 
				"	shows the  help manual\r\n" + 
				" --status --quit --ip 192.168.1.16 :\r\n" + 
				"	shows the server status and then quits the server running on 192.168.1.16\r\n"+
				" --savelog logfile.log :\r\n" + 
				"	save all the log to a file in the root of the server\r\n"+
				" --connections :\r\n" + 
				"	print all the active connections to the http clients\r\n"+
				" --kill 13 :\r\n" + 
				"	kill the connection with id 13(type --connection to see the connection id)\r\n"+
				" --kill all :\r\n" + 
				"	kill all the connections\r\n";

		return ret;
	}
}
