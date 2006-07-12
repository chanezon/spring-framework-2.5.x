/*
 * Copyright 2002-2006 the original author or authors.
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

package org.springframework.web.servlet.view.xslt;

import org.springframework.util.Assert;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;

/**
 * Contains common behaviour relating to {@link Transformer Transformers}.
 *
 * @author Rick Evans
 */
public abstract class TransformerUtils {

	/**
	 * The indent amount of characters if
	 * {@link #enableIndenting(javax.xml.transform.Transformer) indenting is enabled}.
	 * <p>Defaults to "2".
	 */
	public static final int DEFAULT_INDENT_AMOUNT = 2;


	/**
	 * Enables indenting for the supplied {@link Transformer}.
	 * <p>If the underlying XSLT engine is Xalan, then the special output
	 * key <code>indent-amount</code> will be also be set to a value
	 * of {@link #DEFAULT_INDENT_AMOUNT} characters.
	 * @param transformer the target transformer
	 * @throws IllegalArgumentException if the supplied {@link Transformer} is <code>null</code>
	 * @see {@link Transformer#setOutputProperty(String, String)}
	 * @see OutputKeys#INDENT
	 */
	public static void enableIndenting(Transformer transformer) {
		TransformerUtils.enableIndenting(transformer, DEFAULT_INDENT_AMOUNT);
	}


	/**
	 * Enables indenting for the supplied {@link Transformer}.
	 * <p>If the underlying XSLT engine is Xalan, then the special output
	 * key <code>indent-amount</code> will be also be set to a value
	 * of {@link #DEFAULT_INDENT_AMOUNT} characters.
	 * @param transformer  the target transformer
	 * @param indentAmount the size of the indent (2 characters, 3 characters, etc.)
	 * @throws IllegalArgumentException if the supplied {@link Transformer} is <code>null</code>
	 *                                  or if the supplied indent amount is less than zero (that is, negative)
	 * @see {@link Transformer#setOutputProperty(String, String)}
	 * @see OutputKeys#INDENT
	 */
	public static void enableIndenting(Transformer transformer, int indentAmount) {
		Assert.notNull(transformer);
		Assert.isTrue(indentAmount > -1, "The indent amount cannot be less than zero : got " + indentAmount);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		try {
			// Xalan-specific, but this is the most common XSLT engine in any case
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indentAmount));
		} catch (IllegalArgumentException ignored) {
		}
	}

	/**
	 * Disables indenting for the supplied {@link Transformer}.
	 * @param transformer the target transformer
	 * @throws IllegalArgumentException if the supplied {@link Transformer} is <code>null</code>
	 * @see OutputKeys#INDENT
	 */
	public static void disableIndenting(Transformer transformer) {
		Assert.notNull(transformer);
		transformer.setOutputProperty(OutputKeys.INDENT, "no");
	}

}
