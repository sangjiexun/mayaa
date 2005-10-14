/*
 * Copyright 2004-2005 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.maya.impl.builder.library;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.maya.builder.library.LibraryDefinition;
import org.seasar.maya.builder.library.ProcessorDefinition;
import org.seasar.maya.builder.library.converter.PropertyConverter;
import org.seasar.maya.engine.processor.ProcessorProperty;
import org.seasar.maya.engine.specification.NodeAttribute;
import org.seasar.maya.engine.specification.QName;
import org.seasar.maya.engine.specification.SpecificationNode;
import org.seasar.maya.impl.util.StringUtil;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class TLDPropertyDefinition extends PropertyDefinitionImpl {

    private static final Log LOG =
        LogFactory.getLog(TLDPropertyDefinition.class);

    protected PropertyConverter getConverterForProcessorProperty() {
        LibraryDefinition library = getPropertySet().getLibraryDefinition();
        PropertyConverter converter = 
            library.getPropertyConverter(ProcessorProperty.class);
        if(converter == null) {
            throw new IllegalStateException();
        }
        return converter;
    }
    
    public Object createProcessorProperty(
            ProcessorDefinition processorDef, SpecificationNode injected) {
    	if(injected == null) {
    		throw new IllegalArgumentException();
    	}
        Class propertyClass = getPropertyClass(processorDef);
        if(propertyClass == null) {
            // real property not found on the tag.
            String processorName = processorDef.getName();
            if(LOG.isWarnEnabled()) {
                String msg = StringUtil.getMessage(TLDPropertyDefinition.class, 
                        0, new String[] { processorName, getName() });
                LOG.warn(msg);
            }
            return null;
        }
        QName qName = getQName(injected);
        NodeAttribute attribute = injected.getAttribute(qName);
        if(attribute != null) {
            String value = attribute.getValue();
            PropertyConverter converter = getConverterForProcessorProperty(); 
            return converter.convert(attribute, value, propertyClass);
        } else if(isRequired()) {
            String processorName = processorDef.getName();
            throw new NoRequiredPropertyException(processorName, qName);
        }
        return null;
    }
    
}