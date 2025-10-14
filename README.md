Sudoku Game (Java OOP Project)  
Overview  

This project implements a fully functional Sudoku puzzle game in Java using object-oriented design principles.  
It replaces the original chess assignment but follows the same structural and grading expectations, including modular class design, encapsulation, documentation, and command-line interaction.  

Players can load puzzles, edit cells, check solutions, and auto-solve puzzles using a recursive backtracking solver with a built-in search limit to prevent runaway recursion.  

**Features**  
- 9x9 Sudoku board managed by the Board class
- Cell objects representing each square’s value and "given" status
- Multiple puzzle seeds: easy, medium, hard, multi, and impossible
- Safe editing: prevents modification of given cells or illegal placements
- Validation for row, column, and 3x3 box conflicts
- Recursive backtracking solver (Solver) with SEARCH_LIMIT guard
- Console-based user interface (SudokuGame) with error handling and confirmation prompts

**How to Compile and Run:**  
From the project root directory:  
javac sudoku/\*.java util/\*.java  
java sudoku.SudokuGame  

**Commands Reference:**  
help - Displays available commands  
print - Prints the current Sudoku board  
set r c v - Sets a value (1–9) at row r, column c (example: set 3 4 9)  
clear r c - Clears a non-given cell (example: clear 5 6)  
load name - Loads a puzzle by name (easy, medium, hard, multi, impossible)  
load <string> - Loads from an 81-character string representation  
check - Checks whether the puzzle is solved  
solve - Automatically solves the current puzzle  
quit - Exits the program  
