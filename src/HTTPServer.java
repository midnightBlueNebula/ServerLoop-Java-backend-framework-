import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/*
initializes server and runs it in loop.
server runs in loop continuously and waits for client connection and passes connection request to a handler thread,
therefore, handles client requests without blocking main loop.
also, registers callback for HTTP methods in given path via instance of RouteController.
*/

public class HTTPServer {
    final private ServerSocket server;
    final private RouteController routeController = new RouteController();

    // initializes server at given port number.
    public HTTPServer(int portNumber){
        try {
            server = new ServerSocket(portNumber);
        } catch(IOException e) {
            handleSocketError(e);
            throw new RuntimeException(new IOException("Failed to set up server at port: " + portNumber));
        }

        System.out.println("The Server listens on: http://127.0.0.1:"+portNumber);
    }

    // the server runs in infinite loop and waits for client connections.
    // once a client is connected to server, it is passed to handler thread to handle client's request.
    public void listen(){
        try {
            while(true) {
                Socket clientConnection = server.accept();
                SocketStream socketIO = new HTTPRequestResponse(clientConnection);
                Thread requestHandler = new RequestHandler(socketIO);
                requestHandler.start();
            }
        } catch(IOException e) {
            handleSocketError(e);
        }
    }

    // registers callbacks for HTTP methods in given path via instance of RouteController.
    public void get(String path, EventCallback handler){
        routeController.get(path, handler);
    }

    public void post(String path, EventCallback handler){
        routeController.post(path, handler);
    }

    public void put(String path, EventCallback handler){
        routeController.put(path, handler);
    }

    public void patch(String path, EventCallback handler){
        routeController.patch(path, handler);
    }

    public void delete(String path, EventCallback handler){
        routeController.delete(path, handler);
    }

    // registers application-level middleware.
    public void use(EventCallback middleware){
        routeController.use(middleware);
    }

    private static void handleSocketError(IOException e) {
        System.out.println(e.getMessage());
    }
}
