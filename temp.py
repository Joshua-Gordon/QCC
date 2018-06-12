from pyquil.parser import parse_program
from pyquil.api import QVMConnection
qvm = QVMConnection()
p = parse_program("""H 0
Z 1
CNOT 0 2
""")
print(qvm.wavefunction(p).amplitudes)
