// IMyAidlInterface.aidl
package com.rhr.wjc.studyapp;

import com.rhr.wjc.studyapp.Book;
interface IBookManager {
  List<Book> getBookList();
  void addBook(in Book book);

}
