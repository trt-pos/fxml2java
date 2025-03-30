# FXML2Java

This project is a fork of [fxml2javaconverter](https://github.com/garawaa/fxml2javaconverter).

The original project was modified to fit the trt-pos projects needs. The goal of this repo
is to make an annotation processor that can convert FXML files to Java classes that integrate
with the JavaFX and TRT frameworks.

## Index
- [Usage](#usage)
- [Adding the dependency](#adding-the-dependency)
  - [Maven](#maven)

## Usage

After adding the dependency to your project, you can use the `@CompileFxml` annotation to
compile your FXML files to Java classes. The annotation processor will automatically generate
the Java classes for you. The generated classes will be in the same package as the FXML files.

Example:
```java
@CompileFxml(
        fxmls = {
          "org/lebastudios/theroundtable/mainStage.fxml",      
        },
        directories = {
                "org/lebastudios/theroundtable/accounts",
                "org/lebastudios/theroundtable/config",
                "org/lebastudios/theroundtable/dialogs",
                "org/lebastudios/theroundtable/plugins",
                "org/lebastudios/theroundtable/setup",
                "org/lebastudios/theroundtable/tasks",
                "org/lebastudios/theroundtable/ui"
        }
)
public class CorePlugin implements IPlugin
{
    // Implementation of the IPlugin interface
}
```

We recommend using the `@CompileFxml` annotation on the main plugin class
(the one that implements IPlugin) of your project.

As the default behaviour, the Controller abstract class that you extended, tries to find
the compiled view in the same package as the FXML file and, if it finds it, it will use it 
but needs reflection.
```java
protected void loadFXML()
{
    // .....
    
    try
    {
        String viewClassName = this.getClass().getName().replace("Controller", "$View");
        Class<?> viewClass = this.getClass().getClassLoader().loadClass(
                viewClassName
        );

        this.root = (Node) viewClass.getConstructors()[0].newInstance(this);
        this.initialize();
        return;
    }
    catch (ClassNotFoundException ignore) {}
    catch (InvocationTargetException | InstantiationException | IllegalAccessException e)
    {
        throw new RuntimeException(e);
    }

    // ....
}
```

To avoid this, you can override the `loadFXML` method in your controller class as follows:
```java
@Override
protected void loadFxml()
{
    this.root = new ExampleUi$View(this);
    this.initialize();
}
```

Also, the annotations processor can override the method at compile time for you, but, 
to do this, you need to add the following args to the compile command:
```
--add-exports=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED
--add-exports=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED
```

Note: Remember that, for each FXML file with name exampleUi.fxml, the annotations processor 
expects a class named ExampleUiController.java in the same package as the FXML. With this set,
it will be generating a class named ExampleUi$View.java in the same package.

## Adding the dependency
### Maven
Add the following repository to your `pom.xml`:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Add the following dependency to your `pom.xml`:
```xml
<dependencies>
    <dependency>
        <groupId>com.github.trt-pos</groupId>
        <artifactId>fxml2java</artifactId>
        <version>0.2.1</version>
    </dependency>
</dependencies>
```

Add the following compiler plugin configuration to your `pom.xml`:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.13.0</version>
    <configuration>
        <source>22</source>
        <target>22</target>
        <annotationProcessorPaths>
            <path>
                <groupId>com.github.trt-pos</groupId>
                <artifactId>fxml2java</artifactId>
                <version>0.2.1</version>
            </path>
            <path>
                <groupId>org.lebastudios.theroundtable</groupId>
                <artifactId>desktop-app</artifactId>
                <version>3.0.0</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

Note: Limitations of the original implementation that are still present, the fxml2java processor 
needs access to the classes you are using in your FXML files. This means that you, for example, use 
the IconButton class included in the TRT framework, you need to add the dependency to the
annotationProcessorPaths as we do in the example above. 
Note 2: The javafx-controls are included in the annotation processor, so you don't need to add them to the
annotationProcessorPaths.