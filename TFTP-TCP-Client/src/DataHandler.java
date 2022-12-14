import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.*;

import static java.nio.file.StandardOpenOption.*;

/**
 * This class is used
 * - Creating packet headers
 * - Reading file bytes
 * - Writing file bytes
 * - Miscellaneous helper methods
 */
public class DataHandler {

    static public int dataLen = 512;

/*
            2 bytes    string   1 byte     string   1 byte
            -----------------------------------------------
    RRQ/WRQ | 01/02 |  Filename  |   0  |    Mode    |   0  |
            -----------------------------------------------
*/
    /**
     * Craft a request packet in bytes.
     *
     * @param reqType  The type of Opcode for this request, either WRQ or RRQ.
     * @param fileName The name of the file associated with the request.
     * @return An array of bytes representing a WRQ or RRQ packet.
     * @throws IOException if an I/O error occurs.
     */
    static public byte[] req(Opcode reqType, String fileName) throws IOException {
        try(ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            DataOutputStream dataOut = new DataOutputStream(byteArray)
        ) {
            dataOut.writeShort(reqType.getValue());
            dataOut.write(fileName.getBytes());
            dataOut.write(0);
            dataOut.write(Mode.octet.name().getBytes());
            dataOut.write(0);

            return byteArray.toByteArray();
        }
    }

    /**
     * Reads 512 bytes from a file at the corresponding block.
     *
     * @param block The block number for this data packet.
     * @param fileName The name of the file.
     * @return The block of bytes corresponding to the block index.
     * @throws IOException if an I/O error occurs.
     */
    static private byte[] getDataBlock(int block, String fileName) throws IOException {
        if (block != 0) {
            block--;
        }

        int skipBytes = block * dataLen;
        byte[] buff;

        try(ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream()) {
            if (fileExists(fileName)) {
                try(FileInputStream fileStream = new FileInputStream(fileName)) {
                    fileStream.getChannel().position(skipBytes);

                    buff = new byte[dataLen];
                    int bytesRead = fileStream.read(buff);
                    dataOutputStream.write(buff, 0, bytesRead);
                }
            }

            buff = dataOutputStream.toByteArray();
        }

        return buff;
    }

    /*
         2 bytes     2 bytes      n bytes
        ----------------------------------
        | Opcode |   Block #  |   Data     |
        ----------------------------------
*/
    /**
     * Gets an array of bytes for the corresponding block.
     *
     * @param block The block number for this data packet.
     * @param fileName The name of the file.
     * @return DataPacket in bytes.
     * @throws IOException if an I/O error occurs.
     */
    static public byte[] data(int block, String fileName) throws IOException {
        try(ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            DataOutputStream dataOut = new DataOutputStream(byteArray)
        ) {
            dataOut.writeShort(Opcode.DATA.getValue());
            dataOut.writeShort(block);
            byteArray.write(getDataBlock(block, fileName));

            return byteArray.toByteArray();
        }
    }

/*
            2 bytes 2 bytes string 1 byte
        -----------------------------------------
            | Opcode | ErrorCode | ErrMsg | 0 |
        -----------------------------------------
*/
    /**
     * @param errCode Type of error.
     * @param errMsg Error message.
     * @return An array of bytes which from the error packet.
     * @throws IOException if an I/O error occurs.
     */
    static public byte[] error(ErrorCodes errCode, String errMsg) throws IOException {
        try(ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            DataOutputStream dataOut = new DataOutputStream(byteArray)
        ) {
            dataOut.writeShort(Opcode.Error.getValue());
            dataOut.writeShort(errCode.getValue());
            byteArray.write(errMsg.getBytes());
            byteArray.write(0);

            return byteArray.toByteArray();
        }
    }

    /**
     * Saves a file in server's the current directory.
     *
     * @param filename The name of the file.
     * @param data A data packet which forms part of the file which is to be saved.
     * @throws IOException if an I/O error occurs.
     */
    static public void saveFile(String filename, DataPacket data) throws IOException {
        OpenOption[] options = new OpenOption[]{WRITE, CREATE, APPEND};
        Files.write(Paths.get(filename), data.getData(), options);
    }

    /**
     * Checks if a file exists in the server's current directory.
     *
     * @param filename The name of the file.
     * @return true if the file exists, otherwise false.
     */
    static public boolean fileExists(String filename) {
        File f = new File(filename);

        return !f.isDirectory() && f.exists();
    }

    /**
     * The end of a transfer is marked by a DATA packet that contains
     * between 0 and 511 bytes of data (i.e., Datagram length < 516).
     *
     * @param length The length of the data packet.
     * @return true if length < 516, otherwise false.
     */
    static public boolean isLastPacket(int length) {
        return length < 516;
    }

    /**
     * Processes the input stream and returns the packet.
     *
     * @param stream The input stream containing the packet.
     * @return An unpacked packet from the input stream.
     * @throws IOException if an I/O error occurs.
     */
    static public Packet getPacket(InputStream stream) throws IOException {
        DataInputStream dataIn = new DataInputStream(stream);
        Opcode packetType = Opcode.fromInteger(dataIn.readShort());

        Packet packet = switch (packetType) {
            case RRQ, WRQ -> new ReqPacket(dataIn);
            case DATA -> new DataPacket(dataIn);
            case Error -> new ErrorPacket(dataIn);
            default -> throw new IOException("Invalid Packet");
        };

        return packet;
    }

    /**
     * @param msg The message to be logged.
     */
    static public void log(String msg) {
        System.out.println(msg);
    }
}
