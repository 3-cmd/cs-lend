package com.cs;
//
//import org.junit.Test;
//import java.math.BigDecimal;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class MyTest {
//    @Test
//    public void test() {
//        List<Person> list = new ArrayList<>();
//        Person p1 = new Person("cs",new BigDecimal("2"));
//        Person p2 = new Person("cs",new BigDecimal("4"));
//        Person p3 = new Person("cs",new BigDecimal("23"));
//        Person p4 = new Person("cx",new BigDecimal("2"));
//        Person p5 = new Person("cx",new BigDecimal("23"));
//        Person p6 = new Person("cx",new BigDecimal("2"));
//        list.add(p1);
//        list.add(p2);
//        list.add(p3);
//        list.add(p4);
//        list.add(p5);
//        list.add(p6);
//        Map<String, BigDecimal> map = list.stream().collect(Collectors.toMap(Person::getName, Person::getSalary, BigDecimal::add));
//        List<Person> personList = map.entrySet().stream().map(item -> {
//            Person person = new Person();
//            person.setName(item.getKey());
//            person.setSalary(item.getValue());
//            return person;
//        }).collect(Collectors.toList());
//        System.out.println(personList);
//    }
//}
//class Person {
//    private String name;
//    private BigDecimal salary;
//
//    @Override
//    public String toString() {
//        return "Person{" +
//                "name='" + name + '\'' +
//                ", salary=" + salary +
//                '}';
//    }
//
//    public Person() {
//    }
//
//    public Person(String name, BigDecimal salary) {
//        this.name = name;
//        this.salary = salary;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public BigDecimal getSalary() {
//        return salary;
//    }
//
//    public void setSalary(BigDecimal salary) {
//        this.salary = salary;
//    }
//}