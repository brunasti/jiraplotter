package it.brunasti.javatools.jira;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

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
      log.debug("findFromKey : [{}]", key);
      return jiraTickets.get(key);
  }



  public static long countTicketsInStatus(Collection<JiraTicket> jiraTickets, String status) {
    return jiraTickets.stream().filter(jiraTicket -> jiraTicket.status.getFirst().equals(status) ).count();
  }

  public static long countTicketsOfType(Collection<JiraTicket> jiraTickets, String type) {
    return jiraTickets.stream().filter(jiraTicket -> jiraTicket.issueType.getFirst().equals(type) ).count();
  }


  public static HashSet<String> findPeople(HashMap<String, JiraTicket> jiraTickets) {
    HashSet<String> people = new HashSet<>();

    jiraTickets.values().forEach(jiraTicket -> {
      if (!jiraTicket.assignee.isEmpty()) {
        String person = jiraTicket.assignee.getFirst();
        people.add(person);
      }
    });

    return people;
  }



}
