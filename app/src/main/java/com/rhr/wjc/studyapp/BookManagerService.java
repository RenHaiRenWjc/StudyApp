package com.rhr.wjc.studyapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ClassName:com.rhr.wjc.studyapp
 * Description:
 * author:wjc on 18-7-5 20:36
 */

public class BookManagerService extends Service {
    private static final String TAG = "GuangZhouFC BookManagerService";

    private AtomicBoolean mIsServiceDestoryed = new AtomicBoolean(false);

    private CopyOnWriteArrayList<Book> mBooks = new CopyOnWriteArrayList<>();//支持并发读/写
    private CopyOnWriteArrayList<IOnNewBookArrivedListener> mOnNewBookArrivedListeners = new CopyOnWriteArrayList<>();

    private Binder mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBooks;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBooks.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            if (!mOnNewBookArrivedListeners.contains(listener)) {
                mOnNewBookArrivedListeners.add(listener);
            } else {
                LogUtils.i(TAG, "Android_Test_Wjc registerListener: exists");
            }
            LogUtils.i(TAG, "Android_Test_Wjc registerListener: size=" + mOnNewBookArrivedListeners.size());
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            if (mOnNewBookArrivedListeners.contains(listener)) {
                mOnNewBookArrivedListeners.remove(listener);
                LogUtils.i(TAG, "Android_Test_Wjc unregisterListener: success");
            }
            LogUtils.i(TAG, "Android_Test_Wjc unregisterListener: size=" + mOnNewBookArrivedListeners.size());
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBooks.add(new Book(1, "android"));
        mBooks.add(new Book(2, "jave"));
        new Thread(new ServiceWorker()).start();
    }

    @Override
    public void onDestroy() {
        mIsServiceDestoryed.set(true);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void notifyNewBookArrived(Book book) throws RemoteException {
        mBooks.add(book);
        for (int i = 0; i < mOnNewBookArrivedListeners.size(); i++) {
            LogUtils.i(TAG, "Android_Test_Wjc notifyNewBookArrived: ");
            IOnNewBookArrivedListener listener = mOnNewBookArrivedListeners.get(i);
            listener.onNewBookArrived(book);
        }
    }

    private class ServiceWorker implements Runnable {

        @Override
        public void run() {
            while (!mIsServiceDestoryed.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = mBooks.size() + 1;
                Book newBook = new Book(bookId, "new book-" + bookId);
                try {
                    LogUtils.i(TAG, "Android_Test_Wjc run: ");
                    notifyNewBookArrived(newBook);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
