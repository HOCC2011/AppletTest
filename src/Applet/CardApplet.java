package Applet;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;


public class CardApplet extends Applet {

    protected CardApplet() {
        register();
    }

    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new CardApplet();
    }

    public void process(APDU apdu) {
        // Good practice: return immediately if this is the selection APDU
        // and you just want to send a custom selection response.
        if (selectingApplet()) {
            byte[] buffer = apdu.getBuffer();

            // Set the custom response payload byte: 0x01
            buffer[0] = (byte) 0x01;

            // Send 1 byte from the buffer starting at index 0.
            // The JCRE will automatically append the success status word 90 00,
            // resulting in a total response of: 01 90 00
            apdu.setOutgoingAndSend((short) 0, (short) 1);
            return;
        }

        byte[] buffer = apdu.getBuffer();
        short lc = apdu.setIncomingAndReceive();

        // Echo back incoming data for any subsequent APDUs
        apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, lc);
    }
}