package de.schauderhaft.hibernatemultitenantpartition;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class HibernatemultitenantpartitionApplicationTests {

	@Autowired
	Persons persons;

	@Autowired
	TransactionTemplate txTemplate;

	@Test
	void saveAndLoadPerson() {

		Person adam = txTemplate.execute(tx ->
				{
					Person person = Persons.named("Adam");
					return persons.save(person);
				}
		);

		assertThat(adam.getId()).isNotNull();

		Person reloaded = txTemplate.execute(
				tx -> persons.findById(adam.getId()).orElseThrow()
		);

		// makes sure we aren't fooled by JPAs cache
		assertThat(reloaded).isNotSameAs(adam);

	}

}
