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
package org.seasar.maya.impl.engine;

import org.seasar.maya.engine.Engine;
import org.seasar.maya.engine.Page;
import org.seasar.maya.engine.Template;
import org.seasar.maya.engine.specification.Specification;
import org.seasar.maya.engine.specification.SpecificationNode;
import org.seasar.maya.impl.engine.specification.SpecificationUtil;
import org.seasar.maya.impl.util.ObjectUtil;
import org.seasar.maya.provider.ServiceProvider;
import org.seasar.maya.provider.factory.ProviderFactory;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class EngineUtil {

    private EngineUtil() {
    }

    public static Engine getEngine() {
        ServiceProvider provider = ProviderFactory.getServiceProvider();
        return provider.getEngine();
    }

    public static String getEngineSetting(String name, String defaultValue) {
        Engine engine = getEngine();
        String value = engine.getParameter(name);
        if(value != null) {
            return value;
        }
        return defaultValue;
    }

    public static boolean getEngineSettingBoolean(String name, boolean defaultValue) {
        Engine engine = getEngine();
        String value = engine.getParameter(name);
        return ObjectUtil.booleanValue(value, defaultValue);
    }

    public static Template getTemplate() {
        Specification spec = SpecificationUtil.findSpecification();
        if(spec instanceof Page) {
            SpecificationNode parent = spec.getParentNode();
            if(parent != null) {
                spec = SpecificationUtil.findSpecification();
            } else {
                return null;
            }
        }
        if(spec instanceof Template) {
            return (Template)spec;
        }
        throw new IllegalStateException();
    }

    
    
}