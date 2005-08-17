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
package org.seasar.maya.impl.util;

import org.seasar.maya.cycle.ServiceCycle;
import org.seasar.maya.cycle.script.CompiledScript;
import org.seasar.maya.cycle.script.ScriptCompiler;
import org.seasar.maya.engine.specification.NodeAttribute;
import org.seasar.maya.engine.specification.QName;
import org.seasar.maya.engine.specification.Specification;
import org.seasar.maya.engine.specification.SpecificationNode;
import org.seasar.maya.impl.CONST_IMPL;
import org.seasar.maya.provider.ServiceProvider;
import org.seasar.maya.provider.factory.ServiceProviderFactory;
import org.xml.sax.Locator;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ScriptUtil implements CONST_IMPL {

	private ScriptUtil() {
	}

    public static CompiledScript compile(String text, Class expectedType) {
        if(expectedType == null) {
        	throw new IllegalArgumentException();
        }
        if(StringUtil.hasValue(text)) {
            ServiceProvider provider = ServiceProviderFactory.getServiceProvider();
	        ScriptCompiler compiler = provider.getScriptCompiler();
            ServiceCycle cycle = provider.getServiceCycle();
            SpecificationNode node = cycle.getCurrentNode();
            Locator locator = node.getLocator();
            return compiler.compile(text, expectedType,
                    locator.getSystemId(), locator.getLineNumber());
        }
        return null;
    }
    
    public static Object execute(Object obj) {
        Object value = null;
        if (obj instanceof CompiledScript) {
            CompiledScript script = (CompiledScript)obj;
            Specification specification = SpecificationUtil.findSpecification();
            Object model = SpecificationUtil.findSpecificationModel(specification);
            value = script.execute(model);
        } else {
            value = obj;
        }
        return value;
    }
    
    public static  void execEvent(Specification specification, QName eventName) {
        if(specification == null || eventName == null) {
            throw new IllegalArgumentException();
        }
        SpecificationNode maya = SpecificationUtil.getMayaNode(specification);
        if(maya != null) {
	        NodeAttribute attr = maya.getAttribute(eventName);
	        if(attr != null) {
	        	String text = attr.getValue();
	        	if(StringUtil.hasValue(text)) {
		            Object obj = ScriptUtil.compile(text, Void.class);
		            ScriptUtil.execute(obj);
	        	}
	        }
        }
    }

}