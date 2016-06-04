package com.iec.transmitter.common.protocol;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by radumacovei on 04/06/16
 */
public interface Encodable {

    int encode(byte[] buffer, int i);
}
