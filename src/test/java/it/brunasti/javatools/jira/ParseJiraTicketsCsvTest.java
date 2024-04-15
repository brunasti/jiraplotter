package it.brunasti.javatools.jira;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class ParseJiraTicketsCsvTest {

  @BeforeEach
  void setUp() {
  }

  @Test
  void generateDiagrams_noArgs() {
    ParseJiraTicketsCsv parseJiraTicketsCsv = new ParseJiraTicketsCsv();
    assertThrows(NullPointerException.class, ()-> parseJiraTicketsCsv.generateDiagrams(null, null));
    assertThrows(FileNotFoundException.class, ()-> parseJiraTicketsCsv.generateDiagrams("", ""));
  }

  @Test
  void generateDiagrams_test() {
    String fileName = "./src/test/resources/jira-open.csv";
    String outputDir = "./temp/";
    ParseJiraTicketsCsv parseJiraTicketsCsv = new ParseJiraTicketsCsv();
    assertDoesNotThrow(()-> parseJiraTicketsCsv.generateDiagrams(fileName, outputDir));
  }

}