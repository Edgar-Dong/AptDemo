package com.example.android.compiler;

import com.alibaba.fastjson.JSON;
import com.example.android.apt_annotation.BindViewCompiler;
import com.example.android.apt_annotation.ClickViewCompiler;
import com.example.android.model.ClassModel;
import com.example.android.utils.Constants;
import com.example.android.utils.Logger;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * @author:無忌
 * @date:2020/8/7
 * @description:
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({Constants.ANNOTATION_BINDVIEWCOMPILER, Constants.ANNOTATION_CLICKVIEWCOMPILER})
public class BindViewProcessor extends AbstractProcessor {
    Filer mFiler;
    Logger logger;

    private HashMap<String, ClassModel> classMap;

    @Override
    public Set<String> getSupportedOptions() {
        Set<String> options = super.getSupportedOptions();
        logger.info(String.format("getSupportedOptions, options:%s", JSON.toJSONString(options)));
        return options;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = super.getSupportedAnnotationTypes();
        logger.info(String.format("getSupportedAnnotationTypes, types:%s", JSON.toJSONString(types)));
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        SourceVersion sourceVersion = SourceVersion.latestSupported();
        logger.info(String.format("getSupportedSourceVersion, sourceVersion:%s", JSON.toJSONString(sourceVersion)));
        return sourceVersion;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        logger = new Logger(processingEnv.getMessager());
        mFiler = processingEnv.getFiler();

        classMap = new HashMap<>();

        Map<String, String> options = processingEnv.getOptions();
        SourceVersion sourceVersion = processingEnv.getSourceVersion();
        logger.info(String.format("init, options:%s, sourceVersion:%s", JSON.toJSONString(options), JSON.toJSONString(sourceVersion)));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        logger.info(">>>>>>>>>>process begin<<<<<<<<<<");

        classMap.clear();

        if (CollectionUtils.isNotEmpty(annotations)) {
            Set<? extends Element> bindViewCompilerElements = roundEnv.getElementsAnnotatedWith(BindViewCompiler.class);
            Set<? extends Element> clickViewCompilerElements = roundEnv.getElementsAnnotatedWith(ClickViewCompiler.class);
            parseBindViewCompiler(bindViewCompilerElements);
            parseClickViewCompiler(clickViewCompilerElements);
            classMap.forEach((clssName, classModel) -> {
                try {
                    generateJavaFile(classModel);
                } catch (IOException e) {
                    logger.error(e);
                }
            });
            return true;
        }
        return false;
    }

    private void generateJavaFile(ClassModel classModel) throws IOException {
        ClassName injectableInterface = ClassName.get("com.example.android.api.template", "Injectable");
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(classModel.getClassElement().getSimpleName() + Constants.PREFIX_OF_CLASS)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(injectableInterface);

        TypeName fieldTypeName = ClassName.get(classModel.getClassElement().asType());
        FieldSpec.Builder fieldBuilder = FieldSpec.builder(fieldTypeName, "target", Modifier.PRIVATE);

        ParameterSpec parameterSpec1 = ParameterSpec.builder(ClassName.get(classModel.getClassElement().asType()), "target", Modifier.FINAL).build();
        ParameterSpec parameterSpec2 = ParameterSpec.builder(ClassName.get("android.view", "View"), "view").build();
        MethodSpec.Builder methodBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(parameterSpec1)
                .addParameter(parameterSpec2)
                .addStatement("this.target = target");

        classModel.getVariableElements().forEach(variableElement -> {
            int resId = variableElement.getAnnotation(BindViewCompiler.class).value();
            methodBuilder.addStatement("target.$N = ($N)view.findViewById(" + resId + ")", variableElement.getSimpleName(), variableElement.asType().toString());
        });

        classModel.getExecutableElements().forEach(executableElement -> {
            int resId = executableElement.getAnnotation(ClickViewCompiler.class).value();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("view.findViewById(").append(resId).append(").")
                    .append("setOnClickListener(new View.OnClickListener() {\n")
                    .append("@Override\n")
                    .append("public void onClick(View v) {\n")
                    .append("target.").append(executableElement.getSimpleName()).append("();\n")
                    .append("}\n")
                    .append("})");
            methodBuilder.addStatement(stringBuilder.toString());
        });

        TypeSpec classSpec = classBuilder.addField(fieldBuilder.build())
                .addMethod(methodBuilder.build()).build();
        JavaFile javaFile = JavaFile.builder(Constants.PACKAGE_OF_GENERATE_FILE, classSpec).build();
        javaFile.writeTo(mFiler);
    }

    private void parseBindViewCompiler(Set<? extends Element> bindViewCompilerElements) {
        if (CollectionUtils.isNotEmpty(bindViewCompilerElements)) {
            logger.info("bindViewCompiler annotation size:" + bindViewCompilerElements.size());
            for (Element bindViewCompilerElement : bindViewCompilerElements) {
                ClassModel classModel = checkClassModel(bindViewCompilerElement);
                classModel.addVariableElement((VariableElement) bindViewCompilerElement);
            }
        }
    }

    private ClassModel checkClassModel(Element element) {
        TypeElement classElement = (TypeElement) element.getEnclosingElement();
        String qualifiedName = classElement.getQualifiedName().toString();
        ClassModel classModel = classMap.get(qualifiedName);
        if (classModel == null) {
            classModel = new ClassModel(classElement);
            classMap.put(qualifiedName, classModel);
        }
        return classModel;
    }

    private void parseClickViewCompiler(Set<? extends Element> clickViewCompilerElements) {
        if (CollectionUtils.isNotEmpty(clickViewCompilerElements)) {
            logger.info("clickViewCompiler annotation size:" + clickViewCompilerElements.size());
            clickViewCompilerElements.forEach(clickViewCompilerElement -> {
                ClassModel classModel = checkClassModel(clickViewCompilerElement);
                classModel.addExecutableElement((ExecutableElement) clickViewCompilerElement);
            });
        }
    }
}
