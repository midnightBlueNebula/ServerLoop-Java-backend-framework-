import java.io.*;
import java.net.Socket;
import java.util.HashMap;

/*
* reads client requests from client socket's input stream.
* then, generates response and writes on client socket's output stream.
* */

public class HTTPRequestResponse extends SocketReaderWriter {

    // stores parsed client data as key & value pairs
    private final HashMap<String, String> requestHeaders = new HashMap<String, String>();
    private String responseStatus = "200 OK";
    private String responseHeaders;

    public HTTPRequestResponse(Socket clientConnection){
        // passes client socket to parent class,
        // which stores input and output stream of client socket for reading request and writing response (I/O).
        super(clientConnection);

        try {
            readRequestStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // reads and parses client request data, stores in requestHeaders as key & value pairs.
    private void readRequestStream() throws IOException {
        String reqFirstLine = super.readLineFromRequestStream();

        if(reqFirstLine == null || reqFirstLine.isEmpty()) {
            System.err.println("Input stream of request is null or empty");
            return;
        }

        requestHeaders.put("First-line", reqFirstLine);
        parseMethodAndPath();

        while(true) {
            String line;
            try {
                line = super.readLineFromRequestStream();
                if(line == null || line.isEmpty()) {
                    break;
                }
                parseRequestHeader(line);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

        // if a client posts data, parses its content.
        if(getContentLength() > 0)
            parseContent();
    }

    private void parseMethodAndPath(){
        String reqFirstLine = requestHeaders.get("First-line");
        int methodNameEnds = reqFirstLine.indexOf(" ");
        int pathNameStarts = reqFirstLine.indexOf("/");
        int pathNameEnds = pathNameStarts + reqFirstLine.substring(pathNameStarts).indexOf(" ");

        String methodName = reqFirstLine.substring(0, methodNameEnds);
        String pathName = reqFirstLine.substring(pathNameStarts, pathNameEnds);

        addRequestHeader("method", methodName);
        addRequestHeader("path", pathName);
    }

    private void parseRequestHeader(String line){
        String[] headerPair = line.split(":");
        String headerKey = headerPair[0].toLowerCase();
        String headerValue = headerPair[1].trim();
        addRequestHeader(headerKey, headerValue);
    }

    private void addRequestHeader(String field, String value){
        requestHeaders.put(field, value);
    }

    private void removeRequestHeader(String field){
        requestHeaders.remove(field);
    }

    public String getRequestHeader(String field){
        return requestHeaders.get(field);
    }

    private int getContentLength(){
        String clStr = getRequestHeader("content-length");
        if(clStr == null)
            return 0;

        int contentLength = 0;
        try{
            contentLength = Integer.parseInt(clStr);
        } catch (NumberFormatException ignored) {

        } finally {
            return contentLength;
        }
    }

    // reads then parses data client posted to the server.
    private void parseContent(){
        int contentLength = Integer.parseInt(requestHeaders.get("content-length"));

        char[] contentChars = new char[contentLength];
        try {
            super.readFromRequestStream(contentChars, 0, contentLength);
            String content = new String(contentChars);
            addRequestHeader("content", content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStatus(int status){
        responseStatus = status + " ";
    }

    public void setStatus(int status, String description){
        setStatus(status);
        responseStatus += description;
    }

    public void setResponseHeader(String field, String value){
        responseHeaders += field + ": " + value + "\n";
    }

    private void writeResponseHeaders(){
        super.printlnResponseStream("HTTP/1.1 " + responseStatus);
        super.printlnResponseStream(responseHeaders);
    }

    private void sendResponse(String textualResponse) {
        writeResponseHeaders();
        super.response(textualResponse);
    }

    private void sendResponse(InputStream byteStream) {
        writeResponseHeaders();
        super.response(byteStream);
    }

    public void sendText(String text){
        setResponseHeader("Content-type", "text/plain");
        sendResponse(text);
    }

    public void sendJSON(String json){
        setResponseHeader("Content-type", "application/json");
        sendResponse(json);
    }

    public void sendHTML(String directory){
        setResponseHeader("Content-type", "text/html");
        File file = new File(directory);
        String html = "";
        try(BufferedReader fileReader = new BufferedReader(new FileReader(file))){
            String line = fileReader.readLine();
            while(line != null){
                html += line;
            }
            sendResponse(html);
        } catch(IOException e){
            System.out.println("Failed to open HTML file at: " + directory);
            e.printStackTrace();
        }
    }

    public void sendHTMLString(String html){
        setResponseHeader("Content-type", "text/html");
        sendResponse(html);
    }

    public void sendFile(String directory){
        String mimeType = FileTypeResolver.getMimeTypeForHttp(directory);
        File file = new File(directory);
        String length = file.length() + "";
        setResponseHeader("Content-type", mimeType);
        setResponseHeader("Content-length", length);
        setResponseHeader("Connection", "close");
        try{
            sendResponse(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeRequestInfo(){
        if(requestHeaders.isEmpty())
            return;

        File requestHeadersFile = new File("D:\\IntelliJ Idea Projects\\ServerLoop\\src\\BrowserRequestHeader.txt");

        try(PrintWriter fout = new PrintWriter(requestHeadersFile);) {
            for(String key : requestHeaders.keySet()){
                fout.println(key + ": " + requestHeaders.get(key));
            }

            fout.println(getRequestHeader("content"));
            fout.flush();
            fout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}