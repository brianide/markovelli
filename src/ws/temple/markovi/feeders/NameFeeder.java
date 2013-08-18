package ws.temple.markovi.feeders;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ws.temple.markovi.MarkovChain;

/**
 * Reads a newline-separated list of names into a MarkovChain.
 * 
 * @author Brian Dixon
 */
public class NameFeeder extends SequentialFeeder<Character> {

	/**
	 * Constructs a NameFeeder instance feeding the specified MarkovChain.
	 * 
	 * @param chain
	 */
	public NameFeeder(MarkovChain<Character> chain) {
		super(chain);
	}

	/**
	 * Reads names from the specified InputStream.
	 * 
	 * @param is
	 */
	public void feed(InputStream is) throws IOException {
		try(BufferedReader br = new BufferedReader(new InputStreamReader(is));) {
			
			String line;
			while((line = br.readLine()) != null) {
				
				/* registerToken() adds a new entry in the MarkovChain
				 * associating the previous tokens (as a Predictor)
				 * with the token passed as an argument. The window
				 * size is acquired from the MarkovChain automatically.
				 *
				 * As an analogy, if you were writing a Feeder for a
				 * sentence generator, you would call this method on
				 * each word in the source text.
				 */
				for(char c : line.toUpperCase().trim().toCharArray())
					this.registerToken(c);
				
				/* endPath() associates the last set of tokens with
				 * a null token, which means that generates sequences
				 * may end after such a set. This method also prepares
				 * the MarkovChain to register a predictor that can
				 * begin a sequence.
				 *
				 * Referring again to the analogy of the text generator,
				 * you would probably want to call this method at the
				 * end of each sentence, or perhaps each independant
				 * clause, in the source text.
				 */
				this.endPath();
			}
		}
	}

}
