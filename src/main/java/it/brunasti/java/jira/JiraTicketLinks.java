package it.brunasti.java.jira;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;

@ToString
@Getter
@AllArgsConstructor
public class JiraTicketLinks {
  private String name;
  private ArrayList<String> links = new ArrayList<>();
  private ArrayList<JiraTicket> jiraTickets = new ArrayList<>();

  public String getShortName() {
    return Utils.getShortName(name);
  }

}
