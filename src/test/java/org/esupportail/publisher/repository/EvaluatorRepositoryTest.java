/**
 * Copyright (C) 2014 Esup Portail http://www.esup-portail.org
 * @Author (C) 2012 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.publisher.repository;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.esupportail.publisher.Application;
import org.esupportail.publisher.domain.enums.OperatorType;
import org.esupportail.publisher.domain.enums.StringEvaluationMode;
import org.esupportail.publisher.domain.evaluators.AbstractEvaluator;
import org.esupportail.publisher.domain.evaluators.OperatorEvaluator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author GIP RECIA - Julien Gribonvald 1 oct. 2014
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Rollback
@Transactional
@Slf4j
public class EvaluatorRepositoryTest {

	@Inject
	private EvaluatorRepository<AbstractEvaluator> repository;

	private OperatorEvaluator oe1;
	private OperatorEvaluator oe2;

	@Before
	public void setUp() throws Exception {
		log.info("starting up " + this.getClass().getName());
		oe1 = repository.saveAndFlush(ObjTest
				.newGlobalEvaluator(OperatorType.OR));

		oe2 = repository.saveAndFlush(ObjTest
				.newGlobalEvaluator(OperatorType.AND));

	}

	// @Test
	// public void testDTO() throws ObjectNotFoundException {
	// List<AbstractEvaluator> evalList = repository.findAll();
	// log.debug("models before : {}", evalList);
	// List<EvaluatorDTO> dtos = factoryDTO.asDTOList(evalList);
	// log.debug("To dto before : {}", dtos);
	// AbstractEvaluator m = oe1;
	// log.debug("model before all : {}", m);
	// EvaluatorDTO c = factoryDTO.from(oe1);
	// EvaluatorDTO c_old = factoryDTO.from(m);
	// ((OperatorEvaluatorDTO) c).setType(OperatorType.AND);
	// assertThat(c_old, not(samePropertyValuesAs(c)));
	// log.debug("DTO after modif : {}", c);
	// m = factoryDTO.from(c);
	// log.debug("model before saved modif : {}", m);
	// m = repository.save(m);
	// log.debug("model after saved modif : {}", m);
	// EvaluatorDTO c2 = factoryDTO.from(m);
	// log.debug("DTO after saved model modifs : {}", c2);
	// assertThat(c2, samePropertyValuesAs(c));
	// assertThat(c_old, not(samePropertyValuesAs(c2)));
	// }

	@Test
	public void testInsert() {
		OperatorEvaluator oe = ObjTest.newGlobalEvaluator(OperatorType.OR);
		log.info("Before insert : " + oe.toString());
		repository.saveAndFlush(oe);
		assertNotNull(oe.getId());
		log.info("After insert : " + oe.toString());
		Optional<AbstractEvaluator> optionalEvaluator = repository.findById(oe.getId());
		OperatorEvaluator oe2 = optionalEvaluator == null || !optionalEvaluator.isPresent()? null : (OperatorEvaluator) optionalEvaluator.get();
		log.info("After select : " + oe2.toString());
		assertNotNull(oe2);
		assertEquals(oe, oe2);

		OperatorEvaluator oe3 = ObjTest.newGlobalEvaluator(OperatorType.AND);
		log.info("Before insert : " + oe3.toString());
		repository.save(oe3);
		log.info("After insert : " + oe3.toString());
		assertNotNull(oe3.getId());
		assertTrue(repository.existsById(oe3.getId()));
		oe = (OperatorEvaluator) repository.getOne(oe3.getId());
		assertNotNull(oe);
		log.info("After select : " + oe.toString());
		assertTrue(repository.count() == 16);

		List<AbstractEvaluator> results = repository.findAll();
		assertThat(results.size(), is(16));
		assertThat(results, hasItem(oe));

		repository.deleteById(oe.getId());
		assertTrue(repository.findAll().size() == 12);
		assertFalse(repository.existsById(oe.getId()));

		Optional<AbstractEvaluator> optionalAe = repository.findById((long) 0);
		AbstractEvaluator ae = optionalAe.isPresent() ? optionalAe.get() : null;
		assertNull(ae);

	}

	/**
	 * Test method for
	 * {@link org.springframework.data.jpa.repository.JpaRepository#findAll()}.
	 */
	@Test
	public void testFindAll() {
		assertThat(repository.findAll().size(), is(8));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.jpa.repository.JpaRepository#save(java.lang.Iterable)}
	 * .
	 */
	@Test
	public void testSaveIterableOfS() {
		OperatorEvaluator e = ObjTest.newGlobalEvaluator(OperatorType.OR);
		OperatorEvaluator e2 = ObjTest.newGlobalEvaluator(OperatorType.AND);
		repository.saveAll(Arrays.asList(e, e2));
		assertThat(repository.findAll().size(), is(16));
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#exists(java.io.Serializable)}
	 * .
	 */
	@Test
	public void testExists() {
		assertTrue(repository.existsById(oe1.getId()));

	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#count()}.
	 */
	@Test
	public void testCount() {
		assertTrue(repository.count() == 8);
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#delete(java.lang.Object)}
	 * .
	 */
	@Test
	public void testDelete() {
		repository.deleteById(oe2.getId());
		assertTrue(repository.count() == 4);
	}

	/**
	 * Test method for
	 * {@link org.springframework.data.repository.CrudRepository#deleteAll()}.
	 */
	@Test
	public void testDeleteAll() {
		repository.deleteAll(Lists.newArrayList(oe1, oe2));
		// AbstractEvaluator ae1 = ObjTest.newPrefEvaluatorForAttr("portletCtx",
		// "test");
		AbstractEvaluator ae2 = ObjTest.newMVUEvaluatorForGroup("test1");
		AbstractEvaluator ae3 = ObjTest.newRoleEvaluatorForGroup("test1");
		AbstractEvaluator ae4 = ObjTest.newUserAttributeEvaluatorForAttr("uid",
				"F08001ut", StringEvaluationMode.EXISTS);
		// repository.save(Arrays.asList(ae1, ae2, ae3, ae4));
		repository.saveAll(Arrays.asList(ae2, ae3, ae4));
		repository.deleteAll();
		assertTrue(repository.count() == 0);
	}

}
