package it.brunasti.java.jira;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class JiraTicketDescriptor {
  static Logger log = LogManager.getLogger(JiraTicketDescriptor.class);

  // TODO : Reactivate for more complete Jira boards
  static FieldDescriptor assigneeFieldDescriptor;
  static FieldDescriptor assigneeIdFieldDescriptor;
//  static FieldDescriptor descriptionFieldDescriptor;
  static FieldDescriptor issueTypeFieldDescriptor;
  static FieldDescriptor issueIdFieldDescriptor;
  static FieldDescriptor issueKeyFieldDescriptor;
//  static FieldDescriptor parentFieldDescriptor;
//  static FieldDescriptor priorityFieldDescriptor;
  static FieldDescriptor statusFieldDescriptor;
  static FieldDescriptor summaryFieldDescriptor;
  static FieldDescriptor storyPointsFieldDescriptor;

//  static ArrayList<FieldDescriptor> inwardIssueLinkFieldDescriptor;
//  static ArrayList<FieldDescriptor> outwardIssueLinkFieldDescriptor;

  private JiraTicketDescriptor() {
  }

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
    // TODO : Reactivate for more complete Jira boards
//  Issuetype,Issuecode,Issue id,Samenvatting,Uitvoerder,Uitvoerder-ID,Status,Aangemaakt,Σ Oorspronkelijke schatting,Σ Bestede tijd
    issueTypeFieldDescriptor = findFieldDescriptor(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_ISSUE_TYPE);
    issueKeyFieldDescriptor = findFieldDescriptor(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_ISSUE_KEY);
    issueIdFieldDescriptor = findFieldDescriptor(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_ISSUE_ID);
    summaryFieldDescriptor = findFieldDescriptor(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_SUMMARY);
    assigneeFieldDescriptor = findFieldDescriptor(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_ASSIGNEE);
    assigneeIdFieldDescriptor = findFieldDescriptor(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_ASSIGNEE_ID);
    statusFieldDescriptor = findFieldDescriptor(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_STATUS);
    storyPointsFieldDescriptor = findFieldDescriptor(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_STORY_POINT);

//    descriptionFieldDescriptor = findFieldDescriptor(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_DESCRIPTION);
//    parentFieldDescriptor = findFieldDescriptor(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_PARENT);
//    priorityFieldDescriptor = findFieldDescriptor(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_PRIORITY);
//
//    inwardIssueLinkFieldDescriptor = findLinksFieldDescriptors(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_INWARD_ISSUE_LINK);
//    outwardIssueLinkFieldDescriptor = findLinksFieldDescriptors(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_OUTWARD_ISSUE_LINK);
  }


}
