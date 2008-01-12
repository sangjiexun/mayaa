/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
package org.seasar.mayaa.impl.builder;

import org.seasar.mayaa.impl.MayaaException;

/**
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public class TemplateNodeNotResolvedException extends MayaaException {

    private static final long serialVersionUID = 4560512867759109674L;

    private String _originalName;

    public TemplateNodeNotResolvedException(String originalName) {
        _originalName = originalName;
    }

    public String getOriginalName() {
        return _originalName;
    }

    protected String[] getMessageParams() {
        return new String[] { _originalName };
    }

}
