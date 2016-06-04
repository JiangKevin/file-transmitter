package com.iec.transmitter.common;

import com.iec.transmitter.common.exception.TransmitterZipException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by radumacovei on 04/06/16.
 */
public class ZipUtil {

    private static final Logger log = LogManager.getLogger(ZipUtil.class);

    /**
     * Zips a file
     *
     * @param fileToZip - the {@link Path} to the file that is to be zipped
     * @return the {@link Path} to the resulting zip
     */
    public static Path zip(Path fileToZip) throws TransmitterZipException {

        log.debug("Zipping the file " + fileToZip);

        Path zipPath = Paths.get(System.getProperty("java.io.tmpdir"), fileToZip.getFileName().toString());

        ZipOutputStream zos = null;

        try {
            Files.createFile(zipPath);
            log.debug("empty zip file created: " + zipPath);

            zos = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(zipPath)));
            ZipEntry entry = new ZipEntry(fileToZip.getFileName().toString());

            zos.putNextEntry(entry);

            zos.write(Files.readAllBytes(fileToZip));

        } catch (IOException e) {
            throw new TransmitterZipException(e);
        } finally {

            try {
                zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return zipPath;
    }


    public static Path unzip(Path pathToZip, Path pathToDestFolder) throws TransmitterZipException {

        log.debug("Unzipping the file: "+pathToZip+ " to folder "+pathToDestFolder);

        ZipInputStream zis = null;
        OutputStream fos = null;

        Path pathToFile = null;
        try {
            zis = new ZipInputStream((new BufferedInputStream(Files.newInputStream(pathToZip))));
            ZipEntry entry = zis.getNextEntry();

            for (; entry != null; ) {
                pathToFile = Paths.get(pathToDestFolder.toString(), File.separator, entry.getName());

                log.debug("unzipping to: "+pathToFile);

                fos = Files.newOutputStream(pathToFile);

                byte[] buffer = new byte[1024];

                int count;
                while ((count = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }

                zis.closeEntry();
                entry = zis.getNextEntry();
            }

        } catch (IOException e) {
            throw new TransmitterZipException(e);
        } finally {
            try {
                zis.close();
                fos.close();
            } catch (IOException e) {
                throw new TransmitterZipException(e);
            }
        }

        return pathToFile;
    }
}
