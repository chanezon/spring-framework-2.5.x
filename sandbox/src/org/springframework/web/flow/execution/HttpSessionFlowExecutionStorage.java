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
package org.springframework.web.flow.execution;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.RandomGuid;
import org.springframework.web.flow.Event;
import org.springframework.web.flow.FlowExecution;
import org.springframework.web.flow.NoSuchFlowExecutionException;
import org.springframework.web.util.WebUtils;

/**
 * Flow execution storage implementation that stores the flow execution
 * in the HTTP session.
 * <p>
 * This storage strategy requires a <code>HttpServletRequestEvent</code>.
 * 
 * @author Erwin Vervaet
 */
public class HttpSessionFlowExecutionStorage implements FlowExecutionStorage {

	protected final Log logger = LogFactory.getLog(HttpSessionFlowExecutionStorage.class);
	
	private boolean createSession = true;

	/**
	 * Returns whether or not an HTTP session should be created if non
	 * exists. Defaults to true.
	 */
	public boolean isCreateSession() {
		return createSession;
	}
	
	/**
	 * Set whether or not an HTTP session should be created if non exists.
	 */
	public void setCreateSession(boolean createSession) {
		this.createSession = createSession;
	}

	public FlowExecution load(Event requestingEvent, String uniqueId)
			throws NoSuchFlowExecutionException, FlowExecutionStorageException {
		try {
			return (FlowExecution)WebUtils.getRequiredSessionAttribute(getHttpServletRequest(requestingEvent), uniqueId);
		}
		catch (IllegalStateException e) {
			throw new NoSuchFlowExecutionException(uniqueId, e);
		}
	}

	public String save(Event requestingEvent, String uniqueId, FlowExecution flowExecution)
			throws FlowExecutionStorageException {
		if (uniqueId == null) {
			uniqueId = generateUniqueId();
			if (logger.isDebugEnabled()) {
				logger.debug("Saving flow execution in HTTP session using unique id '" + uniqueId + "'");
			}
		}
		// always update session attribute, even if just overwriting
		// an existing one to make sure the servlet engine knows that this
		// attribute has changed!
		getHttpSession(requestingEvent).setAttribute(uniqueId, flowExecution);
		return uniqueId;
	}

	public void remove(Event requestingEvent, String uniqueId) throws FlowExecutionStorageException {
		if (logger.isDebugEnabled()) {
			logger.debug("Removing flow execution with unique id '" + uniqueId + "' from HTTP session");
		}
		getHttpSession(requestingEvent).removeAttribute(uniqueId);
	}
	
	/**
	 * Helper to generate a unique id for a flow execution in the storage
	 */
	protected String generateUniqueId() {
		return new RandomGuid().toString();
	}
	
	/**
	 * Make sure given event is a <code>HttpServletRequestEvent</code>.
	 */
	protected void assertHttpServletRequestEvent(Event event) {
		Assert.isInstanceOf(HttpServletRequestEvent.class, event,
				"'" + ClassUtils.getShortName(this.getClass()) + "' can only work with 'HttpServletRequestEvent'");
	}
	
	/**
	 * Helper to get the HTTP request from given event.
	 */
	protected HttpServletRequest getHttpServletRequest(Event event) {
		return ((HttpServletRequestEvent)event).getRequest();
	}
	
	/**
	 * Helper to get the HTTP session associated with the HTTP request
	 * embedded in given event.
	 */
	protected HttpSession getHttpSession(Event event) {
		return getHttpServletRequest(event).getSession(isCreateSession());
	}

}
