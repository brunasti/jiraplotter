package it.brunasti.javatools.jira;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JiraTicketLinkDescriptorTest {

  String[] fields;
  String name;
  FieldDescriptor fieldDescriptor;

  @BeforeEach
  void setup() {
    fields = new String[5];
    fields[0] = "A";
    fields[1] = "B";
    fields[2] = "C";
    fields[3] = "A (X)";
    fields[4] = "E";
    name = "A (X)";
    fieldDescriptor = new FieldDescriptor(fields, name);
  }

  @Test
  void getShortName() {
    JiraTicketLinkDescriptor jiraTicketLinkDescriptor = new JiraTicketLinkDescriptor(name,fieldDescriptor);
    assertNotNull(jiraTicketLinkDescriptor);
    assertNotNull(jiraTicketLinkDescriptor.getShortName());
    assertEquals("X", jiraTicketLinkDescriptor.getShortName());
  }

  @Test
  void getName() {
    JiraTicketLinkDescriptor jiraTicketLinkDescriptor = new JiraTicketLinkDescriptor(name,fieldDescriptor);
    assertNotNull(jiraTicketLinkDescriptor);
    assertNotNull(jiraTicketLinkDescriptor.getName());
    assertEquals("A (X)", jiraTicketLinkDescriptor.getName());
  }

  @Test
  void getFieldDescriptor() {
    JiraTicketLinkDescriptor jiraTicketLinkDescriptor = new JiraTicketLinkDescriptor(name,fieldDescriptor);
    assertNotNull(jiraTicketLinkDescriptor);
    assertNotNull(jiraTicketLinkDescriptor.getFieldDescriptor());
    assertEquals(fieldDescriptor, jiraTicketLinkDescriptor.getFieldDescriptor());
  }

  @Test
  void testToString() {
    JiraTicketLinkDescriptor jiraTicketLinkDescriptor = new JiraTicketLinkDescriptor(name,fieldDescriptor);
    assertNotNull(jiraTicketLinkDescriptor);
    assertNotNull(jiraTicketLinkDescriptor.toString());
    assertEquals("JiraTicketLinkDescriptor(name=A (X), fieldDescriptor=FieldDescriptor(name=A (X), indexes=[3]))",jiraTicketLinkDescriptor.toString());
  }
}