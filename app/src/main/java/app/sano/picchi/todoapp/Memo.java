package app.sano.picchi.todoapp;

import io.realm.RealmObject;

public class Memo extends RealmObject {
    //タイトル
    public String title;
    //日付
    public String updateDate;
    //内容
    public String content;
    //チェックボックス
    public boolean ischeck;

    public byte[] bitmap;

}