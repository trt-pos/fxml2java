package org.lebastudios.theroundtable.fxml2java.processor;

import com.google.auto.service.AutoService;
import org.lebastudios.theroundtable.fxml2java.CompileFxml;
import org.lebastudios.theroundtable.fxml2java.converter.FXMLToJavaConvertor;
import org.lebastudios.theroundtable.fxml2java.converter.MainClass;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AutoService(Processor.class)
@SupportedAnnotationTypes("org.lebastudios.theroundtable.fxml2java.CompileFxml")
@SupportedSourceVersion(SourceVersion.RELEASE_22)
public class FxmlCompilerProcessor extends AbstractProcessor
{
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        ArrayList<String> fxmlPaths = new ArrayList<>();
        
        for (Element element : roundEnv.getElementsAnnotatedWith(CompileFxml.class)) {

            CompileFxml compileFxml = element.getAnnotation(CompileFxml.class);
            
            fxmlPaths.addAll(Arrays.stream(compileFxml.fxmls()).toList());

            try
            {
                for (String dir : compileFxml.directories())
                {
                    Filer filer = processingEnv.getFiler();
                    String tmpFile = filer.createResource(
                            StandardLocation.CLASS_OUTPUT,
                            dir.replace("/", "."),
                            "dummy.txt"
                    ).toUri().getPath();

                    File directory = new File(tmpFile).getParentFile();
                    if (!directory.exists() || !directory.isDirectory()) continue;

                    for (File fxml : Objects.requireNonNull(directory.listFiles(
                            pathname -> pathname.getName().endsWith(".fxml") && pathname.isFile())))
                    {
                        fxmlPaths.add(dir + "/" + fxml.getName());
                    }
                }
            }
            catch (Exception exception)
            {
                throw new RuntimeException(exception);
            }
        }
        
        for (String fxmlPath : fxmlPaths)
        {
            generateCompiledFxml(fxmlPath);
        }
        
        return true;
    }
    
    private void generateCompiledFxml(String pathToFxml)
    {
        File fxmlFile = new File(pathToFxml);
        String fxmlFileName = fxmlFile.getName();

        String className = fxmlFileName
                .substring(0, 1).toUpperCase()
                + fxmlFileName.substring(1).replace(".fxml", "")
                + "$View";
        
        String packageName = pathToFxml
                .replace(fxmlFileName, "")
                .replace("/", ".");
        
        if (packageName.endsWith(".")) 
        {
            packageName = packageName.substring(0, packageName.length() - 1);
        }

        if (packageName.startsWith(".")) 
        {
            packageName = packageName.substring(1);
        }
        
        try
        {
            JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(className);
            FileObject resource = processingEnv.getFiler().getResource(
                    StandardLocation.CLASS_OUTPUT,
                    packageName,
                    fxmlFileName
            );
            
            try (InputStream inputStream = resource.openInputStream();
                 Writer writer = builderFile.openWriter()
            )
            {
                MainClass mainClass = new MainClass(className, packageName);
                mainClass.setNodeModifiers(1, 16);
                FXMLToJavaConvertor convertor = new FXMLToJavaConvertor();
                convertor.convert(mainClass, inputStream);

                String content = mainClass.toString();

                Matcher matcher = Pattern.compile("\"%([^ \"]*)\"").matcher(content);
                while (matcher.find())
                {
                    String translationKey = matcher.group(1);
                    content = content.replace(matcher.group(0), "LangFileLoader.getTranslation(\"" + translationKey + "\")");
                }
                
                writer.write(content);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
