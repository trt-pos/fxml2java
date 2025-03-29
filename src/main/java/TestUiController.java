import javafx.scene.Node;
import org.lebastudios.theroundtable.fxml2java.CompileFxml;

@CompileFxml(
        fxmls = "src/main/resources/testUi.fxml"
)
public class TestUiController
{
    public Node enableRemoteDb;
    public Node localDbSection;
    public Node databasesDirectory;
    public Node enableBackups;
    public Node backupSection;
    public Node databasesBackupDirectory;
    public Node numMaxBackups;
    public Node remoteDbSection;
    public Node remoteDbName;
    public Node remoteDbHost;
    public Node remoteDbPort;
    public Node remoteDbUser;
    public Node remoteDbPassword;
    
    public void selectDatabasesBackupDirectory(javafx.event.ActionEvent actionEvent) {
        
    }
}
