import java.io.DataInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


public class Request {
	private RequestLine requestLine;
	private Map<String,String> headers=new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
	private byte[] body;
	private String querie;
	private boolean isBad=false;
	private Connection connection;
	public Request(DataInputStream  reader,Connection connection) throws IOException{
		this.connection=connection;
		
		String line=reader.readLine();
		while(line==null){
			 line=reader.readLine();
		}
		/**parse the request line of the request*/
		requestLine=new RequestLine(line,connection);
		/**store all the headers of the request into a hashmap*/
		while(!"".equalsIgnoreCase(line=reader.readLine()) && line!=null){
			String parts[]=line.split(":");
			if(parts.length<2){
				isBad=true;
			}else headers.put(parts[0].trim(), parts[1].trim());
		}
		/**if the header Content-Length is not 0 ,then read the body*/
		if(headers.containsKey("Content-Length") && !"0".equalsIgnoreCase(headers.get("Content-Length"))){
			body=new byte[Integer.parseInt(headers.get("Content-Length"))];
			int counter=0;
			for(byte b=reader.readByte();counter<body.length;b=reader.readByte()){
				body[counter++]=b;
			}
		}
		if(body==null)body=new byte[0];
		
		/**check if the request is bad formatted ,if it is then set the isBad attribute to true*/
		if(requestLine.isBad())isBad=true;
	}
	public String toString(){
		if(requestLine.isBad() || isBad)return "bad request";
		String output=requestLine+Utils.newLine();
		for(Iterator it=headers.entrySet().iterator();it.hasNext();){
			Entry entry=(Entry)it.next();
			output+=entry.getKey()+": "+entry.getValue()+Utils.newLine();
		}
		if(body!=null)output+=Utils.newLine()+new String(body);
		return output;
	}
	
	
	public Connection getConnection() {
		return connection;
	}
	public String getQuerie() {
		return querie;
	}
	public boolean isBadRequest(){
		return isBad;
	}
	public String getHeader(String name){
		return headers.get(name);
	}
	public RequestLine getRequestLine(){
		return requestLine;
	}
	public byte[] getBody() {
		return body;
	}
	public  class RequestLine {
		private String method, url, version;
		public RequestLine(String requestLine,Connection connection) {
			if (requestLine != null && requestLine.contains(" ")) {
				String parts[] = requestLine.split(" ");
				if (parts.length != 0)
					method = parts[0];
				if (parts.length != 1)
					url = parts[1];
				if (parts.length != 2)
					version = parts[2];
				
				/**if the url contains a query string then put it to the querie*/
				if(url!=null && url.contains("?")){
					querie=url.split("\\?")[1];
					url=url.split("\\?")[0];
				}
				
				/*replace %20 with whitespace characters*/
				url=url.replace("%20", " ");
			}
			if(isBad())return;
			
			if(!"*".equalsIgnoreCase(url)){
				String extension = "";
				int i = url.lastIndexOf('.');
				if (i > 0) {
				    extension = url.substring(i+1);
				}
				if(extension.equalsIgnoreCase("")){
					url+="index.html";
				}
			}
			
		}
		public boolean isBad(){
			if(method ==null || url==null || version==null)return true;
			return false;
		}
		
		public String toString() {
			return method + " " + url + " " + version;
		}

		public String getMethod() {
			return method;
		}

		public String getUrl() {
			return url;
		}

		public String getVersion() {
			return version;
		}

	}
}

