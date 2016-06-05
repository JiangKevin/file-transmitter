package com.iec.transmitter.server;

import com.iec.transmitter.common.Constants;
import com.iec.transmitter.common.protocol.Apdu;
import com.iec.transmitter.common.protocol.Decoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by radumacovei on 04/06/16.
 */
public class Server {

    private static final Logger log = LogManager.getLogger(Server.class);

    public static void main(String[] args) throws IOException {

        log.trace("Server starting...");

        int bytesRead;
        int current = 0;

        ServerSocket serverSocket = null;
        serverSocket = new ServerSocket(Constants.PORT);

        List<Apdu> apduList = new ArrayList<>();

        Socket clientSocket = null;
        clientSocket = serverSocket.accept();

        InputStream in = clientSocket.getInputStream();

        Apdu apdu = null;

        while ((apdu = processData(new DataInputStream(in))) != null) {

            apduList.add(apdu);
        }

    }

    private static Apdu processData(DataInputStream in) throws IOException {

        Apdu apdu = Decoder.decodeApdu(in);

        return apdu;

    }
}
