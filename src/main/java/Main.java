import converter.FXMLToJavaConvertor;
import converter.MainClass;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Main
{
    public static void main(String[] args)
    {
        String fxmlPath = args[0];
        String fxmlFolder = fxmlPath.substring(0, fxmlPath.lastIndexOf('/'));
        File fxmlFile = new File(fxmlPath);
        String fxmlFileName = fxmlFile.getName();

        String outputPath = fxmlFolder
                .replace("resources", "java");

        String className = fxmlFileName
                .substring(0, 1).toUpperCase()
                + fxmlFileName.substring(1).replace(".fxml", "")
                + "$View";

        File outputFile = new File(outputPath, className + ".java");

        String packageName = fxmlPath.substring(fxmlPath.indexOf("resources") + 10)
                .replace(fxmlFileName, "")
                .replace("/", ".");

        try (InputStream inputStream = new FileInputStream(fxmlFile);
             Writer writer = new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8)
        )
        {
            MainClass mainClass = new MainClass(className, packageName);
            mainClass.setClassModifiers(1, 1024);
            mainClass.setNodeModifiers(4, 16);
            mainClass.setMethodModifiers(4, 1024);
            FXMLToJavaConvertor convertor = new FXMLToJavaConvertor();
            convertor.convert(mainClass, inputStream);

            String content = mainClass.toString();
            writer.write(content);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
