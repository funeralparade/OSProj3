/*
NOTE: THIS CLASS WILL HAVE THE MAJORITY OF THE COMMENTS REGARDING FUNCTIONALITY. IF YOU NEED TO REFER BACK TO COMMENTS WHILE GOING THROUGH
OTHER CLASSES, PLEASE REFER BACK TO THE CONTIGUOUS CLASS, AS THERE IS A LOT OF SHARED FUNCTIONALITY BETWEEN THE THREE. 
 */
package osproj3;
//Import util, file, and io, as well as exception utils. 
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Contiguous implements Strat{
    //Create a filenamereal, filenamesim, deletefile, and readfile string for the four methods. 
    public static String filenamereal;
    public static String filenamesim;
    public static String deletefile;
    public static String readfile;
    //Create a duplicate 2D byte array as before. 
    public static byte [][] b2 = new byte[256][512];
    @Override
    public void doOp(byte [][] disk){
        //Create a new scanner, as well as a start and numblock object to hold the next block to be wrote to and the number of blocks
        //traversed. 
        Scanner scan = new Scanner(System.in);
        int numblocks = 0;
        int start = 0;
        //Create a flag which is initialized false. 
        boolean flag = false;
        //Blocks traversed counter which will be default set to 1. 
        int blockstraversed = 1;
        //j counter to increment through the array which stores the file. 
        int j = 0;
        //Read in the file to copy from the real system. 
        System.out.println("Copy from: ");
        filenamereal = scan.nextLine();
        //Read in the simulated file name. If it is not equal to 8 characters, it will be declared inadmissible. 
        System.out.println("Copy to: ");
        filenamesim = scan.nextLine();
        while(filenamesim.length()!= 8){
            System.out.println("File must be 8 characters!: ");
            filenamesim = scan.nextLine();  
        }
        //Create a new file object using the filenamereal. If it does not exit, it will quit out of the simulation. 
        File programfile = new File(filenamereal); 
        if(!programfile.exists()) { 
        //If not, print File DNE. 
        System.out.println("File DNE");
        System.exit(0);
        }
        //Set the duplicate byte array equal to the bitmap and filetable from the disk by block. 
        b2[0] = disk[0];
        b2[1] = disk[1];
        try{
            //WARNING, YOU WILL NEED TO ALTER THIS PART BASED ON YOUR FILEPATH IN WHICH YOU PLACE THE PROJECT.
            //This path will be whereever the project folder is plus the filenamereal in order to get us the path
            //location to read the file in. bFile will read in all of the file contentts by byte and store it inside
            //an array. 
             String path = "C:\\Users\\jklei\\Documents\\NetBeansProjects\\OSProj3\\";
             path = path + filenamereal;
             Path location = Paths.get(path);
             byte[] bFile = Files.readAllBytes(location);
                    //Nested for loop which goes on the outside from 2 to 255 and inside for loop which goes from 0 to 512. 
                    for(int i = 2; i < 256; i++){
                        for(int k = 0; k <= 512; k++){
                        //Check to make sure there is more to write to file 
                        if(k == 512 && j < bFile.length && b2[1][i] == 0){
                            //Write to disk from duplicate array. 
                            disk[i] = b2[i];   
                            //Update bitmap in duplicate array. 
                            b2[1][i] = 1;
                            //Increment the number of blocks traversed.
                            blockstraversed++;
                            //If the number of blockstraversed is greater than 10, we will quit out of the program since 
                            //we have overloaded the disk.                           
                            if(blockstraversed > 10){
                                System.out.println("Number of Blocks exceeded! Quitting out...: ");
                                System.exit(0);
                            }
                            break;
                        }
                        //If there are still bytes to read in from the file. 
                        if(j < bFile.length){
                            //If that block is full:
                            if(b2[1][i] != 0){
                                //If j is not equal to zero, then we need to delete the data we already put into the disk. 
                                if(j !=0){
                                //Get number of blocks to delete. 
                                int idup = i - blockstraversed + 1;
                                //For the number of blocks we need to delete:
                                for(int del = 0; del < blockstraversed - 1; del++){
                                    //Reset all entries to zero, set bitmap, and write to disk. 
                                    for(int all = 0; all <= 512; all++){
                                        if(all == 512){
                                            b2[1][idup] = 0;
                                            disk[1] = b2[1];
                                            disk[idup] = b2[idup];
                                            idup++;
                                            break;
                                        }
                                        b2[idup][all] = 0;
                                    }
                                }
                                //Reset j and numblocks to get the new starting value and to make sure our 
                                //numblocks is set to 0. 
                                j = 0; 
                                numblocks = 0;
                                break;
                                }
                                //If j is zero, then we break since we haven't entered anything that needs to be deleted. 
                                else{
                                    break;
                                }
                            }
                            //Else, set the starting block equal to i if it's the first iteration and then read in from the
                            //file into the duplicate array. increment j to get next byte. 
                            else{
                                if(numblocks == 0){
                                    start = i; 
                                    numblocks = -1;
                                };
                                b2[i][k] = bFile[j];
                                j++;
                            }
                          }
                        //If we have reached the end of the file, and the flag is false, we will write to the disk using the block
                        //in the duplicate array and set the bitmap. We update numblocks to blockstraversed and set the flag to true. 
                        else if(j == bFile.length){
                              if(flag == false){
                                  disk[i] = b2[i];  
                                  b2[1][i] = 1;
                                  numblocks =  blockstraversed;
                                  flag = true;
                              }
                              break;
                          }
                        
                    }
            }
        //Write bitmap to disk. 
        disk[1] = b2[1];       
        //Update the filetable. 
        updateTable(start, filenamesim, numblocks, disk); 
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Contiguous.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Contiguous.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    //We need the starting block, filename, the total number of blocks, and the disk. 
    public void updateTable(int startblock, String file, int totalblocks, byte[][]disk){
        //for that block, if b2 at that point is empty, we put the filename in the next 8 blocks. 
        for(int i = 0; i < 512; i++){
            if(b2[0][i] == 0){
                for(int k = 0; k < file.length(); k++){
                    b2[0][i] = (byte) file.charAt(k);
                    i++;
                }
                //After putting in the filename, we put in a tab, then the starting block as a byte, then another tab,
                //then the total number of blocks, and finally a newline. 
                b2[0][i] = '\t';
                i++;
                b2[0][i] = (byte)startblock;
                i++;
                b2[0][i] = '\t';
                i++;
                b2[0][i] = (byte)totalblocks;
                i++;
                b2[0][i] = '\n';
                //Set the bitmap at block 0.
                b2[1][0] = 1;
                //Write bitmap and filetable to disk. 
                disk[1] = b2[1];
                disk[0] = b2[0];
                break;
            }
            //If that space is taken up, move 12 bytes to the next potential space. 
            else{
                i = i + 12;
            }
        }
    }
    @Override
    public void deleteFile(byte[][]disk){
        //Set start and numblocks integers to store filetable entries. 
        int start = -1;
        int numblocks = -1;
        //Get filename to delete. 
        Scanner scan = new Scanner(System.in);
        System.out.println("Delete file: ");
        deletefile = scan.nextLine();
        //For the 0th block: 
        for(int i = 0; i < 512; i++){
            //If i is equal to 512 at any point, exit. 
            if(i == 512){
                   System.exit(0);
               }
           String blockfile = "";
           //Read in the next 8 characters to see if they match the filename given by the user. 
           for(int j = 0; j < 8; j++){
               if(i == 512){
                   System.exit(0);
               }
           blockfile += (char)b2[0][i]; 
             i++;  
           }
           //If so, then get the starting and num blocks. Increment i accordingly and then set a temporary int to i. 
           if(blockfile.equals(deletefile)){
              i++;
              start = b2[0][i]&0xFF;
              i+=2;
              numblocks = b2[0][i]&0xFF;
              i++;
              int temp = i;
              //Delete the information about that file in the filetable. We delete from i-12 to the value of temp. 
              for(int k = i-12; k <= temp; k++){
                  b2[0][k] = 0;
              }
              //Write to disk at block 0. 
              disk[0] = b2[0];
              break;
           }
           //If the filenames are not equal, move four positions. 
           else{
                
                i += 4;
            }
        }
        //Set temp to the starting block and set the bitmap for the next (numblocks) positions equal to zero and increment temp. 
        int temp = start;
        for(int j = 0; j < numblocks; j++){
           b2[1][temp] = 0;
           temp++;
        }
        //Write bitmap to disk. 
        disk[1] = b2[1];
        //Check first block of disk. 
        for(int k = 0; k <= 512; k++){
            if(k == 512){
              //If all entries are 0, update bitmap and write to disk. 
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
        //Set start and numblocks integers to store filetable entries. 
        int start = -1;
        int numblocks = -1;
        //Get filename to read. 
        Scanner scan = new Scanner(System.in);
        System.out.println("Read file: ");
        readfile = scan.nextLine();
        //Same process as above, except we don't delete the file entries. 
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
        //Set temp equal to start. 
       int temp = start;
       //For the number of blocks, go through each block up to 512 and print all the characters. 
       for(int i = 0; i < numblocks; i++){ 
           for(int j = 0; j <= 512; j++){
               //On first iteration, read from disk at temporary location. 
               if(j == 0){
                   b2[temp] = disk[temp];
               }
               //If j is 512, then increment the temp and break. 
               if(j == 512){
                  temp++;
                  break;
               }
               //Print out the characters. 
               System.out.print((char)b2[temp][j]);  
            } 
       }
       System.out.print("\n");
    }
    @Override
    public void sendFile(byte [][]disk){
         //Set start and numblocks integers to store filetable entries. 
        int start = -1;
        int numblocks = -1;
        //Scan in simulation and real filename. 
        Scanner scan = new Scanner(System.in);
        System.out.println("Copy from: ");
        filenamesim = scan.nextLine();
        System.out.println("Copy to: ");
        filenamereal = scan.nextLine();
        //Process is same as above. 
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
            //Create new printwriter using the real filename. 
            PrintWriter writer = new PrintWriter(filenamereal, "UTF-8");
            //Set temp to starting block. 
            int temp = start;
            //for number of blocks, send output to file 
            for(int i = 0; i < numblocks; i++){
                for(int j = 0; j <= 512; j++){
                    //If j is 512, increment temp and break. 
                    if(j == 512){
                        temp++;
                        break;
                    }
                    //If j is zero, read from disk to duplicate array. 
                    if(j == 0){
                        b2[temp] = disk[temp];
                    }
                    //Send output to file. 
                    writer.print((char)b2[temp][j]);
                }
             }
            //Close file.
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Contiguous.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Contiguous.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    }


