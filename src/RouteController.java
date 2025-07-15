import java.util.HashMap;

/*
registers callbacks for specified paths and HTTP methods.
invokes callback for path and HTTP method client connection used.
*/

public class RouteController {
    // stores path & method as key and callback as value.
    // callback is a lambda stored as instance of functional interface EventCallback.
    static HashMap<String, EventCallback> pathHandlers = new HashMap<>();
    private String path = "";

    public RouteController(){

    }

    // creates route controller for only given path and its subpaths to be.
    public RouteController(String path){
        this.path = path;
    }

    // generates absolute path string for the subpath.
    // necessary to use when registering new callbacks to route controller created for a specific path.
    private String getAbsolutePath(String subpath){
        if(path.isEmpty())
            return subpath;
        return path + subpath;
    }

    // returns callback lambda for matching path and HTTP method.
    private static EventCallback getPathHandler(String handlerPath){
        if(pathHandlers.containsKey(handlerPath))
            return pathHandlers.get(handlerPath);

        return null;
    }

    // registers callbacks for HTTP methods at given path.

    public void get(String subpath, EventCallback handler){
        addCallbackToPath(HTTPMethod.GET, getAbsolutePath(subpath), handler);
    }

    public void post(String subpath, EventCallback handler){
        addCallbackToPath(HTTPMethod.POST, getAbsolutePath(subpath), handler);
    }

    public void put(String subpath, EventCallback handler){
        addCallbackToPath(HTTPMethod.PUT, getAbsolutePath(subpath), handler);
    }

    public void patch(String subpath, EventCallback handler){
        addCallbackToPath(HTTPMethod.PATCH, getAbsolutePath(subpath), handler);
    }

    public void delete(String subpath, EventCallback handler){
        addCallbackToPath(HTTPMethod.DELETE, getAbsolutePath(subpath), handler);
    }

    private static void addCallbackToPath(HTTPMethod method, String path, EventCallback handler) {
        String handlerKey = getHandlerKey(method, path);
        pathHandlers.put(handlerKey, handler);
    };

    // returns callback corresponds to HTTP method at given path.
    // if such callback does not exist, returns callback to inform requested page not found.
    private static EventCallback getHandler(HTTPMethod method, String path){
        String handlerKey = getHandlerKey(method, path);
        EventCallback pathHandler = getPathHandler(handlerKey);
        if(pathHandler != null){
            return pathHandler;
        }

        return handlerNotFound();
    }

    // default callback to return when no handler found for the route / 404 page not found
    private static EventCallback handlerNotFound(){
        EventCallback handler = (SocketStream streamer) -> {
            streamer.setStatus(404, "Page not found");
            streamer.sendHTMLString("<html><head><title>404 Not Found</title></head><body><h1>404 Not Found.</h1></body></html>");
        };

        return  handler;
    }

    // fetches method and path of client requests.
    // then, invokes the corresponding callback.
    protected static void forwardRequestToRoute(SocketStream streamer){
        String method = streamer.getRequestHeader("method");
        String path = streamer.getRequestHeader("path");
        EventCallback callback = getHandler(HTTPMethod.valueOf(method), path);
        callback.apply(streamer);
    }

    public static void deleteRoute(HTTPMethod method, String path){
        String handlerKey = getHandlerKey(method, path);
        pathHandlers.remove(handlerKey);
    }

    private static String getHandlerKey(HTTPMethod method, String path){
        String methodName = "";

        switch (method){
            case GET: methodName = "GET"; break;
            case PUT: methodName = "PUT"; break;
            case POST: methodName = "POST"; break;
            case PATCH:;methodName = "PATCH"; break;
            case DELETE: methodName = "DELETE"; break;
        }

        return methodName + "-" + path;
    }
}