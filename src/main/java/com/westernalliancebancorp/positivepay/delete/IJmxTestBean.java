package com.westernalliancebancorp.positivepay.delete;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 3/5/14
 * Time: 9:36 AM
 */
public interface IJmxTestBean {
    public int getAge();

    public void setAge(int age);

    public void setName(String name);

    public String getName();

    public int add(int x, int y);

    public void dontExposeMe() ;
}
