package it.brunasti.java.jira;

public final class ParseJiraTicketsConstants {

  private ParseJiraTicketsConstants() {}

  public static final String PUML_FILE_EXTENSION = ".puml";
  public static final String HEADER_TICKETS = "' Tickets =======";
  public static final String HEADER_LINKS = "' Links =======";
  public static final String DEFINITION_CLASS_START = "class \"";
  public static final String DEFINITION_CLASS_MIDDLE = "\" << (";
  public static final String DEFINITION_CLASS_FULL_END = ",lightblue) >> {";
  public static final String DEFINITION_CLASS_END = ") >> {";
  public static final String DEFINITION_CLASS_ASSIGNED_TO = " Assigned to : ";
  public static final String DEFINITION_CLASS_SUMMARY = " Summary : ";
  public static final String DEFINITION_CLASS_STATUS = " Status : ";
  public static final String DEFINITION_CLASS_TYPE = " Type : ";
  public static final String DEFINITION_CLASS_ESTIMATE = " Points : ";
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
  static final String FIELD_NAME_SUMMARY = "Summary";
  static final String FIELD_NAME_ORIGINAL_ESTIMATE = "Original estimate";
  static final String FIELD_NAME_STORY_POINT = "Custom field (Story point estimate)";

  static final String FIELD_NAME_INWARD_ISSUE_LINK = "Inward issue link";
  static final String FIELD_NAME_OUTWARD_ISSUE_LINK = "Outward issue link";



  public static final String SET_TO_TEXT_TEMPLATE = "{} set to [{}]";





  public static final String TYPE_BUG = "bug";
  public static final String TYPE_RISK = "risks";
  public static final String TYPE_IMPEDIMENT = "impediment (issue)";
  public static final String TYPE_ISSUE = "issue";
  public static final String TYPE_STORY = "story";
  public static final String TYPE_EPIC = "epic";
  public static final String TYPE_NEW = "new feature";
  public static final String TYPE_IMPROVEMENT = "improvement";
  public static final String TYPE_REQUEST = "project request package";
  public static final String TYPE_SUBTASK = "subtask";
  public static final String TYPE_TASK = "task";
  public static final String TYPE_WORK_REQUEST = "work request";
  
}