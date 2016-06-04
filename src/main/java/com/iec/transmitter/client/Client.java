package com.iec.transmitter.client;


import com.iec.transmitter.common.exception.CryptoException;
import com.iec.transmitter.common.exception.TransmitterZipException;
import com.iec.transmitter.common.file.CryptoUtil;
import com.iec.transmitter.common.file.ZipUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.ClassLoader.getSystemResource;

public class Client {

    private static final Logger log = LogManager.getLogger(Client.class);

    public static void main(String[] args) throws CryptoException, TransmitterZipException, URISyntaxException {
        log.trace("Client starting...");

        Path fileToSend = Paths.get(getSystemResource("fileToSend").toURI());

        Path encryptedFile = Paths.get("pathToEncryptedFile");
        CryptoUtil.encrypt(fileToSend, encryptedFile);

        Path zippedFile = ZipUtil.zip(encryptedFile);

        //build payload

        //encode payload

        //send message(s)

        try {
            Files.delete(encryptedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
