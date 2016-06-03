# JArgParse - quick tutorial


## Some project description

JArgParse is a Java library for parsing command-line arguments. It's based on the
[argparse](https://docs.python.org/2.7/library/argparse.html) Python module. Example:

```python
import argparse

parser = argparse.ArgumentParser(description='Simple application on Python with command-line argument parser.',
                                 version='1.0', prog='my_cool_app')
parser.add_argument('some_value', help='some value')
parser.add_argument('optional_value', help='some optional value', nargs='?', default='default value')
parser.add_argument('values_list', help='values list', nargs='+')
parser.add_argument('-n', '--number', help='some number', metavar='SOME_NUMBER', default='1')
parser.add_argument('-V', '--verbose', help='print what the code does')
parser.add_argument('--some-long-flag', help='some long flag')
```
