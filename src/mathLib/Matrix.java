package mathLib;
import java.io.Serializable;
import java.util.function.Function;

import mathLib.operators.DoubleO;
import mathLib.operators.FloatO;
import mathLib.operators.IntegerO;
import mathLib.operators.Operators;


public class Matrix<T> implements Serializable {
	private static final long serialVersionUID = -5950565947565116041L;
	
//	Single Array is faster Overall
	private final T[] comps;
	private final int rows, columns;
	protected final Operators<T> o;
	
	@SuppressWarnings("unchecked")
	protected static <T> Operators<T> getOperators(T num) {
		if(num instanceof Double) {
			return (Operators<T>) new DoubleO();
		}else if(num instanceof Complex) {
			return (Operators<T>) Complex.ZERO();
		}else if(num instanceof Float) {
			return (Operators<T>) new FloatO();
		}else if(num instanceof Integer) {
			return (Operators<T>) new IntegerO();
		}else {
			throw new DefaultMatrixNotSupportedException();
		}
	}
	
	
	/**
	 * Creates a Matrix of elements
	 * 
	 * @param rows
	 * @param columns
	 * @param components a non-zero size array
	 */
	@SafeVarargs
	public Matrix(int rows, int columns, T ... components){
		o = getOperators(components[0]);
		this.comps = components;
		this.rows = rows;
		this.columns = columns;
	}
	/**
	 * Creates a Matrix of elements
	 * @param operator
	 * @param rows
	 * @param columns
	 */
	public Matrix(Operators<T> operator, int rows, int columns){
		o = operator;
		this.comps = o.mkZeroArray(rows * columns);
		this.rows = rows;
		this.columns = columns;
	}
	
	@SafeVarargs
	public Matrix(Operators<T> operator, int rows, int columns, T ... components){
		o = operator;
		this.comps = components;
		this.rows = rows;
		this.columns = columns;
	}
	
	public Matrix<T> add(Matrix<T> mat){
		Matrix<T> temp = new Matrix<T>(o, rows, columns, o.mkZeroArray(rows * columns));
		Operators<T> o = this.o.dup();
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(o.op(v(r, c)).add(mat.v(r, c)), r, c);
		return temp;
	}
	
	public Matrix<T> add(T num){
		Matrix<T> temp = new Matrix<T>(o, rows, columns, o.mkZeroArray(rows * columns));
		Operators<T> o = this.o.dup();
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(o.op(v(r, c)).add(num), r, c);
		return temp;
	}
	
	public Matrix<T> sub(Matrix<T> mat){
		Matrix<T> temp = new Matrix<T>(o, rows, columns, o.mkZeroArray(rows * columns));
		Operators<T> o = this.o.dup();
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(o.op(v(r, c)).sub(mat.v(r, c)), r, c);
		return temp;
	}
	
	public Matrix<T> sub(T num){
		Matrix<T> temp = new Matrix<T>(o, rows, columns, o.mkZeroArray(rows * columns));
		Operators<T> o = this.o.dup();
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(o.op(v(r, c)).sub(num), r, c);
		return temp;
	}
	
	public Matrix<T> mult(Matrix<T> mat){
		Matrix<T> temp = new Matrix<T>(o, rows, mat.columns, o.mkZeroArray(rows * mat.columns));
		Operators<T> o1 = this.o.dup();
		Operators<T> o2 = this.o.dup();
		T sum;
		for(int r = 0; r < rows; r++){
			for(int c = 0; c < mat.columns; c++){
				sum = o1.get0();
				for(int k = 0; k < columns; k++) {
					T m = o2.op(v(r, k)).mult(mat.v(k, c));
					sum = o1.op(sum).add(m);
				}
				temp.r(sum, r, c);
			}
		}
		return temp;
	}
	
	public Matrix<T> mult(T num){
		Matrix<T> temp = new Matrix<T>(o, rows, columns, o.mkZeroArray(rows * columns));
		Operators<T> o = this.o.dup();
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(o.op(v(r, c)).mult(num), r, c);
		return temp;
	}
	
	public Matrix<T> div(T num){
		Matrix<T> temp = new Matrix<T>(o, rows, columns, o.mkZeroArray(rows * columns));
		Operators<T> o = this.o.dup();
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(o.op(v(r, c)).div(num), r, c);
		return temp;
	}
	
