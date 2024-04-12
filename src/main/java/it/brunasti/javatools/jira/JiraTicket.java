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


  static final String Field_Name_Assignee = "Assignee";
  static final String Field_Name_Description = "Description";
  static final String Field_Name_Issue_type = "Issue type";
  static final String Field_Name_Issue_id = "Issue id";
  static final String Field_Name_Issue_key = "Issue key";
  static final String Field_Name_Parent = "Parent";
  static final String Field_Name_Priority = "Priority";
  static final String Field_Name_Status = "Status";

  static final String Field_Name_Inward_issue_link = "Inward issue link";
  static final String Field_Name_Outward_issue_link = "Outward issue link";


  static FieldDescriptor assignee_field_descriptor;
  static FieldDescriptor description_field_descriptor;
  static FieldDescriptor issue_Type_field_descriptor;
  static FieldDescriptor issue_Id_field_descriptor;
  static FieldDescriptor issue_Key_field_descriptor;
  static FieldDescriptor parent_field_descriptor;
  static FieldDescriptor priority_field_descriptor;
  static FieldDescriptor status_field_descriptor;

  static ArrayList<FieldDescriptor> inward_Issue_Link_field_descriptor;
  static ArrayList<FieldDescriptor> outward_Issue_Link_field_descriptor;


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
    assignee_field_descriptor = findFieldDescriptor(fieldDescriptors, Field_Name_Assignee);
    description_field_descriptor = findFieldDescriptor(fieldDescriptors, Field_Name_Description);
    issue_Type_field_descriptor = findFieldDescriptor(fieldDescriptors, Field_Name_Issue_type);
    issue_Id_field_descriptor = findFieldDescriptor(fieldDescriptors, Field_Name_Issue_id);
    issue_Key_field_descriptor = findFieldDescriptor(fieldDescriptors, Field_Name_Issue_key);
    parent_field_descriptor = findFieldDescriptor(fieldDescriptors, Field_Name_Parent);
    priority_field_descriptor = findFieldDescriptor(fieldDescriptors, Field_Name_Priority);
    status_field_descriptor = findFieldDescriptor(fieldDescriptors, Field_Name_Status);
    inward_Issue_Link_field_descriptor = findLinksFieldDescriptors(fieldDescriptors, Field_Name_Inward_issue_link);
    outward_Issue_Link_field_descriptor = findLinksFieldDescriptors(fieldDescriptors, Field_Name_Outward_issue_link);
  }


  ArrayList<String>  assignee;
  ArrayList<String>  description;
  ArrayList<String>  issue_Type;
  ArrayList<String>  issue_Id;
  ArrayList<String>  issue_Key;
  ArrayList<String>  parent;
  ArrayList<String>  priority;
  ArrayList<String>  status;

  ArrayList<JiraTiketLinks> inward_Issue_Link;
  ArrayList<JiraTiketLinks> outward_Issue_Link;

  JiraTicket parentJira;

  public JiraTicket(String[] fields) {
    readFromCSVRow(fields);
  }


  ArrayList<JiraTiketLinks> readLinks(String[] fields, ArrayList<FieldDescriptor> fieldDescriptors) {
    ArrayList<JiraTiketLinks> links = new ArrayList<>();

    fieldDescriptors.forEach(fieldDescriptor -> {
      JiraTiketLinks jiraTiketLinks = new JiraTiketLinks();
      jiraTiketLinks.name = fieldDescriptor.name;
      jiraTiketLinks.links = readField(fields, fieldDescriptor);
      links.add(jiraTiketLinks);
    });

    return links;
  }

  ArrayList<String> readField(String[] fields, FieldDescriptor fieldDescriptor) {
    ArrayList<String> values = new ArrayList<>();

    fieldDescriptor.indexes.forEach(index -> {
      if ((fields[index] != null) && (!fields[index].isBlank())) {
        values.add(fields[index]);
      }
    });

    return values;
  }

  void readFromCSVRow(String[] fields) {
    assignee = readField(fields, assignee_field_descriptor);
    description = readField(fields, description_field_descriptor);
    issue_Type = readField(fields, issue_Type_field_descriptor);
    issue_Id = readField(fields, issue_Id_field_descriptor);
    issue_Key = readField(fields, issue_Key_field_descriptor);
    parent = readField(fields, parent_field_descriptor);
    priority = readField(fields, priority_field_descriptor);
    status = readField(fields, status_field_descriptor);
    inward_Issue_Link = readLinks(fields, inward_Issue_Link_field_descriptor);
    outward_Issue_Link = readLinks(fields, outward_Issue_Link_field_descriptor);
  }


  void connectLinks(HashMap<String, JiraTicket> jiraTickets) {
    if (parent.size() > 0) {
      parentJira = jiraTickets.get(parent.get(0));
    }

    inward_Issue_Link.forEach(links -> {
      links.getLinks().forEach(link -> {
        JiraTicket ticket = ParseJiraTicketsCsv.findFromKey(jiraTickets, link);
        if (ticket != null) {
          links.jiraTikets.add(ticket);
        } else {
          log.error("connectLinks  not possible : [{}] to [{}]", issue_Key, link);
        }
      });
    });

  }

}
