Overview
--------

Uncial is a fast and easy to use logging system. The goal is to be fast and easy not "full featured."

Features
--------

* Uncial supports `printf` style format strings. Like [SLF4J](http://www.slf4j.org/)'s parameterized logging (see [here](http://www.slf4j.org/faq.html#logging_performance) for an explanation), this gives Uncial a performance boost without the user needing to write ugly "if enabled" code blocks.  Unlike, SLF4J, however, Uncial uses the standard `printf` format style in conjunction with varargs.  This style is familiar to developers and using varargs allows for easier usage when logging many arguments.

* It's lean!  The code base consists of __0__ dependencies, __12__ files and __< 1000__ lines of commented/javadoc/formatted code.

* It's fast!  See __Speed__ below.

* Supports dynamic configuration via __JMX__.

Usage
-----

#### Using a `Logger` Instance:
This is basically the same pattern as LOG4J or SLF4J.

    private static final Logger LOG = Loggers.get(MyClass.class);
    
    LOG.trace("Your os is '%s' and you have %d CPUs.", System.getProperty("os.name"), Runtime.getRuntime().availableProcessors());

#### Using the static `Log` Methods:
Same methods as the `Logger` interface but accessed statically.  Note, this does not sacrifice knowing the `Class` from which the log call originated (albeit it does incur a performance penalty, [~90%](http://microbenchmarks.appspot.com/run/uncial.benchmark@gmail.com/net.ocheyedan.uncial.caliper.UncialBenchmark/1396003) from ~2.5 _us_ to ~4.7 _us_ per log call).

    Log.trace("Your os is '%s' and you have %d CPUs.", System.getProperty("os.name"), Runtime.getRuntime().availableProcessors());

Configuration
---------------------

No xml configuration.  Configuration is done in java code or via __JMX__.  By default, no `Appenders` are configured.  So at a minimum you'll need to configure an `Appender`.  Don't worry it's __easy__!
Here are some examples:

##### Console appender (stdout)

    UncialConfig.get().addAppender(new ConsoleAppender());

##### File appender

    UncialConfig.get().addAppender(new FileAppender("/tmp/myapplication.log"));

##### Multiple appenders

    UncialConfig.get().addAppender(new ConsoleAppender());
    UncialConfig.get().addAppender(new FileAppender("/tmp/myapplication.log"));

##### Modify log levels for a set of classes

    UncialConfig.get().setLevel("org.apache", Logger.warn); // any class with package starting with 'org.apache'

##### Modify log levels for a particular class

    UncialConfig.get().setLevel(StringUtils.class, Logger.error);


As one would expect, all the above configurations can be mixed and matched.

Take a look at `net.ocheyedan.uncial.UncialConfig` for a complete list of configuration options and defaults (it is also the `MBean` for __JMX__).


Speed
-----

The benchmarking tests are done with [caliper](http://code.google.com/p/caliper/) and the source for all tests can be found in the [test directory](https://github.com/blangel/uncial/tree/master/src/test/java/net/ocheyedan/uncial/caliper).

In general Uncial is about __2x__ faster than Logback and __14x__ faster than Log4j.  The full caliper results are hosted [here](http://microbenchmarks.appspot.com/user/uncial.benchmark@gmail.com) but here's some explanation of some of the results.

##### LogSystemsBenchmark
This test uses a log thread with _printf_-style formatting (default uncial configuration) for Uncial.  Logback and Log4j are not (cannot) using a log thread.  Benchmark was run using the equivalent __FileAppender__ for each implementation and using the equivalent appender format of `%d{MM/dd/yyyy HH:mm:ss.SSS} %C [%c] - %m%n`

* __Uncial__ 
    * __2.74__ _us_
* __Logback__
    * __5.57__ _us_
* __Log4j__
    * __44.13__ _us_

Full results for this test with more trials [here](http://microbenchmarks.appspot.com/run/uncial.benchmark@gmail.com/net.ocheyedan.uncial.caliper.LogSystemsBenchmark). 

##### LogSystemsSingleThreadedBenchmark
This test does not use a log thread and has _printf_-style formatting for Uncial.  Benchmark was run using the equivalent __FileAppender__ for each implementation and using the equivalent appender format of `%d{MM/dd/yyyy HH:mm:ss.SSS} %C [%c] - %m%n`

* __Uncial__ 
    * __2.94__ _us_
* __Logback__
    * __5.74__ _us_
* __Log4j__
    * __44.45__ _us_

Full results for this test with more trials [here](http://microbenchmarks.appspot.com/run/uncial.benchmark@gmail.com/net.ocheyedan.uncial.caliper.LogSystemsSingleThreadedBenchmark).

##### LogSystemsSingleThreadedSlf4jBenchmark
This test does not use a log thread and has SLF4J-style formatting for Uncial.  Benchmark was run using the equivalent __FileAppender__ for each implementation and using the equivalent appender format of `%d{MM/dd/yyyy HH:mm:ss.SSS} %C [%c] - %m%n`

* __Uncial__ 
    * __3.27__ _us_
* __Logback__
    * __5.67__ _us_
* __Log4j__
    * __44.14__ _us_

Full results for this test with more trials [here](http://microbenchmarks.appspot.com/run/uncial.benchmark@gmail.com/net.ocheyedan.uncial.caliper.LogSystemsThreadedSlf4jBenchmark).

##### LogSystemsThreadedSlf4jBenchmark
This test uses a log thread and has SLF4J-style formatting for Uncial.  Logback and Log4j are not (cannot) using a log thread.  Benchmark was run using the equivalent __FileAppender__ for each implementation and using the equivalent appender format of `%d{MM/dd/yyyy HH:mm:ss.SSS} %C [%c] - %m%n`

* __Uncial__ 
    * __3.12__ _us_
* __Logback__
    * __5.59__ _us_
* __Log4j__
    * __45.12__ _us_

Full results for this test with more trials [here](http://microbenchmarks.appspot.com/run/uncial.benchmark@gmail.com/net.ocheyedan.uncial.caliper.LogSystemsThreadedSlf4jBenchmark).

###### Note about the SLF4J Benchmarks
The above numbers make it appear that the SLF4J formatting for Uncial is much slower when it fact it handles multiple arguments better than the _printf_ style formatter (which is why it is included in Uncial).  Take a look at the caliper results as they include many more tests and details worth noting.

##### LogSystemsWithExpensiveFormatBenchmark
This test uses the default Uncial configuration in terms of log-thread and formatting style however it changes the appender formatter for all implementations to utilize the expensive options of logging method name, line number and file name of the invoking log call.  Benchmark was run using the equivalent __FileAppender__ for each implementation and using the equivalent appender format of `%d{MM/dd/yyyy HH:mm:ss.SSS} %t %F %C#%M @ %L [%c] - %m%n`

* __Uncial__ 
    * __25.4__ _us_
* __Logback__
    * __53.6__ _us_
* __Log4j__
    * __44.1__ _us_

Full results for this test with more trials [here](http://microbenchmarks.appspot.com/run/uncial.benchmark@gmail.com/net.ocheyedan.uncial.caliper.LogSystemsWithExpensiveFormatBenchmark). 


SLF4J Support
--------------

Coming soon...