# QCC
Quantum Cross Compiler, a tool for building and simulating quantum circuits and allowing translation across several platforms.

This is an alpha build. Please submit issues to this repository.

Features
---
1. Build quantum circuits

   QCC lets you build quantum circuits with a clean, easy to learn graphical interface. 
   You can select gates from the gate library, and then solder them onto the board using
   the solder tool.
   
2. Compile
   
   Once you have a quantum circuit designed in the board, you can open the file menu and
   select `Export` to compile to one of several quantum assembly languages. Currently
   supported are QUIL and openQASM; Quipper is currently on hold.
   
3. Decompile
   If you have code in a quantum assembly language, QCC can load it into a circuit board
   representation using the `Import` feature.
   
4. Execute
   
   QCC can execute your code in two ways. One, with an external backend such as PyQUIL or
   qiskit, and two, with it's own quantum simulator. 
   
Advanced Features
---
1. Quantum Walk
   
   QCC provides several methods for creating custom quantum gates, and one of these is a
   quantum walk. This allows for performing continuous-time computation in a quantum circuit!
   
2. Debugger

   Currently in progress. Will allow you to set breakpoints in the circuit, and view the state
   at that point in execution with the simulator.

Dependencies:
---
1. Depends on code from NIST's JAMA library.

Developers:
---
Josh Gordon, Max Cutugno, Tino Tamon.
