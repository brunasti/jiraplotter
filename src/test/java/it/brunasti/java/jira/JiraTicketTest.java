package it.brunasti.java.jira;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JiraTicketTest {

//  String[] simpleFields;
  String[] complexFields;
  String[] testRecord;
//  String name;
  String complexName;

  @BeforeEach
  void setup() throws IOException, CsvException {
//    simpleFields = new String[5];
//    simpleFields[0] = "A";
//    simpleFields[1] = "B";
//    simpleFields[2] = "C";
//    simpleFields[3] = "A";
//    simpleFields[4] = "E";
//
//    name = "A";

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
//    assertEquals(0,jiraTicket.getAssignee().size());
//    assertEquals(0,jiraTicket.getInwardIssueLink().size());
    assertNotNull(jiraTicket.toString());
//    System.out.println(jiraTicket.toString());
  }

  @Test
  void connectLinks() {
    JiraTicket jiraTicket = new JiraTicket(complexFields);
    assertNotNull(jiraTicket);
    assertDoesNotThrow(()->jiraTicket.connectLinks(null));
  }

}