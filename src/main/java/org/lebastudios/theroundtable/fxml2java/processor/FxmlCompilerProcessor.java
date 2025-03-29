package org.lebastudios.theroundtable.fxml2java.processor;

import com.google.auto.service.AutoService;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import org.lebastudios.theroundtable.fxml2java.CompileFxml;
import org.lebastudios.theroundtable.fxml2java.converter.FXMLToJavaConvertor;
import org.lebastudios.theroundtable.fxml2java.converter.MainClass;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("org.lebastudios.theroundtable.fxml2java.CompileFxml")
@SupportedSourceVersion(SourceVersion.RELEASE_22)
public class FxmlCompilerProcessor extends AbstractProcessor
{
    private Trees trees;
    private TreeMaker treeMaker;
    private Names names;
    
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        trees = Trees.instance(processingEnv);
        
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context);
    }
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        ArrayList<String> fxmlPaths = new ArrayList<>();
        
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
                    fxmlPaths.add(fxml.getAbsolutePath());
                }
            }
        }
        
        for (String fxmlPath : fxmlPaths)
        {
            //generateCompiledFxml(fxmlPath);
            //generateControllerMethod(fxmlPath);
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

    private void generateControllerMethod(String pathToFxml)
    {
        File fxmlFile = new File(pathToFxml);
        String fxmlFileName = fxmlFile.getName();

        String className = fxmlFileName
                .substring(0, 1).toUpperCase()
                + fxmlFileName.substring(1).replace(".fxml", "")
                + "Controller";

        String packageName = pathToFxml.substring(pathToFxml.indexOf("resources") + 10)
                .replace(fxmlFileName, "")
                .replace("/", ".");

        if (packageName.endsWith("."))
        {
            packageName = packageName.substring(0, packageName.length() - 1);
        }

        TypeElement typeElement = processingEnv.getElementUtils().getTypeElement(
                (packageName.isBlank() ? "" : (packageName + ".")) + className
        );
        JCTree tree = (JCTree) trees.getTree(typeElement);
        if (tree instanceof JCTree.JCClassDecl classDecl) {
            classDecl.defs = classDecl.defs.append(generarMetodo());
        }
    }

    private JCTree.JCMethodDecl generarMetodo() {
        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC),
                names.fromString("metodoGenerado"),
                treeMaker.TypeIdent(TypeTag.VOID),
                List.nil(),
                List.nil(),
                List.nil(),
                treeMaker.Block(0, List.of(
                        treeMaker.Exec(
                                treeMaker.Apply(
                                        List.nil(),
                                        treeMaker.Select(treeMaker.Ident(names.fromString("System.out")), names.fromString("println")),
                                        List.of(treeMaker.Literal("Método generado"))
                                )
                        )
                )),
                null
        );
    }
}
