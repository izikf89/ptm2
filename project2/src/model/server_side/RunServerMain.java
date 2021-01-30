package model.server_side;

public class RunServerMain {
	
    public static void main(String[] args) {
        Server server = new MySerialServer(); 
        CacheManager<String, String> cacheManager = new FileCacheManager<String, String>();
        MyClientHandler clientHandler = new MyClientHandler(cacheManager);
        server.open(5555 ,new ClientHandlerPath(clientHandler));
    }
}