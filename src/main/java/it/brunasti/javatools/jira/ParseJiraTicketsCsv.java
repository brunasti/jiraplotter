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


  static Logger log = LogManager.getLogger(ParseJiraTicketsCsv.class);

  List<FieldDescriptor> fieldDescriptors = new ArrayList<>();
  ArrayList<JiraTicketLinkDescriptor> jiraTicketLinkDescriptors = new ArrayList<>();
  PrintStream output;




  private void generateHeader() {
    output.println("@startuml");
    output.println("'https://plantuml.com/class-diagram");
    output.println();
    output.println("' GENERATE CLASS DIAGRAM ===========");
    output.println("' Generated at    : " + new Date());
    output.println();
    output.println("hide empty members");
    output.println();
  }

  private void generateTextLegend(final HashMap<String, JiraTicket> selectedJiraTickets) {
    HashSet<String> stata = new HashSet<>();
    HashSet<String> types = new HashSet<>();

    selectedJiraTickets.values().forEach(jiraTicket -> stata.add(jiraTicket.status.getFirst()));
    selectedJiraTickets.values().forEach(jiraTicket -> types.add(jiraTicket.issueType.getFirst()));

    output.println("legend");
    output.println("Jira Ticket Status");
    output.println("----");
    stata.forEach(status ->
      output.println("("+status+") : " + Utils.countTicketsInStatus(selectedJiraTickets.values(),status))
    );
    output.println("----");
    output.println("Jira Ticket Types");
    output.println("----");
    types.forEach(type ->
      output.println("("+type+") : "+Utils.countTicketsOfType(selectedJiraTickets.values(),type))
    );
    output.println("end legend");
    output.println();
  }

  private void generateLegend(final Collection<JiraTicket> jiraTickets, String linkKind) {
    generateTextLegend(Utils.getTicketsForLinkKind(jiraTickets, linkKind));
  }

  private void generateLegendPersona(final Collection<JiraTicket> jiraTickets, String linkKind, String person) {
    log.debug("generateLegendPersona ({}) ({}) ", linkKind, person);

    HashMap<String, JiraTicket> selectedJiraTickets = new HashMap<>();

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

    generateTextLegend(selectedJiraTickets);
  }

  private void generateTicket(JiraTicket jiraTicket) {
    output.println(Utils.createClassHead(jiraTicket));
    if (!jiraTicket.assignee.isEmpty()) {
      output.println(ParseJiraTicketsConstants.DEFINITION_CLASS_ASSIGNED_TO + jiraTicket.assignee.getFirst());
    }
    output.println(ParseJiraTicketsConstants.DEFINITION_CLASS_STATUS + jiraTicket.status.getFirst());
    output.println(ParseJiraTicketsConstants.DEFINITION_CLASS_TYPE + jiraTicket.issueType.getFirst());
    output.println("}");
    output.println();
  }

  private void generateTickets(final Collection<JiraTicket> jiraTickets) {
    output.println();
    output.println("' Jira Tickets =======");
    jiraTickets.forEach(jiraTicket -> generateTicket(jiraTicket));
    output.println();
  }

  private void generateTicketsPerKindLinks(final Collection<JiraTicket> jiraTickets, String kind) {
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
    output.println("' Tickets =======");
    selectedJiraTickets.values().forEach(jiraTicket -> generateTicket(jiraTicket));
    output.println();
  }

  private void generateTicketsPerPersonLinks(final Collection<JiraTicket> jiraTickets, String person) {
    HashMap<String, JiraTicket> selectedJiraTickets = new HashMap<>();
    log.debug("generateTicketsPerPersonLinks ({})", person);

    jiraTickets.forEach(jiraTicket -> {
      if ((!jiraTicket.assignee.isEmpty()) && (jiraTicket.assignee.getFirst().equalsIgnoreCase(person))) {
          log.debug("generateTicketsPerPersonLinks [{}]", jiraTicket.issueKey.getFirst());
          selectedJiraTickets.put(jiraTicket.issueKey.getFirst(), jiraTicket);
          jiraTicket.inwardIssueLink.forEach(links ->
              links.getJiraTickets().forEach(link -> selectedJiraTickets.put(link.issueKey.getFirst(), link))
          );
        }
    });

    output.println();
    output.println("' Tickets =======");
    selectedJiraTickets.values().forEach(jiraTicket -> generateTicket(jiraTicket));
    output.println();
  }

  // TODO: find a test case for the parent links
  private void generateParents(final Collection<JiraTicket> jiraTickets) {
    output.println();
    output.println("' Parents =======");
    jiraTickets.forEach(jiraTicket -> {
      if(jiraTicket.parentJira != null) {
        output.println("\"" + jiraTicket.parentJira.issueKey.getFirst() + "\" <|-- \"" + jiraTicket.issueKey.getFirst()+ "\"");
      }
    });
    output.println();
  }

  private void generateLinks(final Collection<JiraTicket> jiraTickets) {
    output.println();
    output.println(ParseJiraTicketsConstants.HEADER_LINKS);
    jiraTickets.forEach(jiraTicket ->
      jiraTicket.inwardIssueLink.forEach(links ->
        links.getJiraTickets().forEach(link ->
          output.println("\"" + jiraTicket.issueKey.getFirst()
                  + ParseJiraTicketsConstants.DEFINITION_LINK_SIMPLE_MIDDLE + link.issueKey.getFirst()
                  + ParseJiraTicketsConstants.DEFINITION_LINK_SIMPLE_END +links.getShortName())
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
            output.println("\"" + jiraTicket.issueKey.getFirst()
                    + ParseJiraTicketsConstants.DEFINITION_LINK_SIMPLE_MIDDLE + link.issueKey.getFirst()
                    + ParseJiraTicketsConstants.DEFINITION_LINK_SIMPLE_END + links.getShortName())
          );
        }
      })
    );
    output.println();
  }

  private void generateSinglePersonLinks(final Collection<JiraTicket> jiraTickets, String person) {
    output.println();
    output.println(ParseJiraTicketsConstants.HEADER_LINKS);
    jiraTickets.forEach(jiraTicket -> {
      if ((!jiraTicket.assignee.isEmpty()) && (jiraTicket.assignee.getFirst().equalsIgnoreCase(person)))
          jiraTicket.inwardIssueLink.forEach(links ->
            links.getJiraTickets().forEach(link ->
              output.println("\"" + jiraTicket.issueKey.getFirst()
                      + ParseJiraTicketsConstants.DEFINITION_LINK_SIMPLE_MIDDLE + link.issueKey.getFirst()
                      + ParseJiraTicketsConstants.DEFINITION_LINK_SIMPLE_END + links.getShortName())
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


  // TODO: Decompose in smaller functions....
  public void generateDiagrams(String fileName, String outputDir) throws IOException, CsvException {

    try (CSVReader reader = new CSVReader(new FileReader(fileName))) {

      // Load full file
      List<String[]> r = reader.readAll();
      log.info("Records : [{}]", r.size());

      // Load record definition and FieldDescriptors
      fieldDescriptors = Utils.loadDefinitions(r.getFirst(), jiraTicketLinkDescriptors);


      // Jira Ticket Records loading
      HashMap<String, JiraTicket> jiraTickets = new HashMap<>();

      for (int i=1; i<r.size(); i++) {
        JiraTicket jiraTicket = new JiraTicket(r.get(i));
        jiraTickets.put(jiraTicket.issueKey.getFirst(), jiraTicket);
      }

      // Create link pointers from ticket id
      jiraTickets.values().forEach(jiraTicket -> jiraTicket.connectLinks(jiraTickets));

      log.info("Records : [{}]",jiraTickets.size());


      // Reports generation -------------------------
      FileOutputStream file = new FileOutputStream(outputDir + "jira.puml");
      output = new PrintStream(file, true);
      generateHeader();
      generateLegend(jiraTickets.values(), "");
      generateTickets(jiraTickets.values());
      generateParents(jiraTickets.values());
      generateLinks(jiraTickets.values());
      generateFooter();
      output.close();


      // Generate reports for each links kind
      jiraTicketLinkDescriptors.forEach(jiraTicketLinkDescriptor -> {
        String shortName = jiraTicketLinkDescriptor.getShortName();
        log.debug("jiraTicketLinkDescriptor : short name [{}]",shortName);
        log.debug("jiraTicketLinkDescriptor : [{}]",jiraTicketLinkDescriptor);

        int ticketsNumber = Utils.countTicketsForLinkKind(jiraTickets.values(), shortName);
        log.debug("jiraTicketLinkDescriptor : [{}] = {}",jiraTicketLinkDescriptor, ticketsNumber);
        if (ticketsNumber > 0) {
          try {
            FileOutputStream subfile = new FileOutputStream(outputDir + "jira" + jiraTicketLinkDescriptor.getShortName() + ".puml");
            output = new PrintStream(subfile, true);
            generateHeader();
            generateLegend(jiraTickets.values(), jiraTicketLinkDescriptor.getShortName());
            generateTicketsPerKindLinks(jiraTickets.values(), jiraTicketLinkDescriptor.getShortName());
            generateSingleKindLinks(jiraTickets.values(), jiraTicketLinkDescriptor.getShortName());
            generateFooter();
            output.close();
          } catch (Exception ex) {
            log.error(ex);
            ex.printStackTrace();
          }
        }
      });

      // Generate reports for each person
      Set<String> people = Utils.findPeople(jiraTickets);
      people.forEach(person -> {
        log.debug("people : [{}]",person);
        if (Utils.personHasDependingTickets(jiraTickets.values(),person)) {
          try {
            FileOutputStream subfile = new FileOutputStream(outputDir + "jira-" + person + ".puml");
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