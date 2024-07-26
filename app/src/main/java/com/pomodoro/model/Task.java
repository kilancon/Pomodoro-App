package com.pomodoro.model;

public class Task {
    private int id;
    private String name;
    private Boolean state;
    private int count;
    private int est;
    private int userid;

    public Task() {
    }

    public Task(int id, String name, Boolean state, int count, int est) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.count = count;
        this.est = est;
        this.userid = 0; //local user
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public int getEst() {
        return est;
    }

    public void setEst(int est) {
        this.est = est;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", state=" + state +
                ", count=" + count +
                ", est=" + est +
                '}';
    }
}
