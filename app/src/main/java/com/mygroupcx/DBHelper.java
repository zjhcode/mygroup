package com.mygroupcx;

import android.content.Context;

import io.objectbox.Box;
import io.objectbox.BoxStore;

public enum DBHelper {
    INSTANCE;

    private BoxStore boxStore;
    public Box<MemberEntity> memberEntityBox;

    public void init(Context context)
    {
        boxStore = MyObjectBox.builder().androidContext(context).build();

        memberEntityBox = boxStore.boxFor(MemberEntity.class);
    }
}
