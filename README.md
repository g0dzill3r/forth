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
forth> 5 0 DO I . .":= " 5 0 DO I J + 5 U.R LOOP CR LOOP
0 :=     0    1    2    3    4
1 :=     1    2    3    4    5
2 :=     2    3    4    5    6
3 :=     3    4    5    6    7
4 :=     4    5    6    7    8
 ok
```

## interp/SwingInterpreter.kt

Implements a Swing-based GUI for interacting with the forth Interpreter. 

# Examples

## General Purpose

### CR - carriage return

```agsl
forth> : COUNT ."One" CR ."Two" CR ."Three" ; 
 ok
forth> COUNT
One
Two
Three ok
```

### EMIT

```agsl
forth> : STAR 42 EMIT ; 
 ok
forth> : STARS 0 DO STAR LOOP ; 
 ok
forth> 5 STARS
***** ok
```

### QUIT

```agsl
forth> QUIT
Process finished with exit code 255
```

### ABORT 

```agsl
forth> : FOO BAR ; 
 ok
forth> : BAR BAZ ; 
 ok
forth> : BAZ ABORT ."Ooopsie!" ;
 ok
forth> FOO
forth> java.lang.Exception: ABORT: Ooopsie!
```

### FORGET 

```agsl
forth> : FOO ."First one!" ; 
 ok
forth> FOO
First one! ok
forth> : FOO ."Second one!" ; 
 ok
forth> FOO
Second one! ok
forth> FORGET FOO 
 ok
forth> FOO
First one! ok
```

## Stack Operations

### . 

```agsl
forth> 1 2 3 . . . 
3 2 1  ok
```

### .S

```agsl
forth> 1 2 3 .S
<3> 1 2 3  ok
```

### DUP 

```agsl
forth> 123 DUP DUP .S
<3> 123 123 123  ok
```
### ROT

```agsl
forth> 10 20 30 .S CR ROT .S
<3> 10 20 30 
<3> 20 30 10  ok
```
### OVER

```agsl
forth> 100 200 .S CR OVER .S
<2> 100 200 
<3> 100 200 100  ok
```

### DROP

```agsl
forth> 1 2 3 DROP .S
<2> 1 2  ok
```

### 2SWAP 

```agsl
forth> 1 2 3 4 .S CR 2SWAP .S
<4> 1 2 3 4 
<4> 3 4 1 2  ok
```

### 2DUP

```agsl
forth> 1 2 .S CR 2DUP .S
<2> 1 2 
<4> 1 2 1 2  ok
```

### 2OVER

```agsl
forth> 1 2 3 4 .S CR 2OVER .S
<4> 1 2 3 4 
<6> 1 2 3 4 1 2  ok
```

### 2DROP

```agsl
forth> 1 2 3 4 .S CR 2DROP .S
<4> 1 2 3 4 
<2> 1 2  ok
```
## The Return Stack

### .R - dump the return stack

```agsl
forth> 1 2 3 4 5 >R >R >R >R >R .R
5 4 3 2 1  ok
```

### \>R

```agsl
forth> 1 2 3 .S
<3> 1 2 3  ok
forth> >R >R .R
3 2  ok
forth> .S
<1> 1  ok
```

### R>

```agsl
forth> 1 2 3 >R >R >R .R
3 2 1  ok
forth> .S
<0> EMPTY ok
forth> R> R> .S
<2> 1 2  ok
forth> .R
3  ok
```

### @R

```agsl
forth> CLEAR
 ok
forth> 12345 >R .R
12345  ok
forth> .S
<0> EMPTY ok
forth> @R
 ok
forth> .R
12345  ok
forth> .S
<1> 12345  ok
```

## Math

### + 

```agsl
forth> 12 24 + . 
36  ok
```

### - 

```agsl
forth> 24 12 - . 
12  ok
```
### * 

```agsl
forth> 12 10 * . 
120  ok
```

### / 

```agsl
forth> 12 4 / . 
3  ok
```

### NEGATE

```agsl
forth> 100 DUP . NEGATE . 
100 -100  ok
```
### MIN

```agsl
forth> -10 10 MIN . 
-10  ok
```
### MAX

```agsl
forth> -10 10 MAX . 
10  ok
```
### MOD

```agsl
forth> 10 3 MOD . 
1  ok
```
### /MOD

```agsl
forth> 10 3 /MOD . . 
3 1  ok
```

### UR

```agsl
forth> : OUTPUT 5 U.R ; 
 ok
forth> 1 3 5 7 13 5 0 DO OUTPUT LOOP
   13    7    5    3    1 ok
```

### UL

```agsl
forth> : OUTPUT 5 U.L ; 
 ok
forth> 1 3 5 7 13 5 0 DO OUTPUT LOOP
13   7    5    3    1     ok
```

## Conditionals

### IF

```agsl
forth> 5 5 = IF ."EQUALS!" ELSE ."NOT EQUALS!" THEN
EQUALS! ok
```

### = 

```agsl
forth> 1 1 + 1 1 + = . 
-1  ok
forth> 10 -10 = . 
0  ok
```

### <> 

```agsl
forth> 5 5 <> .
0  ok
forth> 5 4 <> .
-1  ok
```

### \<

```agsl
forth> 1 2 < .
-1  ok
forth> 2 1 < .
0  ok
```

### \> 

```agsl
forth> 1 2 > . 
0  ok
forth> 2 1 > . 
-1  ok
```


### 0=

```agsl
forth> 0 0= . 
-1  ok
forth> 1 0= . 
0  ok
```

### 0<

```agsl
forth> 1 0< . 
0  ok
forth> -1 0< .
-1  ok
```

### 0> 

```agsl
forth> 1 0> . 
-1  ok
forth> -1 0> . 
0  ok
```

### AND

```agsl
forth> TRUE TRUE AND . 
-1  ok
forth> TRUE FALSE AND . 
0  ok
forth> BINARY 1011011 1101110 AND . 
1001010  ok
```

### OR

```agsl
forth> FALSE FALSE OR . 
0  ok
forth> FALSE TRUE OR . 
-1  ok
forth> BINARY 10000 01111 OR . 
11111  ok
```

### INVERT

```agsl
forth> TRUE DUP . INVERT DUP . INVERT DUP . 
-1 0 -1  ok
```

### ?DUP

```agsl
forth> 1 .S CR ?DUP .S
<1> 1 
<2> 1 1  ok
forth> CLEAR 0 .S CR ?DUP .S
<1> 0 
<1> 0  ok
```

## Looping

### DO 

```agsl
forth> 5 0 DO I . CR LOOP
0 
1 
2 
3 
4 
 ok
forth> -5 0 DO I . CR -1 +LOOP
0 
-1 
-2 
-3 
-4 
-5 
 ok
```

### BEGIN 

BEGIN ... AGAIN syntax

```agsl
forth> 1 BEGIN
  DUP . 
  1 + 
  DUP 10 > IF LEAVE THEN
  AGAIN
1 2 3 4 5 6 7 8 9 10 ok
```

BEGIN ... UNTIL syntax

```agsl
forth> 1 BEGIN
  DUP . 
  1 + 
DUP 5 > UNTIL 
1 2 3 4 5 ok
```

BEGIN ... WHILE ... REPEAT syntax

```agsl
forth> 1 BEGIN
    DUP . 
    1 + 
  DUP 5 < WHILE
    .", " 
  REPEAT
1 , 2 , 3 , 4 ok  
```

### LEAVE

```agsl
forth> 10 0 DO 
  I . 
  I 5 > IF LEAVE THEN
LOOP
0 1 2 3 4 5 6 ok
```