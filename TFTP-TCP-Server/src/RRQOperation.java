import java.net.*;
import java.io.IOException;

/**
 * This class is used to serve a read request
 * and is intended to be ran on a separate thread.
 */
public class RRQOperation extends Request {

    private boolean isRunning;

    /**
     * @param socket The socket that is connected to the client.
     * @param packet The initial RRQ packet.
     * @throws IOException if an I/O error occurs.
     */
    public RRQOperation(Socket socket, ReqPacket packet) throws IOException {
        super(socket, packet);

        isRunning = DataHandler.fileExists(this.getFileName());

        if (!isRunning) {
            DataHandler.log("Error Packet Sent.");
            String msg = this.getFileName() + " not found.";
            DataHandler.log("Error: " + msg);
            this.send(DataHandler.error(ErrorCodes.NotFound, msg));
        } else {
            this.incrementBlock();
        }
    }

    /**
     * Runs read request operation.
     */
    @Override
    public void run() {
        if (isRunning) {
            byte[] data;

            try {
                DataHandler.log("Received RRQ");

                do {
//                    DataHandler.log("Data Block: " + this.getBlock());
                    data = DataHandler.data(this.getBlock(), this.getFileName());
                    this.send(data);

                    this.incrementBlock();
                } while (!DataHandler.isLastPacket(data.length));

                DataHandler.log("Read Request Completed.\n");
            } catch (IOException e) {
                DataHandler.log("Write Request Failed.\n");
                isRunning = false;
            }
        }
    }
}
