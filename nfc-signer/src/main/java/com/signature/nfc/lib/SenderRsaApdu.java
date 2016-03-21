package com.signature.nfc.lib;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

public class SenderRsaApdu {


    public static byte[] SendAPDU (Intent intent, String message, String password, String keyId) throws IOException {
        byte[] APDUSelectCommand = hexStringToByteArray("00A404000763727970746F64");
        if (intent != null) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            IsoDep isoDep = IsoDep.get(tag);
            if (isoDep == null) {
                throw new IOException("could not get isoDep");
            }

            isoDep.connect();
            byte[] resultSelect = isoDep.transceive(APDUSelectCommand);
            if (!(resultSelect[resultSelect.length - 2] == (byte) 0x90 && resultSelect[resultSelect.length - 1] == (byte) 0x00)) {
                isoDep.close();
                throw new IOException("could not select applet");
            }

            if (password != null && !password.equals("")) {
                byte[] APDUSendPinCommand = hexStringToByteArray(generatePinStr(password));
                byte[] resultPin = isoDep.transceive(APDUSendPinCommand);
                if (!(resultPin[resultPin.length - 2] == (byte) 0x90 && resultPin[resultPin.length - 1] == (byte) 0x00)) {
                    isoDep.close();
                    throw new IOException("pin is incorrect");
                }
            }

            byte[] request = generateApduToSign(message, keyId);
            byte[] resultSend = isoDep.transceive(request);

            isoDep.close();
            if (!(resultSend[resultSend.length - 2] == (byte) 0x90 && resultSend[resultSend.length - 1] == (byte) 0x00)) {
                throw new IOException("could not send command");
            }
            return Arrays.copyOf(resultSend, resultSend.length - 2);

        }
        throw new IOException("could not get intent");
    }

    private static byte[] generateApduToSign(String message, String keyId){
        String requisitesInHex = textToHex(message);
        int numOfBytesInHex = requisitesInHex.getBytes().length / 2;
        StringBuilder stringBuilder = new StringBuilder("002800");
        stringBuilder.append(String.format("%02X", Byte.parseByte(keyId)));
        stringBuilder.append(String.format("%02X", numOfBytesInHex));
        stringBuilder.append(requisitesInHex);
        String apduCommand = new String(stringBuilder);
        return hexStringToByteArray(apduCommand);
    }

    private static String textToHex(String arg) {
        return String.format("%02X", new BigInteger(1, arg.getBytes()));
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private static String generatePinStr(String pin) {
        String length = String.format("%02X", pin.length() / 2 + 1);
        return "00120002" +length + "F" + Integer.toHexString(pin.length()) + pin;
    }
}

