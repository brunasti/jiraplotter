package it.brunasti.javatools.jira;

import java.io.*;

import com.opencsv.exceptions.CsvException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Entry point for the CLI version of the ParseJiraTicketsCsv.
 * This JiraTicketMain uses the library form Apache for Command Line Interface:
 * <a href="https://commons.apache.org/proper/commons-cli/usage.html">commons-cli</a>
 */
public class JiraTicketMain {

  static CommandLine commandLine;
  static Logger log = LogManager.getLogger(JiraTicketMain.class);


  static ParseJiraTicketsCsv parseJiraTicketsCsv;

  private static String configurationFile = "";
  private static String inputCsvFile = "";
  private static String outputDirectory = "";
  private static boolean debug = false;

  private static Options options;


  private static void reset() {
    configurationFile = "";
    options = null;
  }



  private static boolean setDebugOption(Option optionDebug) {
    if (commandLine.hasOption(optionDebug.getOpt())) {
      debug = true;
    }
    return true;
  }

  private static boolean processCommandLine(String[] args) {

    // Reset all the flags, to avoid multiple sequence runs interfering
    reset();

    options = new Options();
    Option optionHelp = new Option("h", "help", false, "Help");
    Option optionShortUsage = new Option("?", false, "Quick Reference");
    Option optionDebug = Option.builder().option("d")
            .longOpt("debug").hasArg(false)
            .desc("Execute in debug mode").build();

    Option optionInputFile = new Option("i", "input", true,
            "Input File");
    Option optionConfigFile = new Option("c", "config", true,
            "Configuration File");
    Option optionOutputDirectory = new Option("o", "output", true, "Output Directory");

    options.addOption(optionHelp);
    options.addOption(optionShortUsage);
    options.addOption(optionDebug);
    options.addOption(optionConfigFile);
    options.addOption(optionInputFile);
    options.addOption(optionOutputDirectory);

    try {
      CommandLineParser parser = new DefaultParser();

      commandLine = parser.parse(options, args);

      setDebugOption(optionDebug);

      if (debug) {
        Utils.dump("ARGS", args, System.err);
        Utils.dump("CMD", commandLine.getArgs(), System.err);
      }

      if (commandLine.hasOption(optionHelp.getOpt())) {
        printHelp(options);
        return false;
      } else if (commandLine.hasOption(optionShortUsage.getOpt())) {
        printUsage(options);
        return false;
      }

      if (commandLine.getArgs().length > 0) {
        inputCsvFile = commandLine.getArgs()[0];
        if (commandLine.getArgs().length > 1) {
          outputDirectory = commandLine.getArgs()[1];
        }
      }
      if (commandLine.hasOption(optionConfigFile.getOpt())) {
        configurationFile = commandLine.getOptionValue(optionConfigFile.getOpt());
        log.debug(ParseJiraTicketsConstants.SET_TO_TEXT_TEMPLATE, optionConfigFile.getDescription(), configurationFile);
      }
      if (commandLine.hasOption(optionInputFile.getOpt())) {
        inputCsvFile = commandLine.getOptionValue(optionInputFile.getOpt());
        log.debug(ParseJiraTicketsConstants.SET_TO_TEXT_TEMPLATE, optionInputFile.getDescription(), inputCsvFile);
      }
      if (commandLine.hasOption(optionOutputDirectory.getOpt())) {
        outputDirectory = commandLine.getOptionValue(optionOutputDirectory.getOpt());
        log.debug(ParseJiraTicketsConstants.SET_TO_TEXT_TEMPLATE, optionOutputDirectory.getDescription(), outputDirectory);
      }

    } catch (ParseException | NullPointerException e) {
      log.error(e.getMessage());
      printHelp(options);
      return false;
    }
    return true;
  }

  static void printHelp() {
    printHelp(options);
  }

  static void printHelp(Options options) {
    if (null == options) {
      System.err.println("ERROR: No options provided to printHelp");
      return;
    }

    HelpFormatter helper = new HelpFormatter();

    String className = JiraTicketMain.class.getCanonicalName();
    PrintWriter outError = new PrintWriter(System.err);

    helper.printHelp(outError,
            100,
            "java " + className,
            "",
            options,
            4,
            4,
            "");
    outError.flush();
  }

  static void printUsage(Options options) {
    if (null == options) {
      System.err.println("ERROR: No options provided to printUsage");
      return;
    }

    HelpFormatter helper = new HelpFormatter();

    String className = JiraTicketMain.class.getCanonicalName();
    PrintWriter outError = new PrintWriter(System.err);

    helper.printUsage(outError, 100, "java " + className, options);
  }

  /**
   * Entry point for the CLI version of the ClassDiagrammer.
   *
   * @param args Command Line options
   */
  public static void main(String[] args) {
    reset();

    boolean cliIsCorrect = processCommandLine(args);

    if (!cliIsCorrect) {
      printHelp();
      return;
    }

    parseJiraTicketsCsv = new ParseJiraTicketsCsv();
    try {
      parseJiraTicketsCsv.generateDiagrams(inputCsvFile, outputDirectory);
    } catch (CsvException | IOException exception) {
      exception.printStackTrace();
    }
  }
}
