package io.github.sandydunlop.newsfeed.app;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import com.rometools.rome.feed.synd.SyndEntry;



public class Article {
    public String title;
    public String description;
    public String link;
    public Date date;
    public long revceivedDate;
    public SyndEntry syndEntry;
    private String key = null;


    public static Article of(SyndEntry entry) {
        String title = "";
        String description = "";
        if (entry.getTitle() != null) {
            title = entry.getTitle();
        }
        if (entry.getDescription() != null) {
            description = entry.getDescription().getValue();
        }
        return new Article(title, description, entry.getLink(), entry.getPublishedDate(), entry);
    }


    public static Article empty() {
        return new Article();
    }


    private Article() {
        this.title = "";
        this.description = "";
        this.link = "";
    }


    private Article(String title, String description, String link, Date date, SyndEntry syndEntry) {
        this.syndEntry = syndEntry;
        this.title = title;
        this.description = description;
        this.link = link;
        this.date = date;
        this.revceivedDate = new Date().getTime();
    }


    public SyndEntry getEntry() {
        return syndEntry;
    }


    public String getKey() {
        if (key == null) {
            key = getMd5(title + link + date.toString());
        }
        return key;
    }   


    public static String getMd5(String input) {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            // of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException ignore){
            return "";
        }
    }    

}
