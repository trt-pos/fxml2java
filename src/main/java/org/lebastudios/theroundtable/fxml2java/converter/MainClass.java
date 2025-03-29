package org.lebastudios.theroundtable.fxml2java.converter;

import java.lang.reflect.Modifier;

import org.w3c.dom.Node;

public class MainClass {
  private String extendedClassName;

  private CodeBuilder mainBuilder;

  private static StringList methodList;

  private static StringList initList;

  private static StringList importList;

  private static StringList declarationList;

  private final StringList childList;

  private final StringList attributeList;

  private final String className;

  private static String packageName;
  private static String controllerClassName;

  private static int classModifier;

  private static int nodeModifier;

  private static int methodModifier;

  private static OnIncludeListener includeListener;

  public MainClass(String className, String packageName) {
    controllerClassName = className.replace("$View", "Controller");
    this.className = className;
    MainClass.packageName = packageName;
    importList = new StringList();
    initList = new StringList();
    initList.add(new DeclarationNode("{0}", "this.controller = controller;"));
    this.attributeList = new StringList();
    this.childList = new StringList();
    methodList = new StringList();
    declarationList = new StringList();
    classModifier = Modifier.PUBLIC;
    nodeModifier = 18;
    methodModifier = Modifier.PRIVATE;
  }

  public void setClassModifiers(int... modifiers) {
    classModifier = 0;
    for (int modifier : modifiers)
      classModifier += modifier;
  }

  public void setNodeModifiers(int... modifiers) {
    nodeModifier = 0;
    for (int modifier : modifiers)
      nodeModifier += modifier;
  }

  public void setMethodModifiers(int... modifiers) {
    methodModifier = 0;
    for (int modifier : modifiers)
      methodModifier += modifier;
  }

  public static int getClassModifier() {
    return classModifier;
  }

  public static int getNodeModifier() {
    return nodeModifier;
  }

  public static int getMethodModifier() {
    return methodModifier;
  }

  private CodeBuilder createClass() {
    CodeBuilder classBuilder = new CodeBuilder(new String[] { Modifier.toString(classModifier) + " class " + this.className, "    " });
    String extendedClassName = (this.extendedClassName == null) ? " {\n\n" : (" extends " + this.extendedClassName + " {\n\n");
    classBuilder.appendWithoutIndent(extendedClassName);
    classBuilder.appendWithoutIndent("    public final " + controllerClassName + " controller;\n");
    classBuilder.appendWithoutIndent(declarationList.toString(classBuilder.getIndent()));
    classBuilder.append(createConstructor(classBuilder.getIndent()));
    classBuilder.append("}\n");
    return classBuilder;
  }

  private CodeBuilder createConstructor(String indent) {
    String consModifier = "public";
    if (Modifier.isPrivate(classModifier))
      consModifier = "private";
    CodeBuilder constructorBuilder = new CodeBuilder(consModifier + " " + this.className 
            + "(" + controllerClassName + " controller"  + ") {\n\n", indent + "    ");
    constructorBuilder.appendWithoutIndent(initList.toString(constructorBuilder.getIndent()));
    
    // initList constains the fields of the class. 
    // each field that starts with $ is a named node (fx:id) and the controller needs to know them
    // so we need to add them to the constructor
      for (Object o : initList)
      {
          String line = o.toString();
          if (line.startsWith("$"))
          {
              String variableName = line.substring(1, line.indexOf('='));
              constructorBuilder.appendWithoutIndent("        controller." + variableName 
                      + " = this.$" + variableName + ";\n");
          }
      }
      constructorBuilder.appendWithoutIndent("\n");
    
    constructorBuilder.appendWithoutIndent(this.attributeList.toString(constructorBuilder.getIndent()));
    constructorBuilder.appendWithoutIndent(this.childList.toString(constructorBuilder.getIndent()));
    return constructorBuilder;
  }

  public static boolean containsImport(String toMatch) {
    return importList.stream().anyMatch(t -> {
          DeclarationNode importNode = (DeclarationNode)t;
          return importNode.getNodeString(1).equals(toMatch);
        });
  }

  public static StringList getDeclarationList() {
    return declarationList;
  }

  public static StringList getInitList() {
    return initList;
  }

  public StringList getAttributeList() {
    return this.attributeList;
  }

  public static String getPackageName() {
    return packageName;
  }

  public String toString() {
    String packageValue = (packageName == null || packageName.isEmpty()) ? "\n" : ("package " + packageName + ";\n\n");
    this.mainBuilder = new CodeBuilder(new String[] { packageValue });
    this.mainBuilder.append("import org.lebastudios.theroundtable.locale.LangFileLoader;\n");
    this.mainBuilder.append(importList.toString());
    this.mainBuilder.append(createClass());
    this.mainBuilder.append(methodList.toString("    "));
    this.mainBuilder.append("}\n");

    return this.mainBuilder.toString();
  }

  public static void addMethod(String methodName, String eventName, String variableName) {
    methodList.add("");
    boolean isAbstract = Modifier.isAbstract(methodModifier);
    String newMethod = Modifier.toString(methodModifier) + " void " + methodName + "(" + eventName + " " + variableName + ")" + (isAbstract ? ";" : " {");
    if (!methodList.contains(newMethod)) {
      methodList.add(newMethod);
      if (!isAbstract) {
        methodList.add("        controller." + methodName + "(" + variableName + ");");
        methodList.add("}");
      }
      if (!Modifier.isAbstract(classModifier) && isAbstract)
        classModifier += 1024;
    }
  }

  public static StringList getImport() {
    return importList;
  }

  public void setRootNode(Node node) {
    FXNode fXNode = new FXNode(node, true);
    this.childList.addAll(filterList(fXNode.getChildList()));
    this.attributeList.addAll(filterList(fXNode.getAttributeList()));
    if (fXNode.getShortestParameterCount() == 0 && !fXNode.isFinal()) {
      this.extendedClassName = fXNode.getNodeName();
    } else {
      this.extendedClassName = "Parent";
      String parentImport = "javafx.scene.Parent";
      if (!containsImport(parentImport))
        importList.add(new DeclarationNode("{0} {1}{2}", "import", parentImport, ";"));
      this.childList.add("getChildren().add(" + fXNode.getVariableName() + ");");
    }
  }

  private StringList filterList(StringList stringList) {
    if (stringList.isEmpty())
      return stringList;
    if (stringList.get(0).equals(""))
      stringList.remove(0);
    for (int i = 0; i < stringList.size(); i++) {
      if (stringList.get(i).equals("")) {
        int next = i + 1;
        if (next < stringList.size() &&
          stringList.get(next).equals(""))
          stringList.remove(next);
      }
    }
    int last = stringList.size() - 1;
    if (last > -1 && stringList.get(last).equals(""))
      stringList.remove(last);
    return stringList;
  }

  public void setIncludeListener(OnIncludeListener includeListener) {
    MainClass.includeListener = includeListener;
  }

  public static String getIncludeName(String fxmlFileName) {
    return (includeListener == null) ? null : includeListener.getIncludeExists(fxmlFileName);
  }

  public static interface OnIncludeListener {
    String getIncludeExists(String param1String);
  }
}
