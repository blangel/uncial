Overview
--------

Uncial is a fast and easy to use logging system. The goal is to be fast and easy not "full featured."

Features
--------

* Uncial supports `printf` style format strings. Like [SLF4J](http://www.slf4j.org/)'s parameterized logging (see [here](http://www.slf4j.org/faq.html#logging_performance) for an explanation), this gives Uncial a performance boost without the user needing to write ugly "if enabled" code blocks.  Unlike, SLF4J, however, Uncial uses the standard `printf` format style in conjunction with varargs.  This style is familiar to developers and using varargs allows for easier usage when logging many arguments.

* It's lean!  The code base consists of __0__ dependencies, __8__ class files and __< 1000__ lines of commented/javadoc/formatted code.

* It's fast!  See __Speed__ below.

* Supports dynamic configuration via __JMX__.

Usage
-----

#### Using a `Logger` Instance:
This is basically the same pattern as LOG4J or SLF4J.

    private static final Logger LOG = Loggers.get(MyClass.class);
    
    LOG.trace("Your os is '%s' and you have %d CPUs.", System.getProperty("os.name"), Runtime.getRuntime().availableProcessors());

#### Using the static `Log` Methods:
Same methods as the `Logger` interface but accessed statically without sacrificing knowing the `Class` from which the log call originated (albeit at a slight performance penalty).

    Log.trace("Your os is '%s' and you have %d CPUs.", System.getProperty("os.name"), Runtime.getRuntime().availableProcessors());

Configuration
---------------------

Nope, no xml configuration available.  Configuration is done in java code or via __JMX__.  By default, no `Appenders` are configured so at minimum you'll need to configure an `Appender`. But don't worry it's __easy__!
Here are some examples:

##### Console appender (stdout)

    UncialConfig.get().addAppender(new PrintStreamAppender());

##### File appender

    UncialConfig.get().addAppender(new FileAppender("/tmp/myapplication.log"));

##### Multiple appenders

    UncialConfig.get().addAppender(new PrintStreamAppender());
    UncialConfig.get().addAppender(new FileAppender("/tmp/myapplication.log"));

##### Modify log levels for a set of classes

    UncialConfig.get().setLevel("org.apache", Logger.warn); // any class with package starting with 'org.apache'

##### Modify log levels for a particular class

    UncialConfig.get().setLevel(StringUtils.class, Logger.error);


Note, as one would expect, all the above configurations can be mixed and matched.

Take a look at `net.ocheyedan.uncial.UncialConfig` for a complete list of configuration options and defaults (it is also the `MBean` for __JMX__).


Speed
-----



SLF4J Support
--------------

Coming soon...