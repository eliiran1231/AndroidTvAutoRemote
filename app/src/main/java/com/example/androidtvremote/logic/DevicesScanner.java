package com.example.androidtvremote.logic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class DevicesScanner extends AsyncTask<Context, Void, Void> {
    public static final int TITLE_TEXT_SIZE = 26;
    private final String TAG = "DevicesScanner";
    private final String SERVICE_TYPE = "_androidtvremote2._tcp.";
    private ArrayList<AndroidTV> androidTVs;
    private final MainMVP.Presenter presenter;
    private AlertDialog dialog;
    private View.OnClickListener listener;
    private NsdManager nsdManager;
    private NsdManager.DiscoveryListener discoveryListener;


    public DevicesScanner(MainMVP.Presenter presenter, View.OnClickListener listener) {
        androidTVs = new ArrayList<>();
        this.presenter = presenter;
        this.listener = listener;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        presenter.operationStarted();
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        this.nsdManager = (NsdManager) contexts[0].getSystemService(Context.NSD_SERVICE);
        this.discoveryListener = new NsdManager.DiscoveryListener() {

            // Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");

            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                    return;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                nsdManager.resolveService(service, new NsdManager.ResolveListener() {
                    @Override
                    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                        Log.e(TAG, "Resolve failed: " + errorCode);
                    }

                    @Override
                    public void onServiceResolved(NsdServiceInfo serviceInfo) {
                        androidTVs.add(new AndroidTV(serviceInfo.getHost().getHostAddress(), serviceInfo.getServiceName()));
                    }
                });
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                Log.e(TAG, "service lost: " + service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);

            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }
        };
        WifiManager wifi = (WifiManager) contexts[0].getSystemService(Context.WIFI_SERVICE);
        WifiManager.MulticastLock multicastLock = wifi.createMulticastLock("multicastLock");
        multicastLock.setReferenceCounted(true);
        multicastLock.acquire();
        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        nsdManager.stopServiceDiscovery(discoveryListener);
        multicastLock.release(); // release after browsing
        return null;
    }

    @Override
    protected void onPostExecute(Void o) {
        super.onPostExecute(o);
        presenter.DevicesScannerFinished(androidTVs, listener, TITLE_TEXT_SIZE);
    }

}
