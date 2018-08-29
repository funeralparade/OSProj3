/*
NOTE: CLASS FUNCTIONALITY IS THE SAME AS IN CONTIGUOUS EXCEPT WHERE NOTED.  
 */
package osproj3;

import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Indexed implements Strat{
    public static String filenamereal;
    public static String filenamesim;
    public static String deletefile;
    public static String readfile;
    public static byte [][] b2 = new byte[256][512];
    @Override
    public void doOp(byte [][] disk){
        //No need to make global since the starting index will store all of the indexes for file allocation 
        //Only needs to be size 10 since that's the max number of blocks a file can take up. 
        byte [] blocklist = new byte[10];
        int blocklistcount = 0;
        boolean flag = false;
        Scanner scan = new Scanner(System.in);
        int j = 0;
        System.out.println("Copy from: ");
        filenamereal = scan.nextLine();
        System.out.println("Copy to: ");
        filenamesim = scan.nextLine();
        while(filenamesim.length()!= 8){
            System.out.println("File must be 8 characters!: ");
            filenamesim = scan.nextLine();  
        }
        File programfile = new File(filenamereal); 
        if(!programfile.exists()) { 
        //If not, print File DNE. 
        System.out.println("File DNE");
        System.exit(0);
        }
        try{
             //Create new random to generate random blocks numbers. 
             Random r = new Random();
             int numblocks = 0;
             int randomblock = 0;
             int startingblock = 0;
             int start = 0;
             String path = "C:\\Users\\jklei\\Documents\\NetBeansProjects\\OSProj3\\";
             path = path + filenamereal;
             Path location = Paths.get(path);
             byte[] bFile = Files.readAllBytes(location);
             b2[0] = disk[0];
             b2[1] = disk[1];
             //Get number of random indexes
             for(int n=0; n <= bFile.length; n++){
                 //Go through file and get the number of blocks by reading it beginning to end. 
                if(n == bFile.length){
                    numblocks++;
                    //If number is greater than 10, then quit out. 
                    if(numblocks > 10){
                                System.out.println("Number of Blocks exceeded! Quitting out...: ");
                                System.exit(0);
                            }
                    break;
                }
                if (n % 512 == 0 && n != 0){
                    numblocks++;
                       //If number is greater than 10, then quit out.
                       if(numblocks > 10){
                           System.out.println("Number of Blocks exceeded! Quitting out...: ");
                           System.exit(0);
                       }
                }
                
             }
             //Generate random indexes and put them in the block list 
             for(int m = 0; m < numblocks; m++){
                 //Generate random integer and check bitmap 
                 randomblock = r.nextInt(255) + 2; 
                 while(b2[1][randomblock] != 0){
                    randomblock = r.nextInt(255) + 2; 
                 }
                 //Add to blocklist as a byte and increment blocklistcount. 
                 blocklist[blocklistcount] = (byte)(randomblock);
                 //System.out.println(blocklist[blocklistcount]);
                 blocklistcount++;
             }
             //Get block to store random ints 
             startingblock = r.nextInt(255) + 2; 
             while(b2[1][startingblock] != 0){
                    startingblock = r.nextInt(255) + 2; 
            }
             //Set bitmap and integers in block 
            for(int i = 0; i < numblocks; i++){
                    b2[startingblock][i] = blocklist[i];
                    b2[1][startingblock] = 1;
                }
            int temp = 0;
            //Write startingblock to disk at that position. 
            disk[startingblock] = b2[startingblock];
            //Only need to loop for numblocks since we already calculated it. 
            for(int i = 0; i < numblocks; i++){
                for(int k = 0; k <= 512; k++){
                    //Read in from the duplicate array to get the blocks to add the file content to (use &0xFF) 
                    //Everything else is the same as before. 
                    start = (b2[startingblock][i]&0xFF);
                    if(j < bFile.length){
                    if(k == 512 && j < bFile.length){
                       temp = start;
                       disk[temp] = b2[temp];    
                       b2[1][temp] = 1;
                       break;
                    }
                    else if(k < 512){
                    temp = start;
                    b2[temp][k] = bFile[j];
                    j++;
                    }
                    }
                    else if(j == bFile.length ){
                        temp = start;
                    if(flag == false){
                        System.out.println(temp);
                        disk[temp] = b2[temp];    
                        b2[1][temp] = 1;
                        flag = true;
                        }
                        break;
                    }
                }
            } 
        disk[1] = b2[1];  
        //Put 0 in totalblocks in order to match filetable for Indexed. 
        updateTable(startingblock, filenamesim, 0, disk); 
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Contiguous.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Contiguous.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void updateTable(int startblock, String file, int totalblocks, byte[][]disk){
        for(int i = 0; i < 512; i++){
            if(b2[0][i] == 0){
                for(int k = 0; k < file.length(); k++){
                    b2[0][i] = (byte) file.charAt(k);
                    i++;
                }
                b2[0][i] = '\t';
                i++;
                b2[0][i] = (byte)startblock;
                i++;
                b2[0][i] = '\t';
                i++;
                b2[0][i] = (byte)totalblocks;
                i++;
                b2[0][i] = '\n';
                b2[1][0] = 1;
                disk[1] = b2[1];
                disk[0] = b2[0];
                break;
            }
            else{
                i += 12;
            }
        }
    }
    @Override
    public void deleteFile(byte[][]disk){
        int start = -1;
        int numblocks = -1;
        Scanner scan = new Scanner(System.in);
        System.out.println("Delete file: ");
        deletefile = scan.nextLine();
        for(int i = 0; i < 512; i++){
           String blockfile = "";
           for(int j = 0; j < 8; j++){
           blockfile += (char)b2[0][i]; 
           i++;
           }
           if(blockfile.equals(deletefile)){
              i++;
              start = b2[0][i]&0xFF;
              i+=2;
              //Placeholder variable in this case since numblocks is 0. 
              numblocks = b2[0][i]&0xFF;
              i++;
              int temp = i;
              for(int k = i-12; k <= temp; k++){
                  b2[0][k] = 0;
              }
              disk[0] = b2[0];
              break;
           }
           else{
                i += 4;
           }
        }
        //Set temp to 0 and set it equal to the list of indexes in the starting index. 
        int temp = 0;
        for(int i = 0; i < 512; i++){
            if(b2[start][i] != 0){
                temp = (b2[start][i]&0xFF);
                //Set bitmap. 
                b2[1][temp] = 0;
            }
            //Set bitmap for index that holds file indexes. 
            else{
                b2[1][start] = 0;
                break;
            }
        }
        disk[1] = b2[1];
        //Same as before. 
        for(int k = 0; k <= 512; k++){
            if(k == 512){
              b2[1][0] = 0;  
              disk[1] = b2[1];
              break;
            }
            else if(b2[0][k] == 0){
                continue;
            } 
            else{
                break;
            }
        }
    }
    @Override
    public void readFile(byte [][]disk){
        int start = -1;
        int numblocks = -1;
        Scanner scan = new Scanner(System.in);
        System.out.println("Read file: ");
        readfile = scan.nextLine();
        for(int i = 0; i < 512; i++){
           String blockfile = "";
           for(int j = 0; j < 8; j++){
           blockfile += (char)b2[0][i]; 
             i++;  
           }
           if(blockfile.equals(readfile)){
              i++;
              start = b2[0][i]&0xFF;
              i+=2;
              numblocks = b2[0][i]&0xFF;
              i++;
              break;
           }
           else{
                i += 4;
            }
        }
        int temp = 0;
        //Read from disk at starting index that holds the file indexes. 
        b2[start] = disk[start];
         for(int i = 0; i < 512; i++){
            //If there's an index to read from: 
            if(b2[start][i] != 0){
                //Set temp. 
                temp = (b2[start][i]&0xFF);
                for(int j = 0; j <= 512; j++){
                    //If it's the first iteration, read from disk at temp position. 
                    if(j == 0){
                        b2[temp] = disk[temp];
                    }
                    //Break at 512. 
                    if(j == 512){
                        break;
                    }
                    //Read out from duplicate array. 
                    System.out.print((char)b2[temp][j]);
                }
            }
            else{
                break;
            }
        }
        System.out.print("\n");
    }
    @Override
    public void sendFile(byte [][]disk){
        int start = -1;
        int numblocks = -1;
        Scanner scan = new Scanner(System.in);
        System.out.println("Copy from: ");
        filenamesim = scan.nextLine();
        System.out.println("Copy to: ");
        filenamereal = scan.nextLine();
        for(int i = 0; i < 512; i++){
           String blockfile = "";
           for(int j = 0; j < 8; j++){
           blockfile += (char)b2[0][i]; 
             i++;  
           }
           if(blockfile.equals(filenamesim)){
              i++;
              start = b2[0][i]&0xFF;
              i+=2;
              numblocks = b2[0][i]&0xFF;
              i++;
              break;
           }
           else{
                i += 4;
            }
        }
        try {
            PrintWriter writer = new PrintWriter(filenamereal, "UTF-8");
            int temp = 0;
            //Read from disk at starting position as before. 
            b2[start] = disk[start];
            //This part combines the above function code with the code from Contiguous. 
            for(int i = 0; i < 512; i++){
                if(b2[start][i] != 0){
                temp = (b2[start][i]&0xFF);
                    for(int j = 0; j <= 512; j++){
                    if(j == 0){
                        b2[temp] = disk[temp];
                    }
                    if(j == 512){
                        break;
                    }
                    writer.print((char)b2[temp][j]);
                    }
                }
                else{
                    break;
                }
             }
            
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Contiguous.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Contiguous.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
