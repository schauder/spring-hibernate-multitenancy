package de.schauderhaft.hibernatemultitenant.partition;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestExecutionListeners(listeners = {DependencyInjectionTestExecutionListener.class})
class ApplicationTests {

	@Autowired
	Persons persons;

	@Autowired
	TransactionTemplate txTemplate;

	@Autowired
	TenantIdentifierResolver currentTenant;

	@Test
	void saveAndLoadPerson() {

		Person adam = createPerson("vmware", "Adam");
//		createPerson("pivotal", "Eve");
//		createPerson("pivotal", "Adam");

		assertThat(adam.getId()).isNotNull();

		Person reloaded = txTemplate.execute(
				tx -> persons.findById(adam.getId()).orElseThrow()
		);

		// makes sure we aren't fooled by JPAs cache
		assertThat(reloaded).isNotSameAs(adam);

		assertThat(adam.getTenant()).isEqualTo("vmware");

	}

	private Person createPerson(String schema, String name) {
		currentTenant.setCurrentTenant(schema);

		Person adam = txTemplate.execute(tx ->
				{
					Person person = Persons.named(name);
					return persons.save(person);
				}
		);
		return adam;
	}
}
