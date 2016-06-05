package com.iec.transmitter.client;


import com.iec.transmitter.common.Constants;
import com.iec.transmitter.common.FileHandler;
import com.iec.transmitter.common.exception.CryptoException;
import com.iec.transmitter.common.exception.TransmitterZipException;
import com.iec.transmitter.common.protocol.Apdu;
import com.iec.transmitter.common.protocol.Decoder;
import com.iec.transmitter.common.protocol.ProtocolHelper;
import com.iec.transmitter.common.protocol.constants.APCI_TYPE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client {

    private static final Logger log = LogManager.getLogger(Client.class);

    public static void main(String[] args) throws CryptoException, TransmitterZipException, URISyntaxException, IOException, InterruptedException {
        log.trace("Client starting...");

        log.trace("Will send file: " + args[0]);

        Path fileToSend = Paths.get(args[0]);

        log.debug("initial size: "+ Files.readAllBytes(fileToSend).length);

        //TODO: need to make sure that when several files are to be transmitted, if something fails, should continue gracefully
        //TODO: add some try - catch blocks

        Path fileReadyToSend = FileHandler.prepareFileForSending(fileToSend);

        log.debug("file Ready to send: " + fileReadyToSend);
        log.error("final size: "+Files.readAllBytes(fileReadyToSend).length);

        //getList of APDUs

        List<byte[]> dataSegments = FileHandler.getDataSegments(fileReadyToSend);

        List<Apdu> dataToSend = ProtocolHelper.getApduList(dataSegments);
        Apdu endSignal = new Apdu(APCI_TYPE.S_FORMAT);
        dataToSend.add(endSignal);

        //send

        InetSocketAddress socketAddress = new InetSocketAddress("localhost", Constants.PORT);
        SocketChannel socketChannel = SocketChannel.open(socketAddress);

        for (Apdu apdu : dataToSend) {

            byte[] initialBuffer = new byte[256];
            int length = apdu.encode(initialBuffer,0);

//            Apdu apduDecoded = Decoder.decodeApdu(new DataInputStream(new ByteArrayInputStream(initialBuffer)));

            byte [] bufferArray = Arrays.copyOf(initialBuffer, length);

            ByteBuffer buffer = ByteBuffer.wrap(bufferArray);
            socketChannel.write(buffer);

            log.debug("sent data length = "+length);

//            log("sending: " + apdu);
            buffer.clear();

            // wait for 2 seconds before sending next message
            Thread.sleep(2000);
        }
        socketChannel.close();
    }

}
