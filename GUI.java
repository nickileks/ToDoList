/* presentation-layer */
package GUI;

import Task.Task;
import TaskManager.TaskManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GUI extends JFrame {

    private TaskManager taskManager;
    private JTable taskTable;
    private DefaultTableModel table; //allowing dynamic update to table data

    public GUI() {
        taskManager = new TaskManager();
        taskManager.loadFromFile();

        setUpFrame();
        setUpMenuBar();
        setUpTable();
        setUpButtons();

        refreshTable(); //this loads the tasks into the table

        pack();
        setVisible(true);
    }

    private DateTimeFormatter dateFormater = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private void setUpFrame() {
        setTitle("TODO List");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    //clears table and fills it using the logic layer so it is always synced
    private void refreshTable() {
        table.setRowCount(0); //clears the ui

        for (Task task : taskManager.getAllTasks()) {

            table.addRow(createTableRow(task));
            }
        }

//HELPER METHOD to makes a single task into a table row
    private Object[] createTableRow(Task task) {
        String deadlineStr = (task.getDeadline() != null)
                ? task.getDeadline().format(dateFormater)
                : "No Deadline";


        Object[] row = {
                task.getDescription(),
                deadlineStr,
                task.isCompleted() ? "Done" : "Not Done",
                task.getCreationDate().format(dateFormater),
                task.getModificationDate().format(dateFormater),
                task.getCompletionDate() != null ? //only showing completion date if it exists
                        task.getCompletionDate().format(dateFormater) : "-",
                task.getDeletionDate() != null ?
                        task.getDeletionDate().format(dateFormater) : "-"
        };
        return row;
    }

    private void addTask() {
        String description = JOptionPane.showInputDialog(this, "Enter task description:");
        if (description == null || description.trim().isEmpty()) return;


        String deadlineStr = JOptionPane.showInputDialog(this,
                "Enter deadline (days from now, or 0 for today), if no deadline you can leave this empty:", "1");

        LocalDateTime deadline = null; //default is no deadline
        try {
            if(deadlineStr != null && !deadlineStr.trim().isEmpty()) {
                int days = Integer.parseInt(deadlineStr);
                 deadline = LocalDateTime.now().plusDays(days);
            }
            Task newTask = new Task(description, deadline);
            taskManager.addTask(newTask);
            refreshTable();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number for days!");
        }
    }

    private void editTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to edit!");
            return;
        }

        Task task = taskManager.getAllTasks().get(selectedRow);

        String newDescription = JOptionPane.showInputDialog(this,
                "Edit description:", task.getDescription());
        if (newDescription == null || newDescription.trim().isEmpty()) return;


        taskManager.updateTask(selectedRow, newDescription, task.getDeadline());
        refreshTable();
    }

    private void deleteTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to delete");
            return;
        }

            taskManager.deleteTask(selectedRow);
            refreshTable();

    }

    private void markComplete() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task!");
            return;
        }

        taskManager.markComplete(selectedRow);
        refreshTable();
    }

    private void showAllTasks(){
        refreshTable();
    }

    private void showCompletedTasks(){
        table.setRowCount(0);
        for (Task task : taskManager.getCompletedTasks()) {
            Object[] row = createTableRow(task);
            table.addRow(row);
        }
    }

    private void showPendingTasks(){
        table.setRowCount(0);
        for (Task task : taskManager.getIncompleteTasks()) {
            Object[] row = createTableRow(task);
            table.addRow(row);
        }
    }

    private void setUpMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem loadItem = new JMenuItem("Load");
        JMenuItem exitItem = new JMenuItem("Exit");
        JMenuItem exportItem = new JMenuItem("Export");
        JMenuItem importItem = new JMenuItem("Import");

        JMenuItem viewAll = new JMenuItem("View All");
        JMenuItem viewCompleted = new JMenuItem("View Completed");
        JMenuItem viewPending = new JMenuItem("View Pending");

        loadItem.addActionListener(e -> {
            taskManager.loadFromFile();

            refreshTable();
            JOptionPane.showMessageDialog(null, "Loaded from file successfully");
        });

        saveItem.addActionListener(e -> {
            taskManager.saveToFile();
            JOptionPane.showMessageDialog(null, "Saved to file successfully");

        });



        exitItem.addActionListener(e -> System.exit(0));

        exportItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Export Tasks to CSV");
            fileChooser.setSelectedFile(new java.io.File("tasks.csv"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();

                if (!file.getName().toLowerCase().endsWith(".csv")) {
                    file = new java.io.File(file.getParent(), file.getName() + ".csv");
                }

                taskManager.exportUsingCSV(file);
                JOptionPane.showMessageDialog(this,
                        "Exported " + taskManager.getAllTasks().size() + " tasks to:\n" +
                                file.getAbsolutePath());
            }
        });

        importItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Import Tasks from CSV");

            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                taskManager.importFromCSV(file);
                refreshTable();
                JOptionPane.showMessageDialog(this,
                        "Imported " + taskManager.getAllTasks().size() + " tasks from CSV!");
            }
        });

        fileMenu.add(loadItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator(); //
        fileMenu.add(exportItem);
        fileMenu.add(importItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        //EDITING
        JMenu editMenu = new JMenu("Edit");
        JMenuItem addItem = new JMenuItem("Add Task");
        JMenuItem removeItem = new JMenuItem("Remove Task");
        JMenuItem editItem = new JMenuItem("Edit Task");
        JMenuItem completeItem = new JMenuItem("Mark Complete");

        addItem.addActionListener(e -> addTask());
        editItem.addActionListener(e -> editTask());
        removeItem.addActionListener(e -> deleteTask());
        completeItem.addActionListener(e -> markComplete());

        editMenu.add(addItem);
        editMenu.add(removeItem);
        editMenu.add(editItem);
        editMenu.add(completeItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        setJMenuBar(menuBar);


    }

    //defines column headers using defaultTableModel
    private void setUpTable() {
        String[] columns = {"Description", "Deadline", "Completed", "Created", "Modified", "Completed Date", "Deleted"};

        table = new DefaultTableModel(columns, 0);


    taskTable =new JTable(table);
    taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scrollPane = new JScrollPane(taskTable);

    add(scrollPane, BorderLayout.CENTER);
}

    private void setUpButtons() {
        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("Add Task");
        JButton editButton = new JButton("Edit Task");
        JButton deleteButton = new JButton("Delete Task");
        JButton completeButton = new JButton("Mark Complete");

        JButton showAll = new JButton("All");
        JButton showCompleted = new JButton("Completed");
        JButton showPending = new JButton("Pending");

        addButton.addActionListener(e -> addTask());
        editButton.addActionListener(e -> editTask());
        deleteButton.addActionListener(e -> deleteTask());
        completeButton.addActionListener(e -> markComplete());
        showAll.addActionListener(e -> showAllTasks());
        showCompleted.addActionListener(e -> showCompletedTasks());
        showPending.addActionListener(e -> showPendingTasks());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(completeButton);
        buttonPanel.add(showAll);
        buttonPanel.add(showCompleted);
        buttonPanel.add(showPending);

        add(buttonPanel, BorderLayout.SOUTH);
    }


    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            GUI frame = new GUI();
            frame.setVisible(true);
        });
    }


}
