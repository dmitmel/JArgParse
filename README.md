# JArgParse

## Introduction
JArgParse is a Java library for parsing command-line arguments. It's based on the Python module [`arparse`][1].

## Usage
JArgParse doesn't use de-serializing or annotations to parse arguments. To use it, you must follow this steps:

1. Make parser
2. Define parsing pattern, using pattern list in parser
3. Parse arguments/Run parser
4. PROFIT!!1111!!!!1!!1!1!1111111!!!111!!!!11!1

Now, I'll explain each step.

#### 1: Make Parser
You have to create instance of parser, which can be found in class `github.dmitmel.jargparse.ArgumentParser`:

```java
// Somewhere in imports:
import github.dmitmel.jarparse.*;

// Somewhere in code (for example, in main method):
ArgumentParser parser = new ArgumentParser("app_name", "app description")
```

#### 2: Define parsing pattern, using pattern list in parser
To define parsing pattern, you have to add some instances of `github.dmitmel.jargparse.Argument` to argument list
in parser. Parser contains public field `github.dmitmel.jargparse.ArgumentList argumentList` (Class 
`ArgumentList` is a simple list, but with some additions, like generating help info). Also, you can add them
using method `addArgument` in parser. Let's define pattern for script with this usage:

```bnf
app_name [ -f | --flag ] [ --just-long-flag ] [ -o | --option OPTION ] [ --long_option LONG_OPTION ] [REQUIRED_VALUE] [OPTIONAL_VALUE] [VALUES_LIST...]
```

Java code:

```java
parser.addArgument(new Flag("-f", "--flag", "some help for flag", "FLAG"));
parser.addArgument(new Flag(null, "--just-a-long-flag", "flag with only long name", "LONG_FLAG"));
parser.addArgument(new Option("-o", "--option", "some help for option", "OPTION", "default value"));
parser.addArgument(new Option(null, "--long-option", "option with only long name", "LONG_OPTION", "long default value"));
// It matters in which sequence you will add positionals, but it doesn't matter where you will add positionals.
// You can even add them between flags and options!
parser.addArgument(new Positional("some help for positional", "REQUIRED_VALUE"));
parser.addArgument(new Positional("positional with optional usage", "OPTIONAL_VALUE", Positional.Usage.OPTIONAL, "default value"));
parser.addArgument(new Positional("list of values", "VALUES_LIST", Positional.Usage.ZERO_OR_MORE));
```

#### 3: Parse arguments/Run parser
Parser contains method `run`, which receives array with arguments to parse and returns 
`github.dmitmel.jargparse.ParsingResult` (which is simple map, but with some additions). If there's some 
errors in parsing, it throws `github.dmitmel.jargparse.ArgumentParseException`. Example:

```java
public static void main(String[] args) {
    // Some initializations...
    
    ParsingResult result = parser.run(args);
    
    // Then, application logic
}
```

#### 4: PROFIT!!1111!!!!1!!1!1!1111111!!!111!!!!11!1
Parser not only parses arguments, but also it can generate help/usage info, convert 
`github.dmitmel.jargparse.ArgumentParseException`s to strings.

## Not supported
Here's a table of additional things that JArgParse can't do.

| Thing                                  | Example                            |
|----------------------------------------|------------------------------------|
| Parse several short flags in one token | `-abc` is equivalent to `-a -b -c` |
| Parse long options                     | `-key=value`                       |
| Parse dynamic options                  | `-Dkey=value`                      |

## License
##### Copyright (c) 2016 Meleshko Dmitriy

Licensed under the Apache License, Version 2.0. You can obtain a copy of the License at 
http://www.apache.org/licenses/LICENSE-2.0.

License isn't copyleft and it's open source. This means, that you can do anything with this code, copy 
and upgrade it. And use it in your projects.

[1]: https://docs.python.org/2.7/library/argparse.html
