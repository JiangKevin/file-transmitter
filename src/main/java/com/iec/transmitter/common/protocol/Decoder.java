package com.iec.transmitter.common.protocol;

import com.iec.transmitter.common.protocol.constants.APCI_TYPE;
import com.iec.transmitter.common.protocol.constants.CauseOfTransmission;
import com.iec.transmitter.common.protocol.constants.TypeId;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by radumacovei on 04/06/16.
 */
public class Decoder {

    private static Asdu decodeAsdu(DataInputStream is, int aSduLength) throws IOException {

        Asdu asdu = new Asdu();

        int typeIdCode = (is.readByte() & 0xff);

        asdu.setTypeId(TypeId.getInstance(typeIdCode));

        if (asdu.getTypeId() == null) {
            throw new IOException("Unknown Type Identification: " + typeIdCode);
        }

        int currentByte = (is.readByte() & 0xff);

        asdu.setSequenceOfElements((currentByte & 0x80) == 0x80);

        asdu.setSequenceLength(currentByte & 0x7f);

        currentByte = (is.readByte() & 0xff);
        asdu.setCauseOfTransmission(CauseOfTransmission.getInstance(currentByte & 0x3f));
        asdu.setTest((currentByte & 0x80) == 0x80);
        asdu.setNegativeConfirm((currentByte & 0x40) == 0x40);

        asdu.setOriginatorAddress(-1);

        asdu.setCommonAddress(is.readByte() & 0xff);

        asdu.setInformationObject(new byte[aSduLength - 4]);
        is.readFully(asdu.getInformationObject());

        return asdu;
    }

    public static Apdu decodeApdu(DataInputStream is) throws IOException {

        Apdu apdu = new Apdu();

        int length = is.readByte() & 0xff;

        if (length < 4 || length > 253) {
            throw new IOException("APDU contains invalid length: " + length);
        }

        byte[] aPduHeader = new byte[4];
        is.readFully(aPduHeader);

        if ((aPduHeader[0] & 0x01) == 0) {
            apdu.setApciType(APCI_TYPE.I_FORMAT);
            apdu.setSendSeqNum(((aPduHeader[0] & 0xfe) >> 1) + ((aPduHeader[1] & 0xff) << 7));
            apdu.setReceiveSeqNum(((aPduHeader[2] & 0xfe) >> 1) + ((aPduHeader[3] & 0xff) << 7));

            apdu.setAsdu(Decoder.decodeAsdu(is, length - 4));

        } else if ((aPduHeader[0] & 0x02) == 0) {
            apdu.setApciType(APCI_TYPE.S_FORMAT);
            apdu.setReceiveSeqNum(((aPduHeader[2] & 0xfe) >> 1) + ((aPduHeader[3] & 0xff) << 7));
        } else {
            if (aPduHeader[0] == (byte) 0x83) {
                apdu.setApciType(APCI_TYPE.TESTFR_CON);
            } else if (aPduHeader[0] == 0x43) {
                apdu.setApciType(APCI_TYPE.TESTFR_ACT);
            } else if (aPduHeader[0] == 0x23) {
                apdu.setApciType(APCI_TYPE.STOPDT_CON);
            } else if (aPduHeader[0] == 0x13) {
                apdu.setApciType(APCI_TYPE.STOPDT_ACT);
            } else if (aPduHeader[0] == 0x0B) {
                apdu.setApciType(APCI_TYPE.STARTDT_CON);
            } else {
                apdu.setApciType(APCI_TYPE.STARTDT_ACT);
            }
        }

        return apdu;
    }
}
