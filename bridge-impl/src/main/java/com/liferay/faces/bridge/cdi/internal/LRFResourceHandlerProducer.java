package com.liferay.faces.bridge.cdi.internal;

// import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptor;
import javax.annotation.Priority;

import javax.faces.application.Application;
import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;

import javax.portlet.annotations.PortletRequestScoped;

import com.sun.faces.cdi.CdiProducer;

/**
 * <p class="changed_added_2_3">
 *  The ResourceHandlerProducer is the CDI producer that allows you to inject the
 *  ResourceHandler and to do EL resolving of #{resource}
 * </p>
 *
 * @since 2.3
 * @see ResourceHandler
 * @see Application#getResourceHandler()
 */
@Alternative
@Priority(Interceptor.Priority.APPLICATION+10)
public class LRFResourceHandlerProducer extends CdiProducer<ResourceHandler> {

    static {
        // This doesn't throw an exception, just shows the stacktrace
        new Exception("My class was just loaded by some classloader").printStackTrace();
    }

    /**
     * Serialization version
     */
    private static final long serialVersionUID = 1L;

    public LRFResourceHandlerProducer() {

System.err.println("LRFResourceHandlerProducer(): Hello!");

        super.name("resource")
                .scope(PortletRequestScoped.class)
                .beanClassAndType(ResourceHandler.class)
                .create(e -> FacesContext.getCurrentInstance().getApplication().getResourceHandler());
    }

}