package org.lebastudios.theroundtable.fxml2java.processor;

import com.google.auto.service.AutoService;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
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
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AutoService(Processor.class)
@SupportedAnnotationTypes("org.lebastudios.theroundtable.fxml2java.CompileFxml")
@SupportedSourceVersion(SourceVersion.RELEASE_22)
public class FxmlCompilerProcessor extends AbstractProcessor
{
    private boolean toolsExported = true;
    private Trees trees;
    private TreeMaker treeMaker;
    private Names names;
    
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        trees = Trees.instance(processingEnv);

        try
        {
            Context context = (Context) processingEnv.getClass().getMethod("getContext").invoke(processingEnv);

            treeMaker = TreeMaker.instance(context);
            names = Names.instance(context);
        }
        catch (Exception exception)
        {
            toolsExported = false;
            printMissingExportWarning();
        }
    }

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
            if (toolsExported) 
            {
                try
                {
                    generateControllerMethod(fxmlPath);
                }
                catch (Exception exception)
                {
                    toolsExported = false;
                    printMissingExportWarning();
                }
            }
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

    private void generateControllerMethod(String pathToFxml)
    {
        File fxmlFile = new File(pathToFxml);
        String fxmlFileName = fxmlFile.getName();

        String className = fxmlFileName
                .substring(0, 1).toUpperCase()
                + fxmlFileName.substring(1).replace(".fxml", "")
                + "Controller";

        String viewClassName = className.replace("Controller", "$View");
        
        String packageName = pathToFxml
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
            classDecl.defs = classDecl.defs.append(generarMetodo(viewClassName));
        }
    }

    private JCTree.JCMethodDecl generarMetodo(String viewClassName) {
        JCTree.JCFieldAccess thisRoot = treeMaker.Select(
                treeMaker.Ident(names.fromString("this")), names.fromString("root")
        );

        JCTree.JCNewClass newInstance = treeMaker.NewClass(
                null,                         
                List.nil(),                    
                treeMaker.Ident(names.fromString(viewClassName)),
                List.of(treeMaker.Ident(names.fromString("this"))),  
                null                          
        );

        JCTree.JCExpressionStatement assignStatement = treeMaker.Exec(
                treeMaker.Assign(thisRoot, newInstance)
        );

        JCTree.JCExpressionStatement initializeCall = treeMaker.Exec(
                treeMaker.Apply(
                        List.nil(),
                        treeMaker.Select(
                                treeMaker.Ident(names.fromString("this")),
                                names.fromString("initialize")
                        ),
                        List.nil()
                )
        );

        JCTree.JCBlock block = treeMaker.Block(0, List.of(assignStatement, initializeCall));

        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PROTECTED),
                names.fromString("loadFXML"),
                treeMaker.TypeIdent(TypeTag.VOID),   
                List.nil(),                          
                List.nil(),                          
                List.nil(),                          
                block,                               
                null                                 
        );
    }
    
    private void printMissingExportWarning()
    {
        processingEnv.getMessager().printMessage(
                Diagnostic.Kind.WARNING,
                """
FXMLCompilerProcessor: Missing exports detected:
--add-exports=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED

The compile process will finish as always but the generated code will not be created.
This means that the FXML has beed compiled but the code to use them isn't generated.
The default loadFxml() method implemented in the Controller class detects the compiled view
and uses reflexion to build it but, if you want to avoid it, override the loadFxml() method
as we show here:

protected void loadFXML() {
    this.root = new <ExamplePane$View>(this);
    this.initialize();
}

Note: The controller for this example is ExamplePaneController and the FXML file is examplePane.fxml
"""
                
        );
    }
}
