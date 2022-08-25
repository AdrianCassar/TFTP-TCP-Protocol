import java.io.*;

/**
 * A generic class that contains all the core features of a packet.
 * This class is intended to be inherited, but can be used on its own.
 */
public class Packet {
    private final DataInputStream inputStream;
    private Opcode packetType;
    private int length;

    /**
     * @param stream The input stream containing the "packet".
     */
    public Packet(InputStream stream) {
        inputStream = new DataInputStream(stream);
    }

    /**
     * @return The next string within the packet.
     * @throws IOException if an I/O error occurs.
     */
    protected String readString() throws IOException {
        String strOut;

        try(ByteArrayOutputStream stringOut = new ByteArrayOutputStream()) {
            int val;
            while ((val = inputStream.read()) != 0 && val != -1) {
                stringOut.write(val);
            }

            strOut = stringOut.toString();
        }

        return strOut;
    }

    /**
     * @param length set the length of the packet.
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @return Get the input stream for this packet.
     */
    public DataInputStream getInputStream() {
        return inputStream;
    }

    /**
     * @return  Get the length of this packet.
     */
    public int getLength() {
        return length;
    }

    /**
     * @return Get the packet type.
     */
    public Opcode getPacketType() { return this.packetType; }

    /**
     * @param packetType Set the packet type.
     */
    public void setPacketType(Opcode packetType) {
        this.packetType = packetType;
    }
}
