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
package org.seasar.maya.impl.builder.injection;

import org.seasar.maya.builder.injection.InjectionChain;
import org.seasar.maya.builder.injection.InjectionResolver;
import org.seasar.maya.builder.library.LibraryManager;
import org.seasar.maya.engine.specification.QName;
import org.seasar.maya.engine.specification.SpecificationNode;
import org.seasar.maya.impl.CONST_IMPL;
import org.seasar.maya.impl.provider.UnsupportedParameterException;
import org.seasar.maya.impl.util.SpecificationUtil;
import org.seasar.maya.impl.util.StringUtil;
import org.seasar.maya.provider.ServiceProvider;
import org.seasar.maya.provider.factory.ProviderFactory;

/**
 * �����������ʂ��R���|�[�l���g�y�[�W�̏ꍇ�ɁA�C���W�F�N�V�����̏C�����s�����]���o�B
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class ComponentPageInjectionResolver 
		implements InjectionResolver, CONST_IMPL {

    public SpecificationNode getNode(
            SpecificationNode original, InjectionChain chain) {
        if(original == null || chain == null) {
            throw new IllegalArgumentException();
        }
        SpecificationNode injected = chain.getNode(original);
        QName qName = injected.getQName();
        String uri = qName.getNamespaceURI();
        if(uri.startsWith("/")) {
	        ServiceProvider provider = ProviderFactory.getServiceProvider(); 
	        LibraryManager libraryManager = provider.getTemplateBuilder().getLibraryManager();
	        if(libraryManager.getProcessorDefinition(qName) == null) {
	            String name = qName.getLocalName();
	            String path = StringUtil.preparePath(uri) + StringUtil.preparePath(name);
                SpecificationNode node = SpecificationUtil.createInjectedNode(
                        QM_COMPONENT_PAGE, uri, injected);
                node.addAttribute(QM_PATH, path);
                node.addAttribute(QM_NAMESPACE_URI, uri);
                return node;
	        }
        }
        return injected;
    }

    public void setParameter(String name, String value) {
        throw new UnsupportedParameterException(name);
    }

}