import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;

public class Main {

    /**
     * @param args Specify the server's IP address and port to connect to.
     *             Default IP address is the loopback address/127.0.0.1.
     *             Default port is 10000
     */
    public static void main(String[] args) {
        try {
            if(args.length == 1 || args.length > 2) {
                System.out.println("java -jar TFTP-TCP-Client.jar\njava -jar TFTP-TCP-Client.jar serverIP serverPort\n");
            } else if(args.length == 2) {
                InetAddress serverIP = InetAddress.getByName(args[0]);
                int serverPort = Integer.parseInt(args[1]);

                new TCPClient(serverIP, serverPort).run();
            } else {
                new TCPClient().run();
            }
        } catch (ConnectException e) {
            if(args.length == 2) {
                System.out.println("Failed to connect, server not found on " + args[0] + ":" + args[1]);
            } else {
                System.out.println("Failed to connect, server not found on " + InetAddress.getLoopbackAddress().getHostAddress() + ":10000");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
