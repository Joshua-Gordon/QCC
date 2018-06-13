from pyquil.parser import parse_program
from pyquil.api import QVMConnection
qvm = QVMConnection()
p = parse_program("""H 2
X 0
X 1
X 2
X 3
H 1
""")
print(qvm.wavefunction(p).amplitudes)
