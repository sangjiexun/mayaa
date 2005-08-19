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
package org.seasar.maya.provider;

/**
 * ServiceProvider提供オブジェクトのチューニング設定。
 * @author Masataka Kurihara (Gluegent, Inc.)
 */
public interface Parameterizable {

	/**
	 * ユーザー設定の受け入れメソッド。ServiceProvider内部で用いられる。
	 * @param name 設定名。
	 * @param value 設定値。
	 */
	void setParameter(String name, String value);

}
