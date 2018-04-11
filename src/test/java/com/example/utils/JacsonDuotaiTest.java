package com.example.utils;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import lombok.Data;

import java.lang.annotation.Annotation;

/**
 * @author qianliao.zhuang
 * jacson多态支持
 */
public class JacsonDuotaiTest {

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="@class")
    @Data
    public static class Test {
        Person person;
    }

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    @Data
    public static class Person {
        Animal animal;
    }

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    @Data
    public static class Animal {
        String hi;
    }

    public static class Dog extends Animal {
        public String name;
    }

    public static class Cat extends Animal {
        public String hello;
    }

    public static void main(String[] args) throws Exception {
        Dog dog = new Dog();
        dog.name = "dog";

        Person person = new Person();
        person.animal = dog;

        Test test = new Test();
        test.person = person;

        ObjectMapper objectMapper = new ObjectMapper();
/*        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.registerSubtypes(Animal.class);
        objectMapper.registerSubtypes(Person.class);
        objectMapper.registerSubtypes(new NamedType(Dog.class,"dog"));
        objectMapper.registerSubtypes(new NamedType(Cat.class,"cat"));*/

        String testJson = objectMapper.writeValueAsString(test);
        System.out.println("test: " + testJson);

        Test test1 = objectMapper.readValue(testJson, new TypeReference<Test>() {});
        System.out.println();

        ObjectMapper objectMapper1 = new ObjectMapper().setAnnotationIntrospector(new DisablingJsonTypeInfoIntrospector());
        String hideJsonTypeInfo = objectMapper1.writeValueAsString(test);
        System.out.println("hideJsonTypeInfo: " + hideJsonTypeInfo);
    }

    public static class DisablingJsonTypeInfoIntrospector extends JacksonAnnotationIntrospector {
        /**
         * 网上说这个方法可以隐藏注解，但是貌似行不通
         * 可能是可以隐藏自定义注解，且自定义注解上有 @JacksonAnnotationsInside 标识
         * @param ann
         * @return
         */
        @Override
        public boolean isAnnotationBundle(Annotation ann) {
            if (ann.annotationType().equals(JsonTypeInfo.class)) {
                return false;
            } else {
                return super.isAnnotationBundle(ann);
            }
        }

        /**
         * 隐藏 JsonTypeInfo 注解
         * @param annotated
         * @param annoClass
         * @param <A>
         * @return
         */
        @Override
        protected <A extends Annotation> A _findAnnotation(Annotated annotated, Class<A> annoClass) {
            if (annoClass.equals(JsonTypeInfo.class)) {
                return null;
            }
            return super._findAnnotation(annotated, annoClass);
        }
    }
}
