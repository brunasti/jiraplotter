package it.brunasti.java.jira;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FieldDescriptorTest {

  String[] fields;
  String name;

  @BeforeEach
  void setup() {
    fields = new String[5];
    fields[0] = "A";
    fields[1] = "B";
    fields[2] = "C";
    fields[3] = "A";
    fields[4] = "E";

    name = "A";
  }

  @Test
  void getName() {
    FieldDescriptor fieldDescriptor = new FieldDescriptor(fields, name);
    assertEquals(name, fieldDescriptor.getName());
  }

  @Test
  void getIndexes() {
    FieldDescriptor fieldDescriptor = new FieldDescriptor(fields, name);
    ArrayList<Integer> indexes = new ArrayList<>();
    assertEquals(2, fieldDescriptor.getIndexes().size());
    indexes.add(0);
    indexes.add(3);
    assertEquals(indexes, fieldDescriptor.getIndexes());
  }

  @Test
  void testToString() {
    FieldDescriptor fieldDescriptor = new FieldDescriptor(fields, name);
    assertEquals("FieldDescriptor(name=A, indexes=[0, 3])", fieldDescriptor.toString());
  }
}