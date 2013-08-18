package ws.temple.util;

/*
 * Copyright (c) 2013, Brian Dixon
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.nio.charset.Charset;

public class Utils {
	
	private Utils(){}
	
	/**
	 * Returns the first of the specified objects that is not null. Throws
	 * a NullPointerException if no non-null objects are passed.
	 * 
	 * @param items
	 * @return
	 */
	@SafeVarargs
	public static <T> T firstNonNull(T... items) {
		if(items != null)
			for(T item : items)
				if(item != null)
					return item;
		throw new NullPointerException();
	}
	
	/**
	 * Checks for equality between the specified objects, with null
	 * considered to be a distinct, valid value.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean equals(Object a, Object b) {
		if(a == b)
			return true;
		else if(a == null || b == null)
			return false;
		return a.equals(b);
	}
	
	/**
	 * Fetches an InputStream for the specified resource on the classpath.
	 * 
	 * @param name
	 * @return
	 */
	public static InputStream getResourceAsStream(String name) {
		return Utils.class.getClassLoader().getResourceAsStream(name);
	}
	
	/**
	 * Fetches the specified resource on the classpath.
	 * 
	 * @param name
	 * @return
	 */
	public static URL getResource(String name) {
		return Utils.class.getClassLoader().getResource(name);
	}
	
	/**
	 * Returns the output of the specified stream as a single string,
	 * preserving newlines. The default charset for the current JVM
	 * is used.
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String readStream(InputStream is) throws IOException {
		return readStream(is, Charset.defaultCharset());
	}
	
	/**
	 * Returns the output of the specified stream as a single string,
	 * preserving newlines. The specified charset is used.
	 * 
	 * @param is
	 * @param cs
	 * @return
	 * @throws IOException
	 */
	public static String readStream(InputStream is, String cs) throws IOException {
		return readStream(is, Charset.forName(cs));
	}
	
	/**
	 * Returns the output of the specified stream as a single string,
	 * preserving newlines. The specified charset is used.
	 * 
	 * @param is
	 * @param cs
	 * @return
	 * @throws IOException
	 */
	public static String readStream(InputStream is, Charset cs) throws IOException {
		final StringBuilder sb = new StringBuilder();
		try(final LineNumberReader br = new LineNumberReader(new InputStreamReader(is, cs));) {
			String line;
			while((line = br.readLine()) != null) {
				if(br.getLineNumber() > 1)
					sb.append('\n');
				sb.append(line);
			}
		}
		return sb.toString();
	}

}
