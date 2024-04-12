package it.brunasti.javatools.jira;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;

@ToString
@Getter
public class JiraTiketLinks {
  String name;
  ArrayList<String> links = new ArrayList<>();
  ArrayList<JiraTicket> jiraTikets = new ArrayList<>();

  public String getShortName() {
    return Utils.getShortName(name);
  }

}
