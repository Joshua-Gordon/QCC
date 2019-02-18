package appFX.framework;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import appFX.framework.exportGates.ExportedGate;
import appFX.framework.exportGates.GateManager;
import appFX.framework.gateModels.PresetGateType;
import appFX.framework.exportGates.Control;
import mathLib.Complex;
import mathLib.Matrix;

public class Translator {

   private static LinkedList<String> definedGates;

   static {
      definedGates = new LinkedList<>();
   }

   //@NotNull
   public static String exportToQUIL(Project p) {
      String cbName = p.getTopLevelCircuitName();
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

   private static String genGateCode(ExportedGate eg) {
      if(eg == null) {
         return null;
      }
      switch(eg.getGateType()) {
         case UNIVERSAL:
            if(eg.isPresetGate()) {
               PresetGateType pgt = eg.getPresetGateType();
               int[] regs = eg.getGateRegister();
               String code = "";
               switch(pgt) {
                  case IDENTITY:
                     return ""; //permissible to return; control identity is not very useful
                  case HADAMARD:
                     code = "H " + regs[0];
                     break;
                  case PAULI_X:
                     code = "X " + regs[0];
                     break;
                  case PAULI_Y:
                     code = "Y " + regs[0];
                     break;
                  case PAULI_Z:
                     code = "Z " + regs[0];
                     break;
                  case CNOT:
                     code = "CNOT " + regs[0] + " " + regs[1];
                     break;
                  case SWAP:
                     code = "SWAP " + regs[0] + " " + regs[1];
                     break;
                  case TOFFOLI:
                     code = "CCNOT " + regs[0] + " " + regs[1] + " " + regs[2];
                     break;
                  case PI_ON_8:
                     code = "T " + regs[0];
                     break;
                  case PHASE:
                     code = "S " + regs[0];
                     break;
                  case MEASUREMENT:
                     code = "MEASURE " + regs[0];
                  default:
                     throw new TranslationException("Gate not implemented!");

               }
               String notBuffer = "";
               String[] split = code.split(" ");
               String gate = split[0];

               for (Control c : eg.getControls()) {
                  if(!c.getControlStatus()) {
                     notBuffer = "X " + c.getRegister() + "\n" + notBuffer;
                  }
                  gate = "CONTROLLED " + gate + " " + c.getRegister();
               }
               for(int i = 0; i < split.length-1; ++i) {
                  gate += " " + split[i+1];
               }
               if(notBuffer.length() == 0) {
                  return gate;
               }
               return notBuffer + gate + "\n" + notBuffer;
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
            System.out.println("POVM");
            return "MEASURE " + eg.getGateRegister()[0];
         case HAMILTONIAN:
            break;
      }
      return "TEST STRING: " + eg.toString();
   }

   private static String defGate(ExportedGate eg) {
      String name = eg.getGateModel().getName();
      String header = "DEFGATE " + name + ":\n";
      String body = "";
      Matrix<Complex> mat = eg.getInputMatrixes()[0]; //only one matrix for a Universal gate
      for(int i = 0; i < mat.getColumns(); ++i) {
         body += "    ";
         for(int j = 0; j < mat.getRows(); ++j) {
            body += mat.v(i,j).toString();
            body += ", ";
         }
         body += "\n";
      }
      return header+body;
   }

   public static class TranslationException extends RuntimeException {
      public TranslationException(String message) { super(message); }
   }
}

