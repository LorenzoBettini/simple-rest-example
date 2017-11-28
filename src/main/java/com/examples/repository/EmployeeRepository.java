package com.examples.repository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.examples.model.Employee;

/**
 * An example repository for employees.
 * 
 * In a real application this should be handled by a database.
 */
public class EmployeeRepository {

	public static final EmployeeRepository instance =
			new EmployeeRepository(new LinkedHashMap<>());

	private Map<String, Employee> employees;

	protected EmployeeRepository(Map<String, Employee> map) {
		this.employees = map;
		initialize();
	}

	public void initialize() {
		employees.clear();
		// initialize the "db" with some contents
		put(new Employee("ID1", "First Employee", 1000));
		put(new Employee("ID2", "Second Employee", 2000));
		put(new Employee("ID3", "Third Employee", 3000));
	}

	/**
	 * Assumes that {@link Employee#getEmployeeId()} does not return null.
	 * 
	 * @param employee
	 *            {@link Employee#getEmployeeId()} must not return null.
	 */
	private void put(Employee employee) {
		employees.put(employee.getEmployeeId(), employee);
	}

	public Collection<Employee> findAll() {
		return employees.values();
	}

	public Employee findOne(String id) {
		return employees.get(id);
	}

	/**
	 * If the passed employee has no id, then it is
	 * generated automatically.
	 * 
	 * @param employee
	 * @return the saved employee
	 */
	public Employee save(Employee employee) {
		if (employee.getEmployeeId() == null) {
			// dumb way of generating an automatic ID
			employee.setEmployeeId("ID" + (employees.size() + 1));
		}
		put(employee);
		return employee;
	}

}
