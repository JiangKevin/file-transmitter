package com.iec.transmitter.common;

import com.iec.transmitter.common.exception.CryptoException;
import com.iec.transmitter.common.exception.TransmitterZipException;
import com.iec.transmitter.common.file.CryptoUtil;
import com.iec.transmitter.common.file.ZipUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by radumacovei on 04/06/16.
 */
public class FileHandler {

    private static final Logger log = LogManager.getLogger(FileHandler.class);

    public static Path prepareFileForSending(Path fileToSend) throws CryptoException, TransmitterZipException, IOException {

        String tempDir = "/Users/radumacovei/projects/file-transmitter/data/client-data";
//        String tempDir = System.getProperty("java.io.tmpdir");

        String encryptedFileName = getFileNameWithExtension(fileToSend.getFileName().toString(), "enc");

        String zippedFileName = getFileNameWithExtension(fileToSend.getFileName().toString(), "zip");

        Path fileToEncrypt = ZipUtil.zip(fileToSend, zippedFileName);

        Path encryptedFile = Paths.get(tempDir, encryptedFileName);

        if(Files.exists(encryptedFile)) {
            Files.delete(encryptedFile);
        }

        try {
            CryptoUtil.encrypt(fileToEncrypt, encryptedFile);

        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        log.debug("encrypted file to :" + encryptedFile.toString());

        return encryptedFile;

    }

    private static String getFileNameWithExtension(String oldFileName, String extension) {

        return oldFileName.substring(0,oldFileName.lastIndexOf('.') + 1) + extension;
    }

    private static void toImplementOnServer() {
//        try {
//            CryptoUtil.decrypt(encryptedFile, controlFile);
//
//        } catch (InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        } catch (InvalidKeySpecException e) {
//            e.printStackTrace();
//        }
    }
}
