package appFX.framework;

import appFX.framework.exportGates.ExportedGate;
import appFX.framework.exportGates.GateManager;
import appFX.framework.gateModels.CircuitBoardModel;
import appFX.framework.gateModels.PresetGateType;
import appSW.framework.CircuitBoard;
import mathLib.Complex;
import mathLib.Matrix;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Translator {

   private static LinkedList<String> definedGates;

   static {
      definedGates = new LinkedList<>();
   }

   //@NotNull
   public static String exportToQUIL(Project p) {
      String cbname = p.getTopLevelCircuitName();
      Stream<ExportedGate> exps = null;
      try {
         exps = GateManager.exportGates(p);
      } catch (GateManager.ExportException e) {
         e.printStackTrace();
      }
      List<String> codeSegs = exps.map(Translator::genGateCode).collect(Collectors.toList());
      String code = "";
      for(String s : codeSegs)
         code += s.length()>0? s+"\n" : "";
      System.err.println("Compiled QUIL:\n"+code);
      return code;
   }

   //@Contract("!null -> !null")
   private static String genGateCode(ExportedGate eg) {
      if(eg == null) {
         return null;
      }
      switch(eg.getGateType()) {
         case UNIVERSAL:
            if(eg.isPresetGate()) {
               PresetGateType pgt = eg.getPresetGateType();
               int[] regs = eg.getGateRegister();
               switch(pgt) {
                  case IDENTITY:
                     return "";
                  case HADAMARD:
                     return "H " + regs[0];
                  case PAULI_X:
                     return "X " + regs[0];
                  case PAULI_Y:
                     return "Y " + regs[0];
                  case PAULI_Z:
                     return "Z " + regs[0];
                  case CNOT:
                     return "CNOT " + regs[0] + " " + regs[1];
                  case SWAP:
                     return "SWAP " + regs[0] + " " + regs[1];
                  case TOFFOLI:
                     return "CCNOT " + regs[0] + " " + regs[1] + " " + regs[2];
                  case PI_ON_8:
                     return "T " + regs[0];
                  case PHASE:
                     return "S " + regs[0];
                  default:
                     throw new TranslationException("Gate not implemented!");

               }
            }
            //eg is not a preset gate, likely multiple qubits
            String gateName = eg.getGateModel().getName();
            if(definedGates.contains(gateName)) {
               String toReturn = gateName;
               for(int i = 0; i < eg.getGateRegister().length; ++i) {
                  toReturn += " " + eg.getGateRegister()[i];
               }
               return toReturn;
            }
            //eg needs to be defined
            definedGates.add(gateName);
            return defGate(eg) + genGateCode(eg);
         case POVM:
            break;
         case HAMILTONIAN:
            break;
      }
      return "TEST STRING: " + eg.toString();
   }

   private static String defGate(ExportedGate eg) {
      String name = eg.getGateModel().getName();
      String header = "DEFGATE " + name + ":\n";
      Matrix<Complex> mat = eg.getInputMatrixes()[0]; //only one matrix for a Universal gate

      return "";
   }

   public static class TranslationException extends RuntimeException {
      public TranslationException(String message) { super(message); }
   }
}

