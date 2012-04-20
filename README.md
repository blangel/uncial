Overview
--------

Uncial is a fast and easy to use logging system. The goal is to be fast and easy not "full featured."

Features
--------

* Uncial supports `printf` style format strings. Like [SLF4J](http://www.slf4j.org/)'s parameterized logging (see [here](http://www.slf4j.org/faq.html#logging_performance) for an explanation), this gives Uncial a performance boost without the user needing to write ugly "if enabled" code blocks.  Unlike, SLF4J, however, Uncial uses the standard `printf` format style in conjunction with varargs.  This style is familiar to developers and using varargs allows for easier usage when logging many arguments.

* It's fast!  See __Speed__ below.   

Usage
-----

* Using a `Logger` Instance (same pattern as LOG4J or SLF4J): create a `Logger` instance based on the `Class`

  	private static final Logger LOG = Loggers.get(MyClass.class);

  	
	LOG.trace("Your os is '%s' and you have %d processors.", System.getProperty("os.name"), Runtime.getRuntime().availableProcessors());

* Using the static `Log` Methods: same methods as the `Logger` interface but accessed statically without sacrificing the `Class` from which the log call originated (albeit at a slight performance penalty)! 

  	Log.trace("Your os is '%s' and you have %d processors.", System.getProperty("os.name"), Runtime.getRuntime().availableProcessors());

Configuration
---------------------



Speed
-----



SLF4J Support
--------------

Coming soon...