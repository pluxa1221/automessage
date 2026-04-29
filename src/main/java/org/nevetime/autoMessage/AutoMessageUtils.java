package org.nevetime.autoMessage;


public class AutoMessageUtils {
    public String stripInteractiveTags(String text) {
        if (text == null) return "";

        return text
                .replaceAll("(?i)<click:[^>]*>","")
                .replaceAll("(?i)</click>","")
                .replaceAll("(?i)<hover:[^>]*>","")
                .replaceAll("(?i)</hover>","");
    }
}
