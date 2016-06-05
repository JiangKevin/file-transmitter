package com.iec.transmitter.common.protocol;

import com.iec.transmitter.common.protocol.constants.APCI_TYPE;
import com.iec.transmitter.common.protocol.constants.CauseOfTransmission;
import com.iec.transmitter.common.protocol.constants.TypeId;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by radumacovei on 05/06/16.
 */
public class ProtocolHelper {

    public static List<Apdu> getApduList(List<byte[]> data) {
        List<Apdu> apduList = new ArrayList<>();

        int i = 0;
        for (byte[] chunck : data) {
            Apdu apdu = new Apdu(APCI_TYPE.I_FORMAT);
            Asdu asdu = initializeAsdu(chunck, data.size());

            apdu.setAsdu(asdu);

            apduList.add(apdu);
        }

        return apduList;
    }

    private static Asdu initializeAsdu(byte[] chunck, int totalNumberOfChunks) {
        Asdu asdu = new Asdu(chunck);
        asdu.setTypeId(TypeId.C_BO_NA_1);
        asdu.setSequenceOfElements(true);
        asdu.setSequenceLength(totalNumberOfChunks);
        asdu.setCauseOfTransmission(CauseOfTransmission.SPONTANEOUS);
        asdu.setTest(false);
        asdu.setNegativeConfirm(false);
        return asdu;
    }
}