	public static <T> Matrix<T> identity(Operators<T> operation, int size){
		T[] comps = operation.mkZeroArray(size * size);
		for(int i = 0; i < size; i++)
			for(int j = 0; j < size; j++) 
				comps[j * size + i] = i == j? operation.get1() : operation.get0();
		return new Matrix<>(operation, size, size, comps);
	}
	
//	private determinant()
	
	public Matrix<T> transpose(){
		Matrix<T> temp = new Matrix<T>(o, columns, rows, o.mkZeroArray(columns * rows));
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(v(r, c), c, r);
		return temp;
	}
	
	
	/**
	 * Returns the value at a specific index of this matrix
	 * @param row
	 * @param column
	 * @return The element at the specified row and column
	 */
	public T v(int row, int column){
		return comps[column + row * columns];
	}
	
	/**
	 * Replaces a value at a specific index of this matrix
	 * @param value
	 * @param row
	 * @param column
	 */
	public void r(T value, int row, int column){
		comps[column + row * columns] = value;
	}
	
	public T determinant(){
		Operators<T> o1 = this.o.dup();
		Operators<T> o2 = this.o.dup();
		Operators<T> o3 = this.o.dup();
		
		if(comps.length == 4){
			return o1.op(o2.op(v(0, 0)).mult(v(1, 1))).sub(o3.op(v(0, 1)).mult(v(1, 0)));
		}else{
			T sum = o1.get0();
			boolean negate = false;
			for(int c = 0; c < columns; c++){
				sum = o1.op(sum).add(o2.op(minor(0, c).determinant()).mult( 
						o3.op(negate? o.getn1():o.get1()).mult(v(0, c))));
				negate = !negate;
			}
			return sum;
		}
	}
	
	public Matrix<T> inverse(){
		return adjugate().div(determinant());
	}
	
	public Matrix<T> adjugate(){
		return ofCofactors().transpose();
	}
	
	
	
	public Matrix<T> ofCofactors(){
		Matrix<T> ofCofactors = ofMinors();
		Operators<T> o = this.o.dup();
		T coef;
		for(int r = 0; r < rows; r++){
			for(int c = 0; c < columns; c++){
				coef = (r+c) % 2==0 ? o.get1() : o.getn1();
				ofCofactors.r(o.op(ofCofactors.v(r, c)).mult(coef), r, c);
			}
		}
		return ofCofactors;
	}
	
	public Matrix<T> ofMinors(){
		if(comps.length == 4)
			return transpose();
		
		Matrix<T> ofMinors = new Matrix<T>(o, rows, columns, o.mkZeroArray(rows * columns));
		
		for(int r = 0; r < rows; r++)
			for(int c  = 0; c < columns; c++)
				ofMinors.r(minor(r, c).determinant(),r, c);
		
		return ofMinors;
	}
	
	
	public Matrix<T> minor(int row, int column){
		Matrix<T> minor = new Matrix<T>(o, rows - 1, columns - 1, o.mkZeroArray((rows - 1) * (columns - 1)));
		
		int rof = 0, cof; 
		
		for(int r = 0; r < rows - 1; r++){
			if(r == row)
				rof++;
			cof = 0;
			for(int c = 0; c < columns - 1; c++){
				if(c == column)
					cof++;
				minor.r(v(r + rof, c + cof), r, c);
			}
		}
		return minor;
	}
	
