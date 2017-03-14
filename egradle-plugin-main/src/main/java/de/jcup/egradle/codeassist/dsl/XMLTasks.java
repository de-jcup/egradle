package de.jcup.egradle.codeassist.dsl;

import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "tasks")
public class XMLTasks{
	
	@XmlElement(name = "task", type=XMLTask.class)
	private Set<Task> tasks = new TreeSet<>();

	public Set<Task> getTasks() {
		return tasks;
	}
	
	@Override
	public String toString() {
		return "XMLTasks [ tasks=" + tasks + "]";
	}
	
}
