package it.brunasti.javatools.jira;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class JiraTicketLinkDescriptor {

  private String name;
  private FieldDescriptor fieldDescriptor;

  public String getShortName() {
    return Utils.getShortName(name);
  }

}
