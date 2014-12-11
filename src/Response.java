import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


public class Response {
	private String statusLine;
	private HashMap<String,String> headers=new HashMap<String, String>();
	private byte[] bodyBytes;
	
	private Connection connection;
	public Response(Request request,Connection connection) throws Exception{
		this.connection=connection;
		
		/*check if the request was not understandable,and send an 400 if so*/
		if(request.isBadRequest()){
			constructBadRequest(request);
			return;
		}
		
		/*check if the Method is supported*/
		if(!Utils.contains(Config.SUPPORTED_METHODS, request.getRequestLine().getMethod())){
			constructUnsupportedMethod(request);
		}
		
		/*check if the HTTP version of the request is not supported and send a 505 if so*/
		if(!Utils.contains(Config.SUPPORTED_HTTP, request.getRequestLine().getVersion())){
			 constructInvalidHttpVersion(request);
			return;
		}
		
		/*check if the requested file exits and send a 404 if it doesn't*/
		if("GET".equalsIgnoreCase(request.getRequestLine().getMethod()) && !("*".equalsIgnoreCase(request.getRequestLine().getUrl()))){
			File f = new File(Config.ROOT+request.getRequestLine().getUrl());
			if(!f.exists() || f.isDirectory()) {
				 constructFileNotFound(request);
				return;
			}
		}
		
		/*if every thing is fine then generate a 200 response with the data*/
		construct200OK(request);
	}
	
	/**Constructs a 405(not supported method) response*/
	public void  constructUnsupportedMethod(Request request){
		statusLine=request.getRequestLine().getVersion()+" "+"405 Unsuported method";
		/*if the file in the Config.FILE_405 path exits then put its contents into body ,else return the default message*/
		if((bodyBytes=Utils.readFile(Config.FILE_405))==null){
			bodyBytes=HTMLMessages.get405(request.getRequestLine().getMethod()).getBytes();
		}
		headers.put("Date", Utils.getDate());
		/**put in to the Allow header all the allowed methods specified at Config.SUPPORTED_METHODS*/
		headers.put("Allow"	,Arrays.toString(Config.SUPPORTED_METHODS).replaceFirst("\\[", "").replaceFirst("\\]", ""));
		headers.put("Server", Config.SERVER_NAME);
		headers.put("Content-Length", bodyBytes.length+"");
		headers.put("Content-Type", "text/html");
	}
	
	/**Constructs a 404(file not found) response*/
	public void  constructFileNotFound(Request request){
		statusLine=request.getRequestLine().getVersion()+" "+"404 Not Found";
		/*if the file in the Config.FILE_404 path exits then put its contents into body ,else return the default message*/
		if((bodyBytes=Utils.readFile(Config.FILE_404))==null){
			bodyBytes=HTMLMessages.get404(request.getRequestLine().getUrl()).getBytes();
		}
		headers.put("Date", Utils.getDate());
		headers.put("Server", Config.SERVER_NAME);
		headers.put("Content-Length", bodyBytes.length+"");
		headers.put("Content-Type", "text/html");
	}
	/**Constructs a 505(unsupported http version) response*/
	public void  constructInvalidHttpVersion(Request request){
		statusLine=request.getRequestLine().getVersion()==null?Config.SUPPORTED_HTTP[0]:request.getRequestLine().getVersion()+" 505 HTTP Version Not Supported";
		/*if the file in the Config.FILE_505 path exits then put its contents into body ,else return the default message*/
		if((bodyBytes=Utils.readFile(Config.FILE_505))==null){
			bodyBytes=HTMLMessages.get505(request.getRequestLine().getUrl()).getBytes();
		}
		headers.put("Date", Utils.getDate());
		headers.put("Server", Config.SERVER_NAME);
		headers.put("Content-Length", bodyBytes.length+"");
		headers.put("Content-Type", "text/html");
	}
	/**Constructs a 400(Bad request) response*/
	public void constructBadRequest(Request request){
		statusLine=request.getRequestLine().getVersion()==null?Config.SUPPORTED_HTTP[0]:request.getRequestLine().getVersion()+" "+"400 Bad Request";
		/*if the file in the Config.FILE_400 path exits then put its contents into body ,else return the default message*/
		if((bodyBytes=Utils.readFile(Config.FILE_400))==null){
			bodyBytes=HTMLMessages.get400().getBytes();
		}
		headers.put("Date", Utils.getDate());
		headers.put("Server", Config.SERVER_NAME);
		headers.put("Content-Length", bodyBytes.length+"");
		headers.put("Content-Type", "text/html");
	}
	
