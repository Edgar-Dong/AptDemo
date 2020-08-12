package com.example.android.model;

import com.example.android.apt_annotation.BindViewCompiler;
import com.example.android.apt_annotation.ClickViewCompiler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashSet;

import javax.annotation.processing.Filer;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;

/**
 * @author:無忌
 * @date:2020/8/5
 * @description:
 */
public class ClassModel {
    /**
     * 成员变量
     */
    private HashSet<VariableElement> variableElements;
    /**
     * 类方法
     */
    private HashSet<ExecutableElement> executableElements;
    /**
     * 包
     */
    private PackageElement packageElement;
    /**
     * 类
     */
    private TypeElement classElement;

    public ClassModel(TypeElement classElement) {
        this.classElement = classElement;
        packageElement = (PackageElement) classElement.getEnclosingElement();
        variableElements = new HashSet<>();
        executableElements = new HashSet<>();
    }

    public HashSet<VariableElement> getVariableElements() {
        return variableElements;
    }

    public HashSet<ExecutableElement> getExecutableElements() {
        return executableElements;
    }

    public PackageElement getPackageElement() {
        return packageElement;
    }

    public TypeElement getClassElement() {
        return classElement;
    }

    public void addVariableElement(VariableElement variableElement) {
        variableElements.add(variableElement);
    }

    public void addExecutableElement(ExecutableElement executableElement) {
        executableElements.add(executableElement);
    }

    /**
     * 生成java文件
     *
     * @param filer
     */
    public void generateJavaFile(Filer filer) {
        try {
            JavaFileObject jfo = filer.createSourceFile(classElement.getQualifiedName() + "$$view_binding");
            BufferedWriter bw = new BufferedWriter(jfo.openWriter());
            bw.append("package ").append(packageElement.getQualifiedName()).append(";\n");
            bw.newLine();
            bw.append(getImportString());
            bw.newLine();
            bw.append("public class ").append(classElement.getSimpleName()).append("$$view_binding implements Injectable {\n");
            bw.newLine();
            bw.append(getFiledString());
            bw.newLine();
            bw.append(getConstructString());
            bw.newLine();
            bw.append("}");
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成import代码
     */
    private String getImportString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("import android.view.View;\n");
        stringBuilder.append("import com.example.android.annotation2.Injectable;\n");
        stringBuilder.append("import ").append(classElement.getQualifiedName()).append(";\n");
        HashSet<String> importStrs = new HashSet<>();
        for (VariableElement element : variableElements) {
            importStrs.add("import " + element.asType().toString() + ";\n");
        }
        for (String str : importStrs) {
            stringBuilder.append(str);
        }
        return stringBuilder.toString();
    }

    /**
     * 生成成员变量
     */
    private String getFiledString() {
        return "private " + classElement.getSimpleName().toString() + " target;\n";
    }

    /**
     * 生成构造函数
     */
    private String getConstructString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("public ").append(classElement.getSimpleName().toString()).append("$$view_binding")
                .append("(final ").append(classElement.getSimpleName()).append(" target, ").append("View view) {\n");
        stringBuilder.append("this.target = target;\n");
        for (VariableElement element : variableElements) {
            int resId = element.getAnnotation(BindViewCompiler.class).value();
            stringBuilder.append("target.").append(element.getSimpleName()).append(" = (").append(element.asType().toString())
                    .append(")view.findViewById(").append(resId).append(");\n");
        }

        for (ExecutableElement element : executableElements) {
            int resId = element.getAnnotation(ClickViewCompiler.class).value();
            stringBuilder.append("view.findViewById(").append(resId).append(").setOnClickListener(new View.OnClickListener() {\n")
                    .append("@Override\n").append("public void onClick(View v) {\n")
                    .append("target.").append(element.getSimpleName()).append("();\n")
                    .append("}\n});\n");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
