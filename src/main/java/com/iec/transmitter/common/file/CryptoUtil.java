package com.iec.transmitter.common.file;

import com.iec.transmitter.common.Constants;
import com.iec.transmitter.common.exception.CryptoException;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

/**
 * Created by radumacovei on 04/06/16.
 */
public class CryptoUtil {

    public static void encrypt(Path inputFile, Path outputFile) throws IOException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {

        if(!Files.exists(outputFile)) {
            Files.createFile(outputFile);
        }

        InputStream inFile = Files.newInputStream(inputFile);
        OutputStream outFile = Files.newOutputStream(outputFile);

        PBEKeySpec pbeKeySpec = new PBEKeySpec(Constants.KEY.toCharArray());
        SecretKeyFactory secretKeyFactory = SecretKeyFactory
                .getInstance("PBEWithMD5AndTripleDES");
        SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);

        byte[] salt = new byte[8];
        Random random = new Random();
        random.nextBytes(salt);

        PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 100);
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndTripleDES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, pbeParameterSpec);
        outFile.write(salt);

        byte[] input = new byte[64];
        int bytesRead;
        while ((bytesRead = inFile.read(input)) != -1) {
            byte[] output = cipher.update(input, 0, bytesRead);
            if (output != null)
                outFile.write(output);
        }

        byte[] output = cipher.doFinal();
        if (output != null)
            outFile.write(output);

        inFile.close();
        outFile.flush();
        outFile.close();
    }

    public static void decrypt(Path inputFile, Path outputFile) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, InvalidKeySpecException {
        PBEKeySpec pbeKeySpec = new PBEKeySpec(Constants.KEY.toCharArray());
        SecretKeyFactory secretKeyFactory = SecretKeyFactory
                .getInstance("PBEWithMD5AndTripleDES");
        SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);

        InputStream fis = Files.newInputStream(inputFile);
        byte[] salt = new byte[8];
        fis.read(salt);

        PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 100);

        Cipher cipher = Cipher.getInstance("PBEWithMD5AndTripleDES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, pbeParameterSpec);
        OutputStream fos = Files.newOutputStream(outputFile);
        byte[] in = new byte[64];
        int read;
        while ((read = fis.read(in)) != -1) {
            byte[] output = cipher.update(in, 0, read);
            if (output != null)
                fos.write(output);
        }

        byte[] output = new byte[0];
        try {
            output = cipher.doFinal();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        if (output != null)
            fos.write(output);

        fis.close();
        fos.flush();
        fos.close();
    }
}
