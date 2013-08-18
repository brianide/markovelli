package ws.temple.markov.feeders;

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

import java.util.LinkedList;

import ws.temple.markov.MarkovChain;
import ws.temple.markov.Predictor;

/**
 * Abstract class that provides some helpful functionality for
 * sequentially feeding paths into a MarkovChain.
 * 
 * @author Brian Dixon
 *
 * @param <T>
 */
public abstract class SequentialFeeder<T> {
	
	/* Tracks tokens as they are added so that they can easily
	 * be used to build Predictors */
	protected final LinkedList<T> queue = new LinkedList<>();
	
	/* The MarkovChain that this object feeds */
	protected final MarkovChain<T> chain;
	
	/**
	 * Constructs a feeder for the specified MarkovChain
	 * 
	 * @param chain
	 */
	public SequentialFeeder(MarkovChain<T> chain) {
		this.chain = chain;
		primeQueue();
	}
	
	/**
	 * Adds the specified token to the chain, using the previous
	 * tokens as the Predictor.
	 * 
	 * @param next
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SequentialFeeder<T> registerToken(T next) {
		chain.addPair(new Predictor<>((T[]) queue.toArray()), next);
		queue.removeFirst();
		queue.add(next);
		return this;
	}
	
	/**
	 * Adds a token to the chain indicating that paths may end on
	 * the previously-registered token.
	 * 
	 * @return
	 */
	public SequentialFeeder<T> endPath() {
		registerToken(null);
		primeQueue();
		return this;
	}
	
	/**
	 * Prime the queue with a null predictor. Tokens in the
	 * null-predictor's set for a chain may be used to begin a path.
	 */
	private void primeQueue() {
		queue.clear();
		for(int i = 0; i < chain.getPredictorLength(); i++)
			queue.add(null);			
	}

}
