/* data-layer */
package Task;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


//using serializable so we can allow whole object to be saved to a binary file
public class Task implements Serializable {

    private static final long serialVersionUID = 1L; //version number handling for saving and loading data(1L ->VERSION01)

    private String description;
    private LocalDateTime deadline;
    private boolean completed;

    //for history tracking as requested
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private LocalDateTime deletionDate;
    private LocalDateTime completionDate; //it tracks the completion time of a task


    //CONSTRUCTOR FOR CREATING NEW TASKS
    public Task(String description, LocalDateTime deadline) {
        this(description, deadline, false, LocalDateTime.now());
    }
    //CONSTRUCTOR FOR LOADING FROM FILE
    public Task(String description, LocalDateTime deadline, boolean completed, LocalDateTime creationDate) {
        this.description = description;
        this.deadline = deadline;
        this.completed = completed;
        this.creationDate = creationDate;
        this.modificationDate = creationDate;
        this.deletionDate = null;
        this.completionDate = null;
    }

    //GETTERS
    public String getDescription() {return description;}
    public LocalDateTime getDeadline() {return deadline;}
    public boolean isCompleted() {return completed;}
    public LocalDateTime getCreationDate() {return creationDate;}
    public LocalDateTime getModificationDate() {return modificationDate;}
    public LocalDateTime getDeletionDate() {return deletionDate;}
    public LocalDateTime getCompletionDate() {return completionDate;}


    //SETTERS
    public void setDescription(String description) {
        this.description = description;
        updateModificationDate(); //since we keep a history of the modifications
    }
    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
        updateModificationDate();
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
        if(completed){
            this.completionDate = LocalDateTime.now();
        }
        else{
            this.completionDate = null; //if it is not done it will reset
        }
        updateModificationDate();
    }
    public void setDeletionDate(LocalDateTime deletionDate) {
        this.deletionDate = deletionDate;
        updateModificationDate();
    }

    public void setCompleteDate(LocalDateTime deletionDate) {
        this.deletionDate = deletionDate;
        updateModificationDate();
    }



    //HELPER METHOD
    public void updateModificationDate() {
        this.modificationDate = LocalDateTime.now();
    }


    //FOR CSV FILE
    public String toCSV(){
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String deadline01 = (deadline != null) ? deadline.format(fmt) : "No Deadline";
        String compDate = (completionDate != null) ? completionDate.format(fmt) : "-";

        return String.format("\"%s\",%s,%s,%s,%s", //for ignoring quotes to prevent parsing errors
                description.replace("\"",  "\"\""),
                deadline01,
                completed ? "Yes" : "No",
                creationDate.format(fmt),
                compDate);
    }


    @Override
    public String toString() {
        return description;
    }
}
