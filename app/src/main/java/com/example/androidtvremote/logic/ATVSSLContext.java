package com.example.androidtvremote.logic;

import android.content.Context;

import com.example.androidtvremote.R;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ATVSSLContext {
    private static SSLContext instance;
    private static Certificate certificate;

    private ATVSSLContext() {
    }


    public static SSLContext getInstance(Context context) {
        if (instance == null) {
            createATVSSLContext(context);
        }
        return instance;
    }

    public static Certificate getCertificate() {
        return certificate;
    }

    private static void createATVSSLContext(Context context) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManager[] trustManagers = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                                throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                                throws CertificateException {
                        }
                    }
            };
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
            InputStream keyStoreStream = context.getResources().openRawResource(R.raw.thebest);
            keyStore.load(keyStoreStream, "123456".toCharArray());
            keyManagerFactory.init(keyStore, "123456".toCharArray());
            certificate = keyStore.getCertificate(ATVSSLContext.class.getPackage().getName().replace(".logic", ""));
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagers, null);
            instance = sslContext;
        } catch (UnrecoverableKeyException | CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException | KeyManagementException e) {
        }
    }
}
