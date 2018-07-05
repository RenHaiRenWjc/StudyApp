// IOnNewBookArrivedListener.aidl
package com.rhr.wjc.studyapp;

import com.rhr.wjc.studyapp.Book;

interface IOnNewBookArrivedListener {

  void onNewBookArrived(in Book newBook);
}
