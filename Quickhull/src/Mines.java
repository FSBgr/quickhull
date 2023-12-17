
import org.w3c.dom.ls.LSOutput;

import java.awt.color.CMMException;
import java.io.*;
import java.util.*;

import static java.lang.Math.*;


public class Mines {

    private String fileName;
    Vector start;
    Vector finish;
    private ArrayList<Vector> coords = new ArrayList<Vector>();


    public Mines(String s){
        fileName = s;
    }

    public void fileRead() throws IOException {
        File mineField = new File(fileName);
        FileReader fr=new FileReader(mineField);   //Creation of File Reader object
        BufferedReader br=new BufferedReader(fr);  //Creation of BufferedReader object
        Integer x;
        Integer y;
        boolean flag = false;
        int c = 0;
        StringBuilder k = new StringBuilder("");
        StringBuilder v = new StringBuilder("");


        while((c = br.read()) != -1)         //Read char by Char
        {
            char character = (char) c;          //converting integer to char
            if (character == ' ') {
                flag = true;
                continue;
            }

            if (character == '\n') {
                v.deleteCharAt(v.length() - 1);
                x = Integer.parseInt(k.toString());
                y = Integer.parseInt(v.toString());
                Vector<Integer> vec = new Vector<>();
                vec.add(x);
                vec.add(y);
                flag = false;
                k.delete(0, k.length());
                v.delete(0, v.length());
                coords.add(vec);
                continue;
            }

            if (flag == false) {
                k.append(character);
            } else
                v.append(character);
        }

    }

    public ArrayList<Vector> quickHull(){
        ArrayList<Vector> hull = new ArrayList<Vector>();

        if (coords.size() < 3)
            return (ArrayList<Vector>) coords.clone();

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;

        for (Vector vector : coords){
            if((int) vector.get(0) < minX){
                minX = (int) vector.get(0);
                start = vector;}


            if ((int) vector.get(0)>maxX) {
                maxX = (int) vector.get(0);
                finish = vector;
            }
        }

        hull.add(start);
        hull.add(finish);
        coords.remove(start);
        coords.remove(finish);


        ArrayList<Vector> leftSet = new ArrayList<Vector>();
        ArrayList<Vector> rightSet = new ArrayList<Vector>();

        for (Vector p : coords) {
            if (pointLocation(start, finish, p) == -1)
                leftSet.add(p);
            else if (pointLocation(start, finish, p) == 1)
                rightSet.add(p);
        }
        quickHull2(start, finish, rightSet, hull);
        quickHull2(finish, start, leftSet, hull);

        return hull;


    }

    public int distance(Vector A, Vector B, Vector C)
    {
        Integer ax = (Integer) A.get(0);
        Integer bx = (Integer) B.get(0);
        Integer ay = (Integer) A.get(1);
        Integer by = (Integer) B.get(1);
        Integer cx = (Integer) C.get(0);
        Integer cy = (Integer) C.get(1);
        int ABx = bx - ax;
        int ABy = by - ay;
        int num = ABx * (ay - cy) - ABy * (ax - cx);
        if (num < 0)
            num = -num;
        return num;
    }

    public int pointLocation(Vector A, Vector B, Vector P)
    {
        Integer ax = (Integer) A.get(0);
        Integer bx = (Integer) B.get(0);
        Integer ay = (Integer) A.get(1);
        Integer by = (Integer) B.get(1);
        Integer px = (Integer) P.get(0);
        Integer py = (Integer) P.get(1);
        int cp1 = (bx - ax) * (py - ay) - (by - ay) * (px - ax);
        if (cp1 > 0)
            return 1;
        else if (cp1 == 0)
            return 0;
        else
            return -1;
    }

    public void quickHull2(Vector A, Vector B, ArrayList<Vector> set, ArrayList<Vector> hull){
        int position = hull.indexOf(B);
        if(set.size() == 0)
            return;
        else if (set.size()==1)
        {
            Vector p = set.get(0);
            set.remove(p);
            hull.add(p);
            return;
        }
        int distanceInstance = Integer.MIN_VALUE;
        Vector pmax = null;
        for (Vector v : set) {
            int distance = distance(A,B,v);
            if(distance>distanceInstance){
                distanceInstance = distance;
                pmax = v;
            }
        }
        set.remove(pmax);
        hull.add(pmax);

        ArrayList<Vector> newLeftA = new ArrayList<Vector>();
        for (Vector M : set) {
            if (pointLocation(A, pmax, M) == 1) {
                newLeftA.add(M);
            }
        }

        ArrayList<Vector> newLeftB = new ArrayList<Vector>();
        for (Vector M : set) {
            if (pointLocation(pmax, B, M) == 1) {
                newLeftB.add(M);
            }
        }

        quickHull2(A, pmax, newLeftA, hull);
        quickHull2(pmax, B, newLeftB, hull);

    }

    public void pathFinder(){
        ArrayList<Vector> hull = quickHull();
        finish = hull.get(hull.size()-1);

        Collections.sort(hull, new Comparator<Vector>() {
            @Override
            public int compare(Vector vector, Vector t1) {
                return (int) vector.get(0) - (int) t1.get(0);
            }
        });

        ArrayList<Vector> upper = new ArrayList<Vector>();
        ArrayList<Vector> lower = new ArrayList<Vector>();

        for(Vector vector : hull){
            if((int)vector.get(1) > (int) hull.get(0).get(1))
                upper.add(vector);
            else if ((int) vector.get(1) < (int) hull.get(0).get(1))
                lower.add(vector);
            else if ((int)vector.get(1) == (int) hull.get(0).get(1) && vector!=start) {
                upper.add(vector);
                lower.add(vector);
            }
        }

        if((int)start.get(1) > (int) hull.get(hull.size()-1).get(1))
            upper.add(hull.get(hull.size()-1));
        else
            lower.add(hull.get(hull.size()-1));

        System.out.println(upper);
        System.out.println(lower);

        Vector a = start;

        double distanceUp = 0;
        double distanceDown = 0;

        for(Vector vector : upper){
            distanceUp += sqrt(pow((int)vector.get(0)-(int)a.get(0), 2) + (int) pow((int)vector.get(1)-(int)a.get(1),2));
            distanceUp = abs(distanceUp);
            a = vector;
        }

        a = start;

        for(Vector vector : lower){
            distanceDown += sqrt(pow((int)finish.get(0)-(int)lower.get(upper.size()-1).get(0), 2) + (int) pow((int)finish.get(1)-(int)lower.get(upper.size()-1).get(1),2));
            distanceDown = abs(distanceDown);
            a = vector;
        }

        System.out.println(distanceUp);
        System.out.println(distanceDown);


    }


        public static void main(String[] args) throws IOException {
            Mines mine = new Mines(args[0]);
            mine.fileRead();
            mine.pathFinder();
        }
    }
