# forth

The start of a forth interpreter in Kotlin. 

Get started at <https://www.forth.com/starting-forth/>.

# Interpreters

## interp/Interpreter.kt

Implements a simple, single-line CLI interpreter. 

It understands the following commands in addition to standard forth inputs: 

* ops - dump the forth dictional 
* quit - exit the process

Example session: 

```agsl
forth> ."Hello, World!"
Hello, World! ok
forth> 1 2 + 3 * 4 - 5 / . 
1  ok
forth> 5 0 DO I . CR 5 0 DO I J + 5 U.R LOOP CR LOOP
0 
    0    1    2    3    4
1 
    1    2    3    4    5
2 
    2    3    4    5    6
3 
    3    4    5    6    7
4 
    4    5    6    7    8
 ok
```

## interp/SwingInterpreter.kt

Implements a Swing-based GUI for interacting with the forth Interpreter. 

