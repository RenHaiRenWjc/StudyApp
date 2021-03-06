package com.rhr.wjc.studyapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "GuangZhouFC MainActivity";

    private static final int MESSAGE_NEW_BOOK_ARRIVED = 1;
    private IBinder.DeathRecipient mdeath;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NEW_BOOK_ARRIVED:
                    LogUtils.i(TAG, "Android_Test_Wjc handleMessage: book=" + msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    };

    private IBookManager bookManager;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bookManager = IBookManager.Stub.asInterface(service);
            try {
                List<Book> list = bookManager.getBookList();
                LogUtils.i(TAG, "Android_Test_Wjc onServiceConnected: list=" + list.toString());
                bookManager.registerListener(mIOnNewBookArrivedListener);
                service.linkToDeath(mdeath, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.i(TAG, "Android_Test_Wjc onServiceDisconnected: ");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        mdeath = new IBinder.DeathRecipient() {
            @Override
            public void binderDied() {
                LogUtils.i(TAG, "Android_Test_Wjc binderDied: ");
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bookManager != null && bookManager.asBinder().isBinderAlive()) {
            try {
                LogUtils.i(TAG, "Android_Test_Wjc onDestroy: ");
                bookManager.unregisterListener(mIOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mServiceConnection);
    }

    private IOnNewBookArrivedListener mIOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book newBook) throws RemoteException {
            mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, newBook).sendToTarget();
        }
    };


}
