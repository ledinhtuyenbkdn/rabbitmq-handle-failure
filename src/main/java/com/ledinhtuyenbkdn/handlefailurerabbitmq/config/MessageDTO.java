package com.ledinhtuyenbkdn.handlefailurerabbitmq.config;

import java.io.Serializable;

public class MessageDTO implements Serializable {

    private String name;

    private Integer age;

    public MessageDTO() {
    }

    public MessageDTO(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "MessageDTO{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
