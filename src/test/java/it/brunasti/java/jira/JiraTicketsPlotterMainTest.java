package it.brunasti.java.jira;

import org.apache.commons.cli.Options;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JiraTicketsPlotterMainTest implements TestConstants {

  @BeforeEach
  void setUp() {
  }

  @Test
  void printHelp() {
    Options options = new Options();

    System.err.println("JiraTicketsPlotterMain.printHelp ------");
    assertDoesNotThrow(() -> it.brunasti.java.jira.JiraTicketsPlotterMain.printHelp());
    assertDoesNotThrow(() -> it.brunasti.java.jira.JiraTicketsPlotterMain.printHelp(null));
    assertDoesNotThrow(() -> it.brunasti.java.jira.JiraTicketsPlotterMain.printHelp(options));
  }

  @Test
  void printUsage() {
    Options options = new Options();

    System.err.println("JiraTicketsPlotterMain.printUsage ------");
    assertDoesNotThrow(() -> it.brunasti.java.jira.JiraTicketsPlotterMain.printUsage(null));
    assertDoesNotThrow(() -> it.brunasti.java.jira.JiraTicketsPlotterMain.printUsage(options));
  }


  @Test
  @DisplayName("Call JiraTicketsPlotterMain main helps")
  void testMainMethod_Helps() {
    String[] args = new String[1];
    args[0] = "-h";
    assertDoesNotThrow(() -> it.brunasti.java.jira.JiraTicketsPlotterMain.main(args));

    String[] argsHelp = new String[1];
    argsHelp[0] = "-?";
    assertDoesNotThrow(() -> it.brunasti.java.jira.JiraTicketsPlotterMain.main(argsHelp));
  }

  @Test
  @DisplayName("Call JiraTicketsPlotterMain main with debug")
  void testMainMethod_Debug() {
    System.err.println("Call JiraTicketsPlotterMain main with debug ---- ");

    System.err.println("[-d]");
    String[] args = new String[1];
    args[0] = "-d";
    assertDoesNotThrow(() -> it.brunasti.java.jira.JiraTicketsPlotterMain.main(args));

    System.err.println("[-d 2]");
    String[] argsHelp = new String[2];
    argsHelp[0] = "-d";
    argsHelp[1] = "2";
    assertDoesNotThrow(() -> it.brunasti.java.jira.JiraTicketsPlotterMain.main(argsHelp));

    System.err.println("[-d 999]");
    argsHelp[0] = "-d";
    argsHelp[1] = "999";
    assertDoesNotThrow(() -> it.brunasti.java.jira.JiraTicketsPlotterMain.main(argsHelp));

    System.err.println("[-d xxx]");
    argsHelp[0] = "-d";
    argsHelp[1] = "xxx";
    assertDoesNotThrow(() -> it.brunasti.java.jira.JiraTicketsPlotterMain.main(argsHelp));
  }

  @Test
  @DisplayName("Call JiraTicketsPlotterMain main with errors")
  void testMainMethod_Errors() {
    String[] args = new String[1];
    args[0] = "-x";
    assertDoesNotThrow(() -> it.brunasti.java.jira.JiraTicketsPlotterMain.main(args));

    String[] argsHelp = new String[1];
    argsHelp[0] = "-z";
    assertDoesNotThrow(() -> it.brunasti.java.jira.JiraTicketsPlotterMain.main(argsHelp));
  }


  @Test
  @DisplayName("Call JiraTicketsPlotterMain main with parameters")
  void testMainMethod_Params() {
    System.err.println("JiraTicketsPlotterMain.main ------ 2 args -----");
    String[] args = new String[2];
    args[0] = testFileName;
    args[1] = tempDirectory;
    assertDoesNotThrow(() -> it.brunasti.java.jira.JiraTicketsPlotterMain.main(args));

    System.err.println("JiraTicketsPlotterMain.main ------ 3 args -----");
    String[] fullArgs = new String[6];
    fullArgs[0] = "-i";
    fullArgs[1] = testFileName;
    fullArgs[2] = "-o";
    fullArgs[3] = tempDirectory;
    fullArgs[4] = "-c";
    fullArgs[5] = configurationFileName;
    assertDoesNotThrow(() -> it.brunasti.java.jira.JiraTicketsPlotterMain.main(fullArgs));
  }


  @Test
  @DisplayName("Call JiraTicketsPlotterMain main with parameters for Tadaah")
  void testMainTadaah() {
    System.err.println("JiraTicketsPlotterMain.testMainTadaah");
    String[] fullArgs = new String[8];
    fullArgs[0] = "-i";
    fullArgs[1] = "/Users/paolo/IdeaProjects/mine/jiraplotter/docs/tadaah/jira-tadaah.csv";
    fullArgs[2] = "-o";
    fullArgs[3] = "/Users/paolo/IdeaProjects/mine/jiraplotter/docs/tadaah/";
    fullArgs[4] = "-c";
    fullArgs[5] = configurationFileName;
    fullArgs[6] = "-d";
    fullArgs[7] = "999";

    assertDoesNotThrow(() -> it.brunasti.java.jira.JiraTicketsPlotterMain.main(fullArgs));
  }

}