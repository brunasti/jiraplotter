package it.brunasti.javatools.jira;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {


  String[] simpleFields;
  String[] header;
  String[] testRecord;
  String name;
  String complexName;

  @BeforeEach
  void setup() throws IOException, CsvException {
    simpleFields = new String[5];
    simpleFields[0] = "A";
    simpleFields[1] = "B";
    simpleFields[2] = "C";
    simpleFields[3] = "A";
    simpleFields[4] = "E";

    name = "A";

    String fileName = "./src/test/resources/jira-full.csv";
    try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
      List<String[]> r = reader.readAll();

      header = r.getFirst();
    };

    fileName = "./src/test/resources/jira-full.csv";
    try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
      List<String[]> r = reader.readAll();

      testRecord = r.get(1);
    };

    complexName = JiraTicket.FIELD_NAME_PARENT;
  }


  @Test
  void test_getShortName() {
    String input = "A (B)";
    assertEquals("B",Utils.getShortName(input));
    // TODO: Manage this situation in the getShortName function
    assertThrows(StringIndexOutOfBoundsException.class, ()->Utils.getShortName(""));
  }

  @Test
  void test_countSameFields() {
    String[] fields = new String[2];
    fields[0] = "A";
    fields[1] = "B";
    String name = "A";
    assertEquals(1,Utils.countSameFields(fields,name));
  }

  @Test
  void test_getShortMark() {
    String input = "A B";
    assertEquals("B",Utils.getShortMark(input));
    assertEquals("A",Utils.getShortMark("A"));
  }

  @Test
  void test_findFromKey() {
    Map<String, JiraTicket> jiraTickets = new HashMap<>();
    String key = "CNG-1388";

    assertNull(Utils.findFromKey(jiraTickets, key));

    ParseJiraTicketsCsv.loadDefinitions(header);

    JiraTicket jiraTicket = new JiraTicket(testRecord);
    System.out.println("Jira ticket : " + jiraTicket);
    System.out.println("Jira ticket id : " + jiraTicket.issueId);
    System.out.println("Jira ticket key : " + jiraTicket.issueKey);
    jiraTickets.put(key, jiraTicket);
    assertNotNull(Utils.findFromKey(jiraTickets, key));
    jiraTickets.put("A", jiraTicket);
    assertNotNull(Utils.findFromKey(jiraTickets, "A"));
    assertNull(Utils.findFromKey(jiraTickets, "X"));
  }
}