	public Matrix<T> kronecker(Matrix<T> mat) {
		Matrix<T> temp = new Matrix<>(o, rows * mat.rows, columns * mat.columns, o.mkZeroArray(rows * mat.rows * columns * mat.columns));
		Operators<T> o = this.o.dup();
		for(int i = 0; i < rows; i++) 
			for(int j = 0; j < columns; j++) 
				for(int k = 0; k < mat.rows; k++) 
					for(int l = 0; l < mat.columns; l++) 
						temp.r(o.op(v(i, j)).mult(mat.v(k, l)),i * mat.rows + k, j * mat.columns + l);
		return temp;
	}
	
	
	@Override
	public String toString(){
		String fs = "";
		int largestNum;
		int stSpace;
		String temp;
		
		String[] fr = new String[rows];
		
		for(int i = 0; i < fr.length; i++)
			fr[i] = "|\n";
		
		for(int c = columns - 1; c > -1; c--){
			largestNum = 0;
			for(int r = 0; r < rows; r++){
				temp = String.valueOf(v(r, c));
				fr[r] = temp.concat("    " + fr[r]);
				largestNum = fr[r].length() > largestNum? fr[r].length() : largestNum;
			}
			// fix spacing
			for(int r = 0; r < rows; r++){
				stSpace = largestNum - fr[r].length();
				temp = "";
				for(int i = 0; i < stSpace; i++)
					temp = temp.concat(" ");
				fr[r] = temp.concat(fr[r]);
			}
		}
		for(int i = 0; i < rows; i++)
			fs = fs.concat(" |    " + fr[i]);
		return fs;
	}
	
	public Matrix<T> copy(){
		Matrix<T> temp = new Matrix<T>(o, rows, columns, o.mkZeroArray(rows * columns));
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(v(r, c), r, c);
		return temp;
	}
	
	public T[] getComponents(){
		return comps;
	}
	
	public int getRows(){
		return rows;
	}
	
	public int getColumns(){
		return columns;
	}
	
	public Vector<T> toVector(){
		if(rows == 1)
			return new Vector<T>(o, false, comps);
		else if(columns == 1)
			return new Vector<T>(o, true, comps);
		else
			return null;
	}
	
	public void absorb(Matrix<T> mat){
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				r(mat.v(r, c), r, c);
	}
	
	/**
	 * getSLice
	 *  returns a submatrix indexed by row ranges and column ranges
	 * @param r1: start row index
	 * @param r2: final row index (inclusive)
	 * @param c1: start column index
	 * @param c2: final column index (inclusive)
	 * @return: submatrix indexed by rows r1 to r2 and columns c1 to c2
	 */
	public Matrix<T> getSlice( int r1, int r2, int c1, int c2 ) {
		// throw exception if r1 > r2 or c1 > c2?
		if ( r1 > r2 || c1 > c2 ) {
			return null;
		}
		
		int numRows = r2 - r1 + 1;
		int numCols = c2 - c1 + 1;
		
		int numItems = numRows * numCols;
		Matrix<T> mat = new Matrix<T>(o, numRows, numCols, o.mkZeroArray( numItems ) );
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++) {
				mat.r( this.v(r1+r, c1+c), r, c);
			}
		}
		return mat;
	}
	
	
	/**
	 * setSlice
	 *  performs a submatrix replacement
	 * @param r1: start row index
	 * @param r2: final row index (inclusive)
	 * @param c1: start column index
	 * @param c2: final column index (inclusive)
	 * @param newmat: a matrix of size (r2-r1+1) by (c2-c1+1) containing the replacement submatrix
	 * @return matrix with newmat as a submatrix
	 */
	public Matrix<T> setSlice( int r1, int r2, int c1, int c2, Matrix<T> newmat ) {
		// throw exception if r1 > r2 or c1 > c2?
		if ( r1 > r2 || c1 > c2 ) {
			return null;
		}
		
		int numRows = r2 - r1 + 1;
		int numCols = c2 - c1 + 1;
		int numItems = numRows * numCols;
		Matrix<T> mat = this.copy(); //new Matrix<T>(o, numRows, numCols, o.mkZeroArray( numItems ) );
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++) {
				mat.r( newmat.v(r1+r, c1+c), r, c);
			}
		}
		return mat;
	}
	

	public static <A, B> Matrix<B> map(Operators<B> operators, Matrix<A> m, Function<A,B> f) {
		int w = m.getRows();
		int h = m.getColumns();
		Matrix<B> newMat = new Matrix<>(operators, w,h);
		for(int x = 0; x < w; ++x) {
			for(int y = 0; y < h; ++y) {
				newMat.r(f.apply(m.v(x, y)), x, y);
			}
		}
		return newMat;
	}

	@SuppressWarnings("serial")
	private static class DefaultMatrixNotSupportedException extends RuntimeException{
		public DefaultMatrixNotSupportedException() {
			super("This Matrix is not compatible with the given type.");
		}
	}

	
}

