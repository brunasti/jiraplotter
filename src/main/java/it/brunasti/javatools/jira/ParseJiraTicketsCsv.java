package it.brunasti.javatools.jira;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class ParseJiraTicketsCsv {

  public static final String HEADER_LINKS = "' Links =======";
  public static final String DEFINITION_CLASS_START = "class \"";
  public static final String DEFINITION_CLASS_MIDDLE = "\" << (";
  public static final String DEFINITION_CLASS_FULL_END = ",lightblue) >> {";
  public static final String DEFINITION_CLASS_END = ") >> {";
  public static final String DEFINITION_CLASS_ASSIGNED_TO = "  Assigned to : ";
  public static final String DEFINITION_CLASS_STATUS = "  Status : ";
  public static final String DEFINITION_CLASS_TYPE = "  Type : ";
  public static final String DEFINITION_LINK_SIMPLE_MIDDLE = "\" <.. \"";
  public static final String DEFINITION_LINK_SIMPLE_END = "\" : ";
  static PrintStream output;

  static Logger log = LogManager.getLogger(ParseJiraTicketsCsv.class);

  static ArrayList<FieldDescriptor> fieldDescriptors = new ArrayList<>();
  static ArrayList<JiraTicketLinkDescriptor> jiraTicketLinkDescriptors = new ArrayList<>();

  public static JiraTicket findFromKey(Map<String, JiraTicket> jiraTickets, String key) {
    try {
      return jiraTickets.values().stream().filter(jiraTicket -> jiraTicket.issue_Key.get(0).equalsIgnoreCase(key)).findFirst().get();
    } catch (NoSuchElementException nsee) {
      log.error("findFromKey  : [{}] {}", key, nsee.getMessage());
      return null;
    }
  }

  private static void generateHeader() {
    output.println("@startuml");
    output.println("'https://plantuml.com/class-diagram");
    output.println();
    output.println("' GENERATE CLASS DIAGRAM ===========");
    output.println("' Generated at    : " + new Date());
    output.println();
    output.println("hide empty members");
    output.println();
  }

  private static long countTicketsInStatus(Collection<JiraTicket> jiraTickets, String status) {
    return jiraTickets.stream().filter(jiraTicket -> jiraTicket.status.get(0).equals(status) ).count();
  }

  private static long countTicketsOfType(Collection<JiraTicket> jiraTickets, String type) {
    return jiraTickets.stream().filter(jiraTicket -> jiraTicket.issue_Type.get(0).equals(type) ).count();
  }

  private static void generateTextLegend(final HashMap<String, JiraTicket> selectedJiraTickets) {
    HashSet<String> stata = new HashSet<>();
    HashSet<String> types = new HashSet<>();

    selectedJiraTickets.values().forEach(jiraTicket -> stata.add(jiraTicket.status.get(0)));
    selectedJiraTickets.values().forEach(jiraTicket -> types.add(jiraTicket.issue_Type.get(0)));

    output.println("legend");
    output.println("Jira Ticket Status");
    output.println("----");
    stata.forEach(status ->
      output.println("("+status+") : " + countTicketsInStatus(selectedJiraTickets.values(),status))
    );
    output.println("----");
    output.println("Jira Ticket Types");
    output.println("----");
    types.forEach(type ->
      output.println("("+type+") : "+countTicketsOfType(selectedJiraTickets.values(),type))
    );
    output.println("end legend");
    output.println();
  }

  private static void generateLegend(final Collection<JiraTicket> jiraTickets, String linkKind) {
    HashMap<String, JiraTicket> selectedJiraTickets = new HashMap<>();

    jiraTickets.forEach(jiraTicket -> {
      jiraTicket.inward_Issue_Link.forEach(links -> {
        if (links.name.contains(linkKind)) {
          links.jiraTikets.forEach(link -> {
            selectedJiraTickets.put(jiraTicket.issue_Key.get(0), jiraTicket);
            selectedJiraTickets.put(link.issue_Key.get(0), link);
          });
        }
      });
    });

    generateTextLegend(selectedJiraTickets);
  }

  private static void generateLegendPersona(final Collection<JiraTicket> jiraTickets, String linkKind, String person) {
    log.debug("generateLegendPersona ({}) ({}) ", linkKind, person);
    HashMap<String, JiraTicket> selectedJiraTickets = new HashMap<>();

    jiraTickets.forEach(jiraTicket -> {
      if ((!jiraTicket.assignee.isEmpty()) && (jiraTicket.assignee.get(0).equalsIgnoreCase(person))) {
        selectedJiraTickets.put(jiraTicket.issue_Key.get(0), jiraTicket);
        jiraTicket.inward_Issue_Link.forEach(links -> {
          if (links.name.contains(linkKind)) {
            links.jiraTikets.forEach(link -> selectedJiraTickets.put(link.issue_Key.get(0), link));
          }
        });
      }
    });

    generateTextLegend(selectedJiraTickets);
  }


  private static String createClassHead(JiraTicket jiraTicket) {
    String type = jiraTicket.issue_Type.get(0).toLowerCase();
    String header;

    switch (type) {
      case "bug" -> header = DEFINITION_CLASS_MIDDLE + "B,red" + DEFINITION_CLASS_END;
      case "risks" -> header = DEFINITION_CLASS_MIDDLE + "R,red" + DEFINITION_CLASS_END;
      case "impediment (issue)", "issue" -> header = DEFINITION_CLASS_MIDDLE + "I,orange" + DEFINITION_CLASS_END;
      case "story" -> header = DEFINITION_CLASS_MIDDLE + "S,lightgreen" + DEFINITION_CLASS_END;
      case "new feature" -> header = DEFINITION_CLASS_MIDDLE + "N,lightgreen" + DEFINITION_CLASS_END;
      case "improvement" -> header = DEFINITION_CLASS_MIDDLE + "I" + DEFINITION_CLASS_FULL_END;
      case "project request package" -> header = DEFINITION_CLASS_MIDDLE + "P" + DEFINITION_CLASS_FULL_END;
      case "sub-task" -> header = DEFINITION_CLASS_MIDDLE + "S" + DEFINITION_CLASS_FULL_END;
      case "task" -> header = DEFINITION_CLASS_MIDDLE + "T" + DEFINITION_CLASS_FULL_END;
      case "work request" -> header = DEFINITION_CLASS_MIDDLE + "W" + DEFINITION_CLASS_FULL_END;
      default -> {
        log.error("createClassHead unknown type [{}] [{}]", type, jiraTicket.issue_Key.get(0));
        header = DEFINITION_CLASS_MIDDLE + "X" + DEFINITION_CLASS_FULL_END;
      }
    }

    return DEFINITION_CLASS_START + jiraTicket.issue_Key.get(0) + header;
  }

  private static void generateTicket(JiraTicket jiraTicket) {
    output.println(createClassHead(jiraTicket));
    if (!jiraTicket.assignee.isEmpty()) {
      output.println(DEFINITION_CLASS_ASSIGNED_TO + jiraTicket.assignee.get(0));
    }
    output.println(DEFINITION_CLASS_STATUS + jiraTicket.status.get(0));
    output.println(DEFINITION_CLASS_TYPE + jiraTicket.issue_Type.get(0));
    output.println("}");
    output.println();
  }

  private static void generateTickets(final Collection<JiraTicket> jiraTickets) {
    output.println();
    output.println("' Jira Tickets =======");
    jiraTickets.forEach(jiraTicket -> generateTicket(jiraTicket));
    output.println();
  }

  private static void generateTicketsPerKindLinks(final Collection<JiraTicket> jiraTickets, String kind) {
    HashMap<String, JiraTicket> selectedJiraTickets = new HashMap<>();

    jiraTickets.forEach(jiraTicket ->
      jiraTicket.inward_Issue_Link.forEach(links -> {
        if (links.name.contains(kind)) {
          links.jiraTikets.forEach(link -> {
            selectedJiraTickets.put(jiraTicket.issue_Key.get(0), jiraTicket);
            selectedJiraTickets.put(link.issue_Key.get(0), link);
          });
        }
      })
    );

    output.println();
    output.println("' Tickets =======");
    selectedJiraTickets.values().forEach(jiraTicket -> generateTicket(jiraTicket));
    output.println();
  }

  private static void generateTicketsPerPersonLinks(final Collection<JiraTicket> jiraTickets, String person) {
    HashMap<String, JiraTicket> selectedJiraTickets = new HashMap<>();
    log.debug("generateTicketsPerPersonLinks ({})", person);

    jiraTickets.forEach(jiraTicket -> {
      if ((!jiraTicket.assignee.isEmpty()) && (jiraTicket.assignee.get(0).equalsIgnoreCase(person))) {
          log.debug("generateTicketsPerPersonLinks [{}]", jiraTicket.issue_Key.get(0));
          selectedJiraTickets.put(jiraTicket.issue_Key.get(0), jiraTicket);
          jiraTicket.inward_Issue_Link.forEach(links ->
              links.jiraTikets.forEach(link -> selectedJiraTickets.put(link.issue_Key.get(0), link))
          );
        }
    });

    output.println();
    output.println("' Tickets =======");
    selectedJiraTickets.values().forEach(jiraTicket -> generateTicket(jiraTicket));
    output.println();
  }

  private static boolean personHasDependingTickets(final Collection<JiraTicket> jiraTickets, String person) {
    HashMap<String, JiraTicket> selectedJiraTickets = new HashMap<>();
    log.debug("personHasDependingTickets ({})", person);

    jiraTickets.forEach(jiraTicket -> {
    if ((!jiraTicket.assignee.isEmpty()) && (jiraTicket.assignee.get(0).equalsIgnoreCase(person))) {
        log.debug("personHasDependingTickets ({}) -> [{}]", person, jiraTicket.issue_Key);
        selectedJiraTickets.put(jiraTicket.issue_Key.get(0), jiraTicket);
      }
    });

    return !selectedJiraTickets.isEmpty();
  }

  private static void generateParents(final Collection<JiraTicket> jiraTickets) {
    output.println();
    output.println("' Parents =======");
    jiraTickets.forEach(jiraTicket -> {
      if(jiraTicket.parentJira != null) {
        output.println("\"" + jiraTicket.parentJira.issue_Key.get(0) + "\" <|-- \"" + jiraTicket.issue_Key.get(0)+ "\"");
      }
    });
    output.println();
  }

  private static void generateLinks(final Collection<JiraTicket> jiraTickets) {
    output.println();
    output.println(HEADER_LINKS);
    jiraTickets.forEach(jiraTicket ->
      jiraTicket.inward_Issue_Link.forEach(links ->
        links.jiraTikets.forEach(link ->
          output.println("\"" + jiraTicket.issue_Key.get(0) + DEFINITION_LINK_SIMPLE_MIDDLE + link.issue_Key.get(0)+ DEFINITION_LINK_SIMPLE_END +links.getShortName())
        )
      )
    );
    output.println();
  }

  private static void generateSingleKindLinks(final Collection<JiraTicket> jiraTickets, String kind) {
    output.println();
    output.println(HEADER_LINKS);
    jiraTickets.forEach(jiraTicket ->
      jiraTicket.inward_Issue_Link.forEach(links -> {
        if (links.name.contains(kind)) {
          links.jiraTikets.forEach(link ->
            output.println("\"" + jiraTicket.issue_Key.get(0) + DEFINITION_LINK_SIMPLE_MIDDLE + link.issue_Key.get(0) + DEFINITION_LINK_SIMPLE_END + links.getShortName())
          );
        }
      })
    );
    output.println();
  }

  private static void generateSinglePersonLinks(final Collection<JiraTicket> jiraTickets, String person) {
    output.println();
    output.println(HEADER_LINKS);
    jiraTickets.forEach(jiraTicket -> {
      if ((!jiraTicket.assignee.isEmpty()) && (jiraTicket.assignee.get(0).equalsIgnoreCase(person)))
          jiraTicket.inward_Issue_Link.forEach(links ->
            links.jiraTikets.forEach(link ->
              output.println("\"" + jiraTicket.issue_Key.get(0) + DEFINITION_LINK_SIMPLE_MIDDLE + link.issue_Key.get(0) + DEFINITION_LINK_SIMPLE_END + links.getShortName())
            )
          );
        }
    );
    output.println();
  }


  private static void generateFooter() {
    output.println();
    output.println("@enduml");
  }


  private static HashSet<String> findPeople(HashMap<String, JiraTicket> jiraTickets) {
    HashSet<String> people = new HashSet<>();

    jiraTickets.values().forEach(jiraTicket -> {
      if (!jiraTicket.assignee.isEmpty()) {
        String person = jiraTicket.assignee.get(0);
        people.add(person);
      }
    });

    return people;
  }



  public static void main(String[] args) throws IOException, CsvException {

    String fileName = "./src/test/resources/jira-full.csv";
    try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
      List<String[]> r = reader.readAll();

      String[] header = r.get(0);

      log.info("Header  : [{}]", Arrays.toString(header));
      log.info("Fields  : [{}]", header.length);
      log.info("Records : [{}]", r.size());

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
            JiraTicketLinkDescriptor jiraTicketLinkDescriptor = new JiraTicketLinkDescriptor();
            jiraTicketLinkDescriptor.name = field;
            jiraTicketLinkDescriptor.fieldDescriptor = fieldDescriptor;
            jiraTicketLinkDescriptors.add(jiraTicketLinkDescriptor);
          }
        }
      });
      log.info("Unique Unique Fields  : [{}]", fieldDescriptors.size());
      log.info("Links Fields  : [{}]", jiraTicketLinkDescriptors.size());

      JiraTicket.readCSVDefinition(fieldDescriptors);

      HashMap<String, JiraTicket> jiraTickets = new HashMap<>();

      for (int i=1; i<r.size(); i++) {
        JiraTicket jiraTicket = new JiraTicket(r.get(i));
        jiraTickets.put(jiraTicket.issue_Id.get(0), jiraTicket);
      }

      // Create link pointers from ticket id
      jiraTickets.values().forEach(jiraTicket -> jiraTicket.connectLinks(jiraTickets));

      log.info("Records : [{}]",jiraTickets.size());

      FileOutputStream file = new FileOutputStream("./temp/jira.puml");
      output = new PrintStream(file, true);
      generateHeader();
      generateLegend(jiraTickets.values(), "");
      generateTickets(jiraTickets.values());
      generateParents(jiraTickets.values());
      generateLinks(jiraTickets.values());
      generateFooter();
      output.close();


      jiraTicketLinkDescriptors.forEach(jiraTicketLinkDescriptor -> {
        log.debug("jiraTicketLinkDescriptor : short name [{}]",jiraTicketLinkDescriptor.getShortName());
        log.debug("jiraTicketLinkDescriptor : [{}]",jiraTicketLinkDescriptor);

        try {
          FileOutputStream subfile = new FileOutputStream("./temp/jira" + jiraTicketLinkDescriptor.getShortName() + ".puml");
          output = new PrintStream(subfile, true);
          generateHeader();
          generateLegend(jiraTickets.values(), jiraTicketLinkDescriptor.getShortName());
          generateTicketsPerKindLinks(jiraTickets.values(), jiraTicketLinkDescriptor.getShortName());
          generateSingleKindLinks(jiraTickets.values(), jiraTicketLinkDescriptor.getShortName());
          generateFooter();
          output.close();
        } catch (Exception ex) {
          log.error(ex);
        }
      });

      HashSet<String> people = findPeople(jiraTickets);
      people.forEach(person -> {
        log.debug("people : [{}]",person);
        if (personHasDependingTickets(jiraTickets.values(),person)) {
          try {
            FileOutputStream subfile = new FileOutputStream("./temp/jira-" + person + ".puml");
            output = new PrintStream(subfile, true);
            generateHeader();
            generateLegendPersona(jiraTickets.values(), "", person);
            generateTicketsPerPersonLinks(jiraTickets.values(), person);
            generateSinglePersonLinks(jiraTickets.values(), person);
            generateFooter();
            output.close();
          } catch (Exception ex) {
            log.error(ex);
            ex.printStackTrace();
          }
        }
      });

    }

  }

}