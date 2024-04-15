package it.brunasti.javatools.jira;

import lombok.Getter;
import lombok.ToString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

@ToString
@Getter
public class JiraTicket {
  static Logger log = LogManager.getLogger(JiraTicket.class);


  static final String FIELD_NAME_ASSIGNEE = "Assignee";
  static final String FIELD_NAME_DESCRIPTION = "Description";
  static final String FIELD_NAME_ISSUE_TYPE = "Issue type";
  static final String FIELD_NAME_ISSUE_ID = "Issue id";
  static final String FIELD_NAME_ISSUE_KEY = "Issue key";
  static final String FIELD_NAME_PARENT = "Parent";
  static final String FIELD_NAME_PRIORITY = "Priority";
  static final String FIELD_NAME_STATUS = "Status";

  static final String FIELD_NAME_INWARD_ISSUE_LINK = "Inward issue link";
  static final String FIELD_NAME_OUTWARD_ISSUE_LINK = "Outward issue link";


  static FieldDescriptor assigneeFieldDescriptor;
  static FieldDescriptor descriptionFieldDescriptor;
  static FieldDescriptor issueTypeFieldDescriptor;
  static FieldDescriptor issueIdFieldDescriptor;
  static FieldDescriptor issueKeyFieldDescriptor;
  static FieldDescriptor parentFieldDescriptor;
  static FieldDescriptor priorityFieldDescriptor;
  static FieldDescriptor statusFieldDescriptor;

  static ArrayList<FieldDescriptor> inwardIssueLinkFieldDescriptor;
  static ArrayList<FieldDescriptor> outwardIssueLinkFieldDescriptor;


  static FieldDescriptor findFieldDescriptor(ArrayList<FieldDescriptor> fieldDescriptors, String fieldName) {
    log.debug("findFieldDescriptor for {}",fieldName);
    AtomicReference<FieldDescriptor> fieldDescriptor = new AtomicReference<>();
    fieldDescriptors.forEach(field -> {
      if (field.name.equalsIgnoreCase(fieldName)) {
        fieldDescriptor.set(field);
      }
    });
    log.debug("findFieldDescriptor for {} : {}",fieldName, fieldDescriptor.get());
    return fieldDescriptor.get();
  }

  static ArrayList<FieldDescriptor> findLinksFieldDescriptors(ArrayList<FieldDescriptor> fieldDescriptors, String fieldName) {
    log.debug("findLinksFieldDescriptors for {}",fieldName);
    ArrayList<FieldDescriptor> linksFieldDescriptors = new ArrayList<>();
    fieldDescriptors.forEach(field -> {
      if (field.name.startsWith(fieldName)) {
        linksFieldDescriptors.add(field);
      }
    });
    log.debug("findLinksFieldDescriptors for {} : {}",fieldName, linksFieldDescriptors.size());
    log.debug("findLinksFieldDescriptors for {} : {}",fieldName, linksFieldDescriptors.toArray());
    return linksFieldDescriptors;
  }

  static void readCSVDefinition(ArrayList<FieldDescriptor> fieldDescriptors) {
    log.debug("readCSVDefinition");
    assigneeFieldDescriptor = findFieldDescriptor(fieldDescriptors, FIELD_NAME_ASSIGNEE);
    descriptionFieldDescriptor = findFieldDescriptor(fieldDescriptors, FIELD_NAME_DESCRIPTION);
    issueTypeFieldDescriptor = findFieldDescriptor(fieldDescriptors, FIELD_NAME_ISSUE_TYPE);
    issueIdFieldDescriptor = findFieldDescriptor(fieldDescriptors, FIELD_NAME_ISSUE_ID);
    issueKeyFieldDescriptor = findFieldDescriptor(fieldDescriptors, FIELD_NAME_ISSUE_KEY);
    parentFieldDescriptor = findFieldDescriptor(fieldDescriptors, FIELD_NAME_PARENT);
    priorityFieldDescriptor = findFieldDescriptor(fieldDescriptors, FIELD_NAME_PRIORITY);
    statusFieldDescriptor = findFieldDescriptor(fieldDescriptors, FIELD_NAME_STATUS);
    inwardIssueLinkFieldDescriptor = findLinksFieldDescriptors(fieldDescriptors, FIELD_NAME_INWARD_ISSUE_LINK);
    outwardIssueLinkFieldDescriptor = findLinksFieldDescriptors(fieldDescriptors, FIELD_NAME_OUTWARD_ISSUE_LINK);
  }


  ArrayList<String> assignee;
  ArrayList<String> description;
  ArrayList<String> issueType;
  ArrayList<String> issueId;
  ArrayList<String> issueKey;
  ArrayList<String> parent;
  ArrayList<String> priority;
  ArrayList<String> status;

  ArrayList<JiraTicketLinks> inwardIssueLink;
  ArrayList<JiraTicketLinks> outwardIssueLink;

  JiraTicket parentJira;

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
    assignee = readField(fields, assigneeFieldDescriptor);
    description = readField(fields, descriptionFieldDescriptor);
    issueType = readField(fields, issueTypeFieldDescriptor);
    issueId = readField(fields, issueIdFieldDescriptor);
    issueKey = readField(fields, issueKeyFieldDescriptor);
    parent = readField(fields, parentFieldDescriptor);
    priority = readField(fields, priorityFieldDescriptor);
    status = readField(fields, statusFieldDescriptor);
    inwardIssueLink = readLinks(fields, inwardIssueLinkFieldDescriptor);
    outwardIssueLink = readLinks(fields, outwardIssueLinkFieldDescriptor);
  }


  void connectLinks(HashMap<String, JiraTicket> jiraTickets) {
    if (!parent.isEmpty()) {
      parentJira = jiraTickets.get(parent.getFirst());
    }

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
