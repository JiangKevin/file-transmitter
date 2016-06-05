package com.iec.transmitter.client;


import com.iec.transmitter.common.Constants;
import com.iec.transmitter.common.FileHandler;
import com.iec.transmitter.common.exception.CryptoException;
import com.iec.transmitter.common.exception.TransmitterZipException;
import com.iec.transmitter.common.protocol.Apdu;
import com.iec.transmitter.common.protocol.ProtocolHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client {

    private static final Logger log = LogManager.getLogger(Client.class);

    public static void main(String[] args) throws CryptoException, TransmitterZipException, URISyntaxException, IOException {
        log.trace("Client starting...");

        log.trace("Will send file: " + args[0]);

        Path fileToSend = Paths.get(args[0]);

        log.debug("initial size: "+ Files.readAllBytes(fileToSend).length);

        //TODO: need to make sure that when several files are to be transmitted, if something fails, should continue gracefully
        //TODO: add some try - catch blocks

        Path fileReadyToSend = FileHandler.prepareFileForSending(fileToSend);

        log.debug("file Ready to send: " + fileReadyToSend);
        log.debug("final size: "+Files.readAllBytes(fileReadyToSend).length);

        //getList of APDUs

        List<byte[]> dataSegments = FileHandler.getDataSegments(fileReadyToSend);

        List<Apdu> dataToSend = ProtocolHelper.getApduList(dataSegments);

        //send

        Socket clientSocket = new Socket("127.0.0.1", Constants.PORT);

        OutputStream out = clientSocket.getOutputStream();

        for(Apdu apdu: dataToSend) {
            byte[] buffer = new byte[255];
            int length = apdu.encode(buffer, 0);
            out.write(buffer, 0, length);
            out.flush();
        }

        out.close();

        clientSocket.close();
    }

}
