package com.example.androidtvremote.logic;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;

public class PairingManager extends AsyncTask<Certificate, Void, Void> {

    private static final byte[] PAIRING_MSG_HEADER = {42, 8, 2, 16, (byte) 200, 1, (byte) 194, 2, 34, 10, 32};
    private static final byte[] PAIRING_MSG = {8, 2, 16, (byte) 200, 1, 82, 43, 10, 21, 105, 110, 102, 111, 46, 107, 111, 100, 111, 110, 111, 46, 97, 115, 115, 105, 115, 116, 97, 110, 116, 18, 13, 105, 110, 116, 101, 114, 102, 97, 99, 101, 32, 119, 101, 98};
    private static final int PAIRING_PORT = 6467;
    private static final byte[] OPTION_MSG = {8, 2, 16, (byte) 200, 1, (byte) 162, 1, 8, 10, 4, 8, 3, 16, 6, 24, 1};
    private final byte[] CONFIGURATION_MSG = {8, 2, 16, (byte) 200, 1, (byte) 242, 1, 8, 10, 4, 8, 3, 16, 6, 16, 1};
    private final String ip;
    private final MainMVP.Presenter presenter;
    private Certificate certificate;
    private PublicKey serverPublicKey;
    private final SSLContext sslContext;
    private OutputStream socket;
    private ATVRemote remote;
    private final String registeredIps;


    public PairingManager(MainMVP.Presenter activity, SSLContext sslContext, String ip, String registeredIps) {
        this.presenter = activity;
        this.ip = ip;
        this.sslContext = sslContext;
        this.registeredIps = registeredIps;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(!registeredIps.contains(ip))presenter.operationStarted();
    }

    @Override
    protected Void doInBackground(Certificate... certificates) {
        if(registeredIps.contains(ip)){
            remote = new ATVRemote(sslContext, ip);
            remote.execute(presenter);
            return null;
        }
        this.certificate = certificates[0];
        try {
            SSLSocket s = (SSLSocket) sslContext.getSocketFactory().createSocket(ip, PAIRING_PORT);
            s.startHandshake();
            this.serverPublicKey = s.getSession().getPeerCertificates()[0].getPublicKey();
            this.socket = s.getOutputStream();

            socket.write(new byte[]{(byte) PAIRING_MSG.length});
            socket.write(PAIRING_MSG);

            socket.write(new byte[]{(byte) OPTION_MSG.length});
            socket.write(OPTION_MSG);

            socket.write(new byte[]{(byte) CONFIGURATION_MSG.length});
            socket.write(CONFIGURATION_MSG);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        if(!registeredIps.contains(ip)) presenter.PairingMangerSetupFinished();
    }

    public void finishPairing(String code) {
        new Thread(() -> {
            byte[] one = PAIRING_MSG_HEADER;
            byte[] two = null;
            try {
                two = computeAlphaValue(code);
            } catch (CertificateException e) {
                e.printStackTrace();
            }
            byte[] combined = new byte[one.length + two.length];

            for (int i = 0; i < combined.length; ++i) {
                combined[i] = i < one.length ? one[i] : two[i - one.length];
            }
            try {
                socket.write(combined);
                socket.flush();
                socket.close();
                remote = new ATVRemote(sslContext, ip);
                remote.execute(presenter);
                presenter.pairingFinished(ip);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private byte[] computeAlphaValue(String text) throws CertificateException {
        X509Certificate certificate = (X509Certificate) this.certificate;
        PublicKey publicKey = certificate.getPublicKey();
        PublicKey publicKey2 = this.serverPublicKey;
        if (!(publicKey instanceof RSAPublicKey) || !(publicKey2 instanceof RSAPublicKey)) {
            Log.e("error", "Expecting RSA public key");
            return null;
        }
        RSAPublicKey rSAPublicKey = (RSAPublicKey) publicKey;
        RSAPublicKey rSAPublicKey2 = (RSAPublicKey) publicKey2;
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-256");
            byte[] bArr = new BigInteger(text.substring(2), 16).toByteArray();
            bArr = removeLeadingNullBytes(bArr);
            byte[] byteArray = rSAPublicKey.getModulus().abs().toByteArray();
            byte[] byteArray2 = rSAPublicKey.getPublicExponent().abs().toByteArray();
            byte[] byteArray3 = rSAPublicKey2.getModulus().abs().toByteArray();
            byte[] byteArray4 = rSAPublicKey2.getPublicExponent().abs().toByteArray();
            byte[] removeLeadingNullBytes = removeLeadingNullBytes(byteArray);
            byte[] removeLeadingNullBytes2 = removeLeadingNullBytes(byteArray2);
            byte[] removeLeadingNullBytes3 = removeLeadingNullBytes(byteArray3);
            byte[] removeLeadingNullBytes4 = removeLeadingNullBytes(byteArray4);
            instance.update(removeLeadingNullBytes);
            instance.update(removeLeadingNullBytes2);
            instance.update(removeLeadingNullBytes3);
            instance.update(removeLeadingNullBytes4);
            instance.update(bArr);
            var a = instance.digest();
            instance.update(new byte[]{1, 2, 3}); //this has no meaning android studio is weird and forced me into this solution
            return a;
        } catch (NoSuchAlgorithmException unused) {
            Log.e("error", "no sha-256 implementation");
            return null;
        }
    }

    private byte[] removeLeadingNullBytes(byte[] byteArray) {
        int i = 0;
        while (byteArray[i] == 0) {
            i++;
        }
        byte[] arr = new byte[byteArray.length - i];
        for (int x = i, y = 0; x < byteArray.length; x++, y++) {
            arr[y] = byteArray[x];
        }
        return arr;
    }

    public ATVRemote getRemote() {
        return this.remote;
    }
}