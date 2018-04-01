package com.example.utils;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

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
        String personJson = objectMapper.writeValueAsString(person);
        System.out.println(personJson);

        Person person1 = objectMapper.readValue(personJson, new TypeReference<Person>() {});
        System.out.println();

        String testJson = objectMapper.writeValueAsString(test);
        System.out.println(testJson);

        Test test1 = objectMapper.readValue(testJson, new TypeReference<Test>() {});
        System.out.println();
    }
}
