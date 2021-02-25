import java.util.Scanner;

public class Kmap {

    /**
     * Turn an int into a binary number as a string
     * @param num the number to be converted to binary
     * @param length the desired length of the resulting string
     * @return a string representation of the binary number
     */
    public static String binString(int num, int length){

        String result = "";

        while(num > 0){
            result = (num%2) + result;
            num /= 2;
        }

        while(result.length() < length){
            result = "0" + result;
        }

         return result;
    }

    //Integer.parseInt(string, 2);

    /**
     * Parses through an array of Strings to convert to an array of ints
     * @param expression the users input of the expression parsed for just the integers
     * @return array of integers
     */
    public static int[] generateInts(String[] expression){

        int[] intExpression = new int[expression.length];

        for(int i = 0; i < expression.length; i++){
            intExpression[i] = Integer.parseInt(expression[i]);
        }
        return(intExpression);
    }

    /**
     * Generates gray code corresponding to num
     * @param num the n'th gray code in the sequence of gray codes
     * @return gray code
     */
    public static int generateGray(int num){
        return(num^(num>>1));
    }

    /**
     * Generates an array of all gray codes below the max
     * @param max The larges code to generate, probably a power of 2 but not necessarily
     * @return array of gray codes in order
     */
    public static int[] generateCodes(int max){
        int[] codes = new int[max];
        for(int i = 0; i < codes.length; i++){
              codes[i] = generateGray(i);
        }
        return(codes);
    }

    /**
     * Checks if an integer array contains a given integer
     * Used to go through the expression and dontCare arrays when deciding what character to fill the table with
     * @param nums the integer array to look through
     * @param num the number to look for
     * @return true if the number is found, false if the number is not in the array
     */
    public static boolean contains(int[] nums, int num){
        for(int i: nums){
            if(num == i){
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);

        String toggle;
        char form;
        char stated;
        char notStated;
        String[] stringExpression;
        int[] expression;
        int[] dontCares = {0};
        String[] dontCareString;

        if(args.length == 3){
            toggle = args[0];
            form = toggle.charAt(0);

            //to make assigning characters to table entries easier later
            stated = '0';
            notStated = '1';
            if(form == 's'){
                stated = '1';
                notStated = '0';
            }

            stringExpression = args[1].split(",");
            expression = generateInts(stringExpression);

            dontCareString = args[2].split(",");
            System.out.println(dontCareString[0]);
            if(!dontCareString[0].equals("n")){
                dontCares = generateInts(dontCareString);
            }
        }
        else{
            //Getting input
            System.out.println("Sum of Products or Product of Sums?(s/p)");
            toggle = scan.nextLine();
            form = toggle.charAt(0);

            //to make assigning characters to table entries easier later
            stated = '0';
            notStated = '1';
            if(form == 's'){
                stated = '1';
                notStated = '0';
            }

            //more user input
            if(form == 's'){
                System.out.println("Σ(comma separated please)");
            }
            else if(form == 'p'){
                System.out.println("Π(comma separated please)");
            }
            //get the integers out of the user input
            stringExpression = scan.nextLine().split(",");
            expression = generateInts(stringExpression);

            System.out.println("D(n if there are none)");

            //get the integers out of the user input, or ignore the user's lack of input
            dontCareString = scan.nextLine().split(",");
            if(!dontCareString[0].equals("n")){
                dontCares = generateInts(dontCareString);
            }
        }




        //Find the largest number in the expressions
        int max = expression[expression.length-1];
        if(dontCares[dontCares.length-1] > max){
            max = dontCares[dontCares.length-1];
        }

        //Increment map size by powers of 2 until it is large enough to take the largest int in the expression
        int mapSize = 1;
        int counter = 0;
        while(mapSize <= max){
            mapSize *= 2;
            counter ++;
        }

        //gives number of variables on the x and y dimensions of the map
        //these are also the lengths of the necessary gray codes
        int varY = counter/2;
        int varX = counter-varY;

        //gives the number of rows and columns
        int mapY = (int)Math.pow(2,(double)varY);
        int mapX = (int)Math.pow(2,(double)varX);

        //generates the gray codes as both strings and ints for the top
        String[] top = new String[mapX];
        int[] topInts = generateCodes(mapX);
        for(int i = 0; i < top.length; i++){
            top[i] = binString(topInts[i], varX);
        }

        //generates the gray codes as both strings and ints for the side
        String[] side = new String[mapY];
        int[] sideInts = generateCodes(mapY);
        for(int i = 0; i < side.length; i++){
            side[i] = binString(sideInts[i], varY);
        }

        //will hold either 0, 1, or d(D?)
        char[][] map = new char[mapY][mapX];

        //goes through the combinations of gray codes and
        //assigns mapCode entries to the correct characters based on the given expression and dont cares
        int mapCode;
        for(int i = 0; i < mapY; i++){
            for(int j = 0; j < mapX; j++){
                mapCode = Integer.parseInt(top[j] + side[i], 2);

                if(contains(expression, mapCode)){
                    map[i][j] = stated;
                }
                else if(contains(dontCares, mapCode)){
                    map[i][j] = 'D';
                }
                else{
                    map[i][j] = notStated;
                }
            }
        }

        String output = "";

        String[][] mapOut = new String[mapY][mapX];

        //to be used to divide rows from one another
        String bar = "";
        for(int i = 0; i < varY; i++){
            bar += "-";
        }
        String barSegment = "-";
        for(int i = 0; i < varX; i++){
            barSegment += "-";
        }
        for(int i = 0; i < mapX; i++){
            bar += barSegment;
        }

        //formatting the table entries to fit within their spaces and line up with the gray codes on top
        for(int i = 0; i < mapOut.length; i++){
            for(int j = 0; j < mapOut[i].length; j++){
                mapOut[i][j] = "|";
                for(int k = 0; k < varX/2; k++){
                    mapOut[i][j] += " ";
                }
                mapOut[i][j] += map[i][j];
                for(int k = mapOut[i][j].length()-1; k < varX; k++){
                    mapOut[i][j] += " ";
                }
            }
        }

        //first row is spaces then gray codes
        String firstRow = "";
        for(int i = 0; i < varY; i++){
            firstRow += " ";
        }
        for(int i = 0; i < mapX; i++){
            firstRow += "|" + top[i];
        }

        output += firstRow;

        //adds the rest of the table to output
        for(int i = 0; i < mapOut.length; i++){
            //adds dividing bar before each line
            output += "\n" + bar + "\n";
            //adds the gray code corresponding to each row
            output += side[i];
            //adds the table entries
            for(int j = 0; j < mapOut[i].length; j++){
                output += mapOut[i][j];
            }

        }
        //print the completed table
        System.out.println(output);

    }
}