	public String getHeader(String name){
		return headers.get(name);
	}
	
	
	public byte[] executePHPScript(Request request,File file) throws Exception{
		byte[] bytes;
		if(Config.PHP_PATH==null){
			bytes="php files are not supported by the server or a valid cgi script has not been set".getBytes();
		}else{
			ArrayList<String> result=PHP.execute(request, file);
			headers.put(result.get(0).split(":")[0], result.get(0).split(":")[1]);
			headers.put(result.get(1).split(":")[0], result.get(1).split(":")[1]);
			String lines="";
			for(int c=2;c<result.size();c++){
				lines+=result.get(c);
			}
			bytes=lines.getBytes();
		}
		return bytes;
	}
	
	/**Constructs a 200(everything went well) response based on the used method*/
	public void construct200OK(Request request) throws Exception{
		if(request.getRequestLine().getMethod().equalsIgnoreCase("GET")){/*if the method is GET ,find the requested file and put it to body*/
			/*read the requested file and store it into the body*/
			File file=new File(Config.ROOT+request.getRequestLine().getUrl());
			if(file.getAbsolutePath().endsWith(".php") || file.getAbsolutePath().endsWith(".PHP")){
				bodyBytes=executePHPScript(request,file);
			}else bodyBytes=Utils.readFile(file);
			/*set the statusLine of the response*/
			statusLine=request.getRequestLine().getVersion()+" "+"200 OK";
			/*insert the date of the last modification made to the file into the headers*/
			headers.put("Content-Type",getFileType(file));  
			headers.put("Last-Modified", Utils.longToDate(file.lastModified()));
			headers.put("Content-Length",bodyBytes.length+"");
		}else if(request.getRequestLine().getMethod().equalsIgnoreCase("HEAD")){/*if the method is HEAD ,return only the headers*/
			/*set the statusLine of the response*/
			statusLine=request.getRequestLine().getVersion()+" "+"200 OK";
			/*read the requested file and store it into the body*/
			File file=new File(Config.ROOT+request.getRequestLine().getUrl());
			if(file.getAbsolutePath().endsWith(".php") || file.getAbsolutePath().endsWith(".PHP")){
				bodyBytes=executePHPScript(request,file);
			}else bodyBytes=Utils.readFile(file);
			/*insert the date of the last modification made to the file into the headers*/
			headers.put("Content-Type",getFileType(file));
			headers.put("Last-Modified", Utils.longToDate(file.lastModified()));
			headers.put("Content-Length",bodyBytes.length+"");
			/*we need to send only the headers ,therefore we set the body to null*/
			bodyBytes=null;
		}else if(request.getRequestLine().getMethod().equalsIgnoreCase("PUT")){/*if the method is PUT ,put the body in the requested url*/
			statusLine=request.getRequestLine().getVersion()+" "+"201 Created";
			FileOutputStream writer = new FileOutputStream(new File(Config.ROOT+request.getRequestLine().getUrl()));
			writer.write(request.getBody());
			writer.close();
			String body="<html><body><h1>The file was created.</h1></body></html>";
			bodyBytes=body.getBytes();
			headers.put("Content-Type", "text/html");
			headers.put("Content-Length", body.length()+"");
		}else if(request.getRequestLine().getMethod().equalsIgnoreCase("DELETE")){/*if the method is DELETE  ,delete the file specified by the url*/
			statusLine=request.getRequestLine().getVersion()+" "+"200 OK";
			File file=new File(Config.ROOT+request.getRequestLine().getUrl());
			if(file.exists())file.delete();
			String body="<html><body><h1>The file was deleted</h1></body></html>";
			bodyBytes=body.getBytes();
			headers.put("Content-Type", "text/html");
			headers.put("Content-Length", body.length()+"");
		}else if(request.getRequestLine().getMethod().equalsIgnoreCase("OPTIONS")){/*if the method is OPTIONS  ,return the supported http methods*/
			statusLine=request.getRequestLine().getVersion()+" "+"200 OK";
			headers.put("Allow"	,Arrays.toString(Config.SUPPORTED_METHODS).replaceFirst("\\[", "").replaceFirst("\\]", ""));
			headers.put("Content-Type", "text/html");
		}else if(request.getRequestLine().getMethod().equalsIgnoreCase("TRACE")){/*if the method is TRACE  ,return the original request body*/
			statusLine=request.getRequestLine().getVersion()+" "+"200 OK";
			bodyBytes=request.toString().getBytes();
			headers.put("Content-Length", bodyBytes.length+"");
			headers.put("Content-Type", "text/html");
		}else if(request.getRequestLine().getMethod().equalsIgnoreCase("POST")){/*if the method is POST  ,store the data and print it on the server's log*/
			/*read the requested file and store it into the body*/
			File file=new File(Config.ROOT+request.getRequestLine().getUrl());
			if(file.getAbsolutePath().endsWith(".php") || file.getAbsolutePath().endsWith(".PHP")){
				bodyBytes=executePHPScript(request,file);
			}else bodyBytes=Utils.readFile(file);
			/*set the statusLine of the response*/
			statusLine=request.getRequestLine().getVersion()+" "+"200 OK";
			/*insert the date of the last modification made to the file into the headers*/
			headers.put("Content-Type",getFileType(file));  
			headers.put("Last-Modified", Utils.longToDate(file.lastModified()));
			headers.put("Content-Length",bodyBytes.length+"");
		}
		headers.put("Date", Utils.getDate());
		headers.put("Server", Config.SERVER_NAME);
	}
	

