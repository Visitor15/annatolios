package com.voodootech.annatolios.annotations.processors;

import com.google.auto.service.AutoService;
import com.voodootech.annatolios.annotations.Monadic;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("com.voodootech.annatolios.annotations.Monadic")
public class MonadicProcessor extends AbstractProcessor {

    private Filer               filer;
    private Messager            messager;
    private Elements            elements;
    private Map<String, String> activitiesWithPackage;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer                   = processingEnvironment.getFiler();
        messager                = processingEnvironment.getMessager();
        elements                = processingEnvironment.getElementUtils();
        activitiesWithPackage   = new HashMap<>();
    }

    /*
    public <B extends MonadT<A>> B flatMap(Function<A, B> block) {
        return block.apply(ref());
    }

    public <B> B map(Function<A, B> block) {
        return block.apply(ref());
    }
     */

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Monadic.class);

        Set<String> uniqueIdCheckList = new HashSet<>();

        for(Element e : elements) {
            if (e.getKind() != ElementKind.INTERFACE) {
                return true;
            }

            if(e.getEnclosedElements().size() > 0) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Interface " + e.getSimpleName() + ".java must be empty.");
            }


//            ClassName monadT = ClassName.get("com.voodootech.annatolios.structures", "MonadT");
//            ClassName func = ClassName.get("java.util.function", "Function");
//
//
//
//            MethodSpec mapMethod = MethodSpec.methodBuilder("map")
//                    .addModifiers(Modifier.PUBLIC)
//                    .addTypeVariable(TypeVariableName.get("B", MonadT.class))
//                    .addParameter()
//
//            ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(AbstractObjectBuilder.class, propertyType,?);
//            ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(MonadT.class)
//
//            ParameterSpec parameterSpec = ParameterSpec.builder(parameterizedTypeName, name+"Builder", Modifier.FINAL).build();
//
//            MethodSpec helloMethod = MethodSpec.methodBuilder("hello")
//                    .addModifiers(Modifier.PUBLIC)
//                    .returns(void.class)
//                    .addParameter(String.class, "arg")
//                    .addStatement("$T.out.println(\"Hello, \" + arg + \"!\")", System.class)
//                    .build();
//
//            TypeSpec helloClass = TypeSpec.classBuilder("HelloMonadic")
//                    .addModifiers(Modifier.PUBLIC)
//                    .addMethod(helloMethod)
//                    .build();



            Monadic monadicAnnotation = e.getAnnotation(Monadic.class);

//            if(e.getKind() != ElementKind.INTERFACE) {
//                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Can only be applied to an interface.");
//                return true;
//            }

//            if (uniqueIdCheckList.contains(monadicAnnotation.as())) {
//                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "AutoImplement#as should be uniquely defined", e);
//            }

            TypeElement typeElement = (TypeElement) e;
//
//
//            TypeSpec modifiedClass = TypeSpec.classBuilder(monadicAnnotation.as())
//                    .addModifiers(Modifier.PUBLIC)
//                    .addOriginatingElement(e)
//                    .addMethod(helloMethod)
//                    .build();
//
            PackageElement packageElement = (PackageElement) typeElement.getEnclosingElement();
            String packageName = packageElement.getQualifiedName().toString();
//
//            JavaFile javaFile = JavaFile.builder(packageName, modifiedClass).build();
//
//            try {
//                javaFile.writeTo(processingEnv.getFiler());
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }

            StringBuilder builder = new StringBuilder()
                    .append("package " + packageName + ";\n")
                    .append("\n")
                    .append("import com.voodootech.annatolios.structures.MonadT;\n")
                    .append("import " + typeElement.getQualifiedName() + ";\n")
                    .append("\n")
                    .append("import java.util.function.Function;\n")
                    .append("\n")
                    .append("public class " + monadicAnnotation.as() + "<A> extends MonadT<A> implements " + typeElement.getSimpleName() + " {\n")
                    .append("\n")
                    .append("   private final A ref;\n")
                    .append("\n")
                    .append("   public " + monadicAnnotation.as() + "(final A ref) {\n")
                    .append("       this.ref = ref;\n")
                    .append("   }\n")
                    .append("\n")
                    .append("   @Override\n")
                    .append("   public A ref() {\n")
                    .append("       return this.ref;\n")
                    .append("   }\n")
                    .append("}\n");

            try {
                JavaFileObject javaFileObject = processingEnv.getFiler().createSourceFile(packageName + "." + monadicAnnotation.as());
                Writer writer = javaFileObject.openWriter();
                writer.write(builder.toString());
                writer.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }


//            StringBuilder builder = new StringBuilder()
//                    .append("package " + packageName + ";\n\n")
//                    .append("class HelloMonadic {\n\n")
//                    .append("   public static String hello() {\n")
//                    .append("       return \"Hello\";\n")
//                    .append("   }\n")
//                    .append("}\n");
//
//            try {
//                JavaFileObject javaFileObject = processingEnv.getFiler().createSourceFile(packageName + ".HelloMonadic");
//                Writer writer = javaFileObject.openWriter();
//                writer.write(builder.toString());
//                writer.close();
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
        }

        return true;
    }

    private String getPackageName(Element element) {
        List<PackageElement> packageElements =
                ElementFilter.packagesIn(Arrays.asList(element.getEnclosingElement()));

        Optional<PackageElement> packageElement = packageElements.stream().findAny();
        return packageElement.isPresent() ?
                packageElement.get().getQualifiedName().toString() : null;

    }
}
