
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class TestUi$View extends VBox {

    public final TestUiController controller;
    public final HBox hBox;
    public final Label label;
    public final CheckBox $enableRemoteDb;
    public final VBox $localDbSection;
    public final HBox hBox0;
    public final Label label0;
    public final Label $databasesDirectory;
    public final HBox hBox1;
    public final Label label1;
    public final CheckBox $enableBackups;
    public final VBox $backupSection;
    public final HBox hBox2;
    public final Label label2;
    public final Label $databasesBackupDirectory;
    public final Button button;
    public final HBox hBox3;
    public final Label label3;
    public final TextField $numMaxBackups;
    public final VBox $remoteDbSection;
    public final HBox hBox4;
    public final Label label4;
    public final TextField $remoteDbName;
    public final HBox hBox5;
    public final Label label5;
    public final TextField $remoteDbHost;
    public final Region region;
    public final Label label6;
    public final TextField $remoteDbPort;
    public final HBox hBox6;
    public final Label label7;
    public final TextField $remoteDbUser;
    public final Region region0;
    public final Label label8;
    public final TextField $remoteDbPassword;

    public TestUi$View(TestUiController controller) {

        this.controller = controller;
        hBox = new HBox();
        label = new Label();
        $enableRemoteDb = new CheckBox();
        $localDbSection = new VBox();
        hBox0 = new HBox();
        label0 = new Label();
        $databasesDirectory = new Label();
        hBox1 = new HBox();
        label1 = new Label();
        $enableBackups = new CheckBox();
        $backupSection = new VBox();
        hBox2 = new HBox();
        label2 = new Label();
        $databasesBackupDirectory = new Label();
        button = new Button();
        hBox3 = new HBox();
        label3 = new Label();
        $numMaxBackups = new TextField();
        $remoteDbSection = new VBox();
        hBox4 = new HBox();
        label4 = new Label();
        $remoteDbName = new TextField();
        hBox5 = new HBox();
        label5 = new Label();
        $remoteDbHost = new TextField();
        region = new Region();
        label6 = new Label();
        $remoteDbPort = new TextField();
        hBox6 = new HBox();
        label7 = new Label();
        $remoteDbUser = new TextField();
        region0 = new Region();
        label8 = new Label();
        $remoteDbPassword = new TextField();

        controller.enableRemoteDb  = this.$enableRemoteDb ;
        controller.localDbSection  = this.$localDbSection ;
        controller.databasesDirectory  = this.$databasesDirectory ;
        controller.enableBackups  = this.$enableBackups ;
        controller.backupSection  = this.$backupSection ;
        controller.databasesBackupDirectory  = this.$databasesBackupDirectory ;
        controller.numMaxBackups  = this.$numMaxBackups ;
        controller.remoteDbSection  = this.$remoteDbSection ;
        controller.remoteDbName  = this.$remoteDbName ;
        controller.remoteDbHost  = this.$remoteDbHost ;
        controller.remoteDbPort  = this.$remoteDbPort ;
        controller.remoteDbUser  = this.$remoteDbUser ;
        controller.remoteDbPassword  = this.$remoteDbPassword ;

        setSpacing(15);
        setPadding(new Insets(10.0));

        hBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        hBox.setSpacing(5);

        HBox.setHgrow(label, javafx.scene.layout.Priority.ALWAYS);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setText("%phrase.enableremotedb");

        $localDbSection.setSpacing(5);

        hBox0.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        hBox0.setSpacing(5);

        HBox.setHgrow(label0, javafx.scene.layout.Priority.ALWAYS);
        label0.setMaxWidth(Double.MAX_VALUE);
        label0.setText("%phrase.databasedir");

        $databasesDirectory.setStyle("-fx-font-style: italic");

        hBox1.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        hBox1.setSpacing(5);

        HBox.setHgrow(label1, javafx.scene.layout.Priority.ALWAYS);
        label1.setMaxWidth(Double.MAX_VALUE);
        label1.setText("%phrase.activatebackups");

        $backupSection.setSpacing(15);

        hBox2.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        hBox2.setSpacing(5);

        HBox.setHgrow(label2, javafx.scene.layout.Priority.ALWAYS);
        label2.setMaxWidth(Double.MAX_VALUE);
        label2.setText("%phrase.databasebackupdir");

        $databasesBackupDirectory.setStyle("-fx-font-style: italic");

        button.setOnAction(this::selectDatabasesBackupDirectory);

        hBox3.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        hBox3.setSpacing(5);

        HBox.setHgrow(label3, javafx.scene.layout.Priority.ALWAYS);
        label3.setMaxWidth(Double.MAX_VALUE);
        label3.setText("%phrase.nummaxBackups");

        $numMaxBackups.setPrefWidth(50);

        $remoteDbSection.setSpacing(5);

        hBox4.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        hBox4.setSpacing(5);

        HBox.setHgrow(label4, javafx.scene.layout.Priority.ALWAYS);
        label4.setMaxWidth(Double.MAX_VALUE);
        label4.setPrefWidth(250);
        label4.setText("%word.namedb");

        $remoteDbName.setPrefWidth(250);

        hBox5.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        hBox5.setSpacing(5);

        HBox.setHgrow(label5, javafx.scene.layout.Priority.SOMETIMES);
        label5.setMaxWidth(Double.MAX_VALUE);
        label5.setPrefWidth(150);
        label5.setText("%word.host");

        $remoteDbHost.setPrefWidth(150);

        HBox.setHgrow(region, javafx.scene.layout.Priority.ALWAYS);

        HBox.setHgrow(label6, javafx.scene.layout.Priority.SOMETIMES);
        label6.setMaxWidth(Double.MAX_VALUE);
        label6.setPrefWidth(150);
        label6.setText("%word.port");

        $remoteDbPort.setPrefWidth(150);

        hBox6.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        hBox6.setSpacing(5);

        HBox.setHgrow(label7, javafx.scene.layout.Priority.SOMETIMES);
        label7.setMaxWidth(Double.MAX_VALUE);
        label7.setPrefWidth(150);
        label7.setText("%word.user");

        $remoteDbUser.setPrefWidth(150);

        HBox.setHgrow(region0, javafx.scene.layout.Priority.ALWAYS);

        HBox.setHgrow(label8, javafx.scene.layout.Priority.SOMETIMES);
        label8.setMaxWidth(Double.MAX_VALUE);
        label8.setPrefWidth(150);
        label8.setText("%word.password");

        $remoteDbPassword.setPrefWidth(150);

        hBox.getChildren().add(label);
        hBox.getChildren().add($enableRemoteDb);
        getChildren().add(hBox);
        hBox0.getChildren().add(label0);
        hBox0.getChildren().add($databasesDirectory);
        $localDbSection.getChildren().add(hBox0);
        hBox1.getChildren().add(label1);
        hBox1.getChildren().add($enableBackups);
        $localDbSection.getChildren().add(hBox1);
        hBox2.getChildren().add(label2);
        hBox2.getChildren().add($databasesBackupDirectory);
        hBox2.getChildren().add(button);
        $backupSection.getChildren().add(hBox2);
        hBox3.getChildren().add(label3);
        hBox3.getChildren().add($numMaxBackups);
        $backupSection.getChildren().add(hBox3);
        $localDbSection.getChildren().add($backupSection);
        getChildren().add($localDbSection);
        hBox4.getChildren().add(label4);
        hBox4.getChildren().add($remoteDbName);
        $remoteDbSection.getChildren().add(hBox4);
        hBox5.getChildren().add(label5);
        hBox5.getChildren().add($remoteDbHost);
        hBox5.getChildren().add(region);
        hBox5.getChildren().add(label6);
        hBox5.getChildren().add($remoteDbPort);
        $remoteDbSection.getChildren().add(hBox5);
        hBox6.getChildren().add(label7);
        hBox6.getChildren().add($remoteDbUser);
        hBox6.getChildren().add(region0);
        hBox6.getChildren().add(label8);
        hBox6.getChildren().add($remoteDbPassword);
        $remoteDbSection.getChildren().add(hBox6);
        getChildren().add($remoteDbSection);

    }

    private void selectDatabasesBackupDirectory(javafx.event.ActionEvent actionEvent) {
    controller.selectDatabasesBackupDirectory(actionEvent);
    }

}
