import java.io.IOException;
import java.net.Socket;

/**
 * This class is used to serve a write request.
 */
public class WRQOperation extends Request {
    private boolean isRunning;

    /**
     * @param socket The socket that is connected with the server.
     * @param fileName The name of the file to be sent to the server.
     * @throws IOException if an I/O error occurs.
     */
    public WRQOperation(Socket socket, String fileName) throws IOException {
        super(socket, fileName);

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
                DataHandler.log("Sent WRQ");
                this.send(DataHandler.req(Opcode.WRQ, this.getFileName()));

                do {
//                    DataHandler.log("Data Block: " + this.getBlock());
                    data = DataHandler.data(this.getBlock(), this.getFileName());
                    this.send(data);

                    this.incrementBlock();
                } while (!DataHandler.isLastPacket(data.length));

                if(isRunning) {
                    DataHandler.log("Write Request Completed.\n");
                } else {
                    DataHandler.log("Write Request Failed.\n");
                }
            } catch (IOException e) {
                isRunning = false;
            }
        }
    }
}
