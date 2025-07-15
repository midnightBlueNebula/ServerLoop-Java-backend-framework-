import java.io.IOException;

/*
creates thread to handle client requests in parallel.
*/

public class RequestHandler extends Thread{
    // SocketStream class parses client requests and packs request data in its instance.
    private final SocketStream socketIO;

    public RequestHandler(SocketStream socketIO) {
        this.socketIO = socketIO;
    }

    @Override
    public void run() {
        processRequest();
    }

    // forwardRequestToRoute method invokes callback for matching path and HTTP method stored in socketIO.
    private void processRequest() {
        RouteController.forwardRequestToRoute(socketIO);
        socketIO.writeRequestInfo();
        closeConnection();
    }

    // closes connection and frees memory after client request processed and response delivered to client.
    private void closeConnection(){
        try{
            socketIO.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}