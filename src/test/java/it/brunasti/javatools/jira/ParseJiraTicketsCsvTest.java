package it.brunasti.javatools.jira;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParseJiraTicketsCsvTest {

  @BeforeEach
  void setUp() {
  }

  @Test
  void findFromKey() {
  }

  @Test
  void main_noArgs() {
    assertDoesNotThrow(()-> ParseJiraTicketsCsv.main(null));
    assertDoesNotThrow(()-> ParseJiraTicketsCsv.main(new String[0]));
  }
}