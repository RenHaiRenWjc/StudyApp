package com.rhr.wjc.studyapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ClassName:com.rhr.wjc.studyapp
 * Description:
 * author:wjc on 18-7-5 00:02
 */

public class Book implements Parcelable {
    public int bookId;
    public String bookName;


    public Book(int bookId, String bookName) {
        this.bookId = bookId;
        this.bookName = bookName;
    }

    private Book(Parcel in) {
        bookId = in.readInt();
        bookName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(bookId);
        out.writeString(bookName);
    }


    public static final Parcelable.Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", bookName='" + bookName + '\'' +
                '}';
    }
}
