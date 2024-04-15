package it.brunasti.javatools.jira;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;

public class Utils {

  static Logger log = LogManager.getLogger(Utils.class);

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


  public static JiraTicket findFromKey(Map<String, JiraTicket> jiraTickets, String key) {
    try {
      log.debug("findFromKey : [{}]", key);
//      return jiraTickets.values().stream().filter(jiraTicket -> jiraTicket.issueKey.getFirst().equalsIgnoreCase(key)).findFirst().get();
      return jiraTickets.get(key);
    } catch (NoSuchElementException nsee) {
      log.error("findFromKey  : [{}] {}", key, nsee.getMessage());
      return null;
    }
  }


}
