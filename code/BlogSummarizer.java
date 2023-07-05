import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;

public class BlogSummarizer {
    public static void main(String[] args) {
        // argument includes the directory name
        File directory = new File(args[0]);

        // if the directory does not exist, print an error
        if (!directory.exists()) {
            System.out.println("Directory does not exist");
        } else {

            // create a csv output file
            File csvFile = new File("blogs.csv");

            // write the header to the csv file
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile));
                writer.write("Author,Date,Title\n");

                // if the directory exists, list the files
                for (File file : directory.listFiles()) {
                    String author = "";
                    String date = "";
                    String title = "";

                    try {
                        // read each line of the file
                        Scanner scanner = new Scanner(file);
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            if (!line.isEmpty()) {
                                if (line.startsWith("authors:")) {
                                    author = scanner.nextLine().substring(2).replace(',', ' ');
                                } else if (line.startsWith("date:")) {
                                    try {
                                        date = line.substring(6);
                                    } catch (StringIndexOutOfBoundsException e) {
                                        date = "";
                                    }
                                } else if (line.startsWith("title:")) {
                                    title = line.substring(7);
                                }
                            }
                        }
                        if (!author.isBlank() || !date.isBlank() || !title.isBlank()) {
                            writer.write(author + "," + date + "," + title + "\n");
                        }
                    } catch (FileNotFoundException fnfe) {
                        fnfe.printStackTrace();
                    }
                }
                writer.flush();
                writer.close();
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}