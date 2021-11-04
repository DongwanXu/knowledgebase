# knowledgebase
Project Description
You have finally found your dream job running an academy for puppies. You are so passionate
about it, you worked long hours and your business is booming. There is only one problem: You
have enlisted so many pups into your academy that you are starting to lose track of everything.
Letting a puppy start the academy depends on their vaccination and health status. Even once
they’re in, what they can train for and which other puppies they interact with depends on their
history in the academy. Thankfully, you have a background in AI, so you decide to develop an
automated system that can evaluate all of the information you have and alert you and your
employees whether an action is possible. The system can also provide an instant guideline to
keep owners informed and minimize the risks to these adorable puppies.
You sit down to develop a beta version of the system using first-order logic resolution. Puppy
status and history data will be encoded as first order logic clauses in the knowledge base. The
knowledge bases contain sentences with the following defined operators:
NOT X ~X
X OR Y X | Y
X AND Y X & Y
X IMPLIES Y X => Y
The program takes a query of n actions and provides a logical conclusion to whether each can be
performed or not.
Format for input.txt:
<N = NUMBER OF QUERIES>
<QUERY 1>
...
<QUERY N>
<K = NUMBER OF GIVEN SENTENCES IN THE KNOWLEDGE BASE>
<SENTENCE 1>
...
<SENTENCE K>
The first line contains an integer N specifying the number of queries. The following N lines contain
one query per line. The line after the last query contains an integer K specifying the number of
sentences in the knowledge base. The remaining K lines contain the sentences in the knowledge
base, one sentence per line.

Query format: Each query will be a single literal of the form Predicate(Constant_Arguments) or
~Predicate(Constant_Arguments) and will not contain any variables. Each predicate will have
between 1 and 25 constant arguments. Two or more arguments will be separated by commas.
KB format: Each sentence in the knowledge base is written in one of the following forms:
1) An implication of the form p1 ∧ p2 ∧ ... ∧ pm ⇒ q, where its premise is a conjunction of
literals and its conclusion is a single literal. Remember that a literal is an atomic sentence
or a negated atomic sentence.
2) A single literal: q or ~q
