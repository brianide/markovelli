About
=====
**Markovi** is a simple, generic implementation of Markov chains in Java.


Usage
=====
Tuples can be added directly to a MarkovChain object, but Markovi currently also provides an abstract
Feeder class, as well as an example NameFeeder concrete class, which can be used to make the process
substantially easier.

The following example, a simple name generator, will print ten names based on a newline-separated list of
source names, *names.txt:*
	
	/* Instiatate a MarkovChain and feed it some source names */
	MarkovChain<Character> nameGenerator = new MarkovChain<>(3);
	InputStream names = Utils.getResourceAsStream("names.txt");
	new NameFeeder(nameGenerator).feed(names);

	/* Generate a few names */
	for(int i = 0; i < 10; i++) {
		System.out.println(nameGenerator.generateString(16, null));
	}
	
The source for the NameFeeder class is a very simple example if you're interested in putting together
a Feeder.


Contributions
=============
Markovi is incomplete, albeit usable enough in its present state for some purposes. Contributions
are deeply appreciated.

Markovi is provided under the **BSD 2-Clause License**, which means you're free to do pretty much
whatever you like with it with minimal obligations. If you happen to improve or expand upon it in a
way that's generally useful or helpful, though, it'd be nice of you to consider sharing.
