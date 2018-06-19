from pyquil.parser import parse_program
from pyquil.api import QVMConnection
qvm = QVMConnection()
p = parse_program("""H 0
""")
print(qvm.wavefunction(p).amplitudes)
