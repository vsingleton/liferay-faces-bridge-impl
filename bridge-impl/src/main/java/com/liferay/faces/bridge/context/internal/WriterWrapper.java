/**
 * Copyright (c) 2000-2017 Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liferay.faces.bridge.context.internal;

import java.io.IOException;
import java.io.Writer;

import javax.faces.FacesWrapper;


/**
 * @author  Neil Griffin
 */
public abstract class WriterWrapper extends Writer implements FacesWrapper<Writer> {

	@Override
	public void close() throws IOException {
		getWrapped().close();
	}

	@Override
	public void flush() throws IOException {
		getWrapped().flush();
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		getWrapped().write(cbuf, off, len);
	}

}
