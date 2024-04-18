package it.brunasti.java.jira;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintStream;
import java.util.*;

public class Utils {

  static Logger log = LogManager.getLogger(Utils.class);

  private Utils() {}



  /**
   * Writes to the passes PrintStream the content of the Object[] array.
   *
   * @param title Identification title of the output text.
   * @param array Array of Objects to be dumped to the output PrintStream
   * @param output PrintStream to be written to.
   */
  public static void dump(final String title, final Object[] array, PrintStream output) {
    output.println("--------" + title + "-------------");
    if (array != null) {
      if (array.length == 0) {
        output.println("-- EMPTY --");
      } else {
        for (int i = 0; i < array.length; i++) {
          output.println("#" + i + "='" + array[i] + "'");
        }
      }
    } else {
      output.println("-- NULL --");
    }
  }

  public static String getShortName(String name) {
    if ((name == null) || (name.isBlank())) {
      return "";
    }
    if (name.indexOf('(') < 0) {
      return name;
    }
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


  public static Set<String> findStata(Map<String, JiraTicket> jiraTickets) {
    HashSet<String> stata = new HashSet<>();

    jiraTickets.values().forEach(jiraTicket -> {
      if (!jiraTicket.status.isEmpty()) {
        String status = jiraTicket.status.getFirst();
        stata.add(status);
      }
    });

    return stata;
  }



  public static String createClassHead(JiraTicket jiraTicket) {
    String type = jiraTicket.issueType.getFirst().toLowerCase();
    String header;

    switch (type) {
      case ParseJiraTicketsConstants.TYPE_BUG
              -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE + "B,red" + ParseJiraTicketsConstants.DEFINITION_CLASS_END;
      case ParseJiraTicketsConstants.TYPE_RISK
              -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE + "R,red" + ParseJiraTicketsConstants.DEFINITION_CLASS_END;
      case ParseJiraTicketsConstants.TYPE_IMPEDIMENT, ParseJiraTicketsConstants.TYPE_ISSUE
              -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE + "I,orange" + ParseJiraTicketsConstants.DEFINITION_CLASS_END;
      case ParseJiraTicketsConstants.TYPE_STORY
              -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE + "S,lightgreen" + ParseJiraTicketsConstants.DEFINITION_CLASS_END;
      case ParseJiraTicketsConstants.TYPE_NEW
              -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE + "N,lightgreen" + ParseJiraTicketsConstants.DEFINITION_CLASS_END;
      case ParseJiraTicketsConstants.TYPE_IMPROVEMENT
              -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE + "I" + ParseJiraTicketsConstants.DEFINITION_CLASS_FULL_END;
      case ParseJiraTicketsConstants.TYPE_REQUEST
              -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE + "P" + ParseJiraTicketsConstants.DEFINITION_CLASS_FULL_END;
      case ParseJiraTicketsConstants.TYPE_SUBTASK
              -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE + "S" + ParseJiraTicketsConstants.DEFINITION_CLASS_FULL_END;
      case ParseJiraTicketsConstants.TYPE_TASK
              -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE + "T" + ParseJiraTicketsConstants.DEFINITION_CLASS_FULL_END;
      case ParseJiraTicketsConstants.TYPE_WORK_REQUEST
              -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE + "W" + ParseJiraTicketsConstants.DEFINITION_CLASS_FULL_END;
      default -> {
        log.error("createClassHead unknown type [{}] [{}]", type, jiraTicket.issueKey.getFirst());
        header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE + "X" + ParseJiraTicketsConstants.DEFINITION_CLASS_FULL_END;
      }
    }

    return ParseJiraTicketsConstants.DEFINITION_CLASS_START + jiraTicket.issueKey.getFirst() + header;
  }



  public static Map<String, JiraTicket> getPersonaTickets(final Collection<JiraTicket> jiraTickets, String linkKind, String person) {
    log.debug("getPersonaTickets ({}) ({}) ", linkKind, person);

    Map<String, JiraTicket> selectedJiraTickets = new HashMap<>();

    jiraTickets.forEach(jiraTicket -> {
      if ((!jiraTicket.assignee.isEmpty()) && (jiraTicket.assignee.getFirst().equalsIgnoreCase(person))) {
        selectedJiraTickets.put(jiraTicket.issueKey.getFirst(), jiraTicket);
        jiraTicket.inwardIssueLink.forEach(links -> {
          if (links.getName().contains(linkKind)) {
            links.getJiraTickets().forEach(link -> selectedJiraTickets.put(link.issueKey.getFirst(), link));
          }
        });
      }
    });

    return selectedJiraTickets;
  }

  public static Map<String, JiraTicket> getStatusTickets(final Collection<JiraTicket> jiraTickets, String linkKind, String status) {
    log.debug("getStatusTickets ({}) ({}) ", linkKind, status);

    HashMap<String, JiraTicket> selectedJiraTickets = new HashMap<>();

    jiraTickets.forEach(jiraTicket -> {
      if ((!jiraTicket.status.isEmpty()) && (jiraTicket.status.getFirst().equalsIgnoreCase(status))) {
        selectedJiraTickets.put(jiraTicket.issueKey.getFirst(), jiraTicket);
        jiraTicket.inwardIssueLink.forEach(links -> {
          if (links.getName().contains(linkKind)) {
            links.getJiraTickets().forEach(link -> selectedJiraTickets.put(link.issueKey.getFirst(), link));
          }
        });
      }
    });
    return selectedJiraTickets;
  }



  public static Map<String, JiraTicket> getTicketsForLinkKind(final Collection<JiraTicket> jiraTickets, String linkKind) {
    Map<String, JiraTicket> selectedJiraTickets = new HashMap<>();

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



  public static boolean personHasDependingTickets(final Collection<JiraTicket> jiraTickets, String person) {
    HashMap<String, JiraTicket> selectedJiraTickets = new HashMap<>();
    log.debug("personHasDependingTickets ({})", person);

    jiraTickets.forEach(jiraTicket -> {
      if ((!jiraTicket.assignee.isEmpty()) && (jiraTicket.assignee.getFirst().equalsIgnoreCase(person))) {
        log.debug("personHasDependingTickets ({}) -> [{}]", person, jiraTicket.issueKey);
        selectedJiraTickets.put(jiraTicket.issueKey.getFirst(), jiraTicket);
      }
    });

    return !selectedJiraTickets.isEmpty();
  }



  public static List<FieldDescriptor>  loadDefinitions(String[] header, List<JiraTicketLinkDescriptor> jiraTicketLinkDescriptors) {
    // Load record definition and FieldDescriptors
    ArrayList<FieldDescriptor> fieldDescriptors = new ArrayList<>();

    log.info("Header  : [{}]", Arrays.toString(header));
    log.info("Fields  : [{}]", header.length);

    HashSet<String> fields = new HashSet<>();
    fields.addAll(Arrays.stream(header).toList());

    log.info("Unique Fields  : [{}]", fields.size());
    fields.stream().sorted().forEach(field -> {
      if (!field.startsWith("Custom field (")) {
        log.info("   - Unique Field  : [{}][{}]",field, Utils.countSameFields(header,field));
        FieldDescriptor fieldDescriptor = new FieldDescriptor(header, field);
        fieldDescriptors.add(fieldDescriptor);
        log.info("                   ->  FieldDescriptor  : [{}]",fieldDescriptor);
        if (fieldDescriptor.name.contains("link")) {
          log.info("                   ->  LINK : FieldDescriptor  : [{}]", fieldDescriptor);
          JiraTicketLinkDescriptor jiraTicketLinkDescriptor = new JiraTicketLinkDescriptor(field, fieldDescriptor);
          jiraTicketLinkDescriptors.add(jiraTicketLinkDescriptor);
        }
      }
    });
    log.info("Unique Unique Fields  : [{}]", fieldDescriptors.size());
    log.info("Links Fields  : [{}]", jiraTicketLinkDescriptors.size());

    JiraTicketDescriptor.readCSVDefinition(fieldDescriptors);

    return fieldDescriptors;
  }

}
