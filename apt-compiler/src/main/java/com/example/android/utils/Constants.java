package com.example.android.utils;

/**
 * @author:無忌
 * @date:2020/8/7
 * @description:
 */
public class Constants {
    public static final String PROJECT = "AptDemo";
    public static final String PREFIX_OF_LOGGER = PROJECT + "::Compiler";
    public static final String PREFIX_OF_CLASS = "$$ViewBinding";

    public static final String PACKAGE_OF_GENERATE_FILE = "com.example.android.aptdemo";

    private static final String ANNOTATION_PACKAGE_NAME = "com.example.android.apt_annotation";
    public static final String ANNOTATION_BINDVIEWCOMPILER = ANNOTATION_PACKAGE_NAME + ".BindViewCompiler";
    public static final String ANNOTATION_CLICKVIEWCOMPILER = ANNOTATION_PACKAGE_NAME + ".ClickViewCompiler";
}
