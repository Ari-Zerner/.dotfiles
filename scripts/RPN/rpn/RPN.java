package rpn;

import java.io.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import static rpn.Token.*;

/**
 * Controls an RPNCalculator using the console to interface with the user.
 */
public class RPN {

    /*
     * All output should be formatted such that: "> " begins lines that require
     * input from the user "= " begins lines that display calculator results "?
     * " begins lines that display help information ": " begins lines that give
     * tracing and other information
     */
    private static final String QUIT_TOKEN = "quit";
    private RPNCalculator calc = new RPNCalculator();
    private Scanner scan = new Scanner(System.in);
    private boolean trace = false, atomic = true;
    private int outputPrecision = 6;
    private Map<String, Token[]> macros = new LinkedHashMap<String, Token[]>() {
        {
            try (Scanner macroScan = new Scanner(new File("math.rpn"))) {
                putAll(loadMacros(macroScan));
            } catch (Exception e) {
                //System.out.println(": Unable to load macros from math.rpn.");
            }
        }
    };
    private static final Map<String, String> HELP_MAP
            = new LinkedHashMap<String, String>() {
                {
                    put("h", "prints this help menu");
                    put("help", "prints this help menu");
                    put("+", "adds the top two numbers.");
                    put("-", "subtracts the top number from the one below it.");
                    put("*", "multiplies the top two numbers.");
                    put("/", "divides the top number into the one below it.");
                    put("abs", "takes the absolute value of the top number.");
                    put("^",
                            "raises the second to top number to the top number.");
                    put("log",
                            "takes the log of the top number base the number below it.");
                    put("sin", "takes the sine of the top number (in radians).");
                    put("arcsin",
                            "takes the sine inverse of the top number (in radians).");
                    put("cp", "copies the top number.");
                    put("rm", "removes the top number.");
                    put("sw", "swaps the top two numbers.");
                    put("clear", "clears the whole stack.");
//                    put("ifelse",
//                            "If third to top is 0, top, else second to top. Replaces the top 3 numbers.");
                    put("toalt",
                            "pops from the current stack and pushes to the alternate stack.");
                    put("swalt", "swaps the current and alternate stacks.");
                    put("macro", "opens macro creation dialogue.");
                    put("loadmacros",
                            "loads macros from a .rpn file. math is preloaded.");
                    put("savemacros",
                            "saves macros to a .rpn file. math is preloaded.");
                    put("restoremath",
                            "sets math to the default set of macros and loads math.");
                    put("=", "displays the top number on the stack.");
                    put("==",
                            "displays the top number on the stack without rounding.");
                    put("dump", "displays the whole stack.");
                    put("tron", "turns tracing on.");
                    put("troff", "turns tracing off.");
                    put("atomon",
                            "makes the calculator evaluate either all of a line or none of it.");
                    put("atomoff",
                            "makes the calculator evaluate lines one token at a time.");
                    put("precision",
                            "sets output precision according to the top number.");
                    put(QUIT_TOKEN, "quits the program.");
                }
            };

    /**
     * Creates a ConsoleViewController with trace initialized to false.
     */
    public RPN() {
        this(false);
    }

    /**
     * Creates a ConsoleViewController with trace initialized to the given
     * value.
     * @param trace the initial trace value
     */
    public RPN(boolean trace) {
        this.trace = trace;
    }

