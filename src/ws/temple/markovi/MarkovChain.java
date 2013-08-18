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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import ws.temple.util.Utils;

public class MarkovChain<T> implements Serializable {

	private static final long serialVersionUID = 1078889713056415705L;

	/* Matches a Predictor to a list of probabilities for each possible token to follow it */
	protected final Map<Predictor<T>, NavigableMap<Double, T>> top = new HashMap<>();
	
	/* Tracks the number of pairs for each Predictor */
	protected final Map<Predictor<T>, Integer> frequency = new HashMap<>();
	
	/* The number of tokens expected from Predictors */
	protected final int predictorLength;
	
	/* RNG for grabbing random tokens */
	protected final transient Random rng = new Random();
	
	/**
	 * Constructs an empty MarkovChain accepting Predictors of the specified length.
	 * 
	 * @param predictorLength
	 */
	public MarkovChain(int predictorLength) {
		this.predictorLength = predictorLength;
	}
	
	/**
	 * Adds a predictor/next-token pair to this Markov chain.
	 * 
	 * @param predictor
	 * @param next
	 */
	public void addPair(Predictor<T> predictor, T next) {
		if(predictor.size() != predictorLength)
			throw new IllegalArgumentException("Predictor length does not match chain");
		
		/* Create a probability map for this Predictor if it doesn't exist yet */
		NavigableMap<Double, T> distribution = top.get(predictor);
		if(distribution == null) {
			distribution = new TreeMap<>();
			top.put(predictor, distribution);
		}
		
		/* Calculate the new frequency for the Predictor */
		final int oldTotal = Utils.firstNonNull(frequency.get(predictor), 0);
		final int newTotal = oldTotal + 1;
		frequency.put(predictor, newTotal);
		
		/* The Entry set is copied so that we can modify the original while iterating over it; an
		 * ArrayList is used so that the original key order is preserved */
		final List<Entry<Double, T>> entries = new ArrayList<Entry<Double, T>>(distribution.entrySet());
		double oldWeightAccum = 0.0;
		double newWeightAccum = 0.0;
		
		/* Rebuild the probability map for the new counts */
		distribution.clear();
		boolean nextInOldDistribution = false;
		for(Entry<Double, T> entry : entries) {
			final boolean isNext = Utils.equals(entry.getValue(),next);
			final double oldWeight = entry.getKey() - oldWeightAccum;
			final double newCount = oldWeight * oldTotal + (isNext ? 1 : 0);
			
			oldWeightAccum = entry.getKey();
			newWeightAccum += newCount / newTotal;
			
			distribution.put(newWeightAccum, entry.getValue());
			
			/* TreeMap.contains() runs in linear time and these things can get pretty large,
			 * so this is tracked here since we're iterating over the set anyway */
			nextInOldDistribution |= isNext;
		}
		
		/* If the word isn't new to the map, add it at the end; the difference in 1.0f and
		 * the value of the previous token produce the appropriate range */
		if(!nextInOldDistribution)
			distribution.put(1.0, next);
	}
	
	/**
	 * Returns a random token from the weighted set of those that can follow the given
	 * Predictor, or null if no such token exists.
	 * 
	 * @param predictor 
	 * @return
	 */
	public T next(Predictor<T> predictor) {
		final NavigableMap<Double, T> dist = top.get(predictor);
		if(dist == null)
			return null;
		else {
			return dist.ceilingEntry(rng.nextDouble()).getValue();
		}
	}
	
	/**
	 * Generates a sequence from the current dictionary. 
	 * 
	 * @param maxLength
	 * @param strictStart
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> generateSequence(int maxLength) {
		final List<T> seq = new LinkedList<>();
		
		for(int i = 0; i < predictorLength; i++)
			seq.add(null);

		T next;
		while(seq.size() < maxLength + predictorLength) {
			next = next(new Predictor<>((T[]) seq.subList(seq.size() - predictorLength, seq.size()).toArray()));
			if(next == null)
				break;
			seq.add(next);
		}
		
		return seq.subList(predictorLength, seq.size());
	}
	
	/**
	 * Generate a string from the current dictionary, inserting the
	 * specified string between tokens.
	 * 
	 * @param maxLength
	 * @param glue
	 * @return
	 */
	public String generateString(int maxLength, String glue) {
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		
		for(T token : generateSequence(maxLength)) {
			if(glue != null && !first)
				sb.append(glue);
			else if(first)
				first = false;
			
			sb.append(token.toString());
		}
		return sb.toString();
	}
	
	/**
	 * Returns the number of tokens this chain expects in each Predictor.
	 * 
	 * @return
	 */
	public int getPredictorLength() {
		return predictorLength;
	}
	
	/**
	 * Outputs all Predictors and their probability distributions for this chain
	 * 
	 * TODO Get rid of this shit or move it somewhere else
	 */
	public void printDictionaries() {
		for(Entry<Predictor<T>, NavigableMap<Double, T>> topEntry : top.entrySet()) {
			System.out.println(topEntry.getKey());
			for(Entry<Double, T> distEntry : top.get(topEntry.getKey()).entrySet()) {
				System.out.println(": " + distEntry.getKey() + " -> " + distEntry.getValue());
			}
			System.out.println();
		}
	}
	
}
