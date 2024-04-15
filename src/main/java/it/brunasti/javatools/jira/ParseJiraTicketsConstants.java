package it.brunasti.javatools.jira;

public final class ParseJiraTicketsConstants {

  private ParseJiraTicketsConstants() {}

  public static final String HEADER_LINKS = "' Links =======";
  public static final String DEFINITION_CLASS_START = "class \"";
  public static final String DEFINITION_CLASS_MIDDLE = "\" << (";
  public static final String DEFINITION_CLASS_FULL_END = ",lightblue) >> {";
  public static final String DEFINITION_CLASS_END = ") >> {";
  public static final String DEFINITION_CLASS_ASSIGNED_TO = "  Assigned to : ";
  public static final String DEFINITION_CLASS_STATUS = "  Status : ";
  public static final String DEFINITION_CLASS_TYPE = "  Type : ";
  public static final String DEFINITION_LINK_SIMPLE_MIDDLE = "\" <.. \"";
  public static final String DEFINITION_LINK_SIMPLE_END = "\" : ";


  static final String FIELD_NAME_ASSIGNEE = "Assignee";
  static final String FIELD_NAME_DESCRIPTION = "Description";
  static final String FIELD_NAME_ISSUE_TYPE = "Issue type";
  static final String FIELD_NAME_ISSUE_ID = "Issue id";
  static final String FIELD_NAME_ISSUE_KEY = "Issue key";
  static final String FIELD_NAME_PARENT = "Parent";
  static final String FIELD_NAME_PRIORITY = "Priority";
  static final String FIELD_NAME_STATUS = "Status";

  static final String FIELD_NAME_INWARD_ISSUE_LINK = "Inward issue link";
  static final String FIELD_NAME_OUTWARD_ISSUE_LINK = "Outward issue link";


}