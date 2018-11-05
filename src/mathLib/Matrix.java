package mathLib;
import java.util.function.Function;

import mathLib.operators.ComplexO;
import mathLib.operators.DoubleO;
import mathLib.operators.FloatO;
import mathLib.operators.IntegerO;
import mathLib.operators.OperatorSet;


public class Matrix<T> extends MathValue {
	private static final long serialVersionUID = -5950565947565116041L;
	
//	Single Array is faster Overall
	private final T[] comps;
	private final int rows, columns;
	protected final OperatorSet<T> o;
	
	@SuppressWarnings("unchecked")
	protected static <T> OperatorSet<T> getOperators(T num) {
		if(num instanceof Double) {
			return (OperatorSet<T>) DoubleO.OPERATOR_SET;
		}else if(num instanceof Complex) {
			return (OperatorSet<T>) ComplexO.OPERATOR_SET;
		}else if(num instanceof Float) {
			return (OperatorSet<T>) FloatO.OPERATOR_SET;
		}else if(num instanceof Integer) {
			return (OperatorSet<T>) IntegerO.OPERATOR_SET;
		}else {
			throw new DefaultMatrixNotSupportedException();
		}
	}
	
	
	
	public static <T> Matrix<T> identity(T elementToInferOperator, int size){
		OperatorSet<T> operation = getOperators(elementToInferOperator);
		T[] comps = operation.mkZeroArray(size * size);
		for(int i = 0; i < size; i++)
			for(int j = 0; j < size; j++) 
				comps[j * size + i] = i == j? operation.get1() : operation.get0();
		return new Matrix<>(operation, size, size, comps);
	}
	
	
	public static <T> Matrix<T> identity(OperatorSet<T> operation, int size){
		T[] comps = operation.mkZeroArray(size * size);
		for(int i = 0; i < size; i++)
			for(int j = 0; j < size; j++) 
				comps[j * size + i] = i == j? operation.get1() : operation.get0();
		return new Matrix<>(operation, size, size, comps);
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
	 * @param elementToInferOperator
	 * @param rows
	 * @param columns
	 */
	public Matrix(T elementToInferOperator, int rows, int columns){
		o = getOperators(elementToInferOperator);
		this.comps = o.mkZeroArray(rows * columns);
		this.rows = rows;
		this.columns = columns;
	}
	
	/**
	 * Creates a Matrix of elements
	 * @param operatorSet
	 * @param rows
	 * @param columns
	 */
	public Matrix(OperatorSet<T> operatorSet, int rows, int columns){
		o = operatorSet;
		this.comps = o.mkZeroArray(rows * columns);
		this.rows = rows;
		this.columns = columns;
	}
	

	@SafeVarargs
	public Matrix(T elementToInferOperator, int rows, int columns, T ... components){
		o = getOperators(elementToInferOperator);
		this.comps = components;
		this.rows = rows;
		this.columns = columns;
	}
	
	
	
	@SafeVarargs
	public Matrix(OperatorSet<T> operatorSet, int rows, int columns, T ... components){
		o = operatorSet;
		this.comps = components;
		this.rows = rows;
		this.columns = columns;
	}
	
	public Matrix<T> add(Matrix<T> mat){
		Matrix<T> temp = new Matrix<T>(o, rows, columns, o.mkZeroArray(rows * columns));
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(   o.add(v(r, c)  ,  mat.v(r, c))   , r, c);
		return temp;
	}
	
	public Matrix<T> add(T num){
		Matrix<T> temp = new Matrix<T>(o, rows, columns, o.mkZeroArray(rows * columns));
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(   o.add(v(r, c)  ,   num)   , r, c);
		return temp;
	}
	
	public Matrix<T> sub(Matrix<T> mat){
		Matrix<T> temp = new Matrix<T>(o, rows, columns, o.mkZeroArray(rows * columns));
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(   o.sub(v(r, c)  ,  mat.v(r, c))  , r, c);
		return temp;
	}
	
	public Matrix<T> sub(T num){
		Matrix<T> temp = new Matrix<T>(o, rows, columns, o.mkZeroArray(rows * columns));
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(  o.sub(v(r, c)   ,  num)   , r, c);
		return temp;
	}
	
	public Matrix<T> mult(Matrix<T> mat){
		if(this.columns != mat.rows)
			throw new MatrixSizeException();
		Matrix<T> temp = new Matrix<T>(o, rows, mat.columns, o.mkZeroArray(rows * mat.columns));
		T sum;
		for(int r = 0; r < rows; r++){
			for(int c = 0; c < mat.columns; c++){
				sum = o.get0();
				for(int k = 0; k < columns; k++) {
					T m = o.mult(v(r, k)   ,  mat.v(k, c));
					sum = o.add(sum   ,   m);
				}
				temp.r(sum, r, c);
			}
		}
		return temp;
	}
	
	public Matrix<T> mult(T num){
		Matrix<T> temp = new Matrix<T>(o, rows, columns, o.mkZeroArray(rows * columns));
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(   o.mult(v(r, c)  ,   num)   , r, c);
		return temp;
	}
	
	public Matrix<T> div(T num){
		Matrix<T> temp = new Matrix<T>(o, rows, columns, o.mkZeroArray(rows * columns));
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(     o.div(v(r, c)   ,   num)   , r, c);
		return temp;
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
		if(comps.length == 4){
			
			
			return o.sub(  o.mult(v(0, 0)  ,  v(1, 1))    ,    o.mult(v(0, 1)  ,  v(1, 0)));
		}else{
			T sum = o.get0();
			boolean negate = false;
			for(int c = 0; c < columns; c++){
				sum = o.add(sum  ,  o.mult(minor(0, c).determinant() ,
						o.mult(negate? o.getn1():o.get1()  ,  v(0, c))));
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
		T coef;
		for(int r = 0; r < rows; r++){
			for(int c = 0; c < columns; c++){
				coef = (r+c) % 2==0 ? o.get1() : o.getn1();
				ofCofactors.r(o.mult(ofCofactors.v(r, c)  ,   coef), r, c);
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
		for(int i = 0; i < rows; i++) 
			for(int j = 0; j < columns; j++) 
				for(int k = 0; k < mat.rows; k++) 
					for(int l = 0; l < mat.columns; l++) 
						temp.r(o.mult(v(i, j)  ,  mat.v(k, l)),  i * mat.rows + k, j * mat.columns + l);
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
	

	public static <A, B> Matrix<B> map(B elementToInferOperator, Matrix<A> m, Function<A,B> f) {
		int w = m.getRows();
		int h = m.getColumns();
		Matrix<B> newMat = new Matrix<>(elementToInferOperator, w,h);
		for(int x = 0; x < w; ++x) {
			for(int y = 0; y < h; ++y) {
				newMat.r(f.apply(m.v(x, y)), x, y);
			}
		}
		return newMat;
	}

	@SuppressWarnings("serial")
	public static class DefaultMatrixNotSupportedException extends RuntimeException{
		public DefaultMatrixNotSupportedException() {
			super("This Matrix is not compatible with the given type.");
		}
	}

	@SuppressWarnings("serial")
	public static class MatrixSizeException extends RuntimeException{
		public MatrixSizeException() {
			super("Can not apply action with this size matrix");
		}
	}
	
}

