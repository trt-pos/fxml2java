package fxml2java.processor;

import fxml2java.CompileFxml;
import fxml2java.converter.FXMLToJavaConvertor;
import fxml2java.converter.MainClass;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@SupportedAnnotationTypes("fxml2java.CompileFxml")
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

        try
        {
            JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(className);
            
            try (InputStream inputStream = new FileInputStream(fxmlFile);
                 Writer writer = builderFile.openWriter()
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
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
