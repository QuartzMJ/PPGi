package com.remi.navidrawer;

import java.util.ArrayList;

public class PagesContainer {
   private int mPagePics;
   private String mPageTitles ;
   private String mPageTexts ;
   private String mPageAdditionalTexts;
   private String[] mPageLinks ;

   public PagesContainer(){
   }

   public PagesContainer(int pic,String title,String text, String additionalText,String[] links)
   {
       mPagePics = pic;
       mPageTitles = title;
       mPageTexts = text;
       mPageAdditionalTexts = additionalText;
       mPageLinks = links;
   }

   public void addPage(int page,String title,String text, String additionalText,String[] links)
   {
       mPagePics = page;
       mPageTitles = title;
       mPageAdditionalTexts = additionalText;
       mPageLinks = links;
   }

   public int getPagePic(){
       return mPagePics;
   }

   public String getPageText(){
       return mPageTexts;
   }

   public String getPageAdditionalText(){
       return mPageAdditionalTexts;
   }

   public String getPageTitle(){
       return mPageTitles;
    }

    public  String[] getPageLinks(){
       return mPageLinks;
    }

}
