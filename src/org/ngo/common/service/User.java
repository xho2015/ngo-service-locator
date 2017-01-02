package org.ngo.common.service;

public class User {
	private long Id;
	private String name;
	private int age;
	public User(long i, String string, int j) {
		this.Id = i; this.name = string; this.age = j;
	}
	public long getId() {
		return Id;
	}
	public void setId(long id) {
		Id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	
	/*public String toString()
	{
		return String.format("User={Id=%d,name=%s,age=%d}", Id,name,age);
	}*/
}
