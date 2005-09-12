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
package org.seasar.maya.impl.builder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.seasar.maya.builder.TemplateBuilder;
import org.seasar.maya.builder.injection.InjectionChain;
import org.seasar.maya.builder.injection.InjectionResolver;
import org.seasar.maya.builder.library.LibraryManager;
import org.seasar.maya.builder.library.ProcessorDefinition;
import org.seasar.maya.cycle.ServiceCycle;
import org.seasar.maya.cycle.script.CompiledScript;
import org.seasar.maya.engine.Template;
import org.seasar.maya.engine.processor.TemplateProcessor;
import org.seasar.maya.engine.specification.NodeNamespace;
import org.seasar.maya.engine.specification.QName;
import org.seasar.maya.engine.specification.Specification;
import org.seasar.maya.engine.specification.SpecificationNode;
import org.seasar.maya.impl.CONST_IMPL;
import org.seasar.maya.impl.builder.injection.DefaultInjectionChain;
import org.seasar.maya.impl.builder.parser.AdditionalHandler;
import org.seasar.maya.impl.builder.parser.TemplateParser;
import org.seasar.maya.impl.builder.parser.TemplateScanner;
import org.seasar.maya.impl.cycle.AbstractServiceCycle;
import org.seasar.maya.impl.engine.processor.AttributeProcessor;
import org.seasar.maya.impl.engine.processor.CharactersProcessor;
import org.seasar.maya.impl.engine.processor.DoBodyProcessor;
import org.seasar.maya.impl.engine.processor.ElementProcessor;
import org.seasar.maya.impl.engine.specification.SpecificationNodeImpl;
import org.seasar.maya.impl.util.StringUtil;
import org.seasar.maya.provider.ServiceProvider;
import org.seasar.maya.provider.factory.ProviderFactory;
import org.seasar.maya.source.SourceDescriptor;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class TemplateBuilderImpl extends SpecificationBuilderImpl
		implements TemplateBuilder, CONST_IMPL {

	private static final long serialVersionUID = 4578697145887676787L;

    private List _resolvers = new ArrayList();
    
    public void addInjectionResolver(InjectionResolver resolver) {
        if(resolver == null) {
            throw new IllegalArgumentException();
        }
        synchronized (_resolvers) {
            _resolvers.add(resolver);
        }
    }
    
    protected XMLReader createXMLReader() {
        return new TemplateParser(new TemplateScanner());
    }

    protected String getPublicID() {
        return URI_MAYA + "/template";
    }
    
    protected void setContentHander(
            XMLReader xmlReader, ContentHandler handler) {
        super.setContentHander(xmlReader, handler);
        if(handler instanceof AdditionalHandler) {
            try {
                xmlReader.setProperty(
                        AdditionalHandler.ADDITIONAL_HANDLER, handler);
            } catch(SAXException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void build(Specification specification) {
        if((specification instanceof Template) == false) {
            throw new IllegalArgumentException();
        }
        SourceDescriptor source = specification.getSource();
        if(source.exists()) {
	        super.build(specification);
	        try {
	            doInjection((Template)specification);
	        } catch(Throwable t) {
	            specification.kill();
				if(t instanceof RuntimeException) {
				    throw (RuntimeException)t;
				}
				throw new RuntimeException(t);
	        }
        }
    }

    protected void saveToCycle(SpecificationNode originalNode,
            SpecificationNode injectedNode) {
        ServiceCycle cycle = AbstractServiceCycle.getServiceCycle();
        cycle.setOriginalNode(originalNode);
        cycle.setInjectedNode(injectedNode);
    }
    
    protected TemplateProcessor findConnectPoint(
            TemplateProcessor processor) {
        if(processor instanceof ElementProcessor &&
                ((ElementProcessor)processor).isDuplicated()) {
            // "processor"'s m:rendered is true, ripping duplicated element.
            return findConnectPoint(processor.getChildProcessor(0));
        }
        for(int i = 0; i < processor.getChildProcessorSize(); i++) {
            TemplateProcessor child = processor.getChildProcessor(i);
            if(child instanceof CharactersProcessor) {
                CharactersProcessor charsProc = (CharactersProcessor)child;
                CompiledScript script = charsProc.getText().getValue(); 
                if(script.isLiteral()) {
                    String value = (String)script.execute();
                    if(StringUtil.hasValue(value.trim())) {
                        // "processor" has child which is not empty.
                        return null;
                    }
                } else {
                    // "processor" has child which is scriptlet.
                    return null;
                }
            } else if(child instanceof AttributeProcessor == false) {
                // "processor" has child which is implicit m:characters or
                // nested child node, but is NOT m:attribute
                return null;
            }
        }
        return processor;
    }
    
    protected TemplateProcessor createProcessor(
            SpecificationNode original, SpecificationNode injected) {
        QName name = injected.getQName();
        ServiceProvider provider = ProviderFactory.getServiceProvider(); 
        LibraryManager libraryManager = provider.getLibraryManager();
        ProcessorDefinition def = libraryManager.getProcessorDefinition(name);
        if(def != null) {
            TemplateProcessor proc = def.createTemplateProcessor(injected);
            proc.setOriginalNode(original);
            proc.setInjectedNode(injected);
            return proc;
        }
        return null;
    }
    
    protected TemplateProcessor resolveInjectedNode(Template template, 
            Stack stack, SpecificationNode original, SpecificationNode injected) {
        if(injected == null) {
            throw new IllegalArgumentException();
        }
        saveToCycle(original, injected);
        TemplateProcessor processor = createProcessor(original, injected);
        if(processor == null) {
            NodeNamespace ns = original.getNamespace("", true);
            if(ns == null) {
                throw new IllegalStateException();
            }
            String defaultURI = ns.getNamespaceURI();
            if(defaultURI.equals(injected.getQName().getNamespaceURI())) {
                InjectionChain chain = DefaultInjectionChain.getInstance(); 
                SpecificationNode retry = chain.getNode(injected);
                processor = createProcessor(original, retry);
            }
            if(processor == null) {
                throw new ProcessorNotInjectedException();
            }
        }
        TemplateProcessor parent = (TemplateProcessor)stack.peek();
        parent.addChildProcessor(processor);
        Iterator it = injected.iterateChildNode();
        if(it.hasNext() == false) {
            return processor;
        }
        // "injected" node has children, nested node definition on .maya
        stack.push(processor);
        TemplateProcessor connectionPoint = null;
        while(it.hasNext()) {
            SpecificationNode childNode = (SpecificationNode)it.next();
            saveToCycle(original, childNode);
            TemplateProcessor childProcessor = resolveInjectedNode(
                    template, stack, original, childNode);
            if(childProcessor instanceof DoBodyProcessor) {
                if(connectionPoint != null) {
                    throw new TooManyDoBodyException();
                }
                connectionPoint = childProcessor;
            }
        }
        stack.pop();
        saveToCycle(original, injected);
        if(connectionPoint != null) {
            return connectionPoint;
        }
        return findConnectPoint(processor);
    }
    
    protected SpecificationNode resolveOriginalNode(
            SpecificationNode original, InjectionChain chain) {
        if(original == null || chain == null) {
            throw new IllegalArgumentException();
        }
        if(_resolvers.size() > 0) {
            InjectionChainImpl first = new InjectionChainImpl(chain);
            return first.getNode(original);
        }
        return chain.getNode(original);
    }
    
    protected void walkParsedTree(
            Template template, Stack stack, SpecificationNode original) {
        if(original == null) {
            throw new IllegalArgumentException();
        }
        Iterator it;
        it = original.iterateChildNode();
        while(it.hasNext()) {
            SpecificationNode child = (SpecificationNode)it.next();
            saveToCycle(child, child);
            if(QM_MAYA.equals(child.getQName())) {
                continue;
            }
            InjectionChain chain = DefaultInjectionChain.getInstance(); 
            SpecificationNode injected = resolveOriginalNode(child, chain);
            if(injected == null) {
                throw new TemplateNodeNotResolvedException();
            }
            saveToCycle(child, injected);
            TemplateProcessor processor = resolveInjectedNode(
                    template, stack, original, injected);
            if(processor != null) {
                stack.push(processor);
                walkParsedTree(template, stack, child);
                stack.pop();
            }
        }
    }
    
    protected void doInjection(Template template) {
        if(template == null) {
            throw new IllegalArgumentException();
        }
        saveToCycle(template, template);
        Stack stack = new Stack();
        stack.push(template);
        SpecificationNode maya = new SpecificationNodeImpl(
                QM_MAYA, template.getSystemID(), 0);
        template.addChildNode(maya);
        walkParsedTree(template, stack, template);
        if(template.equals(stack.peek()) == false) {
            throw new IllegalStateException();
        }
        saveToCycle(template, template);
    }
    
    // support class --------------------------------------------------
    
    protected class InjectionChainImpl implements InjectionChain {
        
        private int _index;
        private InjectionChain _external;
        
        public InjectionChainImpl(InjectionChain external) {
            if (external == null) {
                throw new IllegalArgumentException();
            }
            _external = external;
        }
        
        public SpecificationNode getNode(SpecificationNode original) {
            if(original == null) {
                throw new IllegalArgumentException();
            }
            if(_index < _resolvers.size()) {
                InjectionResolver resolver =
                    (InjectionResolver)_resolvers.get(_index);
                _index++;
                InjectionChain chain;
                if(_index == _resolvers.size()) {
                    chain = _external;
                } else {
                    chain = this;
                }
                return resolver.getNode(original, chain);
            }
            throw new IndexOutOfBoundsException();
        }

    }

}
