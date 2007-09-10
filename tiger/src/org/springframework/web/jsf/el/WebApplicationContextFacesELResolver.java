/*
 * Copyright 2002-2007 the original author or authors.
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

package org.springframework.web.jsf.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeansException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * Special JSF 1.2 <code>ELResolver</code> that exposes the Spring
 * <code>WebApplicationContext</code> instance under a variable named
 * "webApplicationContext".
 *
 * <p>In contrast to {@link DelegatingFacesELResolver}, this ELResolver variant
 * does <i>not</i> resolve JSF variable names as Spring bean names. It rather
 * exposes Spring's root WebApplicationContext <i>itself</i> under a special name,
 * and is able to resolve "webApplicationContext.mySpringManagedBusinessObject"
 * dereferences to Spring-defined beans in that application context.
 *
 * <p>Configure this resolver in your <code>faces-config.xml</code> file as follows:
 *
 * <pre>
 * &lt;application>
 *   ...
 *   &lt;el-resolver>org.springframework.web.jsf.el.WebApplicationContextFacesELResolver&lt;/el-resolver>
 * &lt;/application></pre>
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see DelegatingFacesELResolver
 * @see org.springframework.web.jsf.FacesContextUtils#getWebApplicationContext
 */
public class WebApplicationContextFacesELResolver extends ELResolver {

	/**
	 * Name of the exposed WebApplicationContext variable: "webApplicationContext".
	 */
	public static final String WEB_APPLICATION_CONTEXT_VARIABLE_NAME = "webApplicationContext";


	/** Logger available to subclasses */
	protected final Log logger = LogFactory.getLog(getClass());


	public Object getValue(ELContext elContext, Object base, Object property) throws ELException {
		if (base != null) {
			if (base instanceof WebApplicationContext) {
				WebApplicationContext wac = (WebApplicationContext) base;
				String beanName = property.toString();
				if (logger.isDebugEnabled()) {
					logger.debug("Attempting to resolve property '" + beanName + "' in root WebApplicationContext");
				}
				if (wac.containsBean(beanName)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Successfully resolved property '" + beanName + "' in root WebApplicationContext");
					}
					elContext.setPropertyResolved(true);
					try {
						return wac.getBean(beanName);
					}
					catch (BeansException ex) {
						throw new ELException(ex);
					}
				}
				else {
					// Mimic standard JSF/JSP behavior when base is a Map by returning null.
					return null;
				}
			}
		}
		else {
			if (WEB_APPLICATION_CONTEXT_VARIABLE_NAME.equals(property)) {
				elContext.setPropertyResolved(true);
				return getWebApplicationContext(elContext);
			}
		}

		return null;
	}

	public Class<?> getType(ELContext elContext, Object base, Object property) throws ELException {
		if (base != null) {
			if (base instanceof WebApplicationContext) {
				WebApplicationContext wac = (WebApplicationContext) base;
				String beanName = property.toString();
				if (logger.isDebugEnabled()) {
					logger.debug("Attempting to resolve property '" + beanName + "' in root WebApplicationContext");
				}
				if (wac.containsBean(beanName)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Successfully resolved property '" + beanName + "' in root WebApplicationContext");
					}
					elContext.setPropertyResolved(true);
					try {
						return wac.getType(beanName);
					}
					catch (BeansException ex) {
						throw new ELException(ex);
					}
				}
				else {
					// Mimic standard JSF/JSP behavior when base is a Map by returning null.
					return null;
				}
			}
		}
		else {
			if (WEB_APPLICATION_CONTEXT_VARIABLE_NAME.equals(property)) {
				elContext.setPropertyResolved(true);
				return WebApplicationContext.class;
			}
		}

		return null;
	}

	public void setValue(ELContext elContext, Object base, Object property, Object value) throws ELException {
	}

	public boolean isReadOnly(ELContext elContext, Object base, Object property) throws ELException {
		if (base instanceof WebApplicationContext) {
			elContext.setPropertyResolved(true);
			return false;
		}
		return false;
	}

	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elContext, Object base) {
		return null;
	}

	public Class<?> getCommonPropertyType(ELContext elContext, Object base) {
		return Object.class;
	}


	/**
	 * Retrieve the WebApplicationContext reference to expose.
	 * <p>The default implementation delegates to FacesContextUtils,
	 * returning <code>null</code> if no WebApplicationContext found.
	 * @param elContext the current JSF ELContext
	 * @return the Spring web application context
	 * @see org.springframework.web.jsf.FacesContextUtils#getWebApplicationContext
	 */
	protected WebApplicationContext getWebApplicationContext(ELContext elContext) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		return FacesContextUtils.getRequiredWebApplicationContext(facesContext);
	}

}
