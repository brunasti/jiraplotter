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


  public static Set<String> findPeople(Map<String, JiraTicket> jiraTickets) {
    HashSet<String> people = new HashSet<>();

    jiraTickets.values().forEach(jiraTicket -> {
      if (!jiraTicket.assignee.isEmpty()) {
        String person = jiraTicket.assignee.getFirst();
        people.add(person);
      }
    });

    return people;
  }



  public static String createClassHead(JiraTicket jiraTicket) {
    String type = jiraTicket.issueType.getFirst().toLowerCase();
    String header;

    switch (type) {
      case "bug" -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE + "B,red" + ParseJiraTicketsConstants.DEFINITION_CLASS_END;
      case "risks" -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE + "R,red" + ParseJiraTicketsConstants.DEFINITION_CLASS_END;
      case "impediment (issue)", "issue" -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE + "I,orange" + ParseJiraTicketsConstants.DEFINITION_CLASS_END;
      case "story" -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE + "S,lightgreen" + ParseJiraTicketsConstants.DEFINITION_CLASS_END;
      case "new feature" -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE + "N,lightgreen" + ParseJiraTicketsConstants.DEFINITION_CLASS_END;
      case "improvement" -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE + "I" + ParseJiraTicketsConstants.DEFINITION_CLASS_FULL_END;
      case "project request package" -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE + "P" + ParseJiraTicketsConstants.DEFINITION_CLASS_FULL_END;
      case "sub-task" -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE + "S" + ParseJiraTicketsConstants.DEFINITION_CLASS_FULL_END;
      case "task" -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE + "T" + ParseJiraTicketsConstants.DEFINITION_CLASS_FULL_END;
      case "work request" -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE + "W" + ParseJiraTicketsConstants.DEFINITION_CLASS_FULL_END;
      default -> {
        log.error("createClassHead unknown type [{}] [{}]", type, jiraTicket.issueKey.getFirst());
        header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE + "X" + ParseJiraTicketsConstants.DEFINITION_CLASS_FULL_END;
      }
    }

    return ParseJiraTicketsConstants.DEFINITION_CLASS_START + jiraTicket.issueKey.getFirst() + header;
  }



  public static HashMap<String, JiraTicket> getTicketsForLinkKind(final Collection<JiraTicket> jiraTickets, String linkKind) {
    HashMap<String, JiraTicket> selectedJiraTickets = new HashMap<>();

    jiraTickets.forEach(jiraTicket ->
            jiraTicket.inwardIssueLink.forEach(links -> {
              if (links.getName().contains(linkKind)) {
                links.getJiraTickets().forEach(link -> {
                  selectedJiraTickets.put(jiraTicket.issueKey.getFirst(), jiraTicket);
                  selectedJiraTickets.put(link.issueKey.getFirst(), link);
                });
              }
            })
    );

    return selectedJiraTickets;
  }

  public static int countTicketsForLinkKind(final Collection<JiraTicket> jiraTickets, String linkKind) {
    return getTicketsForLinkKind(jiraTickets, linkKind).size();
  }

}
