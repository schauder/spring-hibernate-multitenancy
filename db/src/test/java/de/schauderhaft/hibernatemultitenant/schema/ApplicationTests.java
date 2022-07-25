package de.schauderhaft.hibernatemultitenant.schema;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class})
class ApplicationTests {

	public static final String PIVOTAL = "PIVOTAL";
	public static final String VMWARE = "VMWARE";
	@Autowired
	Persons persons;

	@Autowired
	TransactionTemplate txTemplate;

	@Autowired
	TenantIdentifierResolver currentTenant;

	@Test
	void saveAndLoadPerson() {

		createPerson(PIVOTAL, "Adam");
		createPerson(VMWARE, "Eve");

		currentTenant.setCurrentTenant(VMWARE);
		assertThat(persons.findAll()).extracting(Person::getName).containsExactly("Eve");

		currentTenant.setCurrentTenant(PIVOTAL);
		assertThat(persons.findAll()).extracting(Person::getName).containsExactly("Adam");
	}

	private Person createPerson(String schema, String name) {

		currentTenant.setCurrentTenant(schema);

		Person person = txTemplate.execute(tx ->
				persons.save(Persons.named(name))
		);

		assertThat(person.getId()).isNotNull();

		return person;
	}
}
