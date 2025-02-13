package it.brunasti.java.jira;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParseJiraTicketsCsv {

  static Logger log = LogManager.getLogger(ParseJiraTicketsCsv.class);

  List<FieldDescriptor> fieldDescriptors = new ArrayList<>();
  ArrayList<JiraTicketLinkDescriptor> jiraTicketLinkDescriptors = new ArrayList<>();
  PrintStream output;

  private void generateHeader(String title) {
    output.println("@startuml");
    output.println("'https://plantuml.com/class-diagram");
    output.println();
    output.println("' GENERATE CLASS DIAGRAM ===========");
    output.println("' Generated at    : " + new Date());
    output.println();
    output.println("title " + title);
    output.println("hide empty members");
    output.println();
  }

  private void generateTextLegend(final Map<String, JiraTicket> selectedJiraTickets) {
    Set<String> stata = new HashSet<>();
    Set<String> types = new HashSet<>();

    selectedJiraTickets.values().forEach(jiraTicket -> stata.add(jiraTicket.status.getFirst()));
    selectedJiraTickets.values().forEach(jiraTicket -> types.add(jiraTicket.issueType.getFirst()));

    output.println("legend");
    output.println("Jira Ticket Status");
    output.println("----");
    stata.forEach(status -> output.println(status + " : "
                    + Utils.countTicketsInStatus(selectedJiraTickets.values(), status))
    );
    output.println("----");
    output.println("Jira Ticket Types");
    output.println("----");
    types.forEach(type -> output.println(type + " : "
              + Utils.countTicketsOfType(selectedJiraTickets.values(), type))
    );
    output.println("end legend");
    output.println();
  }

  private void generateLegend(final Collection<JiraTicket> jiraTickets, String linkKind) {
    generateTextLegend(Utils.getTicketsForLinkKind(jiraTickets, linkKind));
  }

  private void generateLegendEpic(final Map<String, JiraTicket>  jiraTickets, JiraTicket epic) {
    log.debug("generateLegendEpic ({}) ", epic.issueKey.getFirst());

    generateTextLegend(Utils.getEpicTickets(jiraTickets, "", epic));
  }

  private void generateLegendPersona(final Collection<JiraTicket> jiraTickets, String person) {
    log.debug("generateLegendPersona ({}) ", person);

    generateTextLegend(Utils.getPersonaTickets(jiraTickets, "", person));
  }

  private void generateLegendStatus(final Collection<JiraTicket> jiraTickets, String status) {
    log.debug("generateLegendStatus ({}) ", status);

    generateTextLegend(Utils.getStatusTickets(jiraTickets, status));
  }

  private void generateTicket(JiraTicket jiraTicket) {
    output.println(Utils.createClassHead(jiraTicket));
    // TODO: Manage the case of a too long summary
    output.println(ParseJiraTicketsConstants.DEFINITION_CLASS_SUMMARY
            + jiraTicket.summary.getFirst());
    if (!jiraTicket.assignee.isEmpty()) {
      output.println(ParseJiraTicketsConstants.DEFINITION_CLASS_ASSIGNED_TO
              + jiraTicket.assignee.getFirst());
    }
    output.println(ParseJiraTicketsConstants.DEFINITION_CLASS_STATUS
            + jiraTicket.status.getFirst());
    output.println(ParseJiraTicketsConstants.DEFINITION_CLASS_TYPE
            + jiraTicket.issueType.getFirst());
    if (!jiraTicket.storyPoints.isEmpty()) {
      output.println(ParseJiraTicketsConstants.DEFINITION_CLASS_ESTIMATE
              + jiraTicket.storyPoints.getFirst());
    }
    output.println("Total "+ParseJiraTicketsConstants.DEFINITION_CLASS_ESTIMATE
            + jiraTicket.totalstoryPoints);
    output.println("}");
    output.println();
  }

  private void generateTickets(final Collection<JiraTicket> jiraTickets) {
    output.println();
    output.println("' Jira Tickets =======");
    jiraTickets.forEach(this::generateTicket);
    output.println();
  }

  private void computeTicketsTotalPoints(final Collection<JiraTicket> jiraTickets) {
    log.info("computeTicketsTotalPoints");
    jiraTickets.forEach(jiraTicket -> {
//      log.info("  Parents for {}", jiraTicket.issueKey);
      JiraTicket parentJira = jiraTicket.parentJira;
      if (parentJira != null) {
        parentJira.totalstoryPoints = parentJira.totalstoryPoints + jiraTicket.totalstoryPoints;
      }
    });
  }

  private void generateTicketsPerKindLinks(final Collection<JiraTicket> jiraTickets, String kind) {
    // TODO: check if there is a duplication of code with the selection
    //  of these tickets based on links....
    HashMap<String, JiraTicket> selectedJiraTickets = new HashMap<>();

    jiraTickets.forEach(jiraTicket ->
            jiraTicket.inwardIssueLink.forEach(links -> {
              if (links.getName().contains(kind)) {
                links.getJiraTickets().forEach(link -> {
                  selectedJiraTickets.put(jiraTicket.issueKey.getFirst(), jiraTicket);
                  selectedJiraTickets.put(link.issueKey.getFirst(), link);
                });
              }
            })
    );

    output.println();
    output.println(ParseJiraTicketsConstants.HEADER_TICKETS);
    selectedJiraTickets.values().forEach(this::generateTicket);
    output.println();
  }

  private void generateTicketsPerPersonLinks(
          final Collection<JiraTicket> jiraTickets,
          String person) {
    log.debug("generateTicketsPerPersonLinks ({})", person);
    output.println();
    output.println(ParseJiraTicketsConstants.HEADER_TICKETS);
    Map<String, JiraTicket> selectedJiraTickets
            = Utils.getPersonaTickets(jiraTickets, "", person);
    selectedJiraTickets.values().forEach(this::generateTicket);
    output.println();
  }

  private void generateTicketsPerEpicLinks(
          final Map<String, JiraTicket>  jiraTickets,
          JiraTicket epic) {
    log.debug("generateTicketsPerEpicLinks ({})", epic.issueKey);
    output.println();
    output.println(ParseJiraTicketsConstants.HEADER_TICKETS);
    Map<String, JiraTicket> selectedJiraTickets
            = Utils.getEpicTickets(jiraTickets, "", epic);
    selectedJiraTickets.values().forEach(this::generateTicket);
    output.println();
  }

  private void generateTicketsPerStatusLinks(
          final Collection<JiraTicket> jiraTickets, String status) {
    log.debug("generateTicketsPerStatusLinks ({})", status);
    output.println();
    output.println(ParseJiraTicketsConstants.HEADER_TICKETS);
    Map<String, JiraTicket> selectedJiraTickets
            = Utils.getStatusTickets(jiraTickets, status);
    selectedJiraTickets.values().forEach(this::generateTicket);
    output.println();
  }

  // TODO: find a test case for the parent links
  private void generateParents(final Collection<JiraTicket> jiraTickets) {
    output.println();
    output.println("' Parents =======");
    jiraTickets.forEach(jiraTicket -> {
//      log.info("Parents for {}", jiraTicket.issueKey);
      if (jiraTicket.parentJira != null) {
        output.println("\"" + jiraTicket.parentJira.issueKey.getFirst()
                + "\" <|-- \"" + jiraTicket.issueKey.getFirst() + "\"");
      }
    });
    output.println();
  }

  private void generateLink(JiraTicket jiraTicket, JiraTicket link, JiraTicketLinks links) {
    generateLink(jiraTicket, link, links.getShortName());
  }

  private void generateLink(JiraTicket jiraTicket, JiraTicket link, String linksName) {
    output.println("\"" + jiraTicket.issueKey.getFirst()
            + ParseJiraTicketsConstants.DEFINITION_LINK_SIMPLE_MIDDLE
            + link.issueKey.getFirst()
            + ParseJiraTicketsConstants.DEFINITION_LINK_SIMPLE_END
            + linksName
      );
    output.println();
  }

  private void generateLinks(final Collection<JiraTicket> jiraTickets) {
    output.println();
    output.println(ParseJiraTicketsConstants.HEADER_LINKS);
    jiraTickets.forEach(jiraTicket ->
            jiraTicket.inwardIssueLink.forEach(links ->
                    links.getJiraTickets().forEach(link ->
                            generateLink(jiraTicket, link, links)
            )
        )
    );
    output.println();
  }

  private void generateSingleKindLinks(final Collection<JiraTicket> jiraTickets, String kind) {
    output.println();
    output.println(ParseJiraTicketsConstants.HEADER_LINKS);
    jiraTickets.forEach(jiraTicket ->
      jiraTicket.inwardIssueLink.forEach(links -> {
            if (links.getName().contains(kind)) {
              links.getJiraTickets().forEach(link ->
                      generateLink(jiraTicket, link, links)
              );
            }
          }
        )
    );

    output.println();
  }

  private void generateSinglePersonLinks(final Collection<JiraTicket> jiraTickets, String person) {
    output.println();
    output.println(ParseJiraTicketsConstants.HEADER_LINKS);
    jiraTickets.forEach(jiraTicket -> {
        if ((!jiraTicket.assignee.isEmpty())
              && (jiraTicket.assignee.getFirst().equalsIgnoreCase(person))) {
          jiraTicket.inwardIssueLink.forEach(links ->
                    links.getJiraTickets().forEach(link ->
                                    generateLink(jiraTicket, link, links)
                    )
            );
          }
        }
    );
    output.println();
  }

  private void generateSingleEpicLinks(final Map<String, JiraTicket>  jiraTickets, JiraTicket epic) {
    log.debug("generateSingleEpicLinks - {} ===========",epic.issueKey.getFirst());
    output.println();
    output.println(ParseJiraTicketsConstants.HEADER_LINKS);

    Map<String, JiraTicket> selectedJiraTickets
            = Utils.getEpicTickets(jiraTickets, "", epic);

    generateParents(selectedJiraTickets.values());

    selectedJiraTickets.values().forEach(jiraTicket -> {
        jiraTicket.inwardIssueLink.forEach(links ->
          links.getJiraTickets().forEach(link ->
                          generateLink(jiraTicket, link, links)
          )
        );
      }
    );
    output.println();
  }

  private void generateSingleStatusLinks(final Collection<JiraTicket> jiraTickets, String status) {
    output.println();
    output.println(ParseJiraTicketsConstants.HEADER_LINKS);
    jiraTickets.forEach(jiraTicket -> {
      if ((!jiraTicket.status.isEmpty()) && (jiraTicket.status.getFirst().equalsIgnoreCase(status)))
          jiraTicket.inwardIssueLink.forEach(links ->
            links.getJiraTickets().forEach(link ->
              output.println("\"" + jiraTicket.issueKey.getFirst()
                      + ParseJiraTicketsConstants.DEFINITION_LINK_SIMPLE_MIDDLE
                      + link.issueKey.getFirst()
                      + ParseJiraTicketsConstants.DEFINITION_LINK_SIMPLE_END
                      + links.getShortName())
            )
          );
        }
    );
    output.println();
  }


  private void generateFooter() {
    output.println();
    output.println("@enduml");
  }


  // TODO: Test the exception case
  // Generate reports for each links kind
  public void generateLinkKindReports(Map<String, JiraTicket> jiraTickets, String outputDir) {
    log.info("generateLinkKindReports [{}]", outputDir);

    jiraTicketLinkDescriptors.forEach(jiraTicketLinkDescriptor -> {
      String shortName = jiraTicketLinkDescriptor.getShortName();
      log.debug("jiraTicketLinkDescriptor : short name [{}]", shortName);
      log.debug("jiraTicketLinkDescriptor : [{}]", jiraTicketLinkDescriptor);

      int ticketsNumber = Utils.countTicketsForLinkKind(jiraTickets.values(), shortName);
      log.debug("jiraTicketLinkDescriptor : [{}] = {}", jiraTicketLinkDescriptor, ticketsNumber);
      if (ticketsNumber > 0) {
        try {
          String name = Utils.getNameForFile(jiraTicketLinkDescriptor.getShortName());
          FileOutputStream subfile = new FileOutputStream(outputDir + "jira-LinkType-"
                  + name + ParseJiraTicketsConstants.PUML_FILE_EXTENSION);
          output = new PrintStream(subfile, true);
          generateHeader("Jira Tickets for Link Type "
                  + jiraTicketLinkDescriptor.getShortName());
          generateLegend(jiraTickets.values(),
                  jiraTicketLinkDescriptor.getShortName());
          generateTicketsPerKindLinks(jiraTickets.values(),
                  jiraTicketLinkDescriptor.getShortName());
          generateSingleKindLinks(jiraTickets.values(),
                  jiraTicketLinkDescriptor.getShortName());
          generateFooter();
          output.close();
        } catch (IOException ex) {
          log.error(ex);
          ex.printStackTrace();
        }
      }
    });
  }

  // TODO: Test the exception case
  public void generateStatusReports(Map<String, JiraTicket> jiraTickets, String outputDir) {
    log.info("generateStatusReports [{}]", outputDir);

    // Generate reports for each status
    Set<String> stata = Utils.findStata(jiraTickets);
    stata.forEach(status -> {
      log.debug("stata : [{}]", status);
      try {
        String name = Utils.getNameForFile(status);
        FileOutputStream subfile = new FileOutputStream(outputDir
                + "jira-Status-" + name + ParseJiraTicketsConstants.PUML_FILE_EXTENSION);
        output = new PrintStream(subfile, true);
        generateHeader("Jira Tickets for Status " + status);
        generateLegendStatus(jiraTickets.values(), status);
        generateTicketsPerStatusLinks(jiraTickets.values(), status);
        generateSingleStatusLinks(jiraTickets.values(), status);
        generateFooter();
        output.close();
      } catch (IOException ex) {
        log.error(ex);
        ex.printStackTrace();
      }
    });
  }

  // TODO: Test the exception case
  public void generatePersonReports(Map<String, JiraTicket> jiraTickets, String outputDir) {
    log.info("generatePersonReports [{}]", outputDir);

    // Generate reports for each person
    Set<String> people = Utils.findPeople(jiraTickets);
    people.forEach(person -> {
      log.debug("people : [{}]", person);
      if (Utils.personHasDependingTickets(jiraTickets.values(), person)) {
        try {
          String name = Utils.getNameForFile(person);
          FileOutputStream subfile = new FileOutputStream(outputDir
                  + "jira-Person-" + name + ParseJiraTicketsConstants.PUML_FILE_EXTENSION);
          output = new PrintStream(subfile, true);
          generateHeader("Jira Tickets for Person " + person);
          generateLegendPersona(jiraTickets.values(), person);
          generateTicketsPerPersonLinks(jiraTickets.values(), person);
          generateSinglePersonLinks(jiraTickets.values(), person);
          generateFooter();
          output.close();
        } catch (IOException ex) {
          log.error(ex);
          ex.printStackTrace();
        }
      }
    });
  }

  // TODO: Test the exception case
  public void generateEpicReports(Map<String, JiraTicket> jiraTickets, String outputDir) {
    log.info("generateEpicReports [{}]", outputDir);

    // Generate reports for each epic
    Set<String> epics = Utils.findEpics(jiraTickets);
    epics.forEach(epic -> {
      log.debug("epic : [{}]", epic);
      try {
        JiraTicket epicTicket = Utils.findFromId(jiraTickets, epic);
        String epicName = epicTicket.summary.getFirst();
        String name = Utils.getNameForFile(epicName);
        log.debug("epicName : [{}]", epicName);
        FileOutputStream subfile = new FileOutputStream(outputDir
                + "jira-Epic-" + name + ParseJiraTicketsConstants.PUML_FILE_EXTENSION);
        output = new PrintStream(subfile, true);
        generateHeader("Jira Tickets for Epic " + epicName);
        generateLegendEpic(jiraTickets, epicTicket);
        generateTicketsPerEpicLinks(jiraTickets, epicTicket);
        generateSingleEpicLinks(jiraTickets, epicTicket);
        generateFooter();
        output.close();
      } catch (IOException ex) {
        log.error(ex);
        ex.printStackTrace();
      }
    });
  }

  public void generateBaseDiagram(Map<String, JiraTicket> jiraTickets,
                                  String outputDir) throws IOException {
    log.info("generateBaseDiagram [{}]", outputDir);

    // Reports generation -------------------------
    FileOutputStream file = new FileOutputStream(outputDir + "jira.puml");
    output = new PrintStream(file, true);
    generateHeader("Jira Tickets general diagram");
    generateLegend(jiraTickets.values(), null);
    generateTickets(jiraTickets.values());
    generateParents(jiraTickets.values());
    generateLinks(jiraTickets.values());
    generateFooter();
    output.close();
  }

  public boolean generateDiagrams(String fileName,
                                  String outputDir)
          throws IOException, CsvException {
    log.info("generateDiagrams [{}] [{}]", fileName, outputDir);

    if ((fileName == null) || (fileName.isBlank())) {
      log.error("generateDiagrams Input File not defined [{}]", outputDir);
      return false;
    }

    try (CSVReader reader = new CSVReader(new FileReader(fileName))) {

      // Load full file
      List<String[]> r = reader.readAll();
      log.info("Records : [{}]", r.size());

      // Load record definition and FieldDescriptors
      fieldDescriptors = Utils.loadDefinitions(r.getFirst(), jiraTicketLinkDescriptors);


      // Jira Ticket Records loading
      HashMap<String, JiraTicket> jiraTickets = new HashMap<>();

      for (int i = 1; i < r.size(); i++) {
        JiraTicket jiraTicket = new JiraTicket(r.get(i));
        jiraTickets.put(jiraTicket.issueKey.getFirst(), jiraTicket);
      }

      // Create link pointers from ticket id
      jiraTickets.values().forEach(jiraTicket -> jiraTicket.connectLinks(jiraTickets));

      log.info("Records : [{}]", jiraTickets.size());

      computeTicketsTotalPoints(jiraTickets.values());

      // Reports generation -------------------------
      generateBaseDiagram(jiraTickets, outputDir);

      // Generate reports for each links kind
      generateLinkKindReports(jiraTickets, outputDir);

      // Generate reports for each person
      generatePersonReports(jiraTickets, outputDir);

      // Generate reports for each Status
      generateStatusReports(jiraTickets, outputDir);

      // Generate reports for each Epic
      generateEpicReports(jiraTickets, outputDir);

      return true;
    }
  }

}