public class FileTypeResolver {
    public static String  getMimeTypeForHttp(String fileName){
        int pos = fileName.lastIndexOf('.');
        if (pos < 0)
            return "x-application/x-unknown";

        String ext = fileName.substring(pos + 1).toLowerCase();
        if (ext.equals("txt"))
            return "text/plain";
        else if (ext.equals("html"))
            return "text/html";
        else if (ext.equals("htm"))
            return "text/html";
        else if (ext.equals("css"))
            return "text/css";
        else if (ext.equals("js"))
            return "text/javascript";
        else if (ext.equals("java"))
            return "text/x-java";
        else if (ext.equals("jpeg"))
            return "image/jpeg";
        else if (ext.equals("jpg"))
            return "image/jpeg";
        else if (ext.equals("png"))
            return "image/png";
        else if (ext.equals("gif"))
            return "image/gif";
        else if (ext.equals("ico"))
            return "image/x-icon";
        else if (ext.equals("class"))
            return "application/java-vm";
        else if (ext.equals("jar"))
            return "application/java-archive";
        else if (ext.equals("zip"))
            return "application/zip";
        else if (ext.equals("xml"))
            return "application/xml";
        else if (ext.equals("xhtml"))
            return "application/xhtml+xml";
        else
            return "x-application/x-unknown";
    };
}
