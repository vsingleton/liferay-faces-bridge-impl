package com.liferay.faces.demos;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import javax.portlet.Portlet;
import javax.portlet.faces.GenericFacesPortlet;

@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=jsf-applicant-portlet",
		"javax.portlet.init-param.view-template=",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class JSFPortletDS extends GenericFacesPortlet {

	@Activate
	public void activate() {
		System.err.println("!@#$ activated " + this);
	}
}

