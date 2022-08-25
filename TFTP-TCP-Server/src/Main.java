import java.io.IOException;
import java.net.SocketException;

public class Main {

    /**
     * @param args Starts the server on the main thread.
     */
    public static void main(String[] args) {
        try {
            if(args.length > 1) {
                System.out.println("java -jar TFTP-TCP-Server.jar\njava -jar TFTP-TCP-Server.jar ServerPort\n");
            } else if(args.length == 1) {
                int serverPort = Integer.parseInt(args[0]);

                new TFTPServer(serverPort).run();
            } else {
                new TFTPServer().run();
            }
        } catch (SocketException e) {
            System.out.println("Port 10000 is already in use.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
