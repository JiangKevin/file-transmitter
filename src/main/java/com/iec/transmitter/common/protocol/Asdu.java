package com.iec.transmitter.common.protocol;

import com.iec.transmitter.common.protocol.constants.CauseOfTransmission;
import com.iec.transmitter.common.protocol.constants.TypeId;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by radumacovei on 04/06/16.
 */
public class Asdu implements Encodable {

    private TypeId typeId;
    private boolean isSequenceOfElements;
    private CauseOfTransmission causeOfTransmission;
    private boolean test;
    private boolean negativeConfirm;
    private int originatorAddress;
    private int commonAddress;
    private int sequenceLength;
    /**
     * maximum 127 elements
     */
    private byte[] informationObject;

    public Asdu() {
    }

    public Asdu(byte [] data) {
        this.informationObject = data;
    }

    public TypeId getTypeId() {
        return typeId;
    }

    public void setTypeId(TypeId typeId) {
        this.typeId = typeId;
    }

    public boolean isSequenceOfElements() {
        return isSequenceOfElements;
    }

    public void setSequenceOfElements(boolean sequenceOfElements) {
        isSequenceOfElements = sequenceOfElements;
    }

    public CauseOfTransmission getCauseOfTransmission() {
        return causeOfTransmission;
    }

    public void setCauseOfTransmission(CauseOfTransmission causeOfTransmission) {
        this.causeOfTransmission = causeOfTransmission;
    }

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    public boolean isNegativeConfirm() {
        return negativeConfirm;
    }

    public void setNegativeConfirm(boolean negativeConfirm) {
        this.negativeConfirm = negativeConfirm;
    }

    public int getOriginatorAddress() {
        return originatorAddress;
    }

    public void setOriginatorAddress(int originatorAddress) {
        this.originatorAddress = originatorAddress;
    }

    public int getCommonAddress() {
        return commonAddress;
    }

    public void setCommonAddress(int commonAddress) {
        this.commonAddress = commonAddress;
    }

    public int getSequenceLength() {
        return sequenceLength;
    }

    public void setSequenceLength(int sequenceLength) {
        this.sequenceLength = sequenceLength;
    }

    public byte[] getInformationObject() {
        return informationObject;
    }

    public void setInformationObject(byte[] informationObject) {
        this.informationObject = informationObject;
    }

    @Override
    public int encode(byte[] buffer, int i) {

        int origi = i;

        buffer[i++] = (byte) typeId.getId();
        if (isSequenceOfElements) {
            buffer[i++] = (byte) (sequenceLength | 0x80);
        } else {
            buffer[i++] = (byte) sequenceLength;
        }

        if (test) {
            if (negativeConfirm) {
                buffer[i++] = (byte) (causeOfTransmission.getId() | 0xC0);
            } else {
                buffer[i++] = (byte) (causeOfTransmission.getId() | 0x80);
            }
        } else {
            if (negativeConfirm) {
                buffer[i++] = (byte) (causeOfTransmission.getId() | 0x40);
            } else {
                buffer[i++] = (byte) causeOfTransmission.getId();
            }
        }

        buffer[i++] = (byte) commonAddress;


        System.arraycopy(informationObject, 0, buffer, i, informationObject.length);

        return (i+informationObject.length) - origi;
    }

    public void decode(DataInputStream is, int aSduLength) throws IOException {
        int typeIdCode = (is.readByte() & 0xff);

        typeId = TypeId.getInstance(typeIdCode);

        if (typeId == null) {
            throw new IOException("Unknown Type Identification: " + typeIdCode);
        }

        int currentByte = (is.readByte() & 0xff);

        isSequenceOfElements = (currentByte & 0x80) == 0x80;

        sequenceLength = currentByte & 0x7f;

        currentByte = (is.readByte() & 0xff);
        causeOfTransmission = CauseOfTransmission.getInstance(currentByte & 0x3f);
        test = (currentByte & 0x80) == 0x80;
        negativeConfirm = (currentByte & 0x40) == 0x40;

        originatorAddress = -1;

        commonAddress = (is.readByte() & 0xff);

        informationObject = new byte[aSduLength - 4];
        is.readFully(informationObject);
    }

    @Override
    public String toString() {
        return "Asdu{" +
                "typeId=" + typeId +
                ", isSequenceOfElements=" + isSequenceOfElements +
                ", causeOfTransmission=" + causeOfTransmission +
                ", test=" + test +
                ", negativeConfirm=" + negativeConfirm +
                ", originatorAddress=" + originatorAddress +
                ", commonAddress=" + commonAddress +
                ", sequenceLength=" + sequenceLength +
                ", informationObject=" + Arrays.toString(informationObject) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Asdu)) return false;

        Asdu asdu = (Asdu) o;

        if (isSequenceOfElements != asdu.isSequenceOfElements) return false;
        if (test != asdu.test) return false;
        if (negativeConfirm != asdu.negativeConfirm) return false;
        if (originatorAddress != asdu.originatorAddress) return false;
        if (commonAddress != asdu.commonAddress) return false;
        if (sequenceLength != asdu.sequenceLength) return false;
        if (typeId != asdu.typeId) return false;
        if (causeOfTransmission != asdu.causeOfTransmission) return false;
        return Arrays.equals(informationObject, asdu.informationObject);

    }

    @Override
    public int hashCode() {
        int result = typeId.hashCode();
        result = 31 * result + (isSequenceOfElements ? 1 : 0);
        result = 31 * result + causeOfTransmission.hashCode();
        result = 31 * result + (test ? 1 : 0);
        result = 31 * result + (negativeConfirm ? 1 : 0);
        result = 31 * result + originatorAddress;
        result = 31 * result + commonAddress;
        result = 31 * result + sequenceLength;
        result = 31 * result + Arrays.hashCode(informationObject);
        return result;
    }
}
