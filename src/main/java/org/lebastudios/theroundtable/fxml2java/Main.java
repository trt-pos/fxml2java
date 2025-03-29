package org.lebastudios.theroundtable.fxml2java;

import org.lebastudios.theroundtable.fxml2java.converter.FXMLToJavaConvertor;
import org.lebastudios.theroundtable.fxml2java.converter.MainClass;

import java.io.*;

public class Main
{
    public static void main(String[] args)
    {
        String fxmlPath = "src/main/resources/testUi.fxml";

        generateCompiledFxml(fxmlPath);
    }

    private static void generateCompiledFxml(String pathToFxml)
    {
        String fxmlFolder = pathToFxml.substring(0, pathToFxml.lastIndexOf('/'));
        File fxmlFile = new File(pathToFxml);
        String fxmlFileName = fxmlFile.getName();

        String outputPath = fxmlFolder
                .replace("resources", "java");

        String className = fxmlFileName
                .substring(0, 1).toUpperCase()
                + fxmlFileName.substring(1).replace(".fxml", "")
                + "$View";

        File outputFile = new File(outputPath, className + ".java");

        String packageName = pathToFxml.substring(pathToFxml.indexOf("resources") + 10)
                .replace(fxmlFileName, "")
                .replace("/", ".");

        if (packageName.endsWith("."))
        {
            packageName = packageName.substring(0, packageName.length() - 1);
        }

        try
        {
            try (InputStream inputStream = new FileInputStream(fxmlFile);
                 Writer writer = new FileWriter(outputFile)
            )
            {
                MainClass mainClass = new MainClass(className, packageName);
                mainClass.setNodeModifiers(1, 16);
                FXMLToJavaConvertor convertor = new FXMLToJavaConvertor();
                convertor.convert(mainClass, inputStream);

                String content = mainClass.toString();
                writer.write(content);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
