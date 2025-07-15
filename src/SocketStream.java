import java.io.IOException;

/*
* defines interface for managing server-client I/O operations.
* classes can implement this interface for different server-client contexts (e.g., internet, database, etc.).
*/

public interface SocketStream {
    public String getRequestHeader(String field);
    public void setStatus(int status);
    public void setStatus(int status, String description);
    public void setResponseHeader(String field, String value);
    public void sendText(String text);
    public void sendJSON(String json);
    public void sendHTML(String directory);
    public void sendHTMLString(String html);
    public void sendFile(String directory);
    public void close() throws IOException;
    public void writeRequestInfo();
}