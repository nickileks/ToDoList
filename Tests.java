/*logic-layer*/
package Tests;

import Task.Task;
import TaskManager.TaskManager;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.*;


public class Tests {

    //TASK TESTS
    //checks if task is properly created
    @Test
    public void testTaskCreation(){
        Task task = new Task("Laundry", LocalDateTime.now().plusDays(1));

        assertEquals("Laundry", task.getDescription());
        assertFalse(task.isCompleted());
        assertNotNull(task.getCreationDate());
        assertNotNull(task.getModificationDate());
        assertNull(task.getDeletionDate());

    }

    @Test
    public void testTrackingForModification(){
        Task task = new Task("Laundry", LocalDateTime.now().plusDays(1));
        LocalDateTime initialDate = task.getModificationDate();

        task.setDescription("Updated");

        assertNotEquals(initialDate, task.getModificationDate());
    }

    //TASK MANAGER TESTS

    @Test
    public void testAddTask() {
        TaskManager manager = new TaskManager();
        manager.addTask(new Task("Task 1", LocalDateTime.now()));
        assertEquals(1, manager.getAllTasks().size());
    }

    @Test
    public void testDeleteTask() {
        TaskManager manager = new TaskManager();
        manager.addTask(new Task("Task", LocalDateTime.now()));
        manager.deleteTask(0);
        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    public void testMarkComplete() {
        TaskManager manager = new TaskManager();
        manager.addTask(new Task("Task", LocalDateTime.now()));
        manager.markComplete(0);
        assertTrue(manager.getAllTasks().get(0).isCompleted());
    }

    @Test
    public void testUpdateTask() {
        TaskManager manager = new TaskManager();
        manager.addTask(new Task("Old", LocalDateTime.now()));

        LocalDateTime newDeadline = LocalDateTime.now().plusDays(2);
        manager.updateTask(0, "New", newDeadline);

        Task updated = manager.getAllTasks().get(0);
        assertEquals("New", updated.getDescription());
        assertEquals(newDeadline, updated.getDeadline());
    }

    @Test
    public void testSaveAndLoad() throws FileNotFoundException {

        TaskManager saveManager = new TaskManager();
        saveManager.addTask(new Task("Save Test", LocalDateTime.now()));
        saveManager.saveToFile();


        TaskManager loadManager = new TaskManager();
        loadManager.loadFromFile();

        assertEquals(1, loadManager.getAllTasks().size());
        assertEquals("Save Test", loadManager.getAllTasks().get(0).getDescription());
    }

    @Test
    public void testInvalidIndexHandling() {
        TaskManager manager = new TaskManager();

        manager.deleteTask(-1);
        manager.deleteTask(999);
        manager.markComplete(0); // Empty list
        assertTrue("Should handle invalid indexes gracefully", true);
    }

    @Test
    public void testEmptyManager() {
        TaskManager manager = new TaskManager();
        assertEquals(0, manager.getAllTasks().size());
        assertTrue(manager.getAllTasks().isEmpty());
    }
    @Test
    public void testLayerSeparation() {
        Task task = new Task("Test", LocalDateTime.now());


        TaskManager manager = new TaskManager();
        manager.addTask(task);

        assertEquals(1, manager.getAllTasks().size());
        assertEquals("Test", manager.getAllTasks().get(0).getDescription());
    }


}

