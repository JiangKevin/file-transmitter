package com.iec.transmitter.server;

import com.iec.transmitter.common.Constants;
import com.iec.transmitter.common.exception.TransmitterZipException;
import com.iec.transmitter.common.file.CryptoUtil;
import com.iec.transmitter.common.file.ZipUtil;
import com.iec.transmitter.common.protocol.Apdu;
import com.iec.transmitter.common.protocol.Decoder;
import com.iec.transmitter.common.protocol.constants.APCI_TYPE;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

/**
 * Created by radumacovei on 04/06/16.
 */
public class Server {

    private static final Logger log = LogManager.getLogger(Server.class);

    private static List<Apdu> apduList = new ArrayList<>();

    public static void main(String[] args) throws IOException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, TransmitterZipException, InvalidAlgorithmParameterException, InvalidKeySpecException {

        log.trace("Server starting...");

        // Selector: multiplexor of SelectableChannel objects
        Selector selector = Selector.open(); // selector is open here

        // ServerSocketChannel: selectable channel for stream-oriented listening sockets
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress socketAddress = new InetSocketAddress("localhost", Constants.PORT);

        // Binds the channel's socket to a local address and configures the socket to listen for connections
        serverSocketChannel.bind(socketAddress);

        // Adjusts this channel's blocking mode.
        serverSocketChannel.configureBlocking(false);

        int ops = serverSocketChannel.validOps();
        SelectionKey selectKy = serverSocketChannel.register(selector, ops, null);

        // Infinite loop..
        // Keep server running
        while (true) {

            log.debug("i'm a server and i'm waiting for new connection and buffer select...");
            // Selects a set of keys whose corresponding channels are ready for I/O operations
            selector.select();

            // token representing the registration of a SelectableChannel with a Selector
            Set<SelectionKey> crunchifyKeys = selector.selectedKeys();
            Iterator<SelectionKey> crunchifyIterator = crunchifyKeys.iterator();


            while (crunchifyIterator.hasNext()) {
                SelectionKey myKey = crunchifyIterator.next();

                // Tests whether this key's channel is ready to accept a new socket connection
                if (myKey.isAcceptable()) {
                    SocketChannel client = serverSocketChannel.accept();

                    // Adjusts this channel's blocking mode to false
                    client.configureBlocking(false);

                    // Operation-set bit for read operations
                    client.register(selector, SelectionKey.OP_READ);
                    log.debug("Connection Accepted: " + client.getLocalAddress() + "\n");

                    // Tests whether this key's channel is ready for reading
                } else if (myKey.isReadable()) {

                    SocketChannel client = (SocketChannel) myKey.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(256);
                    int length = client.read(buffer);

                    byte[] data = Arrays.copyOf(buffer.array(), length);

                    Apdu apdu = processData(new DataInputStream(new ByteArrayInputStream(data)));

                    log.debug("Message received size: " + data.length);

                    if (apdu.getApciType().equals(APCI_TYPE.S_FORMAT)) {
                        client.close();
                        log.debug("\nIt's time to close connection as we got last APDU of the day");
                        log.debug("\nServer will keep running. Try running client again to establish new connection");
                        processApduList();
                    } else {
                        apduList.add(apdu);
                    }
                }
                crunchifyIterator.remove();
            }
        }
    }

    //TODO: add more Java 8 flavour -> lambda exprssions and all :)
    private static void processApduList() throws IOException, InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, TransmitterZipException {
        int dataLength = 0;

        List<byte []> dataChunks = new ArrayList<>();

        for(Apdu apdu:apduList) {
            byte [] asduData = apdu.getAsdu().getInformationObject();
            dataLength += asduData.length;
            dataChunks.add(asduData);
        }

        byte [] fileDataBuffer = new byte[dataLength];
        int intermediateLength = 0;
        for(byte[] src:dataChunks) {
            System.arraycopy(src,0,fileDataBuffer,intermediateLength,src.length);
            intermediateLength += src.length;
        }

        Path encryptedFilePath = Paths.get("/Users/radumacovei/projects/file-transmitter/data/server-data/FR000010.enc");
        Path zippedFilePath = Paths.get("/Users/radumacovei/projects/file-transmitter/data/server-data/FR000010.zip");
        Path finalFileFolder = Paths.get("/Users/radumacovei/projects/file-transmitter/data/server-data");

        if(Files.exists(encryptedFilePath)) {
            Files.delete(encryptedFilePath);
        }

        Files.newOutputStream(encryptedFilePath).write(fileDataBuffer);

        CryptoUtil.decrypt(encryptedFilePath, zippedFilePath);

        Path finalFilePath =  ZipUtil.unzip(zippedFilePath, finalFileFolder);

        log.debug("Server side final file size: "+Files.readAllBytes(finalFilePath).length);

        apduList = new ArrayList();
    }

    private static Apdu processData(DataInputStream in) throws IOException {

        Apdu apdu = Decoder.decodeApdu(in);

        return apdu;

    }
}
