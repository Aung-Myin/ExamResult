package com.mgngoelay.examresult;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;

public class MyanmarString {

    public static SpannableString get(String myanmarText,Typeface typeface,int color){
        SpannableString mmString = new SpannableString(myanmarText);
        mmString.setSpan(new CustomSpanTypeface("" , typeface,color), 0 , mmString.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return mmString;
    }
}