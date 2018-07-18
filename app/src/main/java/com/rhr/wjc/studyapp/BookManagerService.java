package com.rhr.wjc.studyapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
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
    // private CopyOnWriteArrayList<IOnNewBookArrivedListener> mOnNewBookArrivedListeners = new CopyOnWriteArrayList<>();
    // [wjc on 18-7-18 下午9:58] delete 跨进程 listener interface
    private RemoteCallbackList<IOnNewBookArrivedListener> mOnNewBookArrivedListeners = new RemoteCallbackList<>();

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
            mOnNewBookArrivedListeners.register(listener);
            // [wjc on 18-7-19 上午12:24] beginBroadcast()/finishBroadcast()---配对使用
            int N = mOnNewBookArrivedListeners.beginBroadcast();
            mOnNewBookArrivedListeners.finishBroadcast();
            LogUtils.i(TAG, "Android_Test_Wjc register Listener: N=" + N);
            LogUtils.i(TAG, "Android_Test_Wjc onCreate: currentThread=" + Thread.currentThread());
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mOnNewBookArrivedListeners.unregister(listener);
            int N = mOnNewBookArrivedListeners.beginBroadcast();
            mOnNewBookArrivedListeners.finishBroadcast();
            LogUtils.i(TAG, "Android_Test_Wjc register Listener: N=" + N);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBooks.add(new Book(1, "android"));
        LogUtils.i(TAG, "Android_Test_Wjc onCreate: currentThread=" + Thread.currentThread());
        // mBooks.add(new Book(2, "jave"));
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
        final int N = mOnNewBookArrivedListeners.beginBroadcast();
        LogUtils.i(TAG, "Android_Test_Wjc notifyNewBookArrived: N=" + N);
        for (int i = 0; i < N; i++) {
            IOnNewBookArrivedListener listener = mOnNewBookArrivedListeners.getBroadcastItem(i);
            if (listener != null) {
                try {
                    listener.onNewBookArrived(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        mOnNewBookArrivedListeners.finishBroadcast();
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
