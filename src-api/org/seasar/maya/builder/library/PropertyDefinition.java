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
package org.seasar.maya.builder.library;

import org.seasar.maya.engine.specification.SpecificationNode;

/**
 * MLDのpropertyノードのモデルオブジェクト。
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public interface PropertyDefinition {
    
    /**
     * 所属するプロセッサ情報モデルの取得。
     * @return プロセッサ情報モデル。 
     */
    ProcessorDefinition getProcessorDefinition();
    
    /**
     * MLDのname属性であるプロパティ名。
     * テンプレートや設定XML上の属性と、テンプレートプロセッサのプロパティを
     * バインディングする名前となる。
     * @return バインディング名。 
     */
    String getName();
    
    /**
     * MLDにrequired属性で記述された必須フラグ。デフォルトはfalse。
     * @return 必須フラグ。
     */
    boolean isRequired();
    
    /**
     * MLDにexpectedType属性で記述された属性型。デフォルトはjava.lang.Object。
     * @return 属性型。
     */
    Class getExpectedType();
    
    /**
     * MLDのdefault属性値。カスタマイズで渡すプロパティのデフォルト値。
     * @return カスタマイズデフォルト値。
     */
    String getDefaultValue();

    /**
     * MLDのfinal属性値。MLDに定義した値はユーザーアプリで上書きされない。
     * @return ファイナル値。
     */
    String getFinalValue();
    
    /**
     * プロパティオブジェクトを生成する。
     * @param injected インジェクションするノード。
     * @return プロパティオブジェクト。
     */
    Object createProcessorProperty(SpecificationNode injected);
    
}
