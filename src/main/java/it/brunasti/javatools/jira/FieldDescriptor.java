package it.brunasti.javatools.jira;

import java.util.ArrayList;

public class FieldDescriptor {
  String name;
  ArrayList<Integer> indexes = new ArrayList<>();

  public String toString() {
    return "["+name+"] ["+indexes.toString()+"]";
  }

  public FieldDescriptor(String[] fields, String name) {
    this.name = name;
    for (int i=0; i<fields.length; i++) {
      if (name.equals(fields[i])) {
        indexes.add(i);
      }
    }
  }

}
