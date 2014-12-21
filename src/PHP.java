import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PHP {
	/**
	 * http://php.net/manual/en/reserved.variables.server.php
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	
	public static ArrayList<String> execute(final Request request, final File file)
			throws Exception {
		
		if(!(new File(Config.PHP_PATH).exists())){
			Logger.log("can' t execute php script,the php-cgi  doesn't exists at "+Config.PHP_PATH);
		}
		
		ArrayList<String> result = new ArrayList<String>();

		ArrayList<String> evnVariables = new ArrayList<String>() {
			{
				add("REQUEST_URI=" + request.getRequestLine().getUrl());
				add("GATEWAY_INTERFACE=CGI/1.1");
				add("REMOTE_ADDR="	+ request.getConnection().getSocket().getInetAddress().getHostAddress());
				add("REMOTE_HOST="	+ request.getConnection().getSocket().getInetAddress().getHostName());
				add("REMOTE_PORT="	+ request.getConnection().getSocket().getPort());
				add("REQUEST_METHOD=" + request.getRequestLine().getMethod());
				add("DOCUMENT_ROOT=" + file.getParent());
				add("SCRIPT_FILENAME=" + file.getAbsolutePath());
				add("SCRIPT_NAME=" + file.getName());
				add("SERVER_PORT=" + Config.PORT);
				add("SERVER_SOFTWARE=" + Config.SERVER_NAME);
				add("REDIRECT_STATUS=CGI");
				add("SCRIPT_FILENAME=" + file.getAbsolutePath());
				add("SCRIPT_NAME=" + file.getName());
				
				if (request.getHeader("referer") != null)add("HTTP_REFERER=" + request.getHeader("referer"));
				if (request.getHeader("user-agent") != null)add("HTTP_USER_AGENT=" + request.getHeader("user-agent"));

				if (request.getRequestLine().getMethod().equalsIgnoreCase("GET")) {
					add("QUERY_STRING=" + request.getQuerie());
				}
			}
		};
		String[] variables = new String[evnVariables.size()];
		for (int c = 0; c < variables.length; c++)
			variables[c] = evnVariables.get(c);

		Process p = Runtime.getRuntime().exec(	new String[] { Config.PHP_PATH, "-f", file.getAbsolutePath() },variables);

	    DataInputStream dis = new DataInputStream(p.getInputStream());
		DataOutputStream dos = new DataOutputStream(p.getOutputStream());

		if (request.getRequestLine().getMethod().equalsIgnoreCase("POST")) {
			dos.writeBytes(request.getQuerie());
			dos.close();
		}

		String line;
		while ((line = dis.readLine()) != null) {
			result.add(line);
		}
		dis.close();

		return result;
	}

}
