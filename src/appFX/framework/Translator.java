package appFX.framework;

import appFX.framework.exportGates.ExportedGate;
import appFX.framework.exportGates.GateManager;
import appFX.framework.gateModels.PresetGateType;
import appSW.framework.CircuitBoard;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Translator {

   @NotNull
   public static String exportToQUIL(Project p) {
      Stream<ExportedGate> exps = null;
      try {
         exps = GateManager.exportGates(p);
      } catch (GateManager.ExportException e) {
         e.printStackTrace();
      }
      List<String> codeSegs = exps.map(Translator::genGateCode).collect(Collectors.toList());
      String code = "";
      for(String s : codeSegs)
         code += s+"\n";
      return code;
   }

   @Contract("!null -> !null")
   private static String genGateCode(ExportedGate eg) {
      if(eg == null) {
         return null;
      }
      switch(eg.getGateType()) {
         case UNIVERSAL:
            if(eg.isPresetGate()) {
               PresetGateType pgt = eg.getPresetGateType();
               switch(pgt) {
                  default: System.out.println(eg.getInputMatrixes());
               }
            }
            break;
         case POVM:
            break;
         case HAMILTONIAN:
            break;
      }
      return "TEST STRING: " + eg.toString();
   }

}
