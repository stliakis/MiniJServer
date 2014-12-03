
public class HTMLMessages {
	public static String get404(String url){
		String body="";
		body="<html>";
		body+="<head>";
		body+="  <title>404 Not Found</title>";
		body+="</head>";
		body+="<body>";
		body+="   <h1>Not Found</h1>";
		body+="   <p>The requested URL  "+url+" was not found on this server.</p>";
		body+="</body>";
		body+="</html>";
		return body;
	}
	
	public static String get405(String method){
		String body="";
		body="<html>";
		body+="<head>";
		body+="  <title>505 HTTP Method  Not Supported</title>";
		body+="</head>";
		body+="<body>";
		body+="   <h1>Not supported HTTP method</h1>";
		body+="   <p>The requested HTTP method "+method+" is not supported by the server.</p>";
		body+="</body>";
		body+="</html>";
		return body;
	}
			 
	public static String get400(){
		String body="";
		body="<html>";
		body+="<head>";
		body+=" <title>400 Bad Request</title>";
		body+="</head>";
		body+="<body>";
		body+="  <h1>Bad Request</h1>";
		body+="  <p>Your browser sent a request that this server could not understand.<p>";
		body+="  <p>The request line contained invalid characters following the protocol string.<p>";
		body+="</body>";
		body+="</html>";
		return body;
	}
			 
	public static String get505(String version){
		String body="";
		body="<html>";
		body+="<head>";
		body+="  <title>505 HTTP Version Not Supported</title>";
		body+="</head>";
		body+="<body>";
		body+="   <h1>Not supported HTTP version</h1>";
		body+="   <p>The requested HTTP version "+version+" is not supported by the server.</p>";
		body+="</body>";
		body+="</html>";
		return body;
	}
			 
	
	
	

}

