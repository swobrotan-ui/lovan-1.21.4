package net;

import b.Native;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import data.TranslationResult;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Native
public class TranslationClient {
   private static String apiUrl = "https://api.mymemory.translated.net/get";
   private static int timeout = 5000;
   private static String defaultTargetLanguage = "ru";

   public static String a(String s, String s1, String s2) {
      try {
         String s3 = URLEncoder.encode(s, StandardCharsets.UTF_8);
         String s5 = apiUrl;
         URL url = new URL(s5 + "?q=" + s3 + "&langpair=" + s1 + "|" + s2);
         HttpURLConnection httpurlconnection = (HttpURLConnection)url.openConnection();
         httpurlconnection.setRequestMethod("GET");
         httpurlconnection.setConnectTimeout(timeout);
         httpurlconnection.setReadTimeout(timeout);
         int i = httpurlconnection.getResponseCode();
         boolean flag = i >= 200 && i < 400;
         InputStream inputstream = flag ? httpurlconnection.getInputStream() : httpurlconnection.getErrorStream();
         InputStreamReader inputstreamreader = new InputStreamReader(inputstream, StandardCharsets.UTF_8);
         JsonObject jsonobject = JsonParser.parseReader(inputstreamreader).getAsJsonObject();
         inputstreamreader.close();
         httpurlconnection.disconnect();
         JsonObject jsonobject1 = jsonobject.getAsJsonObject("responseData");
         if (jsonobject1 == null) {
            return null;
         } else {
            String s4 = jsonobject1.get("translatedText").getAsString();
            if (flag && !s4.contains("IS AN INVALID TARGET LANGUAGE") && !s4.contains("PLEASE SELECT TWO DISTINCT")) {
               Object object = null;
               if (jsonobject1.has("detectedLanguage")) {
                  object = jsonobject1.get("detectedLanguage").getAsString();
               }

               return s4;
            } else {
               return null;
            }
         }
      } catch (Exception exception) {
         exception.printStackTrace();
         return null;
      }
   }

   public static String b(String s, String s1) {
      return a(s, "autodetect", s1);
   }

   public static String c(String s) {
      return a(s, "autodetect", defaultTargetLanguage);
   }

   public static TranslationResult d(String s, String s1, String s2) {
      try {
         String s3 = URLEncoder.encode(s, StandardCharsets.UTF_8);
         String s6 = apiUrl;
         URL url = new URL(s6 + "?q=" + s3 + "&langpair=" + s1 + "|" + s2);
         HttpURLConnection httpurlconnection = (HttpURLConnection)url.openConnection();
         httpurlconnection.setRequestMethod("GET");
         httpurlconnection.setConnectTimeout(timeout);
         httpurlconnection.setReadTimeout(timeout);
         int i = httpurlconnection.getResponseCode();
         boolean flag = i >= 200 && i < 400;
         InputStream inputstream = flag ? httpurlconnection.getInputStream() : httpurlconnection.getErrorStream();
         InputStreamReader inputstreamreader = new InputStreamReader(inputstream, StandardCharsets.UTF_8);
         JsonObject jsonobject = JsonParser.parseReader(inputstreamreader).getAsJsonObject();
         inputstreamreader.close();
         httpurlconnection.disconnect();
         JsonObject jsonobject1 = jsonobject.getAsJsonObject("responseData");
         if (jsonobject1 == null) {
            return null;
         } else {
            String s4 = jsonobject1.get("translatedText").getAsString();
            if (flag && !s4.contains("IS AN INVALID TARGET LANGUAGE") && !s4.contains("PLEASE SELECT TWO DISTINCT")) {
               String s5 = null;
               if (jsonobject1.has("detectedLanguage")) {
                  s5 = jsonobject1.get("detectedLanguage").getAsString();
               }

               return new TranslationResult(s4, s5, s2);
            } else {
               return null;
            }
         }
      } catch (Exception exception) {
         exception.printStackTrace();
         return null;
      }
   }
}
