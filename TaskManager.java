package TaskManager;

import Task.Task;

import java.io.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaskManager{
    private ArrayList<Task> tasks; //arraylist for dynamic resizing
    private static final  String FILENAME = "todo.ser";

    //HELPER METHOD TO CHECK IS THE INDEX IS VALID
    private boolean isValidIndex(int index){
        return index >= 0 && index < tasks.size();
    }

    public TaskManager(){
        tasks = new ArrayList<>();
    }

    public void addTask(Task task){
        tasks.add(task);
    }

    public void deleteTask(int index){
        if(isValidIndex(index)) {
            Task task = tasks.get(index);
            task.setDeletionDate(LocalDateTime.now());

            tasks.remove(index);
        }
    }

    public void markComplete(int index){
        if(isValidIndex(index)){
            Task task = tasks.get(index);
            task.setCompleted(true);
        }
    }

    public void updateTask(int index, String description, LocalDateTime deadline){
        if(isValidIndex(index)){
            Task task = tasks.get(index);
            task.setDescription(description);
            task.setDeadline(deadline);

        }
    }

    //returns a ref to the list for gui to display
    public ArrayList<Task> getAllTasks(){
        return new ArrayList<>(tasks);
    }


    public ArrayList<Task> getCompletedTasks(){
        List<Task> completed = new ArrayList<>();
        for(Task task : tasks){
            if(task.isCompleted()) completed.add(task);
        }
        return (ArrayList<Task>) completed;
    }

    public ArrayList<Task> getIncompleteTasks(){
        List<Task> incomplete = new ArrayList<>();
        for(Task task : tasks){
            if(!task.isCompleted()) incomplete.add(task);
        }
        return (ArrayList<Task>) incomplete;
    }

    //serialization- saves entire ArrayList as bin object
    public void saveToFile(){
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILENAME))){
            out.writeObject(tasks);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadFromFile() {
        File file = new File(FILENAME);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(file))) {
                tasks = (ArrayList<Task>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void exportUsingCSV(File file){
        try(PrintWriter writer = new PrintWriter(file)){
            writer.println("Description,Deadline,Completed,Created,CompletionDate");

            for (Task task:  tasks){
                writer.println(task.toCSV());
            }
            System.out.println("Exported" + tasks.size() + "tasks to CSV");
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }


    public void importFromCSV(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                //to ensure the line has enough data columns
                if (parts.length >= 4) {

                    //parse data
                    String description = parts[0].replace("\"", "");
                    String deadlineStr = parts[1];
                    String completedStr = parts[2];
                    String createdStr = parts[3];


                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                    LocalDateTime deadline = deadlineStr.equals("No Deadline") ?
                            null : LocalDateTime.parse(deadlineStr, fmt);

                    LocalDateTime created = LocalDateTime.parse(createdStr, fmt);
                    boolean completed = completedStr.equalsIgnoreCase("Yes");


                    Task task = new Task(description, deadline, completed, created);
                    tasks.add(task);
                }
            }
            System.out.println("Imported " + tasks.size() + " tasks from CSV");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
