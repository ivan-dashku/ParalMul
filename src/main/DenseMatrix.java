package main;
import java.lang.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DenseMatrix implements Matrix {
    public int size;
    public double matrix[][];


    public DenseMatrix(double[][] matrix, int size) {
        this.size = size;
        this.matrix = matrix;
    }

    public DenseMatrix(int size) {
        this.matrix = new double[size][size];
        this.size = size;
    }

    public DenseMatrix(BufferedReader s) {
        try {
            String t = s.readLine();
            String[] array = t.split(" ");
            int k = array.length;
            this.size = k;
            this.matrix = new double[size][size];
            double number;

            for (int j = 0; j < k; j++) {
                number = Double.parseDouble(array[j]);
                this.matrix[0][j] = number;
            }

            for (int i = 1; i < k; i++) {
                t = s.readLine();
                array = t.split(" ");

                for (int j = 0; j < k; j++) {
                    number = Double.parseDouble(array[j]);
                    this.matrix[i][j] = number;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Matrix mul(Matrix other) {
        if (other instanceof DenseMatrix) try {
            return this.mulDenseDense((DenseMatrix) other);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        else return this.mulDenceSparse((SparseMatrix) other);
    }


    public DenseMatrix mulDenseDense(DenseMatrix other) throws InterruptedException {
        other = other.MatrixSTrans();
        DenseMatrix result = new DenseMatrix(size);
        mulDD t = new mulDD(this.matrix,other.matrix,result.matrix);
        Thread t1 = new Thread(t);
        Thread t2 = new Thread(t);
        Thread t3 = new Thread(t);
        Thread t4 = new Thread(t);

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t1.join();
        t2.join();
        t3.join();
        t4.join();
        return result;
    }
    public class mulDD implements Runnable {
        double[][] A;
        double[][] B;
        double[][] result;
        int num = 0;

        public mulDD(double[][] A, double[][] B, double[][] result) {
            this.A = A;
            this.B = B;
            this.result = result;
        }

        public void run() {
            for (int i = next(); i < size; i = next()) {
                for (int j = 0; j < size; j++) {
                    for (int k = 0; k < size; k++) {
                        result[i][j] += A[i][k] * B[j][k];
                    }
                }

            }
        }
        public int next(){
            synchronized (this) {
                return num++;
            }
        }
    }


    public SparseMatrix mulDenceSparse(SparseMatrix other) {
        other = other.MatrixSTrans();
        SparseMatrix res = new SparseMatrix(size);
        for (int i = 0; i < size; i++) {
            row resRow = new row();
            Iterator<Map.Entry<Integer, row>> iter1 = other.map.entrySet().iterator();// итератор строк
            while (iter1.hasNext()) {
                Map.Entry entry1 = iter1.next();
                Integer key1 = (Integer) entry1.getKey();// ключ строки
                HashMap<Integer, Double> value1 = (HashMap<Integer, Double>) entry1.getValue();// сама строка
                Iterator iterElement = value1.entrySet().iterator();// итератор элементов
                double resValue = 0;
                while (iterElement.hasNext()) {
                    Map.Entry entryElement = (Map.Entry) iterElement.next();
                    Integer keyElement = (Integer) entryElement.getKey();// ключ элемента
                    Double valueElement = (Double) entryElement.getValue();//значение элемента
                    resValue = resValue + this.matrix[i][keyElement] * valueElement;
                }
                if (resValue != 0) {
                    resRow.put(key1, resValue);
                }
            }
            if (resRow != null) {
                res.map.put(i, resRow);
            }
        }
        return res;
    }


    public DenseMatrix MatrixSTrans() {
        double[][] mTr = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = i; j < size; j++) {
                double aT = this.matrix[i][j];
                mTr[i][j] = this.matrix[j][i];
                mTr[j][i] = aT;
            }
        }
        return new DenseMatrix(mTr, size);
    }


    public void matOut(BufferedWriter dn) {
        try {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    dn.write(matrix[i][j] + " ");

                }
                dn.write("\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean equals(Object o) {
        boolean t = true;
        if (!(o instanceof DenseMatrix)) {
            return false;
        }
        DenseMatrix other = (DenseMatrix) o;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (this.matrix[i][j] != other.matrix[i][j]) {
                    t = false;
                }
            }
        }
        return t;
    }
}
