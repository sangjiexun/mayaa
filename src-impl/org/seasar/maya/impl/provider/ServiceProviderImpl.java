/*
 * Copyright (c) 2004-2005 the Seasar Foundation and the Others.
 * 
 * Licensed under the Seasar Software License, v1.1 (aka "the License"); you may
 * not use this file except in compliance with the License which accompanies
 * this distribution, and is available at
 * 
 *     http://www.seasar.org/SEASAR-LICENSE.TXT
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.seasar.maya.impl.provider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.maya.builder.SpecificationBuilder;
import org.seasar.maya.builder.TemplateBuilder;
import org.seasar.maya.cycle.Application;
import org.seasar.maya.cycle.ServiceCycle;
import org.seasar.maya.cycle.script.ScriptEnvironment;
import org.seasar.maya.engine.Engine;
import org.seasar.maya.impl.CONST_IMPL;
import org.seasar.maya.impl.cycle.web.ApplicationImpl;
import org.seasar.maya.impl.cycle.web.ServiceCycleImpl;
import org.seasar.maya.impl.util.CycleUtil;
import org.seasar.maya.impl.util.ObjectUtil;
import org.seasar.maya.impl.util.StringUtil;
import org.seasar.maya.provider.ServiceProvider;
import org.seasar.maya.source.SourceDescriptor;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ServiceProviderImpl implements ServiceProvider, CONST_IMPL {
    
    private Object _context;
    private ApplicationImpl _application;
    private Engine _engine;
    private ScriptEnvironment _scriptEnvironment;
    private SpecificationBuilder _specificationBuilder;
    private TemplateBuilder _templateBuilder;
    private Class _pageSourceClass;
    private Map _pageParams;
	private ThreadLocal _currentServiceCycle = new ThreadLocal();
	
    public ServiceProviderImpl(Object context) {
        if(context == null) {
            throw new IllegalArgumentException();
        }
        _context = context;
    }
    
    protected ApplicationImpl getApplication0() {
        if(_application == null) {
            if(_context instanceof ServletContext == false) {
                throw new IllegalStateException();
            }
            ServletContext servletContext = (ServletContext)_context;
            _application = new ApplicationImpl(servletContext);
        }
        return _application;
    }

    public Application getApplication() {
        return getApplication0();
    }    
    
    public void setEngine(Engine engine) {
        if(engine == null) {
            throw new IllegalArgumentException();
        }
        _engine = engine;
    }
    
    public Engine getEngine() {
    	if(_engine == null) {
    	    throw new IllegalStateException();
    	}
        return _engine;
    }
    
    public void setScriptEnvironment(ScriptEnvironment scriptEnvironment) {
        if(scriptEnvironment == null) {
            throw new IllegalArgumentException();
        }
        _scriptEnvironment = scriptEnvironment;
    }
    
    public ScriptEnvironment getScriptEnvironment() {
        if(_scriptEnvironment == null) {
            throw new IllegalStateException();
        }
        return _scriptEnvironment;
    }
    
    public void setSpecificationBuilder(SpecificationBuilder specificationBuilder) {
        if(specificationBuilder == null) {
            throw new IllegalArgumentException();
        }
        _specificationBuilder = specificationBuilder;
    }
    
    public SpecificationBuilder getSpecificationBuilder() {
    	if(_specificationBuilder == null) {
            throw new IllegalStateException();
    	}
        return _specificationBuilder;
    }
    
    public void setTemplateBuilder(TemplateBuilder templateBuilder) {
        if(templateBuilder == null) {
            throw new IllegalArgumentException();
        }
        _templateBuilder = templateBuilder;
    }
    
    public TemplateBuilder getTemplateBuilder() {
    	if(_templateBuilder == null) {
    	    throw new IllegalStateException();
    	}
        return _templateBuilder;
    }
    
    public void setPageSourceClass(Class pageSourceClass) {
        if(pageSourceClass == null) {
            throw new IllegalArgumentException();
        }
        _pageSourceClass = pageSourceClass;
    }

    public void setPageSourceParameter(String name, String value) {
        if(StringUtil.isEmpty(name) || value == null) {
            throw new IllegalArgumentException();
        }
        if(_pageParams == null) {
            _pageParams = new HashMap();
        }
        _pageParams.put(name, value);
    }
    
    public SourceDescriptor getPageSourceDescriptor(String systemID) {
        if(StringUtil.isEmpty(systemID)) {
            throw new IllegalArgumentException();
        }
        if(_pageSourceClass == null) {
            throw new IllegalStateException();
        }
        SourceDescriptor source = 
            (SourceDescriptor)ObjectUtil.newInstance(_pageSourceClass);
        source.setSystemID(systemID);
        if(_pageParams != null) {
            for(Iterator it = _pageParams.keySet().iterator(); it.hasNext(); ) {
                String key = (String)it.next();
                String value = (String)_pageParams.get(key);
                source.setParameter(key, value);
            }
        }
        return source;
    }

	public void initialize(Object request, Object response) {
		if(request == null || response == null ||
                request instanceof HttpServletRequest == false ||
                response instanceof HttpServletResponse == false) {
			throw new IllegalArgumentException();
		}
		ServiceCycleImpl cycle = (ServiceCycleImpl)_currentServiceCycle.get();
    	if(cycle == null) {
    		cycle = new ServiceCycleImpl(getApplication0());
    		_currentServiceCycle.set(cycle);
    	}
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;
		cycle.inithialize(httpRequest, httpResponse);
	}

	public ServiceCycle getServiceCycle() {
		ServiceCycle cycle = (ServiceCycle)_currentServiceCycle.get();
		if(cycle == null) {
			throw new IllegalStateException();
		}
		return cycle;
    }
    
	public Object getModel(Object modelKey, String modelScope) {
        if(modelKey instanceof Class == false) {
            throw new IllegalArgumentException();
        }
        Class modelClass = (Class)modelKey;
        String modelName = modelClass.getName();
        Object model = CycleUtil.getAttribute(modelName, modelScope); 
        if(model == null) {
            model = ObjectUtil.newInstance(modelClass);
            CycleUtil.setAttribute(modelName, model, modelScope);
        }
        return model;
	}

}
