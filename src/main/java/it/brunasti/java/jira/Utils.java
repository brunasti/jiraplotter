package it.brunasti.java.jira;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class collecting different general functions.
 */
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
    it.brunasti.java.utils.Utils.dump(title, array, output);
  }

  /**
   * Convert a long link name to a short version.
   * The link name is often made by a general part followed by a more specific info
   * within round brackets.
   * This function extract the part within the brackets, if it exists.
   *
   * @param name the full name of the link
   * @return a reduced shorter version of the name, if possible
   */
  public static String getShortName(String name) {
    return it.brunasti.java.utils.Utils.extractBracketsContent(name);
  }

  public static String getNameForFile(String name) {
    String fileName = name.replace(' ', '_');
    fileName = fileName.replace('/', '_');
    return fileName;
  }

  public static long countSameFields(String[] fields, String name) {
    return Arrays.stream(fields).filter(field -> field.equals(name)).count();
  }

  public static JiraTicket findFromKey(Map<String, JiraTicket> jiraTickets, String key) {
//    log.debug("findFromKey : [{}]", key);
    return jiraTickets.get(key);
  }

  public static JiraTicket findFromId(Map<String, JiraTicket> jiraTickets, String key) {
//    log.debug("findFromId : [{}]", key);
    AtomicReference<JiraTicket> result = new AtomicReference<>();
    jiraTickets.values().forEach(jiraTicket -> {
      if (key.equalsIgnoreCase(jiraTicket.issueId.getFirst())) {
        result.set(jiraTicket);
      }
    });
    return result.get();
  }



  public static long countTicketsInStatus(Collection<JiraTicket> jiraTickets, String status) {
    return jiraTickets.stream().filter(jiraTicket -> jiraTicket
            .status.getFirst().equals(status)).count();
  }

  public static long countTicketsOfType(Collection<JiraTicket> jiraTickets, String type) {
    return jiraTickets.stream().filter(jiraTicket -> jiraTicket
            .issueType.getFirst().equals(type)).count();
  }


  /**
   * Get all the Assignee people from a list of JiraTickets.
   *
   * @param jiraTickets the list of Tickets
   * @return a Set containing the list of existing People
   */
  public static Set<String> findPeople(Map<String, JiraTicket> jiraTickets) {
    Set<String> people = new HashSet<>();

    jiraTickets.values().forEach(jiraTicket -> {
      if (!jiraTicket.assignee.isEmpty()) {
        String person = jiraTicket.assignee.getFirst();
        people.add(person);
      }
    });

    return people;
  }


  /**
   * Get all the Epics from a list of JiraTickets.
   *
   * @param jiraTickets the list of Tickets
   * @return a Set containing the list of existing People
   */
  public static Set<String> findEpics(Map<String, JiraTicket> jiraTickets) {
    Set<String> epics = new HashSet<>();

    jiraTickets.values().forEach(jiraTicket -> {
      String type = jiraTicket.issueType.getFirst().toLowerCase();
      if (ParseJiraTicketsConstants.TYPE_EPIC.equalsIgnoreCase(type)) {
        String person = jiraTicket.issueId.getFirst();
        epics.add(person);
      }
    });

    return epics;
  }


  /**
   * Get all the Stata from a list of JiraTickets.
   *
   * @param jiraTickets the list of Tickets
   * @return a Set containing the list of existing Stata
   */
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


  /**
   * Create a string containing the ad hoc header of a Ticket class definition.
   *
   * @param jiraTicket Ticket to create the header for
   * @return header for the Ticket
   */
  public static String createClassHead(JiraTicket jiraTicket) {
    String type = "XXXX";
    if (!jiraTicket.assignee.isEmpty()) {
      type = jiraTicket.issueType.getFirst().toLowerCase();
    }

    String header;

    switch (type) {
      case ParseJiraTicketsConstants.TYPE_BUG
              -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE
              + "B,red" + ParseJiraTicketsConstants.DEFINITION_CLASS_END;
      case ParseJiraTicketsConstants.TYPE_RISK
              -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE
              + "R,red" + ParseJiraTicketsConstants.DEFINITION_CLASS_END;
      case ParseJiraTicketsConstants.TYPE_IMPEDIMENT, ParseJiraTicketsConstants.TYPE_ISSUE
              -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE
              + "I,orange" + ParseJiraTicketsConstants.DEFINITION_CLASS_END;
      case ParseJiraTicketsConstants.TYPE_STORY
              -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE
              + "S,lightgreen" + ParseJiraTicketsConstants.DEFINITION_CLASS_END;
      case ParseJiraTicketsConstants.TYPE_EPIC
              -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE
              + "E,green" + ParseJiraTicketsConstants.DEFINITION_CLASS_END;
      case ParseJiraTicketsConstants.TYPE_NEW
              -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE
              + "N,lightgreen" + ParseJiraTicketsConstants.DEFINITION_CLASS_END;
      case ParseJiraTicketsConstants.TYPE_IMPROVEMENT
              -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE
              + "I" + ParseJiraTicketsConstants.DEFINITION_CLASS_FULL_END;
      case ParseJiraTicketsConstants.TYPE_REQUEST
              -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE
              + "P" + ParseJiraTicketsConstants.DEFINITION_CLASS_FULL_END;
      case ParseJiraTicketsConstants.TYPE_SUBTASK
              -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE
              + "S" + ParseJiraTicketsConstants.DEFINITION_CLASS_FULL_END;
      case ParseJiraTicketsConstants.TYPE_TASK, ParseJiraTicketsConstants.TYPE_TASK_NL
              -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE
              + "T" + ParseJiraTicketsConstants.DEFINITION_CLASS_FULL_END;
      case ParseJiraTicketsConstants.TYPE_WORK_REQUEST
              -> header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE
              + "W" + ParseJiraTicketsConstants.DEFINITION_CLASS_FULL_END;
      default -> {
        String issueType = "XXX";
        if (!jiraTicket.assignee.isEmpty()) {
          issueType = jiraTicket.issueType.getFirst();
        }
        log.error("createClassHead unknown type [{}] [{}]",
                type,
                issueType);
        header = ParseJiraTicketsConstants.DEFINITION_CLASS_MIDDLE + "X"
                + ParseJiraTicketsConstants.DEFINITION_CLASS_FULL_END;
      }
    }


    String issueType = "XXX";
    if (!jiraTicket.assignee.isEmpty()) {
      issueType = jiraTicket.issueType.getFirst();
    }
    return ParseJiraTicketsConstants.DEFINITION_CLASS_START
            + issueType + header;
  }

  /**
   * Get all the Tickets assigned to a Person.
   *
   * @param jiraTickets the list of all Tickets
   * @param person the person of whom we want the Tickets
   * @return a Map containing the list of the Tickets assigned to the person
   */
  public static Map<String, JiraTicket> getPersonaTickets(
          final Collection<JiraTicket> jiraTickets, String linkKind, String person) {
    log.debug("getPersonaTickets ({}) ({}) ", linkKind, person);

    Map<String, JiraTicket> selectedJiraTickets = new HashMap<>();

    jiraTickets.forEach(jiraTicket -> {
      if ((!jiraTicket.assignee.isEmpty())
              && (jiraTicket.assignee.getFirst().equalsIgnoreCase(person))) {
        selectedJiraTickets.put(jiraTicket.issueKey.getFirst(), jiraTicket);
        jiraTicket.inwardIssueLink.forEach(links -> {
          if (links.getName().contains(linkKind)) {
            links.getJiraTickets().forEach(
                    link -> selectedJiraTickets.put(link.issueKey.getFirst(), link));
          }
        });
      }
    });

    return selectedJiraTickets;
  }

  /**
   * Get all the Tickets of an Epic.
   *
   * @param jiraTickets the list of all Tickets
   * @param epic the epic of whom we want the Tickets
   * @return a Map containing the list of the Tickets part of the epic
   */
  public static Map<String, JiraTicket> getEpicTickets(
          final Map<String, JiraTicket>  jiraTickets, String linkKind, JiraTicket epic) {
    // TODO: change to epics
//    log.debug("getEpicTickets ({}) EPIC : ({}) ", linkKind, epic.issueKey.getFirst());

    Map<String, JiraTicket> selectedJiraTickets = new HashMap<>();
    selectedJiraTickets.put(epic.issueKey.getFirst(), epic);

    jiraTickets.values().forEach(jiraTicket -> {
      if ((!jiraTicket.parent.isEmpty()) && (jiraTicket.parent.getFirst().equalsIgnoreCase(epic.issueId.getFirst()))) {
        selectedJiraTickets.put(jiraTicket.issueKey.getFirst(), jiraTicket);
      }
      jiraTicket.inwardIssueLink.forEach(links -> {
        links.getJiraTickets().forEach(linkedTicked -> {
          if (epic.issueKey.getFirst().equalsIgnoreCase(linkedTicked.issueKey.getFirst())) {
            selectedJiraTickets.put(linkedTicked.issueKey.getFirst(), linkedTicked);
          }
        });
      });
    });

    Map<String, JiraTicket> subTickets = new HashMap<>();
    jiraTickets.values().forEach(jiraTicket -> {
      if (!jiraTicket.parent.isEmpty()) {
//        log.debug("getEpicTickets ({}) EPIC : ({}) ({})", epic.issueKey.getFirst(), jiraTicket.issueKey.getFirst(), jiraTicket.parent.getFirst());
        JiraTicket parent = findFromId(jiraTickets, jiraTicket.parent.getFirst());
        if ((parent != null) && (selectedJiraTickets.containsKey(parent.issueKey.getFirst()))) {
//          log.debug("getEpicTickets ({}) EPIC : add ({}) ", epic.issueKey.getFirst(), jiraTicket.issueKey.getFirst());
          subTickets.put(jiraTicket.issueKey.getFirst(), jiraTicket);
        }
      }
//      jiraTicket.inwardIssueLink.forEach(links -> {
//        links.getJiraTickets().forEach(linkedTicked -> {
//          if (epic.issueKey.getFirst().equalsIgnoreCase(linkedTicked.issueKey.getFirst())) {
//            selectedJiraTickets.put(linkedTicked.issueKey.getFirst(), linkedTicked);
//          }
//        });
//      });
    });

    selectedJiraTickets.putAll(subTickets);

    return selectedJiraTickets;
  }

  /**
   * Get all the Tickets in a given Status and connected via a specific link Kins.
   *
   * @param jiraTickets the list of all Tickets
   * @param status desired status of the Tickets
   * @return a Map containing the list of the corresponding Tickets
   */
  public static Map<String, JiraTicket> getStatusTickets(
          final Collection<JiraTicket> jiraTickets,
          String status) {
    log.debug("getStatusTickets ({})", status);

    HashMap<String, JiraTicket> selectedJiraTickets = new HashMap<>();

    jiraTickets.forEach(jiraTicket -> {
      if ((!jiraTicket.status.isEmpty())
              && (jiraTicket.status.getFirst().equalsIgnoreCase(status))) {
        selectedJiraTickets.put(jiraTicket.issueKey.getFirst(), jiraTicket);
        jiraTicket.inwardIssueLink.forEach(links ->
            links.getJiraTickets().forEach(
                    ticket -> selectedJiraTickets.put(ticket.issueKey.getFirst(), ticket))
        );
      }
    });
    return selectedJiraTickets;
  }

  public static Map<String, JiraTicket> getTicketsForLinkKind(
          final Collection<JiraTicket> jiraTickets,
          String linkKind) {
    Map<String, JiraTicket> selectedJiraTickets = new HashMap<>();

    if ((linkKind != null) && (!linkKind.isEmpty())) {
      jiraTickets.forEach(jiraTicket ->
              jiraTicket.inwardIssueLink.forEach(links -> {
                if ((linkKind == null) || (linkKind.isEmpty()) || (links.getName().contains(linkKind))) {
                  links.getJiraTickets().forEach(link -> {
                    selectedJiraTickets.put(jiraTicket.issueKey.getFirst(), jiraTicket);
                    selectedJiraTickets.put(link.issueKey.getFirst(), link);
                  });
                }
              })
      );
    } else {
      jiraTickets.forEach(jiraTicket ->
        selectedJiraTickets.put(jiraTicket.issueKey.getFirst(), jiraTicket)
      );
    }

    return selectedJiraTickets;
  }

  public static int countTicketsForLinkKind(
          final Collection<JiraTicket> jiraTickets,
          String linkKind) {
    return getTicketsForLinkKind(jiraTickets, linkKind).size();
  }

  public static boolean personHasDependingTickets(
          final Collection<JiraTicket> jiraTickets,
          String person) {
    HashMap<String, JiraTicket> selectedJiraTickets = new HashMap<>();
    log.debug("personHasDependingTickets ({})", person);

    jiraTickets.forEach(jiraTicket -> {
      if ((!jiraTicket.assignee.isEmpty())
              && (jiraTicket.assignee.getFirst().equalsIgnoreCase(person))) {
//        log.debug("personHasDependingTickets ({}) -> [{}]", person, jiraTicket.issueKey);
        selectedJiraTickets.put(jiraTicket.issueKey.getFirst(), jiraTicket);
      }
    });

    return !selectedJiraTickets.isEmpty();
  }

  public static List<FieldDescriptor> loadDefinitions(
          String[] header,
          List<JiraTicketLinkDescriptor> jiraTicketLinkDescriptors) {
    // Load record definition and FieldDescriptors

    log.info("Header  : [{}]", Arrays.toString(header));
    log.info("Fields  : [{}]", header.length);

    HashSet<String> fields = new HashSet<>(Arrays.stream(header).toList());

    log.info("Unique Fields  : [{}]", fields.size());
    ArrayList<FieldDescriptor> fieldDescriptors = new ArrayList<>();
    fields.stream().sorted().forEach(field -> {
      FieldDescriptor fieldDescriptor = new FieldDescriptor(header, field);
      fieldDescriptors.add(fieldDescriptor);
      if (fieldDescriptor.name.contains("link")) {
        JiraTicketLinkDescriptor jiraTicketLinkDescriptor
                = new JiraTicketLinkDescriptor(field, fieldDescriptor);
        jiraTicketLinkDescriptors.add(jiraTicketLinkDescriptor);
      }
    });
    log.info("Unique Unique Fields  : [{}]", fieldDescriptors.size());
    log.info("Links Fields  : [{}]", jiraTicketLinkDescriptors.size());

    JiraTicketDescriptor.readCSVDefinition(fieldDescriptors);

    return fieldDescriptors;
  }

}
