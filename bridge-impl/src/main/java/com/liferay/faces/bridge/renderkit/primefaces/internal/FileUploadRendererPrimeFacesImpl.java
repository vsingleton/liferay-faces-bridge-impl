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
package com.liferay.faces.bridge.renderkit.primefaces.internal;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.FacesEvent;
import javax.faces.render.Renderer;
import javax.faces.render.RendererWrapper;
import javax.portlet.PortletRequest;
import javax.portlet.faces.BridgeFactoryFinder;

import org.apache.commons.fileupload.FileItem;

import com.liferay.faces.bridge.component.primefaces.internal.PrimeFacesFileUpload;
import com.liferay.faces.bridge.context.map.internal.ContextMapFactory;
import com.liferay.faces.bridge.model.UploadedFile;
import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;


/**
 * This class is a runtime wrapper around the PrimeFaces FileUploadRenderer class that makes the p:fileUpload component
 * compatible with a portlet environment.
 *
 * @author  Neil Griffin
 */
public class FileUploadRendererPrimeFacesImpl extends RendererWrapper {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(FileUploadRendererPrimeFacesImpl.class);

	// Private Constants
	private static final String FQCN_DEFAULT_UPLOADED_FILE = "org.primefaces.model.DefaultUploadedFile";
	private static final String FQCN_FILE_UPLOAD_EVENT = "org.primefaces.event.FileUploadEvent";
	private static final String FQCN_UPLOADED_FILE = "org.primefaces.model.UploadedFile";

	// Private Data Members
	private Renderer wrappedRenderer;

	public FileUploadRendererPrimeFacesImpl(Renderer renderer) {
		this.wrappedRenderer = renderer;
	}

	/**
	 * This method overrides the {@link RendererWrapper#decode(FacesContext, UIComponent)} method so that it can avoid a
	 * Servlet-API dependency in the PrimeFaces FileUploadRenderer. Note that p:fileUpload will do an Ajax postback and
	 * invoke the JSF lifecycle for each individual file.
	 */
	@Override
	public void decode(FacesContext facesContext, UIComponent uiComponent) {

		try {
			String clientId = uiComponent.getClientId(facesContext);
			ExternalContext externalContext = facesContext.getExternalContext();
			Map<String, String> requestParameterMap = externalContext.getRequestParameterMap();
			String submittedValue = requestParameterMap.get(clientId);

			if (submittedValue != null) {

				// Get the UploadedFile from the request attribute map.
				ContextMapFactory contextMapFactory = (ContextMapFactory) BridgeFactoryFinder.getFactory(
						ContextMapFactory.class);
				PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
				Map<String, List<UploadedFile>> uploadedFileMap = contextMapFactory.getUploadedFileMap(portletRequest);

				List<UploadedFile> uploadedFiles = uploadedFileMap.get(clientId);

				if (uploadedFiles != null) {

					for (UploadedFile uploadedFile : uploadedFiles) {

						// Convert the UploadedFile to a Commons-FileUpload FileItem.
						FileItem fileItem = new PrimeFacesFileItem(clientId, uploadedFile);

						// Reflectively create an instance of the PrimeFaces DefaultUploadedFile class.
						Class<?> defaultUploadedFileClass = Class.forName(FQCN_DEFAULT_UPLOADED_FILE);
						Constructor<?> constructor = defaultUploadedFileClass.getDeclaredConstructor(FileItem.class);
						Object defaultUploadedFile = constructor.newInstance(fileItem);

						// If the PrimeFaces FileUpload component is in "simple" mode, then simply set the submitted
						// value of the component to the DefaultUploadedFile instance.
						PrimeFacesFileUpload primeFacesFileUpload = new PrimeFacesFileUpload((UIInput) uiComponent);

						if (primeFacesFileUpload.getMode().equals(PrimeFacesFileUpload.MODE_SIMPLE)) {
							logger.debug("Setting submittedValue=[{0}]", submittedValue);
							primeFacesFileUpload.setSubmittedValue(defaultUploadedFile);
						}

						// Otherwise,
						else {
							logger.debug("Queuing FileUploadEvent for submittedValue=[{0}]", submittedValue);

							// Reflectively create an instance of the PrimeFaces FileUploadEvent class.
							Class<?> uploadedFileClass = Class.forName(FQCN_UPLOADED_FILE);
							Class<?> fileUploadEventClass = Class.forName(FQCN_FILE_UPLOAD_EVENT);
							constructor = fileUploadEventClass.getConstructor(UIComponent.class, uploadedFileClass);

							FacesEvent fileUploadEvent = (FacesEvent) constructor.newInstance(uiComponent,
									defaultUploadedFile);

							// Queue the event.
							primeFacesFileUpload.queueEvent(fileUploadEvent);
						}
					}
				}
			}
		}
		catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public Renderer getWrapped() {
		return wrappedRenderer;
	}
}
