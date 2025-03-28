package fxml2java.processor;

import fxml2java.CompileFxml;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

@SupportedAnnotationTypes("fxml2java.CompileFxml")
@SupportedSourceVersion(SourceVersion.RELEASE_22)
public class FxmlCompilerProcessor extends AbstractProcessor
{
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        for (Element element : roundEnv.getElementsAnnotatedWith(CompileFxml.class)) {
            String className = element.getSimpleName() + "Generado";
            try {
                JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(className);
                try (Writer writer = builderFile.openWriter()) {
                    writer.write("public class " + className + " { }");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return true;
    }
}
