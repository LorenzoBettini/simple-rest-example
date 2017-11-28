package com.examples.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Domain model object.
 * 
 * JAX-RS supports an automatic mapping from JAXB annotated class to XML and JSON,
 * but you need setter methods and a no-arg constructor.
 */
@XmlRootElement
public class Employee {
	private String employeeId;
	private String name;
	private int salary;

	public Employee() {

	}

	public Employee(String employeeId, String name, int salary) {
		super();
		this.employeeId = employeeId;
		this.name = name;
		this.salary = salary;
	}

	/**
	 * The corresponding XML element will be "id", not "employeeId"
	 * 
	 * @return
	 */
	@XmlElement(name = "id")
	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSalary() {
		return salary;
	}

	public void setSalary(int salary) {
		this.salary = salary;
	}

	@Override
	public String toString() {
		return "Employee [employeeId=" + employeeId +
				", name=" + name +
				", salary=" + salary + "]";
	}

}
