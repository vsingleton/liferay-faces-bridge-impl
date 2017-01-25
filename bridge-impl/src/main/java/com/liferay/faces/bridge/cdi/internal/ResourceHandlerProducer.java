package com.liferay.faces.bridge.cdi.internal;

import javax.faces.application.Application;
import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;
import javax.portlet.annotations.PortletRequestScoped;

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
public class ResourceHandlerProducer extends CdiProducer<ResourceHandler> {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = 1L;

    public ResourceHandlerProducer() {
        super.name("resource")
                .scope(PortletRequestScoped.class)
                .beanClassAndType(ResourceHandler.class)
                .create(e -> FacesContext.getCurrentInstance().getApplication().getResourceHandler());
    }

    @Override
    public boolean isAlternative() {
        StackTraceElement[] stes = new Throwable().getStackTrace();
        if (stes != null && stes.length > 1) {
            if (stes[1].getClassName().contains("BeanDisambiguation")) {
                if (stes[1].getMethodName().contains("load")) {
                    System.err.println("isAlternative() = true ... " + stes[1].getClassName() + "." + stes[1].getMethodName());
                    return true;
                }
            }
            System.err.println("isAlternative() = false ... " + stes[1].getClassName() + "." + stes[1].getMethodName());
        }
        return false;
    }

}
