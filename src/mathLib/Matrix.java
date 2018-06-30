package mathLib;

import java.io.Serializable;

public class Matrix<T extends Scalar<T>> implements Serializable {
	private static final long serialVersionUID = -5950565947565116041L;
	
//	Single Array is faster Overall
	private final T[] COMPS;
	private final int ROWS, COLUMNS;
	protected final T M;
	
	
	@SuppressWarnings("unchecked")
	public Matrix(int rows, int columns, T ... components){
		M = (T) components[0].get0();
		COMPS = components;
		ROWS = rows;
		COLUMNS = columns;
	}
	
	
	public Matrix<T> add(Matrix<T> mat){
		Matrix<T> temp = new Matrix<T>(ROWS, COLUMNS, M.mkArray(ROWS * COLUMNS));
		for(int r = 0; r < ROWS; r++)
			for(int c = 0; c < COLUMNS; c++)
				temp.r(v(r, c).add(mat.v(r, c)), r, c);
		return temp;
	}
	
	public Matrix<T> add(T num){
		Matrix<T> temp = new Matrix<T>(ROWS, COLUMNS, M.mkArray(ROWS * COLUMNS));
		for(int r = 0; r < ROWS; r++)
			for(int c = 0; c < COLUMNS; c++)
				temp.r(v(r, c).add(num), r, c);
		return temp;
	}
	
	public Matrix<T> sub(Matrix<T> mat){
		Matrix<T> temp = new Matrix<T>(ROWS, COLUMNS, M.mkArray(ROWS * COLUMNS));
		for(int r = 0; r < ROWS; r++)
			for(int c = 0; c < COLUMNS; c++)
				temp.r(v(r, c).sub(mat.v(r, c)), r, c);
		return temp;
	}
	
	public Matrix<T> sub(T num){
		Matrix<T> temp = new Matrix<T>(ROWS, COLUMNS, M.mkArray(ROWS * COLUMNS));
		for(int r = 0; r < ROWS; r++)
			for(int c = 0; c < COLUMNS; c++)
				temp.r(v(r, c).sub(num), r, c);
		return temp;
	}
	
	public Matrix<T> mult(Matrix<T> mat){
		Matrix<T> temp = new Matrix<T>(ROWS, mat.COLUMNS, M.mkArray(ROWS * mat.COLUMNS));
		T sum;
		for(int r = 0; r < ROWS; r++){
			for(int c = 0; c < mat.COLUMNS; c++){
				sum = M.get0();
				for(int k = 0; k < COLUMNS; k++){
					sum = sum.add(v(r, k).mult(mat.v(k, c))); 
				}
				temp.r(sum, r, c);
			}
		}
		return temp;
	}
	
	public Matrix<T> mult(T num){
		Matrix<T> temp = new Matrix<T>(ROWS, COLUMNS, M.mkArray(ROWS * COLUMNS));
		for(int r = 0; r < ROWS; r++)
			for(int c = 0; c < COLUMNS; c++)
				temp.r(v(r, c).mult(num), r, c);
		return temp;
	}
	
	public Matrix<T> div(T num){
		Matrix<T> temp = new Matrix<T>(ROWS, COLUMNS, M.mkArray(ROWS * COLUMNS));
		for(int r = 0; r < ROWS; r++)
			for(int c = 0; c < COLUMNS; c++)
				temp.r(v(r, c).div(num), r, c);
		return temp;
	}
	
	public void clear() {
		for (int i = 0; i < COMPS.length; i++)
			COMPS[i] = M.get0();
	}

	public Matrix<T> identity() {
		clear();
		int loops = ROWS < COLUMNS ? ROWS : COLUMNS;
		for (int i = 0; i < loops; i++)
			COMPS[i * (1 + COLUMNS)] = M.get1();
		return this;
	}
	
//	private determinant()
	
	public Matrix<T> transpose(){
		Matrix<T> temp = new Matrix<T>(COLUMNS, ROWS, M.mkArray(COLUMNS * ROWS));
		for(int r = 0; r < ROWS; r++)
			for(int c = 0; c < COLUMNS; c++)
				temp.r(v(r, c), c, r);
		return temp;
	}
	
