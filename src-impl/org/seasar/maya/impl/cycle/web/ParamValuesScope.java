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
package org.seasar.maya.impl.cycle.web;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.seasar.maya.impl.cycle.AbstractReadOnlyAttributeScope;
import org.seasar.maya.impl.util.collection.EnumerationIterator;

/**
 * @author Masataka Kurihara (Gluegent, Inc)
 */
public class ParamValuesScope 
        extends AbstractReadOnlyAttributeScope {

	private HttpServletRequest _request;
	
	public ParamValuesScope(HttpServletRequest request) {
		if(request == null) {
			throw new IllegalArgumentException();
		}
		_request = request;
	}

	public String getScopeName() {
		return "paramValues";
	}

    public Iterator iterateAttributeNames() {
        return EnumerationIterator.getInstance(_request.getParameterNames());
    }

    public boolean hasAttribute(String name) {
        for(Iterator it = iterateAttributeNames(); it.hasNext(); ) {
            String paramName = (String)it.next();
            if(paramName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public Object getAttribute(String name) {
        if(hasAttribute(name)) {
            return _request.getParameterValues(name);
        }
        return null;
    }

}
