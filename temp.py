from pyquil.parser import parse_program
from pyquil.api import QVMConnection
qvm = QVMConnection()
p = parse_program("""H 1
H 2
X 0
Y 1
SWAP 2 4
""")
print(qvm.wavefunction(p).amplitudes)
