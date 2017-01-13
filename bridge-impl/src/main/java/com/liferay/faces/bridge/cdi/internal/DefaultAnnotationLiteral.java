package com.liferay.faces.bridge.cdi.internal;

import javax.enterprise.inject.Default;
import javax.enterprise.util.AnnotationLiteral;

/**
 * An annotation literal for @Default.
 *
 * @since 2.3
 */
@SuppressWarnings("all")
class DefaultAnnotationLiteral extends AnnotationLiteral<Default> implements Default {
    private static final long serialVersionUID = 1L;
}

