package com.mobile.alliance.activity;

import android.graphics.drawable.Drawable;
import android.media.Image;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class DeliveryListViewItem {
    private int colorCode;
    private String seq;
    private Drawable img01;
    private Drawable img02;
    private Drawable img03;

    //20211205 추가
    private String text03;

    private Drawable img04;
    private String state;
    private String soNo;
    private String name;
    private String addr1;
    private String addr2;
    private String hp;


    private String tblUserMId;

    private String instMobileMId;
    private String telCnt;
    private int no;

}
