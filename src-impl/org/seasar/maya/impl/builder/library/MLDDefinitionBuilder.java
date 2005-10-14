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

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.maya.builder.library.DefinitionBuilder;
import org.seasar.maya.builder.library.LibraryDefinition;
import org.seasar.maya.impl.CONST_IMPL;
import org.seasar.maya.impl.ParameterAwareImpl;
import org.seasar.maya.impl.builder.library.mld.LibraryDefinitionHandler;
import org.seasar.maya.impl.builder.library.scanner.SourceAlias;
import org.seasar.maya.impl.builder.library.scanner.WebXMLTaglibSourceScanner;
import org.seasar.maya.impl.util.IOUtil;
import org.seasar.maya.impl.util.ObjectUtil;
import org.seasar.maya.impl.util.XMLUtil;
import org.seasar.maya.source.SourceDescriptor;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class MLDDefinitionBuilder extends ParameterAwareImpl
        implements DefinitionBuilder, CONST_IMPL {

    private static Log LOG = 
        LogFactory.getLog(MLDDefinitionBuilder.class);

    public LibraryDefinition build(SourceDescriptor source) {
        if(source == null) {
            throw new IllegalArgumentException();
        }
        String systemID = source.getSystemID();
        if(source.exists() && systemID.toLowerCase().endsWith(".mld")) {
            LibraryDefinitionHandler handler = new LibraryDefinitionHandler();
            InputStream stream = source.getInputStream();
            try {
                XMLUtil.parse(handler, stream, PUBLIC_MLD10,
                        systemID, true, true, false);
            } catch(Throwable t) {
                if(LOG.isErrorEnabled()) {
                    LOG.error("MLD parse error on " + systemID, t);
                }
                return null;
            } finally {
                IOUtil.close(stream);
            }
            LibraryDefinition library = handler.getLibraryDefinition();
            boolean assigned = ObjectUtil.booleanValue(source.getParameter(
                    WebXMLTaglibSourceScanner.ASSIGNED), false);
            if(assigned) {
                library.addAssignedURI(source.getParameter(SourceAlias.ALIAS));
            }
            return library;
        }
        return null;
    }

}
