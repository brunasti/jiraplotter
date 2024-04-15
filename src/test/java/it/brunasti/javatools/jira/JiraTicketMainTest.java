package it.brunasti.javatools.jira;

import org.apache.commons.cli.Options;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JiraTicketMainTest implements TestConstants {

  @BeforeEach
  void setUp() {
  }

  @Test
  void printHelp() {
    Options options = new Options();

    System.err.println("JiraTicketMain.printHelp ------");
    assertDoesNotThrow(() -> JiraTicketMain.printHelp());
    assertDoesNotThrow(() -> JiraTicketMain.printHelp(null));
    assertDoesNotThrow(() -> JiraTicketMain.printHelp(options));
  }

  @Test
  void printUsage() {
    Options options = new Options();

    System.err.println("JiraTicketMain.printUsage ------");
    assertDoesNotThrow(() -> JiraTicketMain.printUsage(null));
    assertDoesNotThrow(() -> JiraTicketMain.printUsage(options));
  }


  @Test
  @DisplayName("Call JiraTicketMain main helps")
  void testMainMethod_Helps() {
    String[] args = new String[1];
    args[0] = "-h";
    assertDoesNotThrow(() -> JiraTicketMain.main(args));

    String[] argsHelp = new String[1];
    argsHelp[0] = "-?";
    assertDoesNotThrow(() -> JiraTicketMain.main(argsHelp));
  }

  @Test
  @DisplayName("Call JiraTicketMain main with debug")
  void testMainMethod_Debug() {
    System.err.println("Call JiraTicketMain main with debug ---- ");

    System.err.println("[-d]");
    String[] args = new String[1];
    args[0] = "-d";
    assertDoesNotThrow(() -> JiraTicketMain.main(args));

    System.err.println("[-d 2]");
    String[] argsHelp = new String[2];
    argsHelp[0] = "-d";
    argsHelp[1] = "2";
    assertDoesNotThrow(() -> JiraTicketMain.main(argsHelp));

    System.err.println("[-d 999]");
    argsHelp[0] = "-d";
    argsHelp[1] = "999";
    assertDoesNotThrow(() -> JiraTicketMain.main(argsHelp));

    System.err.println("[-d xxx]");
    argsHelp[0] = "-d";
    argsHelp[1] = "xxx";
    assertDoesNotThrow(() -> JiraTicketMain.main(argsHelp));
  }

  @Test
  @DisplayName("Call JiraTicketMain main with errors")
  void testMainMethod_Errors() {
    String[] args = new String[1];
    args[0] = "-x";
    assertDoesNotThrow(() -> JiraTicketMain.main(args));

    String[] argsHelp = new String[1];
    argsHelp[0] = "-z";
    assertDoesNotThrow(() -> JiraTicketMain.main(argsHelp));
  }


  @Test
  @DisplayName("Call JiraTicketMain main with parameters")
  void testMainMethod_Params() {
    System.err.println("JiraTicketMain.main ------ 2 args -----");
    String[] args = new String[2];
    args[0] = testFileName;
    args[1] = tempDirectory;
    assertDoesNotThrow(() -> JiraTicketMain.main(args));

    System.err.println("JiraTicketMain.main ------ 3 args -----");
    String[] fullArgs = new String[6];
    fullArgs[0] = "-i";
    fullArgs[1] = testFileName;
    fullArgs[2] = "-o";
    fullArgs[3] = tempDirectory;
    fullArgs[4] = "-c";
    fullArgs[5] = configurationFileName;
    assertDoesNotThrow(() -> JiraTicketMain.main(fullArgs));
  }

}