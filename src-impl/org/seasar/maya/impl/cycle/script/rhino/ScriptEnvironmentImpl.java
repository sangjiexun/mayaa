/*
 * Copyright (c) 2004-2005 the Seasar Foundation and the Others.
 * 
 * Licensed under the Seasar Software License, v1.1 (aka "the License");
 * you may not use this file except in compliance with the License which 
 * accompanies this distribution, and is available at
 * 
 *     http://homepage3.nifty.com/seasar/SEASAR-LICENSE.TXT
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either 
 * express or implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */
package org.seasar.maya.impl.cycle.script.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaAdapter;
import org.mozilla.javascript.Scriptable;
import org.seasar.maya.cycle.AttributeScope;
import org.seasar.maya.cycle.ServiceCycle;
import org.seasar.maya.cycle.script.CompiledScript;
import org.seasar.maya.engine.Engine;
import org.seasar.maya.impl.cycle.script.AbstractScriptEnvironment;
import org.seasar.maya.impl.cycle.script.LiteralScript;
import org.seasar.maya.impl.cycle.script.ScriptBlock;
import org.seasar.maya.impl.provider.UnsupportedParameterException;
import org.seasar.maya.impl.util.CycleUtil;
import org.seasar.maya.impl.util.SpecificationUtil;
import org.seasar.maya.source.SourceDescriptor;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ScriptEnvironmentImpl extends AbstractScriptEnvironment {

    private static Scriptable _standardObjects;
    private static ThreadLocal _parent = new ThreadLocal();

    protected CompiledScript compile(
            ScriptBlock scriptBlock, String sourceName, int lineno) {
        if(scriptBlock == null) {
            throw new IllegalArgumentException();
        }
        String text = scriptBlock.getBlockString();
        if(scriptBlock.isLiteral()) {
            return new LiteralScript(text);
        }
        return new CompiledScriptImpl(text,
                scriptBlock.getBlockSign(), sourceName, lineno);
    }

    public CompiledScript compile(SourceDescriptor source, String encoding) {
        if(source == null) {
            throw new IllegalArgumentException();
        }
        return new CompiledScriptImpl(source, encoding);
    }
    
    protected Scriptable getStandardObjects() {
        if(_standardObjects == null) {
            Context cx = Context.enter();
            _standardObjects = cx.initStandardObjects(null, true);
            Context.exit();
        }
        return _standardObjects;
    }

    protected void setModelToPrototype(Object model, Scriptable scope) {
        if(scope == null) {
            throw new IllegalArgumentException();
        }
        if(model != null) {
            Context cx = Context.enter();
            cx.setWrapFactory(new WrapFactoryImpl());
            Scriptable prototype = cx.getWrapFactory().wrapAsJavaObject(
                    cx, getStandardObjects(), model, model.getClass());
            Context.exit();
            scope.setPrototype(prototype);
        }
    }
    
    public void initScope() {
        Scriptable parent = (Scriptable)_parent.get();
        if(parent == null) {
            Context cx = Context.enter();
            cx.setWrapFactory(new WrapFactoryImpl());
            ServiceCycle cycle = CycleUtil.getServiceCycle();
            parent = cx.getWrapFactory().wrapAsJavaObject(
                    cx, getStandardObjects(), cycle, ServiceCycle.class);
            Context.exit();
            _parent.set(parent);
        }
        PageAttributeScope scope = new PageAttributeScope();
        scope.setParentScope(parent);
        Engine engine = SpecificationUtil.getEngine();
        Object model = SpecificationUtil.findSpecificationModel(engine);
        setModelToPrototype(model, scope);
        ServiceCycle cycle = CycleUtil.getServiceCycle();
        cycle.setPageScope(scope);
    }

    public void startScope(Object model) {
        ServiceCycle cycle = CycleUtil.getServiceCycle();
        AttributeScope scope = cycle.getPageScope();
        if(scope instanceof PageAttributeScope) {
            PageAttributeScope pageScope = (PageAttributeScope)scope;
            PageAttributeScope newPageScope = new PageAttributeScope();
            newPageScope.setParentScope(pageScope);
            setModelToPrototype(model, newPageScope);
            cycle.setPageScope(newPageScope);
        } else {
            throw new IllegalStateException();
        }
    }

    public void endScope() {
        ServiceCycle cycle = CycleUtil.getServiceCycle();
        AttributeScope scope = cycle.getPageScope();
        if(scope instanceof PageAttributeScope) {
            PageAttributeScope pageScope = (PageAttributeScope)scope;
            Scriptable parent = pageScope.getParentScope();
            if(parent instanceof PageAttributeScope) {
                PageAttributeScope parentScope = (PageAttributeScope)parent;
                cycle.setPageScope(parentScope);
                return;
            }
        }
        throw new IllegalStateException();
    }
    
    public Object convertFromScriptObject(Object scriptObject) {
        return JavaAdapter.convertResult(scriptObject, Object.class);
    }

    public void setParameter(String name, String value) {
        throw new UnsupportedParameterException(name);
    }
    
}
