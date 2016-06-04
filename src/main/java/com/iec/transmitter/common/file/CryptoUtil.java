package com.iec.transmitter.common.file;

import com.iec.transmitter.common.Constants;
import com.iec.transmitter.common.exception.CryptoException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * Created by radumacovei on 04/06/16.
 */
public class CryptoUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    public static void encrypt(Path inputFile, Path outputFile)
            throws CryptoException {
        doCrypto(Cipher.ENCRYPT_MODE, inputFile, outputFile);
    }

    public static void decrypt(Path inputFile, Path outputFile)
            throws CryptoException {
        doCrypto(Cipher.DECRYPT_MODE,inputFile, outputFile);
    }

    private static void doCrypto(int cipherMode, Path inputFile,
                                 Path outputFile) throws CryptoException {
        try {
            Key secretKey = new SecretKeySpec(Constants.KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);

            Files.newOutputStream(outputFile).write(cipher.doFinal(Files.readAllBytes(inputFile)));

        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }
}
