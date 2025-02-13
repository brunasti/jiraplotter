package it.brunasti.java.jira;

import lombok.Getter;
import lombok.ToString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

@ToString
@Getter
public class JiraTicket {
  static Logger log = LogManager.getLogger(JiraTicket.class);

  ArrayList<String> assignee;
  ArrayList<String> description;
  ArrayList<String> issueType;
  ArrayList<String> issueId;
  ArrayList<String> issueKey;
  ArrayList<String> parent;
  ArrayList<String> priority;
  ArrayList<String> status;
  ArrayList<String> summary;
  ArrayList<String> storyPoints;

  ArrayList<JiraTicketLinks> inwardIssueLink = new ArrayList<>();
  ArrayList<JiraTicketLinks> outwardIssueLink = new ArrayList<>();

  JiraTicket parentJira;
  int totalstoryPoints = 0;

  public JiraTicket(String[] fields) {
    readFromCSVRow(fields);
  }


  ArrayList<JiraTicketLinks> readLinks(String[] fields, ArrayList<FieldDescriptor> fieldDescriptors) {
    ArrayList<JiraTicketLinks> links = new ArrayList<>();

    if (fieldDescriptors != null) {
      fieldDescriptors.forEach(fieldDescriptor -> {
        JiraTicketLinks jiraTicketLinks = new JiraTicketLinks(fieldDescriptor.getName(), readField(fields, fieldDescriptor), new ArrayList<>());
        links.add(jiraTicketLinks);
      });
    } else {
      log.error("readLinks : fieldDescriptors is null");
    }
    return links;
  }

  ArrayList<String> readField(String[] fields, FieldDescriptor fieldDescriptor) {
    ArrayList<String> values = new ArrayList<>();

    if (fieldDescriptor != null) {
      fieldDescriptor.indexes.forEach(index -> {
        if ((fields[index] != null) && (!fields[index].isBlank())) {
          values.add(fields[index]);
        }
      });
    } else {
      log.error("readField : fieldDescriptor is null");
    }
    return values;
  }

  void readFromCSVRow(String[] fields) {
    // TODO : Reactivate for more complete Jira boards
    assignee = readField(fields, JiraTicketDescriptor.assigneeFieldDescriptor);
//    description = readField(fields, JiraTicketDescriptor.descriptionFieldDescriptor);
    issueType = readField(fields, JiraTicketDescriptor.issueTypeFieldDescriptor);
    issueId = readField(fields, JiraTicketDescriptor.issueIdFieldDescriptor);
    issueKey = readField(fields, JiraTicketDescriptor.issueKeyFieldDescriptor);
//    parent = readField(fields, JiraTicketDescriptor.parentFieldDescriptor);
//    priority = readField(fields, JiraTicketDescriptor.priorityFieldDescriptor);
    status = readField(fields, JiraTicketDescriptor.statusFieldDescriptor);
    summary = readField(fields, JiraTicketDescriptor.summaryFieldDescriptor);
    storyPoints = readField(fields, JiraTicketDescriptor.storyPointsFieldDescriptor);

    try {
      if (!storyPoints.isEmpty())
        totalstoryPoints = Math.round(Float.parseFloat(storyPoints.getFirst()));
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    // TODO : Reactivate for more complete Jira boards
//    inwardIssueLink = readLinks(fields, JiraTicketDescriptor.inwardIssueLinkFieldDescriptor);
//    outwardIssueLink = readLinks(fields, JiraTicketDescriptor.outwardIssueLinkFieldDescriptor);
  }


  void connectLinks(HashMap<String, JiraTicket> jiraTickets) {
//    log.info("Linking : {} - {}", issueKey, issueId);
    if (jiraTickets == null) return;

    if ((parent != null) && (!parent.isEmpty())) {
//      log.info("Linking to parent : {}", parent.getFirst());
      parentJira = Utils.findFromId(jiraTickets, parent.getFirst());
    }

    if (inwardIssueLink != null) {
      inwardIssueLink.forEach(links ->
              links.getLinks().forEach(link -> {
                JiraTicket ticket = Utils.findFromKey(jiraTickets, link);
                if (ticket != null) {
                  links.getJiraTickets().add(ticket);
                } else {
                  log.error("connectLinks  not possible : [{}] to [{}]", issueKey.getFirst(), link);
                }
              })
      );
    }
  }

}