	public void print(){
		System.out.println(toString());
	}
	
	public T v(int row, int column){
		return COMPS[column + row * COLUMNS];
	}
	
	public void r(T value, int row, int column){
		COMPS[column + row * COLUMNS] = value;
	}
	
	public T determinant(){
		if(COMPS.length == 4){
			return v(0, 0).mult(v(1, 1)).sub(v(0, 1).mult(v(1, 0)));
		}else{
			T sum = M.get0();
			boolean negate = false;
			for(int c = 0; c < COLUMNS; c++){
				sum = sum.add(minor(0, c).determinant().mult( 
						(negate? M.getn1():M.get1()).mult(v(0, c))));
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
		for(int r = 0; r < ROWS; r++){
			for(int c = 0; c < COLUMNS; c++){
				coef = (r+c) % 2==0 ? M.get1() : M.getn1();
				ofCofactors.r(ofCofactors.v(r, c).mult(coef), r, c);
			}
		}
		return ofCofactors;
	}
	
	public Matrix<T> ofMinors(){
		if(COMPS.length == 4)
			return transpose();
		
		Matrix<T> ofMinors = new Matrix<T>(ROWS, COLUMNS, M.mkArray(ROWS * COLUMNS));
		
		for(int r = 0; r < ROWS; r++)
			for(int c  = 0; c < COLUMNS; c++)
				ofMinors.r(minor(r, c).determinant(),r, c);
		
		return ofMinors;
	}
	
	
	public Matrix<T> minor(int row, int column){
		Matrix<T> minor = new Matrix<T>(ROWS - 1, COLUMNS - 1, M.mkArray((ROWS - 1) * (COLUMNS - 1)));
		
		int rof = 0, cof; 
		
		for(int r = 0; r < ROWS - 1; r++){
			if(r == row)
				rof++;
			cof = 0;
			for(int c = 0; c < COLUMNS - 1; c++){
				if(c == column)
					cof++;
				minor.r(v(r + rof, c + cof), r, c);
			}
		}
		return minor;
	}
	
	@Override
	public String toString(){
		String fs = "";
		int largestNum;
		int stSpace;
		String temp;
		
		String[] fr = new String[ROWS];
		
		for(int i = 0; i < fr.length; i++)
			fr[i] = "|\n";
		
		for(int c = COLUMNS - 1; c > -1; c--){
			largestNum = 0;
			for(int r = 0; r < ROWS; r++){
				temp = String.valueOf(v(r, c));
				fr[r] = temp.concat(" " + fr[r]);
				largestNum = fr[r].length() > largestNum? fr[r].length() : largestNum;
			}
			// fix spacing
			for(int r = 0; r < ROWS; r++){
				stSpace = largestNum - fr[r].length();
				temp = "";
				for(int i = 0; i < stSpace; i++){
					temp = temp.concat(" ");
				}
				fr[r] = temp.concat(fr[r]);
			}
		}
		for(int i = 0; i < ROWS; i++)
			fs = fs.concat(" | " + fr[i]);
		return fs;
	}
	
	public Matrix<T> copy(){
		Matrix<T> temp = new Matrix<T>(ROWS, COLUMNS, M.mkArray(ROWS * COLUMNS));
		for(int r = 0; r < ROWS; r++)
			for(int c = 0; c < COLUMNS; c++)
				temp.r(v(r, c), r, c);
		return temp;
	}
	
	public T[] getComponents(){
		return COMPS;
	}
	
	public int getRows(){
		return ROWS;
	}
	
	public int getColumns(){
		return COLUMNS;
	}
	
	public Vector<T> toVector(){
		if(ROWS == 1)
			return new Vector<T>(COMPS, false);
		else if(COLUMNS == 1)
			return new Vector<T>(COMPS, true);
		else
			return null;
	}
	
	public void absorb(Matrix<T> mat){
		for(int r = 0; r < ROWS; r++)
			for(int c = 0; c < COLUMNS; c++)
				r(mat.v(r, c), r, c);
	}

	
	
}

