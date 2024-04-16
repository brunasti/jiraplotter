package it.brunasti.java.jira;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;

@ToString
@Getter
public class FieldDescriptor {
  String name;
  ArrayList<Integer> indexes = new ArrayList<>();

  public FieldDescriptor(String[] fields, String name) {
    this.name = name;
    for (int i=0; i<fields.length; i++) {
      if (name.equals(fields[i])) {
        indexes.add(i);
      }
    }
  }

}
