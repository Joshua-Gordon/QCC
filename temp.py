import qiskit
qp = qiskit.QuantumProgram()
name = "test"
qp.load_qasm_file("test.qasm",name=name)
if __name__ == "__main__":
   ret = qp.execute([name])
   print(ret.get_counts(name))