package mathLib;

import java.io.Serializable;

public class Matrix<T extends Scalar<T>> implements Serializable {
	private static final long serialVersionUID = -5950565947565116041L;
	
//	Single Array is faster Overall
	private final T[] comps;
	private final int rows, columns;
	protected final T m;
	
	
	@SuppressWarnings("unchecked")
	public Matrix(int rows, int columns, T ... components){
		m = (T) components[0].get0();
		this.comps = components;
		this.rows = rows;
		this.columns = columns;
	}
	
	
	public Matrix<T> add(Matrix<T> mat){
		Matrix<T> temp = new Matrix<T>(rows, columns, m.mkArray(rows * columns));
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(v(r, c).add(mat.v(r, c)), r, c);
		return temp;
	}
	
	public Matrix<T> add(T num){
		Matrix<T> temp = new Matrix<T>(rows, columns, m.mkArray(rows * columns));
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(v(r, c).add(num), r, c);
		return temp;
	}
	
	public Matrix<T> sub(Matrix<T> mat){
		Matrix<T> temp = new Matrix<T>(rows, columns, m.mkArray(rows * columns));
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(v(r, c).sub(mat.v(r, c)), r, c);
		return temp;
	}
	
	public Matrix<T> sub(T num){
		Matrix<T> temp = new Matrix<T>(rows, columns, m.mkArray(rows * columns));
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(v(r, c).sub(num), r, c);
		return temp;
	}
	
	public Matrix<T> mult(Matrix<T> mat){
		Matrix<T> temp = new Matrix<T>(rows, mat.columns, m.mkArray(rows * mat.columns));
		T sum;
		for(int r = 0; r < rows; r++){
			for(int c = 0; c < mat.columns; c++){
				sum = m.get0();
				for(int k = 0; k < columns; k++){
					sum = sum.add(v(r, k).mult(mat.v(k, c))); 
				}
				temp.r(sum, r, c);
			}
		}
		return temp;
	}
	
	public Matrix<T> mult(T num){
		Matrix<T> temp = new Matrix<T>(rows, columns, m.mkArray(rows * columns));
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(v(r, c).mult(num), r, c);
		return temp;
	}
	
	public Matrix<T> div(T num){
		Matrix<T> temp = new Matrix<T>(rows, columns, m.mkArray(rows * columns));
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(v(r, c).div(num), r, c);
		return temp;
	}
	
	public void clear() {
		for (int i = 0; i < comps.length; i++)
			comps[i] = m.get0();
	}

	public Matrix<T> identity() {
		clear();
		int loops = rows < columns ? rows : columns;
		for (int i = 0; i < loops; i++)
			comps[i * (1 + columns)] = m.get1();
		return this;
	}
	
//	private determinant()
	
	public Matrix<T> transpose(){
		Matrix<T> temp = new Matrix<T>(columns, rows, m.mkArray(columns * rows));
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(v(r, c), c, r);
		return temp;
	}
	
	public void print(){
		System.out.println(toString());
	}
	
	public T v(int row, int column){
		return comps[column + row * columns];
	}
	
	public void r(T value, int row, int column){
		comps[column + row * columns] = value;
	}
	
	public T determinant(){
		if(comps.length == 4){
			return v(0, 0).mult(v(1, 1)).sub(v(0, 1).mult(v(1, 0)));
		}else{
			T sum = m.get0();
			boolean negate = false;
			for(int c = 0; c < columns; c++){
				sum = sum.add(minor(0, c).determinant().mult( 
						(negate? m.getn1():m.get1()).mult(v(0, c))));
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
				coef = (r+c) % 2==0 ? m.get1() : m.getn1();
				ofCofactors.r(ofCofactors.v(r, c).mult(coef), r, c);
			}
		}
		return ofCofactors;
	}
	
	public Matrix<T> ofMinors(){
		if(comps.length == 4)
			return transpose();
		
		Matrix<T> ofMinors = new Matrix<T>(rows, columns, m.mkArray(rows * columns));
		
		for(int r = 0; r < rows; r++)
			for(int c  = 0; c < columns; c++)
				ofMinors.r(minor(r, c).determinant(),r, c);
		
		return ofMinors;
	}
	
	
	public Matrix<T> minor(int row, int column){
		Matrix<T> minor = new Matrix<T>(rows - 1, columns - 1, m.mkArray((rows - 1) * (columns - 1)));
		
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
		Matrix<T> temp = new Matrix<>(rows * mat.rows, columns * mat.columns, m.mkArray(rows * mat.rows * columns * mat.columns));
		for(int i = 0; i < rows; i++) 
			for(int j = 0; j < columns; j++) 
				for(int k = 0; k < mat.rows; k++) 
					for(int l = 0; l < mat.columns; l++) 
						temp.r(v(i, j).mult(mat.v(k, l)),i * mat.rows + k, j * mat.columns + l);
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
		Matrix<T> temp = new Matrix<T>(rows, columns, m.mkArray(rows * columns));
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
			return new Vector<T>(comps, false);
		else if(columns == 1)
			return new Vector<T>(comps, true);
		else
			return null;
	}
	
	public void absorb(Matrix<T> mat){
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				r(mat.v(r, c), r, c);
	}

	
	
}

