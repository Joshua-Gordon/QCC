from pyquil.parser import parse_program
from pyquil.api import QVMConnection
qvm = QVMConnection()
p = parse_program("""X 0
H 1
Z 1
""")
print(qvm.wavefunction(p).amplitudes)
