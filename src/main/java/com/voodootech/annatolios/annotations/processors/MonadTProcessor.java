package com.voodootech.annatolios.annotations.processors;

import com.google.auto.service.AutoService;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.template.Substitution;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import java.util.HashSet;
import java.util.Set;

@AutoService(spoon.processing.Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("com.voodootech.annatolios.annotations.Monad")
public class MonadTProcessor extends AbstractProcessor<CtClass<?>> {


    @Override
    public boolean isToBeProcessed(CtClass<?> element) {
        return true;
    }

    @Override
    public void process(CtClass<?> element) {
        System.out.println("PROCESSING! " + element.getQualifiedName());
        // we declare a new snippet of code to be inserted.
//        CtCodeSnippetStatement snippet = getFactory().Core().createCodeSnippetStatement();

        // this snippet contains an if check.
//        final String value = String.format("if (%s == null) "
//                        + "throw new IllegalArgumentException(\"[Spoon inserted check] null passed as parameter\");",
//                element.getSimpleName());
//        snippet.setValue(value);

        HelloMethodTemplate helloMethodTemplate = new HelloMethodTemplate();

        CtCodeSnippetStatement statement = getFactory().Core().createCodeSnippetStatement();

        final String statementValue = String.format("System.out.println(\"Hello, world!\")");
        statement.setValue(statementValue);

        CtTypeReference<Void> returnType = getFactory().Core().createTypeReference();

        Set<ModifierKind> modifierKinds = new HashSet<ModifierKind>() {{ add(ModifierKind.PUBLIC); }};

        CtMethod<Void> newMethod = getFactory().Method().create(element, modifierKinds, returnType, "hello", null, null);


        newMethod.setBody(statement);

        Substitution.insertAll(element, helloMethodTemplate);

//        element.addMethod(newMethod);


        // we insert the snippet at the beginning of the method body.
//        if (element.getParent(CtExecutable.class).getBody() != null) {
//            element.getParent(CtExecutable.class).getBody().insertBegin(snippet);
//        }
    }
}
