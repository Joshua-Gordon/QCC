from pyquil.parser import parse_program
from pyquil.api import QVMConnection
qvm = QVMConnection()
p = parse_program("""
""")
print(qvm.wavefunction(p).amplitudes)
