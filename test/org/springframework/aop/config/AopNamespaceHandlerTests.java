/*
 * Copyright 2002-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.aop.config;

import junit.framework.TestCase;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.CountingBeforeAdvice;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.ITestBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author robh
 */
public class AopNamespaceHandlerTests extends TestCase {

	private ApplicationContext context;

	public void setUp() {
		this.context = new ClassPathXmlApplicationContext("org/springframework/aop/config/aopNamespaceHandlerTests.xml");
	}

	public void testIsProxy() throws Exception {
		ITestBean bean = getTestBean();

		assertTrue("Bean is not a proxy", AopUtils.isAopProxy(bean));

		// check the advice details
		Advised advised = (Advised) bean;
		Advisor[] advisors = advised.getAdvisors();

		assertEquals("Incorrect number of advisors applied.", 4, advisors.length);
	}

	public void testAdviceInvokedCorrectly() throws Exception {
		CountingBeforeAdvice getAgeCounter = (CountingBeforeAdvice) this.context.getBean("getAgeCounter");
		CountingBeforeAdvice getNameCounter = (CountingBeforeAdvice) this.context.getBean("getNameCounter");

		ITestBean bean = getTestBean();

		assertEquals("Incorrect initial getAge count", 0, getAgeCounter.getCalls("getAge"));
		assertEquals("Incorrect initial getName count", 0, getNameCounter.getCalls("getName"));

		bean.getAge();

		assertEquals("Incorrect getAge count on getAge counter", 1, getAgeCounter.getCalls("getAge"));
		assertEquals("Incorrect getAge count on getName counter", 0, getNameCounter.getCalls("getAge"));

		bean.getName();

		assertEquals("Incorrect getName count on getName counter", 1, getNameCounter.getCalls("getName"));
		assertEquals("Incorrect getName count on getAge counter", 0, getAgeCounter.getCalls("getName"));
	}

	private ITestBean getTestBean() {
		return (ITestBean) this.context.getBean("testBean");
	}

	public void testAspectApplied() throws Exception {
		ITestBean testBean = getTestBean();

		CountingAspectJAdvice advice = (CountingAspectJAdvice) this.context.getBean("countingAdvice");

		assertEquals("Incorrect before count", 0, advice.getBeforeCount());
		assertEquals("Incorrect after count", 0, advice.getAfterCount());

		testBean.setName("Sally");

		assertEquals("Incorrect before count", 1, advice.getBeforeCount());
		assertEquals("Incorrect after count", 1, advice.getAfterCount());

		testBean.getName();

		assertEquals("Incorrect before count", 1, advice.getBeforeCount());
		assertEquals("Incorrect after count", 1, advice.getAfterCount());
	}

}
