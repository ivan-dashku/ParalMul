package main;


import java.util.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;


public class SparseMatrix  implements Matrix {

    public int size;
    public ConcurrentHashMap<Integer, row> map;

    public SparseMatrix(ConcurrentHashMap<Integer, row> m, int size) {
        this.size = size;
        this.map = m;
    }


    public SparseMatrix(int size) {
        this.size = size;
        this.map = new ConcurrentHashMap<>();
    }

    public SparseMatrix(BufferedReader s) {
        try {

            String temp = s.readLine();
            String[] arr = temp.split(" ");
            int k = arr.length;
            double number;
            size = k;
            map = new ConcurrentHashMap<Integer, row>();
            row tmap = new row();

            for (int j = 0; j < size; j++) {
                number = Double.parseDouble(arr[j]);
                if (number != 0.0) {
                    tmap.put(j, number);
                }
            }
            if (tmap != null) {
                map.put(0, tmap);
            }


            for (int i = 1; i < size; i++) {

                temp = s.readLine();
                arr = temp.split(" ");
                tmap = new row();
                for (int j = 0; j < size; j++) {
                    number = Double.parseDouble(arr[j]);
                    if (number != 0.0) {
                        tmap.put(j, number);
                    }
                }
                if (tmap != null) {
                    map.put(i, tmap);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Matrix mul(Matrix other) {
        if (other instanceof SparseMatrix) try {
            return this.mulSparseSparse((SparseMatrix) other);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        else return this.mulSparseDence((DenseMatrix) other);
    }
    public SparseMatrix mulSparseSparse(SparseMatrix other) throws InterruptedException {

        other = other.MatrixSTrans();
        SparseMatrix result = new SparseMatrix(size);
        Iterator<ConcurrentHashMap.Entry<Integer, row>> iter1 = this.map.entrySet().iterator();
        MulSS t = new MulSS(this.map,other.map,result.map,iter1);


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

    class MulSS implements Runnable{
        ConcurrentHashMap<Integer,row> A;
        ConcurrentHashMap<Integer,row> B;
        ConcurrentHashMap<Integer,row> result;
        Iterator<ConcurrentHashMap.Entry<Integer, row>> iter1;

        public MulSS(ConcurrentHashMap<Integer,row> A,ConcurrentHashMap<Integer,row> B,ConcurrentHashMap<Integer,row> result,Iterator<ConcurrentHashMap.Entry<Integer, row>> iter1){
            this.A = A;
            this.B = B;
            this.result = result;
            this.iter1 = iter1;
        }

        public void run() {
            while (iter1.hasNext()) {
                Map.Entry entry1 = iter1.next();
                Integer key1 = (Integer) entry1.getKey();
                HashMap<Integer, Double> value1 = (HashMap<Integer, Double>) entry1.getValue();
                Iterator<HashMap.Entry<Integer, row>> iter2 = B.entrySet().iterator();
                row resRow = new row();
                while (iter2.hasNext()) {
                    HashMap.Entry entry2 = iter2.next();
                    Integer key2 = (Integer) entry2.getKey();
                    HashMap<Integer, Double> value2 = (HashMap<Integer, Double>) entry2.getValue();
                    Iterator iterElement = value1.entrySet().iterator();
                    double resValue = 0;
                    while (iterElement.hasNext()) {
                        HashMap.Entry entryElement = (HashMap.Entry) iterElement.next();
                        Integer keyElement1 = (Integer) entryElement.getKey();
                        Double valueElement1 = (Double) entryElement.getValue();
                        if (value2.get(keyElement1) != null) {
                            double a = value2.get(keyElement1);
                            resValue = resValue + valueElement1 * a;
                        }
                    }
                    if (resValue != 0) {
                        resRow.put(key2, resValue);
                    }
                }
                if (resRow != null) {
                    result.put(key1, resRow);
                }
            }

        }
    }
    public SparseMatrix MatrixSTrans() {
        Iterator<Map.Entry<Integer, row>> iter = map.entrySet().iterator();
        ConcurrentHashMap<Integer, row> matrixTr = new ConcurrentHashMap<Integer, row>();
        while (iter.hasNext()) {
            Map.Entry entry = iter.next();
            Integer keyRow = (Integer) entry.getKey();
            HashMap<Integer, row> value = (HashMap<Integer, row>) entry.getValue();
            Iterator iterRow = value.entrySet().iterator();
            while (iterRow.hasNext()) {
                row RowTr = new row();
                Map.Entry entryRow = (Map.Entry) iterRow.next();
                Integer keyElements = (Integer) entryRow.getKey();
                Double valueElements = (Double) entryRow.getValue();
                RowTr = matrixTr.get(keyElements);
                if (RowTr == null) {
                    RowTr = new row();
                }
                RowTr.put(keyRow, valueElements);
                matrixTr.put(keyElements, RowTr);
            }

        }
        return new SparseMatrix(matrixTr, size);
    }

    public SparseMatrix mulSparseDence(DenseMatrix other) {
        SparseMatrix res = new SparseMatrix(size);
        other = other.MatrixSTrans();
        double[][] a = other.matrix;
        Iterator<Map.Entry<Integer, row>> iter1 = this.map.entrySet().iterator();
        while (iter1.hasNext()) {
            Map.Entry entry1 = iter1.next();
            Integer key1 = (Integer) entry1.getKey();
            HashMap<Integer, Double> value1 = (HashMap<Integer, Double>) entry1.getValue();
            row resRow = new row();
            for (int i = 0; i < size; i++) {
                double resValue = 0.0;
                Iterator iterElement = value1.entrySet().iterator();
                while (iterElement.hasNext()) {
                    Map.Entry entryElement = (Map.Entry) iterElement.next();
                    Integer keyElement = (Integer) entryElement.getKey();
                    Double valueElement = (Double) entryElement.getValue();
                    if (other.matrix[i][keyElement] != 0.0) {
                        resValue = resValue + valueElement * a[i][keyElement];
                    }
                }
                if (resValue != 0.0) {
                    resRow.put(i, resValue);
                }
            }
            if (resRow != null) {
                res.map.put(key1, resRow);
            }

        }

        return res;
    }


    public void mapOut(BufferedWriter sp) {
        try {
            double e;
            for (int i = 0; i < size; i++) {
                row a = map.get(i);
                if (a != null) {
                    for (int j = 0; j < size; j++) {
                        if (a.get(j) != null) {
                            e = a.get(j);
                            sp.write(e + " ");
                        } else {
                            sp.write("0.0" + " ");
                        }
                    }
                    sp.write("\n");

                } else {
                    for (int j = 0; j < size; j++) {
                        sp.write("0.0 ");
                    }
                    sp.write("\n");
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }


    public boolean equals(SparseMatrix other) {
        boolean t = true;
        for (int i = 0; i < size; i++) {
            row a = this.map.get(i);
            row b = other.map.get(i);
            for (int j = 0; j < size; j++) {
                if (a.get(j) != b.get(j)) {
                    t = false;
                }
            }
        }
        return t;
    }
}
