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
package org.seasar.maya.source.factory;

import java.io.Serializable;

import org.seasar.maya.provider.Parameterizable;
import org.seasar.maya.source.SourceDescriptor;

/**
 * ソース定義のファクトリ。
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public interface SourceFactory extends Parameterizable, Serializable {

    /**
     * ソース定義の生成・取得をおこなう。
     * @param systemID ソースのSystemID。
     * @return ソース定義。
     */
    SourceDescriptor getSourceDescriptor(String systemID);
    
}
