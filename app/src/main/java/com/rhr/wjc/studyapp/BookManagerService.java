package com.rhr.wjc.studyapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ClassName:com.rhr.wjc.studyapp
 * Description:
 * author:wjc on 18-7-5 20:36
 */

public class BookManagerService extends Service {

    private CopyOnWriteArrayList<Book> mBooks = new CopyOnWriteArrayList<>();//支持并发读/写
    private Binder mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBooks;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBooks.add(book);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBooks.add(new Book(1, "android"));
        mBooks.add(new Book(2, "jave"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


}
