// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

@KBD
D = A
@SCREEN
D = D - A
@diff
M = D

(LOOP)
@i
M = 0
@KBD
D = M
@WHITE
D; JEQ
@BLACK
JMP

(WHITE)
@i
D = M
@diff
D = M - D
@LOOP
D; JEQ
@SCREEN
D = A
@i
A = D + M
M = 0
@i
M = M + 1
@WHITE
JMP

(BLACK)
@i
D = M
@diff
D = M - D
@LOOP
D; JEQ
@SCREEN
D = A
@i
A = D + M
M = -1
@i
M = M + 1
@BLACK
JMP


