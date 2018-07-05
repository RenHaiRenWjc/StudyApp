// IMyAidlInterface.aidl
package com.rhr.wjc.studyapp;

import com.rhr.wjc.studyapp.Book;
import com.rhr.wjc.studyapp.IOnNewBookArrivedListener;

interface IBookManager {
  List<Book> getBookList();
  void addBook(in Book book);
  void registerListener(IOnNewBookArrivedListener listener);
  void unregisterListener(IOnNewBookArrivedListener listener);
}
