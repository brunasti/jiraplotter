package it.brunasti.javatools.jira;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

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
}