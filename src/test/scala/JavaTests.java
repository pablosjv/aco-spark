/**
 * Created by Pablo on 15/6/17.
 */

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import java.io.IOException;
import java.util.*;

public class JavaTests {

    public static void main(String args[]) throws IOException {
        new JavaTests().arrayListToArray();
    }


    void arrayListToArray(){
        List<String> myArrayList = new ArrayList<String>();
        myArrayList.add("Hola");
        myArrayList.add("Adios");
        myArrayList.add("Joder");
        myArrayList.add("Puta");
        myArrayList.add("Perra");

        System.out.println(myArrayList);
        String[] myArray = myArrayList.toArray(new String[myArrayList.size()]);

        for (int i = 0; i < myArray.length; i++) {
            System.out.print(myArray[i] + " ");
        }
    }

    public class DumObject{
        public int lol;
        public String message;

        public DumObject(int lol, String message){
            this.lol = lol;
            this.message = message;
        }

        @Override
        public String toString() {
            String result = message + ": " + lol;
            return result;
        }
    }
    void referenceListTest(){
        List<DumObject> lista = new ArrayList<DumObject>();
        DumObject objeto;
        for (int i = 0; i < 5; i++) {
            objeto = new DumObject(i, "Message number " + i);
            lista.add(objeto);
        }

        Iterator<DumObject> iterator = lista.iterator();

        DumObject dumObject1;
        while(iterator.hasNext()){
            dumObject1 = iterator.next();
            System.out.println(dumObject1);
        }
    }
    void randomTests(){
        Random generator = new Random();
        Double random;
        for (int i = 0; i < 10; i++) {
            random = generator.nextDouble();
            System.out.println(random);
        }
    }

    void chocoTest() {
        //---------------- CHOCO SOLVER TESTS --------------------

        //two different integers, a and b, must satisfy the following equation : a + b < 8
        Model model = new Model("Choco Solver Hello World");
        // Integer variables
        IntVar a = model.intVar("a", 0, 10);
        IntVar b = model.intVar("b", 0, 10);
        // Add an arithmetic constraint between a and b
        model.arithm(a, "+", b, "<", 8).post();

        Scanner reader = new Scanner(System.in);  // Reading from System.in

        int nA;
        int nB;
        boolean finish = false;
        Solver solver = model.getSolver();
        BoolVar r1;
        BoolVar r2;
        BoolVar rNotEqual;


        try {
            solver.propagate();

        } catch (ContradictionException e) {
            System.out.println("---------- THERE WAS A CONTRADICTION EXCEPTION ----------");
            e.printStackTrace();
        }

        while (!finish) {

            System.out.println("Enter two numbers: ");
            nA = reader.nextInt();
            nB = reader.nextInt();


            r1 = model.arithm(a, "=", nA).reify();
            r2 = model.arithm(b, "=", nB).reify();
            rNotEqual = model.arithm(a, "!=", b).reify();

            System.out.println("Before propagate");

            System.out.println("a = " + nA + " => " + r1.getBooleanValue());
            System.out.println("b = " + nB + " => " + r2.getBooleanValue());
            System.out.println("a != b => " + rNotEqual.getBooleanValue());

            try {
                solver.propagate();

            } catch (ContradictionException e) {
                System.out.println("---------- THERE WAS A CONTRADICTION EXCEPTION ----------");
                e.printStackTrace();
            }
            System.out.println("After propagate");

            System.out.println("a = " + nA + " => " + r1.getBooleanValue());
            System.out.println("b = " + nB + " => " + r2.getBooleanValue());
            System.out.println("a != b => " + rNotEqual.getBooleanValue());

            System.out.println("Solve: " + solver.solve());

            System.out.println("After solve");

            System.out.println("a = " + nA + " => " + r1.getBooleanValue());
            System.out.println("b = " + nB + " => " + r2.getBooleanValue());
            System.out.println("a != b => " + rNotEqual.getBooleanValue());


        }

    }

    void commentedTest(){
        //      /*Creation of ArrayList: I'm going to add String
//       *elements so I made it of string type */
//        ArrayList<ArrayList<Integer>> obj = new ArrayList<ArrayList<Integer>>();
//
//	  /*This is how elements should be added to the array list*/
//	    obj.add(new ArrayList<Integer>());
//        obj.get(0).add(10);
//        obj.get(0).add(11);
//        obj.get(0).add(12);
//        obj.get(0).add(13);
//        obj.get(0).add(14);
//
//	  /* Displaying array list elements */
//        System.out.println("Currently the array list has following elements:" + obj);
//
//	  /*Add element at the given index*/
//        obj.get(0).add(0, 20);
//        obj.get(0).add(1, 21);
//
//	  /*Remove elements from array list like this*/
//        obj.get(0).remove((Integer)10);
//        obj.get(0).remove((Integer)11);
//
//        System.out.println("Current array list is:" + obj);
//
//	  /*Remove element from the given index*/
//        obj.remove(0);
//
//        System.out.println("Current array list is:" + obj);

//        int[][] arrayMulti = new int[2][];
//
//        String[] arrayUni1 = new String[3];
//        int[] arrayUni2 = new int[4];
//
//        for (int i = 0; i < arrayUni1.length; i++) {
//            arrayUni1[i] = "hola" + i;
//            System.out.println(arrayUni1[i]);
//        }
//
//        for (int i = 0; i < arrayUni2.length; i++) {
//            arrayUni2[i] = i * 100;
//        }

//        arrayMulti[0] = arrayUni1;
//        arrayMulti[1] = arrayUni2;
//        for(int i = 0; i<arrayMulti.length; i++ ){
//            for(int j = 0; j<arrayMulti[i].length; j++ ) {
//                System.out.print(arrayMulti[i][j]);
//            }
//            System.out.println("\n--------------------");
//        }
//        System.out.print(Arrays.asList(arrayUni1).indexOf("hola" + 0));

//        System.out.println(arrayMulti[0][3]);
//        System.out.println(arrayMulti[1][3]);

    }
}

