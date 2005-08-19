/*
 * Copyright (c) 2004-2005 the Seasar Foundation and the Others.
 *
 * Licensed under the Seasar Software License, v1.1 (aka "the License");
 * you may not use this file except in compliance with the License which
 * accompanies this distribution, and is available at
 *
 *     http://www.seasar.org/SEASAR-LICENSE.TXT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.seasar.maya.impl.jsp.builder.library;

import java.io.InputStream;

import org.seasar.maya.builder.library.LibraryDefinition;
import org.seasar.maya.builder.library.DefinitionBuilder;
import org.seasar.maya.impl.builder.library.scanner.SourceAlias;
import org.seasar.maya.impl.builder.library.scanner.WebXmlAliasSourceScanner;
import org.seasar.maya.impl.jsp.builder.library.tld.TLDHandler;
import org.seasar.maya.impl.util.ObjectUtil;
import org.seasar.maya.impl.util.XmlUtil;
import org.seasar.maya.source.SourceDescriptor;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class TLDDefinitionBuilder implements DefinitionBuilder {

    public void setParameter(String name, String value) {
        throw new UnsupportedOperationException();
    }
    
    public LibraryDefinition build(SourceDescriptor source) {
        if(source == null) {
            throw new IllegalArgumentException();
        }
        String systemID = source.getSystemID();
        if(source.exists() && systemID.toLowerCase().endsWith(".tld")) {
            InputStream stream = source.getInputStream();
            TLDHandler handler = new TLDHandler();
            XmlUtil.parse(handler, stream, "tld", systemID, true, false, false);
            JspLibraryDefinition library = handler.getLibraryDefinition();
            boolean assigned = ObjectUtil.booleanValue(
                    source.getAttribute(WebXmlAliasSourceScanner.ASSIGNED), false);
            if(assigned || "/META-INF/taglib.tld".equals(systemID)) {
                library.setAssignedURI(source.getAttribute(SourceAlias.ALIAS));
            }
            return library;
        }
        return null;
    }
    
}
