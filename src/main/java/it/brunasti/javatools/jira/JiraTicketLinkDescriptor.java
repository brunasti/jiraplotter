package it.brunasti.javatools.jira;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class JiraTicketLinkDescriptor {

  String name;
  FieldDescriptor fieldDescriptor;

  public String getShortName() {
    return Utils.getShortName(name);
  }

}
