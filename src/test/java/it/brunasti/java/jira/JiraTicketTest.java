package it.brunasti.java.jira;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JiraTicketTest {

  String[] complexFields;
  String[] testRecord;
  String complexName;

  @BeforeEach
  void setup() throws IOException, CsvException {
    String fileName = "./src/test/resources/jira-open.csv";
    try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
      List<String[]> r = reader.readAll();

      complexFields = r.getFirst();
    };

    fileName = "./src/test/resources/jira-open.csv";
    try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
      List<String[]> r = reader.readAll();

      testRecord = r.get(1);
    };

    complexName = ParseJiraTicketsConstants.FIELD_NAME_PARENT;
  }


  @Test
  void readFromCSVRow() {
    JiraTicket jiraTicket = new JiraTicket(testRecord);
    assertNotNull(jiraTicket);
    assertNotNull(jiraTicket.toString());
  }

  @Test
  void connectLinks() {
    JiraTicket jiraTicket = new JiraTicket(complexFields);
    assertNotNull(jiraTicket);
    assertDoesNotThrow(()->jiraTicket.connectLinks(null));
  }

  @Test
  void readLinks() {
    JiraTicket jiraTicket = new JiraTicket(complexFields);
    ArrayList<FieldDescriptor> fieldDescriptors = new ArrayList<>();
    assertDoesNotThrow(()->jiraTicket.readLinks(testRecord, fieldDescriptors));
    assertDoesNotThrow(()->jiraTicket.readLinks(testRecord, null));
  }

  @Test
  void readField() {
    JiraTicket jiraTicket = new JiraTicket(complexFields);
    FieldDescriptor fieldDescriptor = new FieldDescriptor(complexFields, "NULL");
    assertDoesNotThrow(()->jiraTicket.readField(testRecord, fieldDescriptor));
    assertDoesNotThrow(()->jiraTicket.readField(testRecord, null));
  }
}