package org.lebastudios.theroundtable.fxml2java.processor;

import com.google.auto.service.AutoService;
import org.lebastudios.theroundtable.fxml2java.CompileFxml;
import org.lebastudios.theroundtable.fxml2java.converter.FXMLToJavaConvertor;
import org.lebastudios.theroundtable.fxml2java.converter.MainClass;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.*;
import java.util.Objects;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("org.lebastudios.theroundtable.fxml2java.CompileFxml")
@SupportedSourceVersion(SourceVersion.RELEASE_22)
public class FxmlCompilerProcessor extends AbstractProcessor
{
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        for (Element element : roundEnv.getElementsAnnotatedWith(CompileFxml.class)) {

            CompileFxml compileFxml = element.getAnnotation(CompileFxml.class);

            for (String fxmlPath : compileFxml.fxmls())
            {
                generateCompiledFxml(fxmlPath);
            }
            
            for (String dir : compileFxml.directories())
            {
                File directory = new File(dir);
                if (!directory.exists() || !directory.isDirectory()) continue;
                
                for (File fxml : Objects.requireNonNull(directory.listFiles(
                        pathname -> pathname.getName().endsWith(".fxml") && pathname.isFile())))
                {
                    generateCompiledFxml(fxml.getAbsolutePath());
                }
            }
        }
        
        return true;
    }
    
    private void generateCompiledFxml(String pathToFxml)
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
            JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(className);
            
            try (InputStream inputStream = new FileInputStream(fxmlFile);
                 Writer writer = builderFile.openWriter()
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
