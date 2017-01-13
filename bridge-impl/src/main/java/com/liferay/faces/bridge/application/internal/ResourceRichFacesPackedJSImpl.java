/**
 * Copyright (c) 2000-2017 Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liferay.faces.bridge.application.internal;

import javax.faces.application.Resource;

import com.liferay.faces.util.application.FilteredResourceBase;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * @author  Kyle Stiemann
 */
public class ResourceRichFacesPackedJSImpl extends FilteredResourceBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ResourceRichFacesPackedJSImpl.class);

	// Private Members
	private Resource wrappedResource;

	public ResourceRichFacesPackedJSImpl(Resource wrappedResource) {

		// Since we cannot extend two classes, we wrap the default ResourceRichFacesImpl to ensure that all RichFaces
		// resource implementations include the base functionality.
		this.wrappedResource = new ResourceRichFacesImpl(wrappedResource);
	}

	@Override
	public Resource getWrapped() {
		return wrappedResource;
	}

	@Override
	protected String filter(String javaScriptText) {

		// Replace the URL used by rich:fileUpload for forum submission.
		// http://issues.liferay.com/browse/FACES-1234
		// https://issues.jboss.org/browse/RF-12273
		String token = "this.form.attr(\"action\", originalAction + delimiter + UID + \"=\" + this.loadableItem.uid);";
		int pos = javaScriptText.indexOf(token);

		if (pos > 0) {
			logger.debug("Found first token in packed.js");

			StringBuilder buf = new StringBuilder();
			buf.append(javaScriptText.substring(0, pos));
			buf.append(
				"this.form.attr(\"action\", this.form.children(\"input[name='javax.faces.encodedURL']\").val() + delimiter + UID + \"=\" + this.loadableItem.uid);");
			buf.append(javaScriptText.substring(pos + token.length() + 1));
			javaScriptText = buf.toString();
		}

		// Fix JavaScript error "TypeError: jQuery.atmosphere is undefined" by inserting checks for undefined variable.
		// http://issues.liferay.com/browse/FACES-1532
		token = "if (jQuery.atmosphere.requests.length > 0) {";
		pos = javaScriptText.indexOf(token);

		if (pos > 0) {
			logger.debug("Found second token in packed.js");

			StringBuilder buf = new StringBuilder();
			buf.append(javaScriptText.substring(0, pos));
			buf.append("if (!jQuery.atmosphere) { return; }; ");
			buf.append(javaScriptText.substring(pos));
			javaScriptText = buf.toString();
		}

		// jQuery.atmosphere.unsubscribe();
		token = "jQuery.atmosphere.unsubscribe();";
		pos = javaScriptText.indexOf(token);

		if (pos > 0) {
			logger.debug("Found third token in packed.js");

			StringBuilder buf = new StringBuilder();
			buf.append(javaScriptText.substring(0, pos));
			buf.append("if (!jQuery.atmosphere) { return; }; ");
			buf.append(javaScriptText.substring(pos));
			javaScriptText = buf.toString();
		}

		token = "$.atmosphere.unsubscribe();";
		pos = javaScriptText.indexOf(token);

		if (pos > 0) {
			logger.debug("Found fourth token in packed.js");

			StringBuilder buf = new StringBuilder();
			buf.append(javaScriptText.substring(0, pos));
			buf.append("if (!$.atmosphere) { return; }; ");
			buf.append(javaScriptText.substring(pos));
			javaScriptText = buf.toString();
		}

		return javaScriptText;
	}
}
