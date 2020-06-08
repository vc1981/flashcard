package flashcards;

import java.io.*;
import java.util.*;


public class Main {

    static Map<String, String> definitions = new LinkedHashMap<String, String>();
    static Map<Object, Integer> statistics = new HashMap<Object, Integer>();
    static ArrayList<String> log = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);
    static String cardAnswer = null;
    static String definitionAnswer = null;
    static int exitSwitch = 1;
    static Random generator = new Random();
    static String fileName = null;
    static String messageOut = null;
    static String commandExportFile = null;
    static String commandImportFile = null;
    static boolean doExitExport = false;

    public static int callMenu() {
        //System.out.println("Input the action (add, remove, import, export, ask, exit):");
        outputMsg("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
        String userInputMenu = scanner.nextLine();
        log.add(userInputMenu);
        switch (userInputMenu) {
            case "add":
                callAdd();
                break;
            case "remove":
                callRemove();
                break;
            case "import":
                callImport();
                break;
            case "export":
                callExport();
                break;
            case "ask":
                callAsk();
                exitSwitch = 1;
                break;
            case "log":
                callLog();
                break;
            case "hardest card":
                callHardestCard();
                break;
            case "reset stats":
                callResetStats();
                break;
            case "exit":
                outputMsg("Bye bye!");
                if (doExitExport) {
                    File file = new File("./" + commandExportFile);
                    try (PrintWriter printWriter = new PrintWriter(new FileOutputStream(file, false));) {
                        for (Map.Entry<String, String> entry : definitions.entrySet()) {
                            printWriter.write(entry.getKey() + ":" + entry.getValue() + ":" + (statistics.get(entry.getKey()) == null ? 0 : statistics.get(entry.getKey())));
                            printWriter.println();
                        }
                        printWriter.flush();
                        outputMsg(definitions.size() + " cards have been saved");
                    } catch (IOException e) {
                        outputMsg("File not found.");
                    }
                }

                exitSwitch = 0;
                break;
        }
        return exitSwitch;

    }

    public static void callAdd() {
        System.out.println("The card:");
        cardAnswer = scanner.nextLine();
        log.add(cardAnswer);
        if (!definitions.containsKey(cardAnswer)) {
            outputMsg("The definition of the card:");
            definitionAnswer = scanner.nextLine();
            log.add(definitionAnswer);
            if (!definitions.containsValue(definitionAnswer)) {
                definitions.put(cardAnswer, definitionAnswer);
                outputMsg("The pair (\"" + cardAnswer + "\":\"" + definitionAnswer + "\") has been added.");
            } else {
                outputMsg("The definition \"" + definitionAnswer + "\" already exists.");
            }
        } else {

            outputMsg("The card \"" + cardAnswer + "\" already exists.");
        }
    }

    public static void callRemove() {
        outputMsg("The card:");
        cardAnswer = scanner.nextLine();
        log.add(cardAnswer);
        if (definitions.containsKey(cardAnswer)) {
            definitions.remove(cardAnswer);
            statistics.remove(cardAnswer);
            outputMsg("The card has been removed.");
        } else {
            outputMsg("Can't remove \"" + cardAnswer + "\": there is no such card.");
        }

    }

    public static void callAsk() {

        Object[] values = definitions.keySet().toArray();
        Object randomValue = values[generator.nextInt(values.length)];
        outputMsg("How many times to ask?");
        int timesToAsk = scanner.nextInt();
        log.add(String.valueOf(timesToAsk));
        String fixNextInt = scanner.nextLine();
        for (int iterate = 0; iterate < timesToAsk; iterate++) {
            outputMsg("Print the definition of \"" + randomValue + "\":");
            String answer = scanner.nextLine();
            log.add(answer);
            if (definitions.containsValue(answer)) {
                if (answer.equals(definitions.get(randomValue))) {
                    outputMsg("Correct answer");
                } else {
                    outputMsg("Wrong answer. The correct one is \"" + definitions.get(randomValue) + "\", you've just written the definition of \"" + findTheCard((String) answer) + "\".");
                    statistics.put(randomValue, ((statistics.get(randomValue) == null) ? 1 : (statistics.get(randomValue) + 1)));
                }
            } else {
                outputMsg("Wrong answer. The correct one is \"" + definitions.get(randomValue) + "\".");
                statistics.put(randomValue, ((statistics.get(randomValue) == null) ? 1 : (statistics.get(randomValue) + 1)));
            }
        }


    }

    public static void callImport() {
        outputMsg("File name:");
        fileName = scanner.nextLine();
        log.add(fileName);
        File file = new File("./" + fileName);
        try (Scanner scanner = new Scanner(file)) {
            int counter = 0;
            while (scanner.hasNext()) {
                String record = scanner.nextLine();
                definitions.put(record.split(":")[0], record.split(":")[1]);
                statistics.put(record.split(":")[0], (record.split(":")[2] == null ? 0 : Integer.valueOf(record.split(":")[2])));
                counter++;
            }
            //System.out.println(definitions.size() + " cards have been loaded.");
            outputMsg(counter + " cards have been loaded.");
        } catch (IOException e) {
            outputMsg("File not found.");
        }


    }

    public static void callExport() {
        outputMsg("File name:");
        fileName = scanner.nextLine();
        log.add(fileName);
        File file = new File("./" + fileName);
        try (PrintWriter printWriter = new PrintWriter(new FileOutputStream(file, false));) {
            for (Map.Entry<String, String> entry : definitions.entrySet()) {
                printWriter.write(entry.getKey() + ":" + entry.getValue() + ":" + (statistics.get(entry.getKey()) == null ? 0 : statistics.get(entry.getKey())));
                printWriter.println();
            }
            printWriter.flush();
            outputMsg(definitions.size() + " cards have been saved");
        } catch (IOException e) {
            outputMsg("File not found.");
        }

    }

    public static String findTheCard(String definitionToSearch) {
        String returnCard = null;
        for (Map.Entry<String, String> entry : definitions.entrySet()) {
            if (entry.getValue().equals(definitionToSearch)) {
                returnCard = entry.getKey();
                break;
            }
        }
        return returnCard;
    }

    public static void callLog() {
        outputMsg("File name:");
        fileName = scanner.nextLine();
        log.add(fileName);
        File file = new File("./" + fileName);
        try (PrintWriter printWriter = new PrintWriter(new FileOutputStream(file, false));) {
            for (int m = 0; m < log.size(); m++) {

                printWriter.println(log.get(m));
            }
            printWriter.flush();
            outputMsg("The log has been saved.");
        } catch (IOException e) {
            outputMsg("File not found.");
        }
    }

    public static void outputMsg(String output) {
        System.out.println(output);
        log.add(output);
    }

    public static void callResetStats() {
        statistics.clear();
        outputMsg("Card statistics has been reset.");
    }

    public static void callHardestCard() {
        int maxError = 0;
        String maxErrorCard = null;
        if (statistics.isEmpty()) {
            outputMsg("There are no cards with errors.");
        } else {
            for (Map.Entry<Object, Integer> cardsEntry : statistics.entrySet()) {
                if (cardsEntry.getValue() > maxError) {
                    maxErrorCard = "\"" + (String) cardsEntry.getKey() + "\"";
                    maxError = cardsEntry.getValue();
                    messageOut = "The hardest card is " + maxErrorCard + ". You have " + maxError + " errors answering it.";
                } else if (cardsEntry.getValue() == maxError) {
                    maxErrorCard = "\"" + (String) cardsEntry.getKey() + "\", " + maxErrorCard;
                    messageOut = "The hardest cards are " + maxErrorCard + ". You have " + maxError + " errors answering them.";
                }
            }

            outputMsg(messageOut);
            // \"Russia\", \"France\"

        }

    }


    public static void main(String[] args) {
        int returnValue;

        if (args.length != 0) {
            for (int i = 0; i < args.length; i += 2) {
                if (args[i].equals("-export")) {
                    commandExportFile = args[i + 1];
                    doExitExport = true;
                } else if (args[i].equals("-import")) {
                    commandImportFile = args[i + 1];
                    File file = new File("./" + commandImportFile);
                    try (Scanner scanner = new Scanner(file)) {
                        int counter = 0;
                        while (scanner.hasNext()) {
                            String record = scanner.nextLine();
                            definitions.put(record.split(":")[0], record.split(":")[1]);
                            statistics.put(record.split(":")[0], (record.split(":")[2] == null ? 0 : Integer.valueOf(record.split(":")[2])));
                            counter++;
                        }
                        //System.out.println(definitions.size() + " cards have been loaded.");
                        outputMsg(counter + " cards have been loaded.");
                    } catch (IOException e) {
                        outputMsg("File not found.");
                    }

                }
            }

        }

        do {
            returnValue = callMenu();
        }
        while (returnValue != 0);


    }


}
