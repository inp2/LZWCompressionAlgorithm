/*************************************************************************
 *  Compilation:  javac LZW.java
	//command line input java LZWmod - n < input.txt > output.lzw
	//command line input java LZWmod + < output.lzw > output.txt
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Compress or expand binary input from standard input using LZW.
 *	Imani Palmer
 * 	6/28/2013
 *************************************************************************/
import java.math.*;

public class LZWmod
{
    //Number ofinput chars
    private static int R = 256;
    //The codeword with
   	private static int W = 9;
	//Number of codewords = 2^W
   	private static int L = createCodeWords(W);
   	//Set the flag
   	private static int flag = 0;
   	//Set the monitor flag
   	private static boolean monFlag = false;
   	//Compression Ratio
   	private static double compRatio  = 0;
   	private static TST<Integer> st = new TST<Integer>();
   	private static String[] string = new String[65536];
   	private static int code;
   	private static int t;
	private static String input;
	private static String s;
	private static int i;
	//Keep track of read in bits
	private static double readIn = 0.0;
	//Keep track of bits read out
	private static double readOut = 0.0;
	//Keep track of read in bits
	private static double oldReadIn = 0.0;
	//Keep track of bits read out
	private static double oldReadOut = 0.0;
	//The threshold
	private static double threshold = 0.0;
	//Old compression ratio
	private static double oldRatio = 0.0;
	//New compression ratio
	private static double newRatio = 0.0;

	//Takes W and makes the number of codewords = 2^W
   public static int createCodeWords(int codeWordInt)
   {
	   //Calculate the word 2^codeWordInt
	   double temp = Math.pow(2, codeWordInt);
	   //Convert the word to int
	   int temp1 = (int)temp;
	   //Return the int
	   return temp1;
   }//createCodewords

   //Resize the code word length to W + 1
   public static void resize()
   {
	   //If W = 16
		if(W == 16)
		{
			//Check which option
			switch(flag)
			{
				//N flag
				case 0:
					//Do nothing
					break;
				//R flag
				case 1:
					//Reset the dictionary
					reset();
					if(W != 9)
					{
						reset();
					}
					//else
					{
						i = 256;
						code = 257;
						break;
					}
				//M flag
				case 2:
				  	//Monitor
		  			//monFlag = true;
		  			//Determine oldRatio
					//Keep track of bits read in
					if (oldRatio == 0) {

						//Calculate compression ratio
						oldRatio = (oldReadIn / oldReadOut);
					} //if
					break;
			}//switch
		}//if
		else
		{
			//Increase codeword width by 1
			W = W + 1;
			//Calculate L
			L = createCodeWords(W);
		}//else
   }//resize

   public static double compareRatio(double newRatio)
   {
	   //SOLVE FOR OLD RATIO

	   //Determine threshold
	   threshold = oldRatio / newRatio;

		return threshold;
   }//compareRatio

   public static void reset()
   {
	   	 //Create a TST
		  st = new TST<Integer>();
		  //Go through all 256 input characters
		  for (int i = 0; i < R; i++)
		  {
			  //Put the string, codeword into TST
		      st.put("" + (char) i, i);
		  }//for
		  //Add the end of file flag
		  st.put("" + (char) 256, 256);
		  //Create a new string array
		  string = new String[65536];
		  //Go through all 256 input characters
		  for (i = 0; i < 256; i++)
		  {
			  string[i] = "" + (char) i;
		  }
		  //Add the end of file flag
		  string[i++] = "";
		  //i = 257;

		  //Increase codeword width by 1
		  W = 9;
		  //Calculate L
		  L = createCodeWords(W);
   }//reset

   //Compress
   public static void compress()
   {
        //Get the string from BinaryStdIn
        String input = BinaryStdIn.readString();
		BinaryStdOut.write(flag);

        //Create a TST
        st = new TST<Integer>();
        //Go through all 256 input characters
        for (int i = 0; i < R; i++)
        {
			//Put the string, codeword into TST
            st.put("" + (char) i, i);
		}//for
		// R is codeword for EOF
        code = R+1;

		//Length of string is greater than 0
        while (input.length() > 0)
        {
			//Find max prefix match s
            s = st.longestPrefixOf(input);
            //Print s's encoding
            BinaryStdOut.write(st.get(s), W);
            t = s.length();
            //Add s to symbol table
            if (t < input.length() && code < L)
            {
				st.put(input.substring(0, t + 1), code++);
			}//if
			//Scan past s in input
            //input = input.substring(t);
            //CHECK IF CODEWORD LENGTH IS FULL
			if(code == L)
			{
				resize();
			}
			if(monFlag == true)
			{
				//Keep track of bits read in
				readIn = readIn + (8 * t);
				//Keep track of bits read out
				//char [] charArray = input.substring(0, t+1).toCharArray();
				readOut = readOut + W;

				oldReadIn = oldReadIn + (8 * t);
				oldReadOut = oldReadOut + W;

				//Calculate compression ratio
				newRatio = (readIn / readOut);
				//Determine threshold
				threshold = compareRatio(newRatio);

				//If threshold exceeds 1.1
				if(threshold > 1.1)
				{
					reset();
					oldRatio = 0;
				}
			}//if
			//Scan past s in input
            input = input.substring(t);
        }//while
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    }//compress

    public static void expand()
    {
        //Make sure the array is extend to the largest value of L
        string = new String[65536];
        //Next available codeword value
		flag  = BinaryStdIn.readInt();

		if (flag == 2) {
			monFlag = true;
		} //if

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
        {
            string[i] = "" + (char) i;
		}//for
         // (unused) lookahead for EOF
        string[i++] = "";
        int codeword = BinaryStdIn.readInt(W);
        String val = string[codeword];
        while(true)
        {
            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(W);
            if(codeword == R)
            {
				break;
			}//if
            String s = string[codeword];
            if(i == codeword)
            {
				//special case hack
				s = val + val.charAt(0);
			}//if
            if (i < L)
            {
				string[i++] = val + s.charAt(0);
			}//if
            if(i == L - 1)
			{
				resize();
			}//if
			if(monFlag == true)
			{
				//Keep track of bits read in
				readIn = readIn + W;
				readOut = readOut + (8 * val.length());

				t = s.length();

				oldReadOut = oldReadOut + (8 * t);
				oldReadIn = oldReadIn + W;

				//Calculate compression ratio
				newRatio = (readOut / readIn);

				//Determine threshold
				threshold = compareRatio(newRatio);

				//If threshold exceeds 1.1
				if(threshold > 1.1)
				{
					reset();
					oldRatio = 0;
					i = 257;
				}
			}//if
			val = s;
        }//while
        BinaryStdOut.close();
    }//expand

	//Command line input java LZWmod - n < input.txt > output.lzw
	//Command line input java LZWmod + < output.lzw > output.txt
    public static void main(String[] args)
    {
        if(args[0].equals("-"))
        {
			if(args[1].equals("n"))
			{
				//This is do nothing
				flag = 0;
			}//if
			else if(args[1].equals("r"))
			{
				//This is the reset
				flag = 1;
			}//else if
			else if (args[1].equals("m"))
			{
				//This is the monitor
				flag = 2;
				monFlag = true;
			}//else if
			else throw new RuntimeException("Illegal command line argument");
			compress();
		}//if
        else if(args[0].equals("+"))
        {
			expand();
		}//else if
        else throw new RuntimeException("Illegal command line argument");
    }//if
}//main
