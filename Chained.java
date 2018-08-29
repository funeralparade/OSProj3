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
public class Chained implements Strat{
    public static String filenamereal;
    public static String filenamesim;
    public static String deletefile;
    public static String readfile;
    public static byte [][] b2 = new byte[256][512];
    //Blocklist in order to keep track of blocks visited randomly. 
    //Blocklistcount is used in order to keep track of the number of blocks visited. 
    public static int [] blocklist = new int[1000];
    public static int blocklistcount = 0;
    @Override
    public void doOp(byte [][] disk){
        Scanner scan = new Scanner(System.in);
        int j = 0;
        boolean flag = false;
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
             //Create a random to generate random block numbers. 
             Random r = new Random();
             int numblocks = 0;
             int blockstraversed = 1;
             int start = 0;
             int startingblock = 0;
             String path = "C:\\Users\\jklei\\Documents\\NetBeansProjects\\OSProj3\\";
             path = path + filenamereal;
             Path location = Paths.get(path);
             byte[] bFile = Files.readAllBytes(location);
             b2[0] = disk[0];
             b2[1] = disk[1];
             int temp = 0;
                    for(int i = 2; i < 256; i++){
 
                        for(int k = 0; k <= 512; k++){
                        
                        if(j < bFile.length){
                            //Get subsequent blocks 
                            if(k == 512){
                                temp = start;
                              if(j < bFile.length && b2[1][temp] == 0){
                                 disk[temp] = b2[temp]; 
                                 b2[1][temp] = 1;
                                 blockstraversed++;
                                 if(blockstraversed > 10){
                                    System.out.println("Number of Blocks exceeded! Quitting out...: ");
                                    System.exit(0);
                                }
                              }
                            //Set start equal to a random integer from 0 to 255 plus 2 (since 0 and 1 are reserved). Check
                            //to make sure it hasn't been visited already. 
                            start = r.nextInt(255) + 2; 
                            while(b2[1][start] != 0){
                             start = r.nextInt(255) + 2;
                            }
                            //Add that block to the blocklist and increment the counter. 
                            blocklist[blocklistcount] = start;
                            blocklistcount++;
                            //If the blocklistcount gets to 1000, then quit out since the array size has been exceeded. 
                            if(blocklistcount == 1000){
                                System.out.println("Number of Blocks exceeded! Quitting out...: ");
                                System.exit(0);
                            }
                            break;
                            }
                            //Get the first block 
                            else if(k == 0 && i==2){
                            //Set start equal to a random integer from 0 to 255 plus 2 (since 0 and 1 are reserved). Check
                            //to make sure it hasn't been visited already. 
                            start = r.nextInt(255) + 2;
                            while(b2[1][start] != 0){
                             start = r.nextInt(255) + 2;
                            }
                            //Get the first start block in order to use it for the filetable. 
                            startingblock = start; 
                            //Add that block to the blocklist and increment the counter. 
                            blocklist[blocklistcount] = start;
                            blocklistcount++;
                            //If the blocklistcount gets to 1000, then quit out since the array size has been exceeded. 
                            if(blocklistcount == 1000){
                                System.out.println("Number of Blocks exceeded! Quitting out...: ");
                                System.exit(0);
                            }
                            }
                            
                            else{
                                if(numblocks == 0){
                                    numblocks = -1;
                                }
                                temp = start;
                                b2[temp][k] = bFile[j];
                                j++;
                            }
                            }
                          if(j == bFile.length ){
                              temp = start;
                              if(flag == false){
                                  disk[temp] = b2[temp];  
                                  b2[1][temp] = 1;
                                  numblocks =  blockstraversed;
                                  flag = true;
                              }
                              break;
                          }
                        
                    }
            }  
        disk[1] = b2[1];  
        //Update filetable with startingblock isntead of start. 
        updateTable(startingblock, filenamesim, numblocks, disk); 
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
        int tempblockcount = 0;
        int start = -1;
        int numblocks = -1;
        Scanner scan = new Scanner(System.in);
        System.out.println("Delete file: ");
        deletefile = scan.nextLine();
        for(int i = 0; i < 512; i++){
            if(i == 512){
                   System.exit(0);
               }
           String blockfile = "";
           for(int j = 0; j < 8; j++){
               if(i == 512){
                   System.exit(0);
               }
           blockfile += (char)b2[0][i]; 
           i++;
           }
           if(blockfile.equals(deletefile)){
              i++;
              start = b2[0][i]&0xFF;
              i+=2;
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
        //Set temp equal to 0. 
        int temp = 0;
        //For the length of the block list, get the starting block. 
        for(int i = 0; i < blocklist.length; i++){
            //If the blocklist entry is equal to start, set temp equal to that entry and 
            //read the blockcount into a temporary variable. 
            if(blocklist[i] == start){
                temp = blocklist[i];
                tempblockcount = i;
                break;
            }
        }
        //For numblocks, set the bitmap equal to 0 at those points. increment the temporaryblockcount and set temp equal
        //to the blocklist at that counter. 
        for(int j = 0; j < numblocks; j++){
           b2[1][temp] = 0;
           tempblockcount++;
           temp = blocklist[tempblockcount];
        }
        //Set bitmap as before. 
        disk[1] = b2[1];
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
        int tempblockcount = 0;
        int numblocks = -1;
        Scanner scan = new Scanner(System.in);
        System.out.println("Read file: ");
        readfile = scan.nextLine();
        for(int i = 0; i < 512; i++){
            if(i == 512){
                   System.exit(0);
               }
           String blockfile = "";
           for(int j = 0; j < 8; j++){
               if(i == 512){
                   System.exit(0);
               }
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
        //This process is same as above. 
       int temp = 0;
       for(int i = 0; i < blocklist.length; i++){
            if(blocklist[i] == start){
                temp = blocklist[i];
                tempblockcount = i;
                break;
            }
        }
       //This is similar to contiguous except for using the temporaryblockcount and blocklist to get the next block as 
       //seen in the deleteFile function. 
       for(int i = 0; i < numblocks; i++){
            for(int j = 0; j <= 512; j++){
                if(j == 0){
                   b2[temp] = disk[temp];
                }
                if(j == 512){
                        tempblockcount++;
                        temp = blocklist[tempblockcount];
                        break;
                    }
                System.out.print((char)b2[temp][j]);
                
            }
       }
       System.out.print("\n");
    }
    @Override
    public void sendFile(byte [][]disk){
        int start = -1;
        int tempblockcount = 0;
        int numblocks = -1;
        Scanner scan = new Scanner(System.in);
        System.out.println("Copy from: ");
        filenamesim = scan.nextLine();
        System.out.println("Copy to: ");
        filenamereal = scan.nextLine();
        for(int i = 0; i < 512; i++){
            if(i == 512){
                   System.exit(0);
               }
           String blockfile = "";
           for(int j = 0; j < 8; j++){
               if(i == 512){
                   System.exit(0);
               }
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

            for(int i = 0; i < blocklist.length; i++){
                if(blocklist[i] == start){
                    temp = blocklist[i];
                    tempblockcount = i;
                    break;
                }
            }   
            for(int i = 0; i < numblocks; i++){
                for(int j = 0; j <= 512; j++){
                    if(j == 0){
                        b2[temp] = disk[temp];
                    }
                    if(j == 512){ 
                        //Increment tempblock count and get next block from list to store in temp. 
                        tempblockcount++;
                        temp = blocklist[tempblockcount];
                        break;
                    }
                    writer.print((char)b2[temp][j]);
                }
             }
            
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Contiguous.class.getName()).log(Level.SEVERE, null, ex);

        }   catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Chained.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}

