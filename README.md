# Recursive Descent Syntax Recogniser 

This project implements a recursive descent parser and syntax analyser in Java. Developed for the SCC312 Compilers module, it reads and checks the syntactic correctness of input programs written in a simplified language.

## 📘 Description

The system simulates a compiler's front-end with:
- Lexical analysis
- Syntax analysis using recursive descent
- Error reporting with custom exceptions

It processes `.txt` programs and reports whether they conform to the expected grammar.

## 🧠 Technologies Used

- Java SE
- Recursive descent parsing
- File I/O
- Command-line execution
- Makefile and `.bat` scripts

## 💡 Features

- Modular syntax analyser and token system
- Lexical scanner and abstract syntax tree generation
- Built-in error handling with exceptions
- Command-line build and run scripts
- Sample programs for testing

## 🗂️ Project Structure

```
SCC312 Recursive Descent Syntax Recogniser/
├── AbstractSyntaxAnalyser.java
├── AbstractGenerate.java
├── LexicalAnalyser.java
├── CompilationException.java
├── SyntaxAnalyser.java
├── Token.java
├── Compile.java
├── Generate.java
├── makefile
├── compile.bat / execute.bat / makeClean.bat
├── SCC312 Compilers CW.pdf
├── Programs Folder/         # Sample test programs
```

## 🚀 How to Compile & Run

### Using Makefile:
```bash
make        # Compile
make run    # Execute
make clean  # Clean .class files
```

### Or use batch scripts (on Windows):
- `compile.bat`
- `execute.bat`
- `makeClean.bat`

## 📜 License

This project is licensed under the MIT License. See the `LICENSE` file for details.