	/**Sends the data(status lines+headers+data) to the client*/
	public void send(DataOutputStream dos){
		try {
			dos.write(getStatusLineAndHeader().getBytes());
			if(bodyBytes!=null){
				dos.write(bodyBytes);
				dos.writeChars(Utils.newLine());
			}
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**Returns the status line and the header in a String*/
	public String getStatusLineAndHeader(){
		String ret=statusLine+Utils.newLine();
		for(Iterator it=headers.entrySet().iterator();it.hasNext();){
			Entry entry=(Entry)it.next();
			ret+=entry.getKey()+": "+entry.getValue()+Utils.newLine();
		}
		if(bodyBytes!=null){
			ret+=Utils.newLine();
		}
		return ret;
	}
	
	public String toString(){
		String ret=getStatusLineAndHeader();
		if(this.bodyBytes!=null){
			ret+=new String(this.bodyBytes);
		}
		return ret;
	}
	
	private static String getFileType(File file){
		String ex=Utils.getFileExtension(file);
		if(fileTypes.containsKey(ex)){
			return fileTypes.get(ex);
		}else return "text/plain";
	}
	
	private static Map<String,String> fileTypes=new HashMap<String,String>(){{
	    put("htm","text/html");
	    put("html","text/html");
	    put("xml","text/xml");
	    put("txt","text/plain");
	    put("js","text/javascript");
	    put("css","text/css");
	    put("png","image/png");
	    put("jpg","image/jpg");
	    put("gif","image/gif");
	}};
	
	
}

