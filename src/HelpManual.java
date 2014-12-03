
public class HelpManual {
	/**returns a String that contains the help manual*/
	public static String getManual(){
		String ret="\r\n"+
				"[help]\r\n"+
				"server --start :\r\n" + 
				"	starts the server using the default port(81)\r\n" + 
				"server --port 500  :\r\n" + 
				"	starts the server on port 500\r\n" + 
				"server --quit :\r\n" + 
				"	quits an already running server\r\n" + 
				"server --config settings.conf :\r\n" + 
				"	start the server with the specified configuration\r\n" + 
				"server --ip 192.168.1.16 --status]  :\r\n" + 
				"	the status of the server running at 192.168.1.16\r\n" + 
				"server --status :\r\n" + 
				"	returns the status of the running server on the local machine\r\n" + 
				"server --log :\r\n" + 
				"	starts listening to the server log messeges and prints them\r\n" + 
				"server --help :\r\n" + 
				"	shows the  help manual\r\n" + 
				"server --status --quit --ip 192.168.1.16 :\r\n" + 
				"	shows the server status and then quits the server running on 192.168.1.16";
		return ret;
	}
}
