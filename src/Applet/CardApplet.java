package Applet;

import javacard.framework.*;


public class CardApplet extends Applet {

    private byte[] counter;

    protected CardApplet() {
        counter = new byte[4];
    }

    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new CardApplet().register();
    }

    public void process(APDU apdu) {
        // Good practice: return immediately if this is the selection APDU
        // and you just want to send a custom selection response.
        if (selectingApplet()) {
            byte[] buffer = apdu.getBuffer();

            JCSystem.beginTransaction();

            incrementCounter(counter);

            JCSystem.commitTransaction();

            // Set the custom response payload byte: 0x01
            Util.arrayCopyNonAtomic(counter, (short)0, buffer, (short) 0, (short) 4);

            // Send 1 byte from the buffer starting at index 0.
            // The JCRE will automatically append the success status word 90 00,
            // resulting in a total response of: 01 90 00
            apdu.setOutgoingAndSend((short) 0, (short) 4);
            return;
        }

        byte[] buffer = apdu.getBuffer();
        short lc = apdu.setIncomingAndReceive();

        // Echo back incoming data for any subsequent APDUs
        apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, lc);
    }

    private void incrementCounter(byte[] cnt) {
        for (short i = 3; i >= 0; i--) {
            short val = (short) ((cnt[i] & 0xFF) + 1);
            cnt[i] = (byte) val;
            if (val <= 0xFF) break;
        }
    }
}