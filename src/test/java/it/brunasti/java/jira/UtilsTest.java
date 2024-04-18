package it.brunasti.java.jira;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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
    }

    fileName = "./src/test/resources/jira-full.csv";
    try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
      List<String[]> r = reader.readAll();

      testRecord = r.get(1);
    }

    complexName = ParseJiraTicketsConstants.FIELD_NAME_PARENT;
  }


  @Test
  void test_getShortName() {
    String input = "A (B)";
    assertEquals("B",Utils.getShortName(input));
    assertDoesNotThrow(()->Utils.getShortName(""));
    assertEquals("B",Utils.getShortName("B"));
  }

  @Test
  void test_countSameFields() {
    String[] fields = new String[2];
    fields[0] = "A";
    fields[1] = "B";
    String name = "A";
    assertEquals(1,Utils.countSameFields(fields,name));
  }

//  @Test
//  void test_getShortMark() {
//    String input = "A B";
//    assertEquals("B",Utils.getShortMark(input));
//    assertEquals("A",Utils.getShortMark("A"));
//  }

  @Test
  void test_findFromKey() {
    ArrayList<JiraTicketLinkDescriptor> jiraTicketLinkDescriptors = new ArrayList<>();
    Map<String, JiraTicket> jiraTickets = new HashMap<>();
    String key = "CNG-1388";

    assertNull(Utils.findFromKey(jiraTickets, key));

    Utils.loadDefinitions(header, jiraTicketLinkDescriptors);

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

  @Test
  void test_createClassHead() {
    ArrayList<JiraTicketLinkDescriptor> jiraTicketLinkDescriptors = new ArrayList<>();
    Utils.loadDefinitions(header, jiraTicketLinkDescriptors);

    JiraTicket jiraTicket = new JiraTicket(testRecord);
    String header = Utils.createClassHead(jiraTicket);
    assertNotNull(header);

    jiraTicket.issueType = new ArrayList<>();
    assertThrows(NoSuchElementException.class, () -> Utils.createClassHead(jiraTicket));

    jiraTicket.issueType.add("XXXXXXX");
    header = Utils.createClassHead(jiraTicket);
    assertNotNull(header);
    assertEquals("class \"CNG-1411\" << (X,lightblue) >> {", header);
  }


  @Test
  void testDump()
  {
    String t = "Title";
    String[] arr = {"A", "B"};
    assertDoesNotThrow(() -> Utils.dump(t, arr, System.out));
  }

  @Test
  void testDump_errors()
  {
    String t = "Title";
    String[] arr = {"A", "B"};
    assertDoesNotThrow(() -> Utils.dump(null, arr, System.out));
    assertDoesNotThrow(() -> Utils.dump(t, null, System.out));
  }

}