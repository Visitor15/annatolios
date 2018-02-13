package com.voodootech.annatolios.annotations.processors;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.template.Local;
import spoon.template.Template;

public class HelloMethodTemplate  implements Template {

    @Local
    public HelloMethodTemplate() { }

    @Override
    public CtElement apply(CtType targetType) {
        return null;
    }

    public void hello() {
        System.out.println("Hello, world!");
    }
}
