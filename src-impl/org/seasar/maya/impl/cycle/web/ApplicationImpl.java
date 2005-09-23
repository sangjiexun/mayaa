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

import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.ServletContext;

import org.seasar.maya.cycle.Application;
import org.seasar.maya.cycle.ServiceCycle;
import org.seasar.maya.cycle.script.ScriptEnvironment;
import org.seasar.maya.impl.cycle.AbstractWritableAttributeScope;
import org.seasar.maya.impl.cycle.script.ScriptUtil;
import org.seasar.maya.impl.provider.UnsupportedParameterException;
import org.seasar.maya.impl.util.StringUtil;
import org.seasar.maya.impl.util.collection.EnumerationIterator;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ApplicationImpl extends AbstractWritableAttributeScope 
        implements Application {

    private ServletContext _servletContext;

    protected void check() {
        if(_servletContext == null) {
            throw new IllegalStateException();
        }
    }
    
    // Application implements ----------------------------------------
    
    public String getMimeType(String fileName) {
        check();
        if(StringUtil.isEmpty(fileName)) {
            throw new IllegalArgumentException();
        }
        return _servletContext.getMimeType(fileName);
    }

    public String getRealPath(String contextRelatedPath) {
        check();
        if(StringUtil.isEmpty(contextRelatedPath)) {
            throw new IllegalArgumentException();
        }
        return _servletContext.getRealPath(contextRelatedPath);
    }
    
    // AttributeScope implements -------------------------------------

    public String getScopeName() {
        return ServiceCycle.SCOPE_APPLICATION;
    }
    
    public Iterator iterateAttributeNames() {
        check();
        return EnumerationIterator.getInstance(
                _servletContext.getAttributeNames());
    }

    public boolean hasAttribute(String name) {
        check();
        if(StringUtil.isEmpty(name)) {
            return false;
        }
        for(Enumeration e = _servletContext.getAttributeNames();
        		e.hasMoreElements(); ) {
        	if(e.nextElement().equals(name)) {
        		return true;
        	}
        }
        return false;
	}

	public Object getAttribute(String name) {
        check();
        if(StringUtil.isEmpty(name)) {
            return null;
        }
        ScriptEnvironment env = ScriptUtil.getScriptEnvironment();
        return env.convertFromScriptObject(
                _servletContext.getAttribute(name));
    }

    public void setAttribute(String name, Object attribute) {
        check();
        if(StringUtil.isEmpty(name)) {
            return;
        }
        _servletContext.setAttribute(name, attribute);
    }
    
    public void removeAttribute(String name) {
        check();
        if(StringUtil.isEmpty(name)) {
            return;
        }
        _servletContext.removeAttribute(name);
    }

    // Underlyable implemetns ----------------------------------------
    
    public void setUnderlyingObject(Object context) {
        if(context == null || context instanceof ServletContext == false) {
            throw new IllegalArgumentException();
        }
        _servletContext = (ServletContext)context;
    }
    
    public Object getUnderlyingObject() {
        check();
        return _servletContext;
    }
    
    // Parameterizable implements ------------------------------------
    
    public void setParameter(String name, String value) {
        throw new UnsupportedParameterException(getClass(), name);
    }
        
}
