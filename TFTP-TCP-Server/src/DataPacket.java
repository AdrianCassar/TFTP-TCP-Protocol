import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a data packet.
 */
public class DataPacket extends Packet {
    private byte[] data;
    private final int block;

    /**
     * @param stream  The input stream containing the data "packet".
     * @throws IOException if an I/O error occurs.
     */
    public DataPacket(InputStream stream) throws IOException {
        super(stream);
        this.setPacketType(Opcode.DATA);

        block = this.getInputStream().readShort();

        try(ByteArrayOutputStream dataOut = new ByteArrayOutputStream()) {
            data = new byte[512];
            int bytesRead = this.getInputStream().read(data, 0, 512);
            dataOut.write(data, 0, bytesRead);

            this.setLength(bytesRead + 4);
            this.data = dataOut.toByteArray();
        }
    }

    /**
     * @return An array of bytes containing the data for the data packet.
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @return Get the number for this Data packet.
     */
    public int getBlock() {
        return block;
    }
}
