package duke;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Class Storage helps Danh's Duke keep the data in .txt file up to date,
 * and load it from that file when the Danh's Duke wakes up.
 */
public class Storage {
    private File taskFile;

    /**
     * Returns a Storage with specified path of txt file and the folder containing it.
     *
     * @param txtPathname The pathname of the txt file.
     * @param dirPathname The pathname of the directory containing txt file.
     */
    public Storage(String txtPathname, String dirPathname) {
        try {
            Path filePath = Paths.get(txtPathname);
            if (Files.exists(filePath)) {
                this.taskFile = filePath.toFile();
            } else if (Files.exists(Paths.get(dirPathname))) {
                Files.createFile(Paths.get(txtPathname));
                this.taskFile = filePath.toFile();
            } else {
                Files.createDirectories(Paths.get(dirPathname));
                Files.createFile(Paths.get(txtPathname));
                this.taskFile = filePath.toFile();
            }
        } catch (IOException ie) {
            System.out.println("Something went wrong" + ie.getMessage());
        }
    }

    /**
     * Load the data written in txt file back to Danh's Duke memory when it wakes up.
     *
     * @param taskList The taskList of Duke to load data to.
     * @throws IOException Exception related to open and access txt file.
     */
    public void writeBack(ArrayList<Task> taskList) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(this.taskFile));
        String line = reader.readLine();
        while (line != null) {
            switch (line.substring(1, 2)) {
                case "T":
                    if (line.charAt(4) == ' ') {
                        taskList.add(new ToDo(line.substring(7)));
                    } else {
                        ToDo newToDo = new ToDo(line.substring(7));
                        newToDo.markAsDone();
                        taskList.add(newToDo);
                    }
                    break;
                case "D":
                    int dlIndex = line.indexOf("(by: ");
                    LocalDateTime dlTime = LocalDateTime.parse(line.substring(dlIndex + 5, line.length() - 1), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    if (line.charAt(4) == ' ') {
                        taskList.add(new Deadline(line.substring(7, dlIndex - 1), dlTime));
                    } else {
                        Deadline newDL = new Deadline(line.substring(7, dlIndex - 1), dlTime);
                        newDL.markAsDone();
                        taskList.add(newDL);
                    }
                    break;
                default:
                    int etIndex = line.indexOf("(at: ");
                    LocalDateTime eventTime = LocalDateTime.parse(line.substring(etIndex + 5, line.length() - 1), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    if (line.charAt(4) == ' ') {
                        taskList.add(new Deadline(line.substring(7, etIndex - 1), eventTime));
                    } else {
                        Deadline newDL = new Deadline(line.substring(7, etIndex - 1), eventTime);
                        newDL.markAsDone();
                        taskList.add(newDL);
                    }
            }
            line = reader.readLine();
        }
        reader.close();
    }

    /**
     * Update the taask file (txt file) correspondingly when there are changes to taskList.
     *
     * @param taskList the TaskList that have been changed.
     */
    public void updateFile(ArrayList<Task> taskList) {
        try {
            FileWriter fw = new FileWriter(this.taskFile);
            String toWrite = "";
            for (Task task : taskList) {
                toWrite += task.printTask() + "\n";
            }
            fw.write(toWrite);
            fw.close();
        } catch (IOException ie) {
            System.out.println("Something went wrong" + ie.getMessage());
        }
    }
}
