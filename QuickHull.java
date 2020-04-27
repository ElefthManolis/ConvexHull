import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.lang.ArrayIndexOutOfBoundsException;


public class Mines {
    public static class Point {                     // Δημιουργώ μια βοηθητική κλάση Point για να απεικονήσω καλύτερα τα σημεία με μεταβλητες την τετμημενη και την
        // και την τεταγμένη (x,y)
        public int x;
        public int y;

        public Point(int x, int y) {
            this.x = x;                             // Ο κατασκευαστής της συνάρτησης
            this.y = y;
        }
    }

    public static ArrayList<Point> quickHull(ArrayList<Point> points) {
        ArrayList<Point> convexHull1 = new ArrayList<Point>();
        ArrayList<Point> convexHull2 = new ArrayList<Point>();
        ArrayList<Point> convexHull = new ArrayList<Point>();
        if (points.size() == 3)
            return (ArrayList) points.clone();

        Point A = points.get(0);        // Ξέρουμε ότι τα δύο πρώτα σημεία είναι οι συντεταγμένες εκκίνησης και οι συντεταγμενες του θησαυρού
        Point B = points.get(1);
        convexHull.add(A);                  // Τα προσθέτω στην λίστα που δημιούργησα
        convexHull.add(B);
        points.remove(0);           // Τα αφαιρώ από την αρχική λίστα
        points.remove(1);

        ArrayList<Point> leftSet = new ArrayList<Point>();
        ArrayList<Point> rightSet = new ArrayList<Point>();

        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            if (pointLocation(A, B, p) == -1)       // Βρίσκω τα σημεία που είναι αριστερά των δύο προηγούμενων σημείων
                leftSet.add(p);
            else if (pointLocation(A, B, p) == 1)   // Βρίσκω τα σημεία που είναι δεξιά των δύο προηγούμενων σημείων
                rightSet.add(p);
        }
        hullSet(A,B,rightSet,convexHull);                       //  Πρώτα βρίσκω όλα το πολυγωνικό περιτύλιγμα
        hullSet(B,A,leftSet,convexHull);
        int temp=0;
        for(int i=0;i<convexHull.size();i++){
            if(convexHull.get(i).x==A.x && convexHull.get(i).y==A.y){       //Βρίσκω που βρίσκεται το αρχικό σημείο στην λίστα
                temp=i;
            }
        }
        for(int i=temp;i<convexHull.size();i++){
            convexHull1.add(convexHull.get(i));                     // Και τελικά διαχωρίζω τα σημεία σε αυτά που βρίσκονται κάτω από το ευθύγραμμο τμήμα
                                                                    //και σε αυτά που βρίσκονται πάνω από το ευθύγραμμο τμήμα
        }
        for(int i=temp;i>=0;i--){
            convexHull2.add(convexHull.get(i));
        }
        convexHull2.add(B);
        if(EuclideanLenghtofPath(convexHull1) > EuclideanLenghtofPath(convexHull2))
            return convexHull2;
        return convexHull1;
    }

    public static double distance(Point A, Point B, Point C) {      // Υπολογισμός της απόστασης ενός σημείου C από την ευθεία που ορίζεται από τα σημεία A και B
        int ABx = B.x - A.x;
        int ABy = B.y - A.y;
        double num = ABx * (A.y - C.y) - ABy * (A.x - C.x);
        if (num < 0)
            num = -num;
        return num;
    }

    public static void hullSet(Point A, Point B, ArrayList<Point> set,    // Τρέχει αναδρομικά το σύνολο των σημείων που βρίσκονται είτε αριστερά είτα δεξιά
                               ArrayList<Point> hull) {                     // από τα δύο αρχικά σημεία
        int insertPosition = hull.indexOf(B);
        if (set.size() == 0)
            return;
        if (set.size() == 1) {
            Point p = set.get(0);
            set.remove(p);
            hull.add(insertPosition, p);
            return;
        }
        double dist = Integer.MIN_VALUE;
        int furthestPoint = -1;
        for (int i = 0; i < set.size(); i++) {
            Point p = set.get(i);
            double distance = distance(A, B, p);
            if (distance > dist) {
                dist = distance;
                furthestPoint = i;
            }
        }
        Point P = set.get(furthestPoint);
        set.remove(furthestPoint);
        hull.add(insertPosition, P);


        ArrayList<Point> leftSetAP = new ArrayList<Point>();
        for (int i = 0; i < set.size(); i++) {
            Point M = set.get(i);
            if (pointLocation(A, P, M) == 1) {
                leftSetAP.add(M);
            }
        }

        ArrayList<Point> leftSetPB = new ArrayList<Point>();
        for (int i = 0; i < set.size(); i++) {
            Point M = set.get(i);
            if (pointLocation(P, B, M) == 1) {
                leftSetPB.add(M);
            }
        }
        hullSet(A, P, leftSetAP, hull);
        hullSet(P, B, leftSetPB, hull);
    }

    public static int pointLocation(Point A, Point B, Point P) {        // Βρίσκει εάν ένα σημείο P ανήκει αριστερά ή δεξιά των σημείων A και B
        int cp1 = (B.x - A.x) * (P.y - A.y) - (B.y - A.y) * (P.x - A.x);
        if (cp1 > 0)
            return 1;
        else if (cp1 == 0)
            return 0;
        else
            return -1;
    }

    public static double EuclideanLenghtofPath(ArrayList<Point> hull)
    {
        double lenght = 0;
        for (int i = 0; i < hull.size() - 1; i++) {
            // Εφόσον στην μεταβλητή finalPath βρίσκεται το πιο σύντομο μονοπάτι υπολογίζω την ευκλείδεια απόσταση του μονοπατιού
            lenght += Math.sqrt(Math.pow(hull.get(i).x - hull.get(i + 1).x, 2) + Math.pow(hull.get(i).y - hull.get(i + 1).y, 2));
        }
        return lenght;
    }


    public static void main(String[] args) throws IOException {
        try {
            FileInputStream fstream = new FileInputStream(args[0]);             //Διαβάζω το αρχείο arg[0] που περνά σαν όρισμα στην συναρτηση main
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            ArrayList<Point> listOfPoints = new ArrayList<>();
            String strLine;                             //Προτιμώ να διαβάσω τις γραμμές σαν string και μετά να κάνω τον διαχωρισμό των δύο αριθμών
            int i = 0;
            while ((strLine = br.readLine()) != null) {
                String[] tokens = strLine.split(" ");       //Εδώ γίνεται ο διαχωρισμός της κάθε γραμμής
                int a = Integer.parseInt(tokens[0]);
                int b = Integer.parseInt(tokens[1]);
                Point point = new Point(a, b);              //  Σε αυτό το σημείο δημιουργώ ένα αντικείμενο Point με παραμέτρους τα a και b
                listOfPoints.add(i, point);             // Τοποθετώ το σημείο που δημιούργησα στην λίστα με όλα τα σημεία που διαβάζω από το αρχείο
                i++;
            }
            ArrayList<Point> finalPath = new ArrayList<Point>();
            double minDistance = 0;
            finalPath = quickHull(listOfPoints);
            minDistance = EuclideanLenghtofPath(finalPath);

            DecimalFormat minDist = new DecimalFormat("#.#####");
            System.out.println("The shortest distance is " + minDist.format(minDistance));  // Στρογγυλοποιώ στο 5ο δεκαδικο ψηφίο
            System.out.print("The shortest path is:");
            for (int j = 0; j < finalPath.size(); j++) {
                if (j != finalPath.size() - 1)
                    System.out.print("(" + finalPath.get(j).x + "," + finalPath.get(j).y + ")" + "-->");
                else
                    System.out.print("(" + finalPath.get(j).x + "," + finalPath.get(j).y + ")");
            }
        }
        catch(IOException ioException)
        {
            System.err.println("Error opening file. Terminating.");
            System.exit(1);
        }
        catch(NoSuchElementException elementException)
        {
            System.out.println("File improperly formed. Terminating");
        }
        catch(IllegalStateException stateException)
        {
            System.err.println("Error reading from file. Terminating.");
        }
    }
}
