package it.brunasti.java.jira;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class JiraTicketDescriptor {
  static Logger log = LogManager.getLogger(JiraTicketDescriptor.class);

  static FieldDescriptor assigneeFieldDescriptor;
  static FieldDescriptor descriptionFieldDescriptor;
  static FieldDescriptor issueTypeFieldDescriptor;
  static FieldDescriptor issueIdFieldDescriptor;
  static FieldDescriptor issueKeyFieldDescriptor;
  static FieldDescriptor parentFieldDescriptor;
  static FieldDescriptor priorityFieldDescriptor;
  static FieldDescriptor statusFieldDescriptor;
  static FieldDescriptor summaryFieldDescriptor;

  static ArrayList<FieldDescriptor> inwardIssueLinkFieldDescriptor;
  static ArrayList<FieldDescriptor> outwardIssueLinkFieldDescriptor;

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
    assigneeFieldDescriptor = findFieldDescriptor(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_ASSIGNEE);
    descriptionFieldDescriptor = findFieldDescriptor(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_DESCRIPTION);
    issueTypeFieldDescriptor = findFieldDescriptor(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_ISSUE_TYPE);
    issueIdFieldDescriptor = findFieldDescriptor(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_ISSUE_ID);
    issueKeyFieldDescriptor = findFieldDescriptor(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_ISSUE_KEY);
    parentFieldDescriptor = findFieldDescriptor(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_PARENT);
    priorityFieldDescriptor = findFieldDescriptor(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_PRIORITY);
    statusFieldDescriptor = findFieldDescriptor(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_STATUS);
    summaryFieldDescriptor = findFieldDescriptor(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_SUMMARY);

    inwardIssueLinkFieldDescriptor = findLinksFieldDescriptors(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_INWARD_ISSUE_LINK);
    outwardIssueLinkFieldDescriptor = findLinksFieldDescriptors(fieldDescriptors, ParseJiraTicketsConstants.FIELD_NAME_OUTWARD_ISSUE_LINK);
  }


}
