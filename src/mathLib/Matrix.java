package mathLib;
import java.io.Serializable;
import java.util.function.Function;


public class Matrix<T> implements Serializable {
	private static final long serialVersionUID = -5950565947565116041L;
	
//	Single Array is faster Overall
	private final T[] comps;
	private final int rows, columns;
	protected final Scalar<T> s;
	
	
	/**
	 * Creates a Matrix of elements
	 * 
	 * @param rows
	 * @param columns
	 * @param components a non-zero size array
	 */
	@SafeVarargs
	public Matrix(Scalar<T> operation, int rows, int columns, T ... components){
		s = operation;
		this.comps = components;
		this.rows = rows;
		this.columns = columns;
	}
	/**
	 * Creates a Matrix of elements
	 * @param component dummy variable to infer type of this Matrix
	 * @param rows
	 * @param columns
	 */
	public Matrix(Scalar<T> operation, int rows, int columns){
		s = operation;
		this.comps = s.mkZeroArray(rows * columns);
		this.rows = rows;
		this.columns = columns;
	}
	
	public Matrix<T> add(Matrix<T> mat){
		Matrix<T> temp = new Matrix<T>(s, rows, columns, s.mkZeroArray(rows * columns));
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(s.dup(v(r, c)).add(mat.v(r, c)), r, c);
		return temp;
	}
	
	public Matrix<T> add(T num){
		Matrix<T> temp = new Matrix<T>(s, rows, columns, s.mkZeroArray(rows * columns));
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(s.dup(v(r, c)).add(num), r, c);
		return temp;
	}
	
	public Matrix<T> sub(Matrix<T> mat){
		Matrix<T> temp = new Matrix<T>(s, rows, columns, s.mkZeroArray(rows * columns));
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(s.dup(v(r, c)).sub(mat.v(r, c)), r, c);
		return temp;
	}
	
	public Matrix<T> sub(T num){
		Matrix<T> temp = new Matrix<T>(s, rows, columns, s.mkZeroArray(rows * columns));
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(s.dup(v(r, c)).sub(num), r, c);
		return temp;
	}
	
	public Matrix<T> mult(Matrix<T> mat){
		Matrix<T> temp = new Matrix<T>(s, rows, mat.columns, s.mkZeroArray(rows * mat.columns));
		T sum;
		for(int r = 0; r < rows; r++){
			for(int c = 0; c < mat.columns; c++){
				sum = s.get0();
				for(int k = 0; k < columns; k++){
					sum = s.dup(sum).add(s.dup(v(r, k)).mult(mat.v(k, c))); 
				}
				temp.r(sum, r, c);
			}
		}
		return temp;
	}
	
	public Matrix<T> mult(T num){
		Matrix<T> temp = new Matrix<T>(s, rows, columns, s.mkZeroArray(rows * columns));
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(s.dup(v(r, c)).mult(num), r, c);
		return temp;
	}
	
	public Matrix<T> div(T num){
		Matrix<T> temp = new Matrix<T>(s, rows, columns, s.mkZeroArray(rows * columns));
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(s.dup(v(r, c)).div(num), r, c);
		return temp;
	}
	
	public static <T> Matrix<T> identity(Scalar<T> operation, int size){
		T[] comps = operation.mkZeroArray(size * size);
		for(int i = 0; i < size; i++)
			for(int j = 0; j < size; j++) 
				comps[j * size + i] = i == j? operation.get1() : operation.get0();
		return new Matrix<>(operation, size, size, comps);
	}
	
//	private determinant()
	
	public Matrix<T> transpose(){
		Matrix<T> temp = new Matrix<T>(s, columns, rows, s.mkZeroArray(columns * rows));
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
			return s.dup(s.dup(v(0, 0)).mult(v(1, 1))).sub(s.dup(v(0, 1)).mult(v(1, 0)));
		}else{
			T sum = s.get0();
			boolean negate = false;
			for(int c = 0; c < columns; c++){
				sum = s.dup(sum).add(s.dup(minor(0, c).determinant()).mult( 
						s.dup(negate? s.getn1():s.get1()).mult(v(0, c))));
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
				coef = (r+c) % 2==0 ? s.get1() : s.getn1();
				ofCofactors.r(s.dup(ofCofactors.v(r, c)).mult(coef), r, c);
			}
		}
		return ofCofactors;
	}
	
	public Matrix<T> ofMinors(){
		if(comps.length == 4)
			return transpose();
		
		Matrix<T> ofMinors = new Matrix<T>(s, rows, columns, s.mkZeroArray(rows * columns));
		
		for(int r = 0; r < rows; r++)
			for(int c  = 0; c < columns; c++)
				ofMinors.r(minor(r, c).determinant(),r, c);
		
		return ofMinors;
	}
	
	
	public Matrix<T> minor(int row, int column){
		Matrix<T> minor = new Matrix<T>(s, rows - 1, columns - 1, s.mkZeroArray((rows - 1) * (columns - 1)));
		
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
		Matrix<T> temp = new Matrix<>(s, rows * mat.rows, columns * mat.columns, s.mkZeroArray(rows * mat.rows * columns * mat.columns));
		for(int i = 0; i < rows; i++) 
			for(int j = 0; j < columns; j++) 
				for(int k = 0; k < mat.rows; k++) 
					for(int l = 0; l < mat.columns; l++) 
						temp.r(s.dup(v(i, j)).mult(mat.v(k, l)),i * mat.rows + k, j * mat.columns + l);
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
				fr[r] = temp.concat(" " + fr[r]);
				largestNum = fr[r].length() > largestNum? fr[r].length() : largestNum;
			}
			// fix spacing
			for(int r = 0; r < rows; r++){
				stSpace = largestNum - fr[r].length();
				temp = "";
				for(int i = 0; i < stSpace; i++){
					temp = temp.concat(" ");
				}
				fr[r] = temp.concat(fr[r]);
			}
		}
		for(int i = 0; i < rows; i++)
			fs = fs.concat(" | " + fr[i]);
		return fs;
	}
	
	public Matrix<T> copy(){
		Matrix<T> temp = new Matrix<T>(s, rows, columns, s.mkZeroArray(rows * columns));
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
			return new Vector<T>(s, comps, false);
		else if(columns == 1)
			return new Vector<T>(s, comps, true);
		else
			return null;
	}
	
	public void absorb(Matrix<T> mat){
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				r(mat.v(r, c), r, c);
	}

	public static <A, B> Matrix<B> map(Scalar<B> operation, Matrix<A> m, Function<A,B> f) {
		int w = m.getColumns();
		int h = m.getRows();
		Matrix<B> newMat = new Matrix<>(operation, w,h);
		for(int x = 0; x < w; ++x) {
			for(int y = 0; y < h; ++y) {
				newMat.r(f.apply(m.v(x, y)), x, y);
			}
		}
		return newMat;
	}
	
}

