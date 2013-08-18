package ws.temple.markovi;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An immutable wrapper for a series of tokens defining a predictor
 * in a Markov chain.
 * 
 * @author Brian Dixon
 *
 * @param <T>
 */
public class Predictor<T> implements Serializable {

	private static final long serialVersionUID = 5989631577581463046L;

	protected final T[] elements;
	
	/**
	 * Constructs a Predictor from the given elements.
	 * 
	 * @param elements
	 */
	@SafeVarargs
	public Predictor(T... elements) {
		if(elements == null)
			throw new NullPointerException();
		
		/* Store a copy rather than the original, so that our shit
		 * doesn't fucking explode if the original is modified */
		this.elements = Arrays.copyOf(elements, elements.length);
	}
	
	public List<T> asList() {
		return new ArrayList<>(Arrays.asList(elements));
	}

	/**
	 * Returns the token at the specified index in the Predictor.
	 * 
	 * @param i
	 * @return
	 */
	public T get(int i) {
		return elements[i];
	}
	
	/**
	 * Returns the token at the first index of the Predictor.
	 * 
	 * @return
	 */
	public T getFirst() {
		return elements[0];
	}
	
	/**
	 * Returns the token at the last index of the Predictor.
	 * 
	 * @return
	 */
	public T getLast() {
		return elements[elements.length - 1];
	}
	
	/**
	 * Returns the number of tokens in the Predictor.
	 * 
	 * @return
	 */
	public int size() {
		return elements.length;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(elements);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		Predictor<T> other = (Predictor<T>) obj;
		if(!Arrays.deepEquals(elements, other.elements))
			return false;
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Arrays.deepToString(elements);
	}

}
