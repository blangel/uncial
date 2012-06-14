Overview
--------

Uncial is a fast and easy to use logging system. The goal is to be fast and easy not "full featured."

Features
--------

* It's fast!  See __Speed__ below.

* It's lean!  The code base consists of __1__ dependenciy, __22__ files and __< 2400__ lines of commented/javadoc/formatted code.

* Supports dynamic configuration via __JMX__.

* Natively implements [SLF4J](http://www.slf4j.org/).

* Uncial supports `printf` style format strings. Like [SLF4J](http://www.slf4j.org/)'s parameterized logging (see [here](http://www.slf4j.org/faq.html#logging_performance) for an explanation), this gives Uncial a performance boost without the user needing to write ugly "if enabled" code blocks.  Unlike, SLF4J, however, Uncial uses the standard `printf` format style in conjunction with varargs.  This style is familiar to developers and using varargs allows for easier usage when logging many arguments.

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

##### Get a handle to the configuration

    UncialConfig config = UncialConfig.get();

##### Console appender (stdout)

    config.addAppender(new ConsoleAppender());

##### File appender

    config.addAppender(new FileAppender("/tmp/myapplication.log"));

##### Rolling file appender (time based)

    config.addAppender(new RollingFileAppender("/tmp/myapplication.log", 1, TimeUnit.DAYS));

##### Rolling file appender (size based)

    config.addAppender(new RollingFileAppender("/tmp/myapplication.log", 5, SizeUnit.MEGABYTES));

##### Multiple appenders (log statements go to both appenders)

    config.addAppender(new ConsoleAppender());
    config.addAppender(new FileAppender("/tmp/myapplication.log"));

##### Modify log levels for a set of classes

    config.setLevel("org.apache", Logger.warn); // any class with package starting with 'org.apache'

##### Modify log levels for a particular class

    config.setLevel(StringUtils.class, Logger.error);


As one would expect, all the above configurations can be mixed and matched.

Take a look at `net.ocheyedan.uncial.UncialConfig` for a complete list of configuration options and defaults (it is also the `MBean` for __JMX__).


Speed
-----

The benchmarking tests are done with [caliper](http://code.google.com/p/caliper/) and the source for all tests can be found in the [test directory](https://github.com/blangel/uncial/tree/master/src/test/java/net/ocheyedan/uncial/caliper).

In general Uncial is about __2x__ faster than Logback and __14x__ faster than Log4j.  The full caliper results are hosted [here](http://microbenchmarks.appspot.com/user/uncial.benchmark@gmail.com) but here's some explanation of some of the results.

##### LogSystemsBenchmark
This test uses a log thread with _printf_-style formatting (default uncial configuration) for Uncial.  Logback and Log4j are not (cannot) using a log thread.  Benchmark was run using the equivalent __FileAppender__ for each implementation and using the equivalent appender format of `%d{MM/dd/yyyy HH:mm:ss.SSS} %C [%c] - %m%n`

<table>
  <tr><th>Benchmark</th><th>Logger</th><th>Time (us)</th><th>Linear Runtime</th><th>%</th></tr>
  <tr><td>Message (0 params)</td><td></td><td></td><td></td><td></td></tr>
  <tr><td></td><td>Uncial</td><td>2.74</td><td>=</td><td>100%</td></tr>
  <tr><td></td><td>Logback</td><td>5.57</td><td>===</td><td>203%</td></tr> <tr><td></td><td>Log4j</td><td>44.13</td><td>=============================</td><td>1,612%</td></tr>
  <tr><td>Message (1 params)</td><td></td><td></td><td></td><td></td></tr>
  <tr><td></td><td>Uncial</td><td>3.14</td><td>==</td><td>115%</td></tr>
  <tr><td></td><td>Logback</td><td>6.32</td><td>====</td><td>231%</td></tr> <tr><td></td><td>Log4j</td><td>44.46</td><td>=============================</td><td>1,625%</td></tr>
  <tr><td>Message (many params)</td><td></td><td></td><td></td><td></td></tr>
  <tr><td></td><td>Uncial</td><td>6.40</td><td>====</td><td>234%</td></tr>
  <tr><td></td><td>Logback</td><td>7.01</td><td>====</td><td>256%</td></tr> <tr><td></td><td>Log4j</td><td>44.52</td><td>==============================</td><td>1,627%</td></tr>
</table>

See [here](http://microbenchmarks.appspot.com/run/uncial.benchmark@gmail.com/net.ocheyedan.uncial.caliper.LogSystemsBenchmark) for full results of this test with more trials.

##### LogSystemsSingleThreadedBenchmark
This test does not use a log thread and has _printf_-style formatting for Uncial.  Benchmark was run using the equivalent __FileAppender__ for each implementation and using the equivalent appender format of `%d{MM/dd/yyyy HH:mm:ss.SSS} %C [%c] - %m%n`

<table>
  <tr><th>Benchmark</th><th>Logger</th><th>Time (us)</th><th>Linear Runtime</th><th>%</th></tr>
  <tr><td>Message (0 params)</td><td></td><td></td><td></td><td></td></tr>
  <tr><td></td><td>Uncial</td><td>2.94</td><td>=</td><td>100%</td></tr>
  <tr><td></td><td>Logback</td><td>5.74</td><td>===</td><td>196%</td></tr> <tr><td></td><td>Log4j</td><td>45.45</td><td>=============================</td><td>1,548%</td></tr>
  <tr><td>Message (1 params)</td><td></td><td></td><td></td><td></td></tr>
  <tr><td></td><td>Uncial</td><td>3.25</td><td>==</td><td>111%</td></tr>
  <tr><td></td><td>Logback</td><td>6.27</td><td>====</td><td>214%</td></tr> <tr><td></td><td>Log4j</td><td>44.95</td><td>=============================</td><td>1,531%</td></tr>
  <tr><td>Message (many params)</td><td></td><td></td><td></td><td></td></tr>
  <tr><td></td><td>Uncial</td><td>6.61</td><td>====</td><td>225%</td></tr>
  <tr><td></td><td>Logback</td><td>7.15</td><td>====</td><td>244%</td></tr> <tr><td></td><td>Log4j</td><td>44.45</td><td>=============================</td><td>1,548%</td></tr>
</table>

See [here](http://microbenchmarks.appspot.com/run/uncial.benchmark@gmail.com/net.ocheyedan.uncial.caliper.LogSystemsSingleThreadedBenchmark) for full results of this test with more trials.

##### LogSystemsSingleThreadedSlf4jBenchmark
This test does not use a log thread and has SLF4J-style formatting for Uncial.  Benchmark was run using the equivalent __FileAppender__ for each implementation and using the equivalent appender format of `%d{MM/dd/yyyy HH:mm:ss.SSS} %C [%c] - %m%n`

<table>
  <tr><th>Benchmark</th><th>Logger</th><th>Time (us)</th><th>Linear Runtime</th><th>%</th></tr>
  <tr><td>Message (0 params)</td><td></td><td></td><td></td><td></td></tr>
  <tr><td></td><td>Uncial</td><td>3.27</td><td>==</td><td>100%</td></tr>
  <tr><td></td><td>Logback</td><td>5.64</td><td>===</td><td>174%</td></tr> <tr><td></td><td>Log4j</td><td>45.14</td><td>=============================</td><td>1,382%</td></tr>
  <tr><td>Message (1 params)</td><td></td><td></td><td></td><td></td></tr>
  <tr><td></td><td>Uncial</td><td>3.94</td><td>==</td><td>121%</td></tr>
  <tr><td></td><td>Logback</td><td>6.52</td><td>====</td><td>200%</td></tr> <tr><td></td><td>Log4j</td><td>45.39</td><td>==============================</td><td>1,390%</td></tr>
  <tr><td>Message (many params)</td><td></td><td></td><td></td><td></td></tr>
  <tr><td></td><td>Uncial</td><td>3.22</td><td>==</td><td>99%</td></tr>
  <tr><td></td><td>Logback</td><td>7.11</td><td>====</td><td>220%</td></tr> <tr><td></td><td>Log4j</td><td>45.16</td><td>=============================</td><td>1,383%</td></tr>
</table>

See [here](http://microbenchmarks.appspot.com/run/uncial.benchmark@gmail.com/net.ocheyedan.uncial.caliper.LogSystemsThreadedSlf4jBenchmark) for full results of this test with more trials.

##### LogSystemsThreadedSlf4jBenchmark
This test uses a log thread and has SLF4J-style formatting for Uncial.  Logback and Log4j are not (cannot) using a log thread.  Benchmark was run using the equivalent __FileAppender__ for each implementation and using the equivalent appender format of `%d{MM/dd/yyyy HH:mm:ss.SSS} %C [%c] - %m%n`

<table>
  <tr><th>Benchmark</th><th>Logger</th><th>Time (us)</th><th>Linear Runtime</th><th>%</th></tr>
  <tr><td>Message (0 params)</td><td></td><td></td><td></td><td></td></tr>
  <tr><td></td><td>Uncial</td><td>3.12</td><td>==</td><td>100%</td></tr>
  <tr><td></td><td>Logback</td><td>5.59</td><td>===</td><td>179%</td></tr> <tr><td></td><td>Log4j</td><td>45.12</td><td>============================</td><td>1,446%</td></tr>
  <tr><td>Message (1 params)</td><td></td><td></td><td></td><td></td></tr>
  <tr><td></td><td>Uncial</td><td>3.32</td><td>==</td><td>107%</td></tr>
  <tr><td></td><td>Logback</td><td>6.43</td><td>====</td><td>206%</td></tr> <tr><td></td><td>Log4j</td><td>45.49</td><td>==============================</td><td>1,458%</td></tr>
  <tr><td>Message (many params)</td><td></td><td></td><td></td><td></td></tr>
  <tr><td></td><td>Uncial</td><td>3.24</td><td>==</td><td>104%</td></tr>
  <tr><td></td><td>Logback</td><td>7.49</td><td>====</td><td>240%</td></tr> <tr><td></td><td>Log4j</td><td>45.41</td><td>=============================</td><td>1,456%</td></tr>
</table>

See [here](http://microbenchmarks.appspot.com/run/uncial.benchmark@gmail.com/net.ocheyedan.uncial.caliper.LogSystemsThreadedSlf4jBenchmark) for full results of this test with more trials.

###### Note about the SLF4J Benchmarks
The SLF4J formatting for Uncial is slower with no arguments but much better than the _printf_ style formatter when handling many arguments (which is why it is included in Uncial).  Take a look at the caliper results as they include many more tests and details worth noting.

##### LogSystemsWithExpensiveFormatBenchmark
This test uses the default Uncial configuration in terms of log-thread and formatting style however it changes the appender formatter for all implementations to utilize the expensive options of logging method name, line number and file name of the invoking log call.  Benchmark was run using the equivalent __FileAppender__ for each implementation and using the equivalent appender format of `%d{MM/dd/yyyy HH:mm:ss.SSS} %t %F %C#%M @ %L [%c] - %m%n`

<table>
  <tr><th>Benchmark</th><th>Logger</th><th>Time (us)</th><th>Linear Runtime</th><th>%</th></tr>
  <tr><td>Message (0 params)</td><td></td><td></td><td></td><td></td></tr>
  <tr><td></td><td>Uncial</td><td>25.4</td><td>=============</td><td>100%</td></tr>
  <tr><td></td><td>Logback</td><td>53.6</td><td>=============================</td><td>210%</td></tr> <tr><td></td><td>Log4j</td><td>44.1</td><td>========================</td><td>173%</td></tr>
  <tr><td>Message (1 params)</td><td></td><td></td><td></td><td></td></tr>
  <tr><td></td><td>Uncial</td><td>27.8</td><td>===============</td><td>109%</td></tr>
  <tr><td></td><td>Logback</td><td>54.1</td><td>=============================</td><td>213%</td></tr> <tr><td></td><td>Log4j</td><td>45.3</td><td>========================</td><td>178%</td></tr>
  <tr><td>Message (many params)</td><td></td><td></td><td></td><td></td></tr>
  <tr><td></td><td>Uncial</td><td>31.4</td><td>=================</td><td>123%</td></tr>
  <tr><td></td><td>Logback</td><td>54.9</td><td>==============================</td><td>216%</td></tr> <tr><td></td><td>Log4j</td><td>45.3</td><td>========================</td><td>178%</td></tr>
</table>
 
See [here](http://microbenchmarks.appspot.com/run/uncial.benchmark@gmail.com/net.ocheyedan.uncial.caliper.LogSystemsWithExpensiveFormatBenchmark) for full results of this test with more trials. 


SLF4J Support
--------------

Uncial natively implements the __org.slf4j.Logger__ interface and is configured [properly](http://www.slf4j.org/faq.html#slf4j_compatible) so that as long as you have the uncial jar in your classpath and use the SLF4J interfaces, you'll automatically be using Uncial as your logger (and get the speed benefits too!).
Using Uncial via SLF4J will limit you to using the SLF4J style (i.e., __{}__) parameterized logging.  Uncial is planned to be augmented in the future to also support `printf` style logging with SLF4J.

#### Obtaining a Uncial logger with SLF4J

You obtain it the same as any other SLF4J implementation:

    private static final Logger LOG = LoggerFactory.getLogger(MyClass.class);