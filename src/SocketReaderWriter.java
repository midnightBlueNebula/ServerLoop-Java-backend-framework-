import java.io.*;
import java.net.Socket;

/*
 * this is an abstract class designed to handle server-client I/O operations.
 * subclasses can inherit from the SocketReaderWriter abstract class to manage
 * specific server-client communication.
 * ex: subclass HTTPRequestResponse for HTTP communication.
 */

public abstract class SocketReaderWriter implements SocketStream{
    private final Socket client;
    private BufferedReader requestStream; // reads incoming string data from client.
    private PrintStream responseStream; // outputs string data to client.

    protected SocketReaderWriter(Socket client) {
        this.client = client;

        try{
            loadSocketIOStreams();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSocketIOStreams () throws IOException {
        requestStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
        responseStream = new PrintStream(client.getOutputStream());
    }

    public void close() throws IOException {
        requestStream.close();
        responseStream.close();
        client.close();
    }

    @Override
    public void writeRequestInfo(){

    }

    protected String readLineFromRequestStream() throws IOException{
        return requestStream.readLine();
    }

    protected int readFromRequestStream(char cbuf[], int off, int length) throws IOException{
        return requestStream.read(cbuf, 0, length);
    }

    protected void printlnResponseStream(String line){
        responseStream.println(line);
    }

    // the response can either be plain text (sending text, HTML, etc.) or binary data (a file; jpg, mp4, etc.)

    // for plain text response:
    protected void response(String textualResponse){
        responseStream.println(textualResponse);
        responseStream.flush();
    }

    // for binary data response:
    protected void response(InputStream byteStream){
        try{
            // creates a new stream to write binary data (byte instead of characters.)
            DataOutputStream responseByteStream = new DataOutputStream(client.getOutputStream());
            while (true){
                int b = byteStream.read();
                if(b < 0)
                    break;
                responseByteStream.write(b);
            }
            responseByteStream.flush(); // sends buffered binary data (file).
            responseStream.flush(); // sends buffered textual data (content header for file).
            byteStream.close();
        } catch (IOException e) {
            System.out.println("Could not send the file: failed to read from byte stream");
            e.printStackTrace();
        }
    }

    @Override
    public String getRequestHeader(String field) {
        return "";
    }

    @Override
    public void setStatus(int status) {

    }

    @Override
    public void setStatus(int status, String description) {

    }

    @Override
    public void setResponseHeader(String field, String value) {

    }

    @Override
    public void sendText(String text) {

    }

    @Override
    public void sendJSON(String json) {

    }

    @Override
    public void sendHTML(String directory) {

    }

    @Override
    public void sendHTMLString(String html) {

    }

    @Override
    public void sendFile(String directory) {

    }
}
