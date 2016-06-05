package com.iec.transmitter.common.protocol;

import com.iec.transmitter.common.protocol.constants.APCI_TYPE;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * The main message class
 * Created by radumacovei on 04/06/16.
 */

public class Apdu implements Encodable {
    private int sendSeqNum = 0;

    private int receiveSeqNum = 0;

    private APCI_TYPE apciType;

    private Asdu asdu;

    public Apdu() {
    }

    public Apdu(APCI_TYPE apci_type, Asdu asdu) {
        this.apciType = apci_type;
        this.asdu = asdu;
    }

    public Apdu(APCI_TYPE apci_type) {
        this.apciType = apci_type;
    }

    public Asdu getAsdu() {
        return asdu;
    }

    public void setAsdu(Asdu asdu) {
        this.asdu = asdu;
    }

    public int getSendSeqNum() {
        return sendSeqNum;
    }

    public void setSendSeqNum(int sendSeqNum) {
        this.sendSeqNum = sendSeqNum;
    }

    public int getReceiveSeqNum() {
        return receiveSeqNum;
    }

    public void setReceiveSeqNum(int receiveSeqNum) {
        this.receiveSeqNum = receiveSeqNum;
    }

    public APCI_TYPE getApciType() {
        return apciType;
    }

    public void setApciType(APCI_TYPE apciType) {
        this.apciType = apciType;
    }

    @Override
    public int encode(byte[] buffer, int i) {
        buffer[0] = 0x68;

        int length = 4;

        if (apciType == APCI_TYPE.I_FORMAT) {
            buffer[2] = (byte) (sendSeqNum << 1);
            buffer[3] = (byte) (sendSeqNum >> 7);
            buffer[4] = (byte) (receiveSeqNum << 1);
            buffer[5] = (byte) (receiveSeqNum >> 7);

            length += asdu.encode(buffer, 6);

        } else if (apciType == APCI_TYPE.STARTDT_ACT) {
            buffer[2] = 0x07;
            buffer[3] = 0x00;
            buffer[4] = 0x00;
            buffer[5] = 0x00;
        } else if (apciType == APCI_TYPE.STARTDT_CON) {
            buffer[2] = 0x0b;
            buffer[3] = 0x00;
            buffer[4] = 0x00;
            buffer[5] = 0x00;
        } else if (apciType == APCI_TYPE.S_FORMAT) {
            buffer[2] = 0x01;
            buffer[3] = 0x00;
            buffer[4] = (byte) (receiveSeqNum << 1);
            buffer[5] = (byte) (receiveSeqNum >> 7);
        }

        buffer[1] = (byte) length;

        return length + 2;
    }

}
