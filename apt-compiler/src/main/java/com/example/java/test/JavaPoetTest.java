package com.example.java.test;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.lang.model.element.Modifier;

/**
 * @author:無忌
 * @date:2020/8/7
 * @description:
 */
public class JavaPoetTest {
    public static void main(String[] args) {
//        public class MainActivity extends Activity{
//
//            @Override
//            protected void onCreate(@Nullable Bundle savedInstanceState) {
//                super.onCreate(savedInstanceState);
//                setContentView(R.layout.activity_main);
//            }
//        }

        ClassName activity = ClassName.get("android.app","Activity");
        TypeSpec.Builder mainActivityBuilder = TypeSpec.classBuilder("MainActivity").addModifiers(Modifier.PUBLIC)
                .superclass(activity);

        ClassName override = ClassName.get("java.lang","Override");
        ClassName bundle = ClassName.get("android.os","Bundle");
        ClassName nullable = ClassName.get("android.support.annotation","Nullable");
        ParameterSpec saveInstanceState = ParameterSpec.builder(bundle,"savedInstance").addAnnotation(nullable).build();

        MethodSpec onCreate= MethodSpec.methodBuilder("onCreate")
                .addAnnotation(override)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(saveInstanceState)
                .addStatement("super.onCreate(savedInstance)")
                .addStatement("setContentView(R.layout.activity_main)")
                .build();

        TypeSpec mainActivity = mainActivityBuilder.addMethod(onCreate).build();

        JavaFile file = JavaFile.builder("com.example.java.test",mainActivity).build();
        System.out.println("============================");
        try {
            file.writeTo(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
