package it.brunasti.javatools.jira;

import java.util.Arrays;

public class Utils {

  private Utils() {}

  public static String getShortName(String name) {
    return name.substring(name.indexOf('(')+1, name.lastIndexOf(')'));
  }

  public static long countSameFields(String[] fields, String name) {
    return Arrays.stream(fields).filter(field -> field.equals(name) ).count();
  }

  public static String getShortMark(String mark) {
    String shortMark = "";
    String[] tokens = mark.split("\\s+");
    for (int i=0; i<tokens.length; i++) {
      shortMark = shortMark + tokens[i].charAt(0);
    }
    return shortMark.substring(shortMark.length()-1);
  }

}