    /**
     * Breaks a line into tokens separated by spaces or tabs.
     * @param line the line to tokenize
     * @return a Token[] containing the tokens
     */
    private Token[] tokenizeLine(String line) {
        if (line.trim().isEmpty()) return new Token[0];
        String[] tokenStrings = line.split("\\s+");
        Token[] tokens = new Token[tokenStrings.length];
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = parseToken(tokenStrings[i].trim());
        }
        return tokens;
    }

    /**
     * Inverse of tokenizeLine.
     */
    private String linifyTokens(Token[] tokens) {
        if (tokens.length == 0) return "";
        String line = formatToken(tokens[0]);
        for (int i = 1; i < tokens.length; i++) {
            line += " " + formatToken(tokens[i]);
        }
        return line;
    }

    /**
     * Parses a token and takes the appropriate action.
     * @param token the token to parse
     * @return true if the token is the quit token, false otherwise
     */
    private Token parseToken(String token) {
        if (token.equals(QUIT_TOKEN)) return new QuitToken();
        try {
            return new NumberToken(Double.parseDouble(token));
        } catch (NumberFormatException e) {
            return new OperationToken(token.toLowerCase());
        }

    }

    class UnrecognizedTokenException extends Exception {

        private Token token;

        public UnrecognizedTokenException(Token token) {
            this.token = token;
        }

        @Override
        public String toString() {
            return ": Unrecognized token: " + formatToken(token);
        }
    }

    /**
     * Takes the appropriate action given a Token. Does nothing if token is a
     * QuitToken.
     * @param token the token to process
     * @return true iff the token is a QuitToken
     */
    private boolean doToken(Token token)
            throws UnrecognizedTokenException, Exception {
        if (token instanceof NumberToken) {
            calc.enter(token.getNumber());
            if (trace) {
                System.out.println(": " + formatValue(token.getNumber())
                        + " entered");
            }
        } else if (token instanceof OperationToken) {
            String op = token.getOperation();
            PersistentStack<Double> oldStack = calc.getStack();
            switch (op) {
                case "h":
                case "help":
                    printHelp();
                    break;
                case "+":
                    calc.add();
                    break;
                case "-":
                    calc.subtract();
                    break;
                case "*":
                    calc.multiply();
                    break;
                case "/":
                    calc.divide();
                    break;
                case "abs":
                    calc.abs();
                    break;
                case "^":
                    calc.power();
                    break;
                case "log":
                    calc.log();
                    break;
                case "sin":
                    calc.sin();
                    break;
                case "arcsin":
                    calc.arcsin();
                    break;
                case "cp":
                    calc.copy();
                    break;
                case "rm":
                    calc.remove();
                    break;
                case "sw":
                    calc.swap();
                    break;
                case "clear":
                    calc.clear();
                    break;
//                case "ifelse":
//                    calc.ifThenElse();
//                    break;
                case "toalt":
                    calc.toAlt();
                    break;
                case "swalt":
                    calc.swapAlt();
                    break;
                case "macro":
                    macro();
                    break;
                case "loadmacros":
                    loadMacros();
                    break;
                case "savemacros":
                    saveMacros();
                    break;
                case "restoremath":
                    restoreMath();
                    break;
                case "=":
                    printResult();
                    break;
                case "==":
                    printResultNoRounding();
                    break;
                case "dump":
                    printStack();
                    break;
                case "tron":
                    trace = true;
                    break;
                case "troff":
                    trace = false;
                    break;
                case "atomon":
                    atomic = true;
                    break;
                case "atomoff":
                    atomic = false;
                    break;
                case "precision":
                    precision();
                    break;
                default:
                    if (macros.containsKey(op)) {
                        runMacro(op);
                    } else {
                        throw new UnrecognizedTokenException(token);
                    }
            }
            if (trace && calc.getStack() != oldStack) {
                printTrace(op);
            }
        }
        return token instanceof QuitToken;
    }

    /**
     * Runs a group of Tokens.
     * @return true iff one of the Tokens is a QuitToken.
     */
    private boolean doTokens(Token[] tokens) {
        RPNCalculator rollback = calc.clone();
        for (Token token : tokens) {
            if (!atomic) rollback = calc.clone();
            try {
                if (doToken(token)) {
                    return true;
                }
            } catch (Exception e) {
                if (e instanceof UnrecognizedTokenException)
                    System.out.println(e);
                calc = rollback;
                if (atomic) break;
            }
        }
        return false;
    }

    private void printTrace(String op) {
        if (op.equals(QUIT_TOKEN))
            System.out.println(": Quitting. Final result = "
                    + formatValue(calc.getResult()));
        else {
            System.out.print(": Stack after " + op + " ");
            printStack();
        }
    }

    /**
     * Displays the help menu.
     */
    private void printHelp() {
        System.out.println();
        System.out.println("? Built in operators:");
        for (Entry<String, String> entry : HELP_MAP.entrySet()) {
            System.out.println("? " + entry.getKey() + " " + entry.getValue());
        }
        if (!macros.isEmpty()) {
            System.out.println("? Macros:");
            for (Entry<String, Token[]> alias : macros.entrySet()) {
                System.out.print("? " + alias.getKey() + " =>");
                for (Token token : alias.getValue())
                    System.out.print(" " + formatToken(token));
                System.out.println();
            }
        }
        System.out.println();
    }

    /**
     * Sets output precision according to the top number.
     */
    private void precision() {
        outputPrecision = Math.max(0, calc.getResult().intValue());
        if (trace) System.out.println(": Precision set to "
                    + outputPrecision + ".");
    }

    /**
     * Formats a number for printing.
     * @param val the number to print
     */
    private String formatValue(Double val) {
        val = Double.parseDouble(String
                .format("%." + outputPrecision + "f", val));
        if (val.longValue() == val) return String.valueOf(val.longValue());
        return String.valueOf(val);
    }

    /**
     * Formats a token for printing.
     * @param token the Token to format
     * @return a String representing the Token
     */
    private String formatToken(Token token) {
        if (token instanceof NumberToken) return formatValue(token.getNumber());
        if (token instanceof QuitToken) return QUIT_TOKEN;
        return token.getOperation();
    }

    /**
     * Displays the top of the stack.
     */
    private void printResult() {
        System.out.println("= " + formatValue(calc.getResult()));
    }

    private void printResultNoRounding() {
        System.out.println("= " + calc.getResult());
    }

    /**
     * Displays the entire stack.
     */
    private void printStack() {
        System.out.print("= Top ");
        for (Double val : calc.getStack()) {
            System.out.print(formatValue(val) + " ");
        }
        System.out.println("Bottom");
    }

    /**
     * Gets a yes/no answer from the user.
     * @param question1 the question to ask the user initially
     * @param question2 what to ask the user if first answer was not y/n
     * @return true if yes, false if no
     */
    private boolean getYNAnswer(String question1, String question2) {
        char answer;
        String suffix = " (y/n) ", question = question1;
        do {
            System.out.print("> " + question + suffix);
            answer = Character.toLowerCase(scan.nextLine().trim().charAt(0));
            question = question2;
        } while (answer != 'y' && answer != 'n');
        return answer == 'y';
    }

    /**
     * Gets an array of tokens to be assigned to a macro.
     * @return an array of Tokens, or null if there was a problem getting tokens
     */
    private Token[] getMacroTokens() {
        System.out.println(": Enter macro tokens on one line.");
        System.out.print("> ");
        return tokenizeLine(scan.nextLine().trim());
    }

    /**
     * Creates a macro.
     */
    private void macro() {
        boolean validName = false;
        String macroName = null;
        getName:
        while (!validName) {
            System.out.print("> Macro name: ");
            macroName = scan.nextLine();
            if (HELP_MAP.containsKey(macroName)) {
                System.out.println(": "
                        + "Macro name cannot be a built in operation.");
                continue;
            }
            for (int i = 0; i < macroName.length(); i++) {
                if (Character.isWhitespace(macroName.charAt(i))) {
                    System.out.println(": "
                            + "Macro name cannot contain whitespace.");
                    continue getName;
                }
            }
            try {
                Double.parseDouble(macroName);
                System.out.println(": " + "Macro name cannot be a number");
            } catch (NumberFormatException e) {
                Token[] currentTokens = macros.get(macroName);
                if (currentTokens != null && !getYNAnswer(
                        "Overwrite " + macroName + "? Currently assigned to \""
                        + linifyTokens(currentTokens) + '"', "Overwrite?")) {
                    return;
                }
                validName = true;
            }
        }
        Token[] macroTokens = getMacroTokens();
        if (macroTokens != null) macros.put(macroName, macroTokens);
    }

    /**
     * Executes the tokens in an alias.
     * @param macroName the name of the macro
     */
    private void runMacro(String macroName) throws Exception {
        boolean wasTrace = trace;
        trace = false;
        Token[] tokens = macros.get(macroName);
        if (tokens != null) doTokens(tokens);
    }

    /**
     * Parses the input from in into macros.
     * @param in
     * @return a map containing the macros
     */
    private Map<String, Token[]> loadMacros(Scanner in) {
        Map<String, Token[]> loadedMacros = new LinkedHashMap<>();
        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.isEmpty()) continue;
            String[] lineParts = line.split("\\s+", 2);
            String macroName = lineParts[0];
            if (HELP_MAP.containsKey(macroName)) {
                System.out.println(
                        ": Macro names cannot be built in operations ("
                        + macroName + ").");
            } else try {
                Double.parseDouble(macroName);
                System.out.println(": Macro names cannot be numbers ("
                        + macroName + ").");
            } catch (NumberFormatException e) {
                Token[] macroTokens = tokenizeLine(lineParts[1]);
                loadedMacros.put(macroName, macroTokens);
            }
        }
        return loadedMacros;
    }

    /**
     * Loads macros from a .rpn file.
     */
    private void loadMacros() {
        boolean tryAgain = true;
        while (tryAgain) {
            System.out.print("> Macro file name: ");
            try (Scanner in = new Scanner(
                    new File(scan.nextLine().trim() + ".rpn"))) {
                macros.putAll(loadMacros(in));
                if (in.ioException() != null) throw in.ioException();
                tryAgain = false;
            } catch (IOException e) {
                tryAgain = getYNAnswer("Error reading file. Try again?",
                        "Try again?");
            } catch (Exception e) {
                System.out.println(": Badly formatted macro file.");
                tryAgain = false;
            }
        }
    }

    /**
     * Writes the macros in macrosToSave to out.
     * @param macrosToSave
     * @param out
     */
    private void saveMacros(Map<String, Token[]> macrosToSave, PrintStream out) {
        macrosToSave.forEach((name, tokens) -> {
            out.print(name);
            for (Token token : tokens) out.print(" " + formatToken(token));
            out.println();
        });
    }

    /**
     * Saves macros to a .rpn file.
     */
    private void saveMacros() {
        Map<String, Token[]> macrosToSave = new LinkedHashMap<>();
        System.out.print(
                ": Enter macros to save on one line, or leave blank to save all:\n> ");
        for (String macroName : scan.nextLine().split("\\s+")) {
            Token[] tokens = macros.get(macroName);
            if (tokens != null) macrosToSave.put(macroName, tokens);
        }
        if (macrosToSave.isEmpty())
            macrosToSave = java.util.Collections.unmodifiableMap(macros);
        boolean tryAgain = true;
        while (tryAgain) {
            System.out.print("> Macro file name: ");
            String macroFileName = scan.nextLine().trim() + ".rpn";
            File macroFile = new File(macroFileName);
            if (!macroFile.exists() || getYNAnswer("Overwrite " + macroFileName
                    + "?", "Overwrite " + macroFileName + "?"))
                try (PrintStream out = new PrintStream(macroFile)) {
                    saveMacros(macrosToSave, out);
                    if (out.checkError()) throw new IOException();
                    tryAgain = false;
                } catch (IOException e) {
                    tryAgain = getYNAnswer("Error writing to file. Try again?",
                            "Try again?");
                }
        }
    }

    /**
     * Overwrites math.rpn with the default macros.
     */
    private void restoreMath() {
        if (getYNAnswer(
                "Restoring math will overwrite any changes you have made to it."
                + " Continue?", "Continue?")) {
            try (PrintStream math = new PrintStream(new File("math.rpn"))) {
                math.println("+- -1 *");
                math.println("1/ 1 sw /");
                math.println("root 1/ ^");
                math.println("sqrt 2 root");
                math.println("cos pi 2 / sw - sin");
                math.println("tan cp sin sw cos /");
                math.println("csc sin 1/");
                math.println("sec cos 1/");
                math.println("cot tan 1/");
                math.println("arccos pi 2 / sw arcsin -");
                math.println("arctan cp 2 ^ 1 + sqrt / arcsin");
                math.println("arccsc 1/ arcsin");
                math.println("arcsec 1/ arccos");
                math.println("arccot pi 2 / sw arctan -");
                math.println("e 2.718281828459045");
                math.println("pi 3.141592653589793");
                math.println("fralt swalt toalt swalt");
                math.println("dumpalt swalt dump swalt");
                math.println("dumpboth dump dumpalt");
            } catch (Exception e) {
                System.out.println(": Error restoring math file.");
            }
            try (Scanner math = new Scanner(new File("math.rpn"))) {
                macros.putAll(loadMacros(math));
            } catch (Exception e) {
                System.out.println(": Error loading from restored math file.");
            }
        }
    }

    /**
     * Runs the calculator. Reads and tokenizes input lines until the quit token
     * is encountered.
     */
    public void runCalculator() {
        while (true) {
            PersistentStack<Double> oldStack = calc.getStack();
            System.out.print("> ");
            if (doTokens(tokenizeLine(scan.nextLine()))) {
                if (trace) printTrace(QUIT_TOKEN);
                break;
            }
            if (calc.getStack() != oldStack) printResult();
        }
    }

    /**
     * Runs a new RPN calculator with the given arguments, or reads from
     * standard in if no arguments are given.
     * @param args the arguments passed to the calculator
     */
    public static void main(String[] args) {
        RPN cvc = new RPN();
        if (args.length > 0) {
            Token[] tokens = new Token[args.length];
            for (int i = 0; i < tokens.length; i++) {
                tokens[i] = cvc.parseToken(args[i]);
            }
            cvc.atomic = false;
            cvc.doTokens(tokens);
            cvc.printResult();
        } else cvc.runCalculator();
    }
}
